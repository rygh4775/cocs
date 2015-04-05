package com.cocs.service.google;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cocs.beans.FileMeta;
import com.cocs.common.Env;
import com.cocs.common.FileResource;
import com.cocs.webapp.api.exception.ApiException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;

public class GoogleDAO {

	private final static String VENDOR = "google";
	
	private Drive client = null;
	
	public void setClient(Drive client) {
		this.client = client;
	}

	public JSONObject getUserInfo() throws ApiException {
		JSONObject result = new JSONObject();
		try {
			About about = client.about().get().execute();
			
			result.put("name", about.getName());
			result.put("quotaUsed", about.getQuotaBytesUsed());
			result.put("quotaTotal", about.getQuotaBytesTotal());
			result.put("rootFolderId", about.getRootFolderId());
		} catch (Exception e) {
			if(e instanceof HttpResponseException) {
				HttpResponseException he = (HttpResponseException)e;
				throw new ApiException(e.getMessage(), he.getStatusCode()).setVendor(VENDOR);
			} else {
				throw new ApiException(e.getMessage()).setVendor(VENDOR);
			}
		      
		}
		return result;
	}
	
	public void createFolder(String name, String parentId) throws ApiException {
		File body = new File();
	    body.setTitle(name);
	    body.setMimeType("application/vnd.google-apps.folder");
	    body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
		
	    try {
	    	client.files().insert(body).execute();
	    } catch (IOException e) {
	    	throw new ApiException(e);
	    }
	}
	
	public void rename(String id, String name) throws ApiException {
		File body = new File();
		body.setTitle(name);
		
		try {
			client.files().patch(id, body).execute();
	    } catch (IOException e) {
	    	throw new ApiException(e);
	    }
	}
	
	public void delete(String id) throws ApiException {
		try {
			client.files().trash(id).execute();
	    } catch (IOException e) {
	    	throw new ApiException(e);
	    }
	}
	
	public void untrashFile(String id) throws ApiException {
		try {
			client.files().untrash(id).execute();
	    } catch (IOException e) {
	    	throw new ApiException(e);
	    }
	}
	
