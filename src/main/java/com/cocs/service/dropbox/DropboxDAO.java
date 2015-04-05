package com.cocs.service.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cocs.beans.FileMeta;
import com.cocs.common.Env;
import com.cocs.common.FileResource;
import com.cocs.webapp.api.exception.ApiException;
import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxEntry.WithChildren;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxException.BadResponse;
import com.dropbox.core.DbxException.BadResponseCode;
import com.dropbox.core.DbxPath;
import com.dropbox.core.DbxThumbnailFormat;
import com.dropbox.core.DbxThumbnailSize;
import com.dropbox.core.DbxUrlWithExpiration;
import com.dropbox.core.DbxWriteMode;

public class DropboxDAO{

	private final static String VENDOR = "dropbox";
	
	private DbxClient client = null;
	
	public void setClient(DbxClient client) {
		this.client = client;
	}
	
	public JSONObject getUserInfo() throws ApiException {
		JSONObject result = new JSONObject();
		try {
			DbxAccountInfo accountInfo = client.getAccountInfo();
			
			result.put("name", accountInfo.displayName);
			result.put("quotaUsed", accountInfo.quota.normal);
			result.put("quotaTotal", accountInfo.quota.total);
		} catch (DbxException e) {
			if(e instanceof BadResponse) {
				throw new ApiException(e.getMessage()).setVendor(VENDOR).setStatusCode(HttpStatus.SC_UNAUTHORIZED);
			}
	        else {
	        	new ApiException(e.getMessage()).setVendor(VENDOR);
	        }
		}
		return result;
	}

	public void createFolder(String name, String parentPath) throws ApiException {
		if(!parentPath.endsWith("/")) {
			parentPath += "/";
		}
		String path = parentPath + name;
		try {
			client.createFolder(path);
		} catch (DbxException e) {
			throw new ApiException(e);
		}
	}
	
	public void rename(String name, String path) throws ApiException {
		try {
			client.move(path, DbxPath.getParent(path)+"/"+name);
		} catch (DbxException e) {
			throw new ApiException(e);
		}
	}
	
	public List<FileMeta> getFiles(String parentPath, boolean includeDeleted) throws ApiException {
		if(StringUtils.isBlank(parentPath)) {
			parentPath = "/";
		}
		
		try {
			WithChildren listing = client.getMetadataWithChildren(parentPath);
			
			List<FileMeta> fileMetas = new ArrayList<FileMeta>();
			for(DbxEntry file : listing.children){
				if(!includeDeleted && file.isDeleted) {
					continue;
				}
				
				FileMeta fileMeta = new FileMeta();
				fileMeta.setPath(file.path);
				fileMeta.setName(file.name);
				if(file.isFile()) {
					fileMeta.setSize(file.asFile().numBytes);
					fileMeta.setCreated(file.asFile().clientMtime.getTime());
					fileMeta.setModified(file.asFile().lastModified.getTime());
					
					String mimeType = file.asFile().mimeType;
					fileMeta.setMimeType(mimeType);
					
					if(mimeType.startsWith("image")) {
						fileMeta.setPreviewSupported(true);
						fileMeta.setPreviewLink(client.createTemporaryDirectUrl(file.path).url);
					}
					
				}
				fileMeta.setIsDeleted(file.isDeleted);
				fileMeta.setIsFile(file.isFile());
//				temp
				fileMeta.setIconInfo(file.iconName);
				
				fileMetas.add(fileMeta);
			}
			return fileMetas;
			
		} catch (DbxException e) {
			throw new ApiException(e);
		}
	}
	
	public void delete(String path) throws ApiException {
		try{
			client.delete(path);
		} catch (DbxException e) {
			throw new ApiException(e);
		}
	}
	
	public void untrashFile(String path) throws ApiException {
		throw new NotImplementedException();
	}

	public void copy(String path, String targetParentPath) throws ApiException {
		String name = DbxPath.getName(path);
		
		try {
			client.copy(path, targetParentPath+"/"+name);
		} catch (DbxException e) {
			if(e instanceof BadResponseCode) {
				if(((BadResponseCode) e).statusCode == 403) {
					throw new ApiException("동일한 이름의 파일이 존재합니다.");
				}
			}
			throw new ApiException(e);
		}
	}