	public List<FileMeta> getFiles(String parentId, boolean includeDeleted) throws ApiException {
		if(StringUtils.isBlank(parentId)) {
			parentId = "root";
		}
		
		try {
			Files.List list = client.files().list();
			list.setQ("'" + parentId + "' in parents");
			FileList files = list.execute();

			List<FileMeta> fileMetas = new ArrayList<FileMeta>(); 
			for (File file : files.getItems()) {
				if(!includeDeleted && file.getLabels().getTrashed()) {
					continue;
				}
				FileMeta fileMeta = new FileMeta();
				
				String mimeType = file.getMimeType();
				fileMeta.setMimeType(mimeType);
				
				if(mimeType.endsWith("folder")) {
					fileMeta.setIsFile(false);
				} else {
					new GooglePermissionThread(client, file.getId()).start();
					fileMeta.setIsFile(true);
					fileMeta.setPreviewLink(file.getEmbedLink() == null ? file.getWebContentLink() : file.getEmbedLink());
					
					if(mimeType.startsWith("image")) {
						fileMeta.setPreviewSupported(true);
					} else if(mimeType.startsWith("application/vnd.google-apps") && (mimeType.endsWith("spreadsheet") || mimeType.endsWith("document") || mimeType.endsWith("presentation"))){
						fileMeta.setPreviewSupported(true);
					} else {
						fileMeta.setPreviewSupported(false);
					}
				}
				
				if(file.getFileSize() != null) {
					fileMeta.setSize(file.getFileSize());
				}
				
				fileMeta.setId(file.getId());
				fileMeta.setName(file.getTitle());
				fileMeta.setCreated(file.getCreatedDate().getValue());
				fileMeta.setModified(file.getModifiedDate().getValue());
				fileMeta.setIsDeleted(file.getLabels().getTrashed());
				fileMeta.setThumbnailLink(file.getThumbnailLink());
				
//				temp
				fileMeta.setIconInfo(file.getIconLink());
				
				fileMetas.add(fileMeta);
			}
			return fileMetas;
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}

	public void copy(String id, String targetParentId) throws ApiException {
		try {
			File newFile = new File();
			newFile.setTitle(client.files().get(id).execute().getTitle());
			newFile.setParents(Arrays.asList(new ParentReference().setId(targetParentId)));
			
			client.files().copy(id, newFile).execute();
		} catch (IOException e) {
			if(e instanceof GoogleJsonResponseException) {
				if(((GoogleJsonResponseException) e).getDetails().getMessage().equals("File does not support copying")) {
					throw new ApiException("구글 드라이브는 폴더 복사를 지원하지 않습니다.");
				}
			}
			throw new ApiException(e);
		}
	}
	
	public void move(String id, String targetParentId) throws ApiException {
		try {
			String originalParentId = client.files().get(id).execute().getParents().get(0).getId();
			
			client.parents().insert(id, new ParentReference().setId(targetParentId)).execute();
			//after new parent created successfully
			client.parents().delete(id, originalParentId).execute();
		} catch (IOException e) {
			new ApiException(e);
		}
	}
	
	public FileMeta sendMove(String id) throws ApiException {
		FileMeta originalFile = new FileMeta();
		try {
			String originalParentId = client.files().get(id).execute().getParents().get(0).getId();
			File file = client.files().get(id).execute();
		    HttpResponse resp =	client.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
		    originalFile.setName(file.getTitle());
		    originalFile.setMimeType(file.getMimeType());
		    originalFile.setFileBinary(resp.getContent());
		    client.parents().delete(id, originalParentId).execute();
		    return originalFile;
		} catch (IOException e) {
			new ApiException(e);
		} finally{
			
		}
		return originalFile;
		
	}
	
	public FileMeta sendCopy(String id) throws ApiException {
		FileMeta originalFile = new FileMeta();
		try {
			String originalParentId = client.files().get(id).execute().getParents().get(0).getId();
			File file = client.files().get(id).execute();
		    HttpResponse resp =	client.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
		    originalFile.setName(file.getTitle());
		    originalFile.setMimeType(file.getMimeType());
		    originalFile.setFileBinary(resp.getContent());
		    return originalFile;
		} catch (IOException e) {
			new ApiException(e);
		} finally{
			
		}
		return originalFile;
		
	}

	public void uploadFile(String parentId, MultipartFile file) throws ApiException {
		
//		MultipartFileResource multipartFileResource = new MultipartFileResource(file);
	    
	    File body = new File();
	    body.setTitle(file.getOriginalFilename());
	    body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
	    
		try {
			InputStreamContent mediaContent = new InputStreamContent(file.getContentType(), file.getInputStream());
			client.files().insert(body, mediaContent).execute();
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}
	
	public void uploadFile(String parentId, FileMeta file) throws ApiException {
		
//		MultipartFileResource multipartFileResource = new MultipartFileResource(file);
	    
	    File body = new File();
	    body.setTitle(file.getName());
	    body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
	    
		try {
			InputStreamContent mediaContent = new InputStreamContent(file.getMimeType(), file.getFileBinary());
			client.files().insert(body, mediaContent).execute();
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}


	public List<FileMeta> searchFiles(String keyword) throws ApiException {
		StringBuffer query = new StringBuffer("mimeType != 'application/vnd.google-apps.folder' and title contains '"+keyword+"'");
//		String[] titles = keyword.split("\\s+"); // `\\s+` matches one or more white space characters
//		StringBuffer query = new StringBuffer("mimeType != 'application/vnd.google-apps.folder'");
//		for (String title : titles) {
//			query.append("and title contains '"+title+"'");
//		}
		try {
			Files.List list = client.files().list();
			list.setQ(query.toString());
			FileList files = list.execute();

			List<FileMeta> fileMetas = new ArrayList<FileMeta>(); 
			for (File file : files.getItems()) {
				FileMeta fileMeta = new FileMeta();
				fileMeta.setId(file.getId());
				fileMeta.setName(file.getTitle());
				Long size = file.getFileSize(); 
				if(size != null) {
					fileMeta.setSize(size);
				}
				fileMeta.setCreated(file.getCreatedDate().getValue());
				fileMeta.setModified(file.getModifiedDate().getValue());
				fileMeta.setMimeType(file.getMimeType());
				fileMeta.setIsDeleted(file.getLabels().getTrashed());
				fileMeta.setIsFile(!file.getMimeType().endsWith("folder"));
				fileMeta.setThumbnailLink(file.getThumbnailLink());
				if(file.getMimeType().startsWith("image")) {
					fileMeta.setPreviewSupported(true);
				} else if(file.getMimeType().startsWith("application/vnd.google-apps")) {
					if(file.getMimeType().endsWith("spreadsheet") || file.getMimeType().endsWith("document") || file.getMimeType().endsWith("presentation"))
						fileMeta.setPreviewSupported(true);
				} else {
					fileMeta.setPreviewSupported(false);
				}
				fileMeta.setVendor("google");
				
//				temp
				fileMeta.setIconInfo(file.getIconLink());
				
				fileMetas.add(fileMeta);
			}
			return fileMetas;
		} catch (IOException e) {
			throw new ApiException(e);
		}
	    
	}

	public FileResource downloadFile(String id) throws IOException {
		File file = client.files().get(id).execute();
		String downloadUrl = file.getDownloadUrl();
		String title = file.getTitle();
		if(StringUtils.isBlank(downloadUrl)) {
			if(file.getMimeType().equals("application/vnd.google-apps.spreadsheet")) {
				downloadUrl = file.getExportLinks().get("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				title += ".xlsx";
			} else if (file.getMimeType().equals("application/vnd.google-apps.document")) {
				downloadUrl = file.getExportLinks().get("text/plain");
				title += ".txt";
			} else {
				downloadUrl = file.getExportLinks().get("application/pdf");
				title += ".pdf";
			}
		}
		
		HttpResponse googleResponse = client.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl)).execute();
		String mimeType = file.getMimeType();
		
		if (mimeType == null) {
			mimeType = "application/octet-stream";
	    }
		
		FileResource fileResource = new FileResource(googleResponse.getContent());
		fileResource.setName(title);
		
		return fileResource;
	}

	public void downloadFiles(String[] ids, HttpServletResponse response) throws IOException {
		String zipFileName = "archived_"+System.currentTimeMillis()+".zip";
		
		java.io.File dir = new java.io.File(Env.getRepositoryUploadDirPath(), "archive_" + System.currentTimeMillis());
		dir.mkdirs();

		java.io.File zipFile = new java.io.File(dir, zipFileName);
		zipFile.createNewFile();
		
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
		zos.setLevel(Deflater.BEST_COMPRESSION);
//		zos.setCharSet("utf-8");
		
		for (String fileId : ids) {
			File file = client.files().get(fileId).execute();
			
			String downloadUrl = file.getDownloadUrl();
			String title = file.getTitle();
			if(StringUtils.isBlank(downloadUrl)) {
				downloadUrl = file.getExportLinks().get("application/pdf");
				title += ".pdf";
			}
			
			HttpResponse rep = client.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl)).execute();
			
			zos.putNextEntry(new ZipEntry(new String(title.getBytes("utf-8"))));
			InputStream input = rep.getContent();
			
			IOUtils.copy(input, zos);
			
//			zos.flush();
			zos.closeEntry();
		}
		zos.close();
		
		java.io.File tempFile = new java.io.File(Env.getRepositoryUploadDirPath(), Long.toString(System.currentTimeMillis()));
		zipFile.renameTo(tempFile);
		zipFile = tempFile;
		dir.delete();
		
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=COCS_"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".zip");
		
		FileCopyUtils.copy(new FileInputStream(zipFile), response.getOutputStream());
		
		tempFile.delete();
	}

	public List<FileMeta> getFolders(String parentId, boolean includeDeleted) throws ApiException {
		if(StringUtils.isBlank(parentId)) {
			parentId = "root";
		}
		
		try {
			Files.List list = client.files().list();
			list.setQ("'" + parentId + "' in parents and mimeType = 'application/vnd.google-apps.folder'"); // parentId
			FileList files = list.execute();

			List<FileMeta> fileMetas = new ArrayList<FileMeta>();
			for (File file : files.getItems()) {
				if(!includeDeleted && file.getLabels().getTrashed()) {
					continue;
				}
				
				FileMeta fileMeta = new FileMeta();
				fileMeta.setId(file.getId());
				fileMeta.setName(file.getTitle());
				fileMeta.setCreated(file.getCreatedDate().getValue());
				fileMeta.setModified(file.getModifiedDate().getValue());
				fileMeta.setIsDeleted(file.getLabels().getTrashed());
				fileMeta.setIsFile(false);
				fileMeta.setParentsId(parentId);
				
				fileMetas.add(fileMeta);
				
			}
			
			return fileMetas;
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}

	public String getPreviewLink(String id) throws ApiException {
		String previewLink = null;
		try {
			File file = client.files().get(id).execute();
			if(file.getEmbedLink() != null) {
				previewLink = file.getEmbedLink();
			} else {
				previewLink = file.getWebContentLink();
			}
		} catch (IOException e) {
			throw new ApiException(e);
		}
		
		return previewLink;
	}
	
	class GooglePermissionThread implements Runnable {

		private Thread thread;
		private Drive client;
		private String fileId;
		
		public GooglePermissionThread(Drive client, String fileId) {
			this.client = client;
			this.fileId = fileId;
		}
		
		@Override
		public void run() {
			Permission permission = new Permission();
			permission.setRole("reader");
			permission.setType("anyone");
			permission.setWithLink(true);
			permission.setValue("me");
			try {
				client.permissions().insert(fileId, permission).execute();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		public void start() {
			if (thread == null) {
				thread = new Thread (this, fileId);
				thread.start();
			}
		}
		
	}
}