	public void move(String path, String targetParentPath) throws ApiException {
		try {
			client.move(path, targetParentPath+"/"+DbxPath.getName(path));
		} catch (DbxException e) {
			if(e instanceof BadResponseCode) {
				if(((BadResponseCode) e).statusCode == 403) {
					throw new ApiException("동일한 이름의 파일이 존재합니다.");
				}
			}
			throw new ApiException(e);
		}
	}

	public void uploadFile(String parentPath, MultipartFile file) throws ApiException {
		if(StringUtils.isEmpty(parentPath)) {
			parentPath = "";
		}
		DbxWriteMode writeMode = DbxWriteMode.add();
		try {
			client.uploadFile(parentPath+"/"+file.getOriginalFilename(), writeMode, -1, file.getInputStream());
		} catch (DbxException | IOException e) {
			throw new ApiException(e);
		}
	}
	
	public void uploadFile(String parentPath, FileMeta file) throws ApiException {
		if(StringUtils.isEmpty(parentPath)) {
			parentPath = "";
		}
		DbxWriteMode writeMode = DbxWriteMode.add();
		try {
			client.uploadFile(parentPath+"/"+file.getName(), writeMode, -1, file.getFileBinary());
		} catch (DbxException | IOException e) {
			throw new ApiException(e);
		}
	}
	
	public FileMeta sendMove(String path) throws ApiException{
		FileMeta originalFile = new FileMeta();
		FileResource fileResource = new FileResource();
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileResource.getFile());
			com.dropbox.core.DbxEntry.File file = client.getFile(path, null, outputStream);
			if(file.mimeType != null) {
				fileResource.setMimeType(file.mimeType);
			}
			fileResource.setName(DbxPath.getName(path));
			originalFile.setMimeType(fileResource.getMimeType());
			originalFile.setName(fileResource.getName());
			originalFile.setFileBinary(fileResource.getInputStream());
			client.delete(path);
			
		} catch (DbxException e) {
			throw new ApiException(e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
		
		return originalFile;
	}
	
	public FileMeta sendCopy(String path) throws ApiException{
		FileMeta originalFile = new FileMeta();
		FileResource fileResource = new FileResource();
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileResource.getFile());
			com.dropbox.core.DbxEntry.File file = client.getFile(path, null, outputStream);
			if(file.mimeType != null) {
				fileResource.setMimeType(file.mimeType);
			}
			fileResource.setName(DbxPath.getName(path));
			originalFile.setMimeType(fileResource.getMimeType());
			originalFile.setName(fileResource.getName());
			originalFile.setFileBinary(fileResource.getInputStream());
		} catch (DbxException e) {
			throw new ApiException(e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
		
		return originalFile;
	}
	
	
	public List<FileMeta> searchFiles(String keyowrd) throws ApiException {
		try {
			List<DbxEntry> searchFileAndFolderNames = client.searchFileAndFolderNames("/", keyowrd);
			
			List<FileMeta> fileMetas = new ArrayList<FileMeta>();
			for(DbxEntry file : searchFileAndFolderNames){
				
				if(file.isFile()) {
					FileMeta fileMeta = new FileMeta();
					fileMeta.setPath(file.path);
					fileMeta.setName(file.name);
					fileMeta.setSize(file.asFile().numBytes);
					fileMeta.setCreated(file.asFile().clientMtime.getTime());
					fileMeta.setModified(file.asFile().lastModified.getTime());
					fileMeta.setMimeType(file.asFile().mimeType);
					fileMeta.setPreviewSupported(file.asFile().mimeType.startsWith("image") || file.asFile().mimeType.startsWith("application/pdf"));
					fileMeta.setIsDeleted(file.isDeleted);
					fileMeta.setIsFile(file.isFile());
					fileMeta.setVendor("dropbox");
					
//					temp
					fileMeta.setIconInfo(file.iconName);
					
					fileMetas.add(fileMeta);
				}
				
			}
			return fileMetas;
			
		} catch (DbxException e) {
			throw new ApiException(e);
		}
	}

	public FileResource downloadFile(String path) throws IOException, ApiException {
		FileResource fileResource = new FileResource();
		OutputStream outputStream = new FileOutputStream(fileResource.getFile());
		try {
			com.dropbox.core.DbxEntry.File file = client.getFile(path, null, outputStream);
			if(file.mimeType != null) {
				fileResource.setMimeType(file.mimeType);
			}
		} catch (DbxException e) {
			throw new ApiException(e);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
		
		fileResource.setName(DbxPath.getName(path));
		return fileResource;
	}

	public void downloadFiles(String[] paths, HttpServletResponse response) throws IOException, ApiException {
		String zipFileName = "archived_"+System.currentTimeMillis();
		
		java.io.File dir = new java.io.File(Env.getRepositoryUploadDirPath(), "archive_" + System.currentTimeMillis());
		dir.mkdirs();

		java.io.File zipFile = new java.io.File(dir, zipFileName);
		zipFile.createNewFile();
		
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
		zos.setLevel(Deflater.BEST_COMPRESSION);
//		zos.setCharSet("utf-8");
		
		for (String path : paths) {
			String name = DbxPath.getName(path);
			
			File target = File.createTempFile(name+System.currentTimeMillis(), null, dir);
			OutputStream out = new FileOutputStream(target);
			
			try {
				client.getFile(path, null, out);
			} catch (DbxException e) {
				throw new ApiException(e);
			} finally {
				out.close();
			}
				
			zos.putNextEntry(new ZipEntry(new String(name.getBytes("utf-8"))));
			
			IOUtils.copy(new FileInputStream(target), zos);
			
//			zos.flush();
			zos.closeEntry();
			target.delete();
		}
		zos.close();
		
		File tempFile = new File(Env.getRepositoryUploadDirPath(), Long.toString(System.currentTimeMillis()));
		zipFile.renameTo(tempFile);
		zipFile = tempFile;
		dir.delete();
		
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=COCS_"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".zip");
		
		FileCopyUtils.copy(new FileInputStream(zipFile), response.getOutputStream());
		
		tempFile.delete();
	}

	public List<FileMeta> getFolders(String parentPath, boolean includeDeleted) throws ApiException {
		if(StringUtils.isBlank(parentPath)) {
			parentPath = "/";
		}
		
		try {
			WithChildren listing = client.getMetadataWithChildren(parentPath);
			
			List<FileMeta> fileMetas = new ArrayList<FileMeta>();
			for(DbxEntry file : listing.children){
				if(file.isFolder()) {
					if(!includeDeleted && file.isDeleted) {
						continue;
					}	
					
					FileMeta fileMeta = new FileMeta();
					fileMeta.setPath(file.path);
					fileMeta.setName(file.name);
					fileMeta.setIsDeleted(file.isDeleted);
					fileMeta.setIsFile(false);
					fileMeta.setParentsId(parentPath);
					
					fileMetas.add(fileMeta);
				}
			}
			
			return fileMetas;
			
		} catch (DbxException e) {
			throw new ApiException(e);
		}
	}

	public String getPreviewLink(String path) throws ApiException {
		try {
			DbxUrlWithExpiration temproaryDirectUrl = client.createTemporaryDirectUrl(path);
			return temproaryDirectUrl.url;
		} catch (DbxException e) {
			throw new ApiException(e);
		}
	}
	
	public void getThumbnail(String path, HttpServletResponse response) throws ApiException {
		try {
			DbxThumbnailSize sizeBound = new DbxThumbnailSize("m", 100, 100);
			
			client.getThumbnail(sizeBound , DbxThumbnailFormat.JPEG, path, null, response.getOutputStream());
		} catch (DbxException | IOException e) {
			throw new ApiException(e);
		}
	}


//	public JSONObject uploadFileChuncked(Map<String, Object> param, MultipartFile file) throws ApiException {
//		String parentPath = (String) param.get(PARENT_PATH);
//		if(StringUtils.isEmpty(parentPath)) {
//			parentPath = "";
//		}
//		DbxWriteMode writeMode = DbxWriteMode.add();
//		try {
//			DbxStreamWriter<IOException> writer = new InputStreamCopier(file.getInputStream());
//			client.uploadFileChunked(parentPath+"/"+file.getOriginalFilename(), writeMode, -1, writer);
//		} catch (DbxException | IOException e) {
//			throw new ApiException(e);
//		}
//		JSONObject result = new JSONObject();
//		result.put(SUCCESS, true);
//		return result;
//	}
}
