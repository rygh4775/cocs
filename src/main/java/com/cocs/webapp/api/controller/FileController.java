package com.cocs.webapp.api.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cocs.beans.UploadFile;
import com.cocs.common.DefaultConstants;
import com.cocs.common.FileResource;
import com.cocs.handler.Params;
import com.cocs.handler.ResponseResult;
import com.cocs.handler.ResponseRows;
import com.cocs.handler.ResponseSuccess;
import com.cocs.service.ClientManager;
import com.cocs.service.IService;
import com.cocs.service.Vendors;
import com.cocs.webapp.api.exception.ApiException;

@Controller
public class FileController implements DefaultConstants{
	
	@Autowired
	ClientManager clientManager;
	
	@RequestMapping(value = { "/{vendor}/file/upload.*" })
	public void upload(	@PathVariable String vendor,
						final MultipartHttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		String fileName = null;
		MultipartFile file = null;
		Iterator<String> it = request.getFileNames();
		while(it.hasNext()){
			String fileId = it.next();
			file = request.getFile(fileId);
			fileName = file.getOriginalFilename();
		}
		
		Params params = new Params(request.getParameterMap());
		ResponseSuccess response = service.uploadFile(params, file);
		
		if(response.isSuccess()) {
			LinkedList<UploadFile> fileMetas = new LinkedList<UploadFile>();
			fileMetas.add(new UploadFile(fileName, file.getSize(), "dd", "www"));
			model.put("files", fileMetas);
		}
	}
	
////	@RequestMapping(value = { "/dropbox/file/uploadChunked.*" })
//	private void uploadChunked(	@PathVariable String vendor,
//						final MultipartHttpServletRequest request, final ModelMap model) throws ApiException {
//		DropboxService service = DropboxService.getInstance();
////		service.setClient(request.getSession());
//		
//		String fileName = null;
//		MultipartFile file = null;
//		Iterator<String> it = request.getFileNames();
//		while(it.hasNext()){
//			String fileId = it.next();
//			file = request.getFile(fileId);
//			fileName = file.getOriginalFilename();
//		}
//		Map params = getParams(request);
//		JSONObject result = service.uploadFileChuncked(params, file);
//		
//		if(result.getBoolean(SUCCESS)) {
//			LinkedList<UploadFile> fileMetas = new LinkedList<UploadFile>();
//			fileMetas.add(new UploadFile(fileName, file.getSize(), "dd", "www"));
//			model.put("files", fileMetas);
//		}
//	}
	
	@RequestMapping(value = { "/{vendor}/file/rename.*" })
	public void rename(	@PathVariable String vendor,
						final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseSuccess response = service.rename(params);
		
		model.addAllAttributes(response);
	}
	
	@RequestMapping(value = { "/{vendor}/file/getFiles.*" })
	public void getFiles(	@PathVariable String vendor,
								final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseRows response = service.getFiles(params);
		
		model.addAllAttributes(response);
	}
	
	@RequestMapping(value = { "/{vendor}/file/update.*" })
	public void update(	@PathVariable String vendor,
								final HttpServletRequest request, final ModelMap model) throws ApiException {
		
	}
	
	@RequestMapping(value = { "/{vendor}/file/delete.*" })
	public void delete(	@PathVariable String vendor,
								final HttpServletRequest request, final ModelMap model) throws ApiException {
		
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseSuccess response = service.delete(params);
		
		model.addAllAttributes(response);
		
	}
	
	@RequestMapping(value = { "/{vendor}/file/untrash.*" })
	public void unTrash(	@PathVariable String vendor,
								final HttpServletRequest request, final ModelMap model) throws ApiException {
		
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseSuccess response = service.untrashFile(params);
		
		model.addAllAttributes(response);
		
	}
	
	@RequestMapping(value = { "/{vendor}/file/search.*" })
	public void search(	@PathVariable String vendor,
								final HttpServletRequest request, final ModelMap model) throws ApiException {
		
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseRows response = service.searchFiles(params);
		
		model.addAllAttributes(response);
	}
	
	@RequestMapping(value = { "/{vendor}/file/downloadFile.*" })
	public void downloadFile(	@PathVariable String vendor,
								final HttpServletRequest request, final HttpServletResponse response,
								final ModelMap model) throws ApiException, IOException {
		
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		FileResource fileResource = service.downloadFile(params);
		
		response.setContentType(fileResource.getMimeType());
		response.setHeader("Content-Disposition", "attachment; filename="+fileResource.getName()+"");
		FileCopyUtils.copy(fileResource.getInputStream(), response.getOutputStream());
		
		fileResource.deleteFile();
	}
	
	@RequestMapping(value = { "/{vendor}/file/downloadFiles.*" })
	public void downloadFiles(	@PathVariable String vendor,
								final HttpServletRequest request, final HttpServletResponse response,
								final ModelMap model) throws ApiException, IOException {
		
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		service.downloadFiles(params, response);
	}
	
//	@RequestMapping(value = { "/{vendor}/file/archiveFiles.*" })
//	public void archiveFiles(	@PathVariable String vendor,
//								final HttpServletRequest request, final HttpServletResponse response,
//								final ModelMap model) throws ApiException, IOException {
//		
//		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
//		
//		Params params = new Params(request.getParameterMap());
//		ResponseResult result = service.archiveFiles(params, response);
//		
//		model.addAllAttributes(result);
//	}
	
	@RequestMapping(value = { "/{vendor}/file/move.*" })
	public void move(	@PathVariable String vendor,
						@RequestParam(value="targetVendor", required=true) String targetVendor,
						final HttpServletRequest request, final ModelMap model) throws ApiException {
		
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		Params params = new Params(request.getParameterMap());
		ResponseSuccess response = null;
		
		if(StringUtils.isNotBlank(targetVendor) && vendor.equals(targetVendor)) {
			response = service.move(params);
		} else {
			FileResource fileResource = service.downloadFile(params);
			IService targetService = Vendors.valueOf(targetVendor).getService().setClient(clientManager);
			MockMultipartFile multipartFile = null;
			try {
				multipartFile = new MockMultipartFile(fileResource.getName(), fileResource.getName(), fileResource.getMimeType(), fileResource.getInputStream());
			} catch (IOException e) {
				throw new ApiException(e);
			}
			
			response = targetService.uploadFile(params, multipartFile);
			if(response.isSuccess()) {
				response = service.delete(params);
			}
		}
		
		model.addAllAttributes(response);
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/{vendor}/file/other/move.*" })
	public void otherMove(	@PathVariable String vendor,
						@RequestParam(value="targetVendor", required=true) String targetVendor,
						final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		IService targetService = Vendors.valueOf(targetVendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		// 파일을 받아온 후
		ResponseSuccess response = targetService.targetMove(params, service.sendMove(params));
		model.addAllAttributes(response);
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/{vendor}/file/other/copy.*" })
	public void otherCopy(	@PathVariable String vendor,
						@RequestParam(value="targetVendor", required=true) String targetVendor,
						final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		IService targetService = Vendors.valueOf(targetVendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		// 파일을 받아온 후
		ResponseSuccess response = targetService.targetCopy(params, service.sendCopy(params));
		model.addAllAttributes(response);
		
	}
	
	@RequestMapping(value = { "/{vendor}/file/copy.*" })
	public void copy(	@PathVariable String vendor,
						@RequestParam(value="targetVendor", required=true) String targetVendor,
						final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		Params params = new Params(request.getParameterMap());
		ResponseSuccess response = null;
		
		if(StringUtils.isNotBlank(targetVendor) && vendor.equals(targetVendor)) {
			response = service.copy(params);
		} else {
			FileResource fileResource = service.downloadFile(params);
			IService targetService = Vendors.valueOf(targetVendor).getService().setClient(clientManager);
			MockMultipartFile multipartFile = null;
			try {
				multipartFile = new MockMultipartFile(fileResource.getName(), fileResource.getName(), fileResource.getMimeType(), fileResource.getInputStream());
			} catch (IOException e) {
				throw new ApiException(e);
			}
			
			response = targetService.uploadFile(params, multipartFile);
		}
		
		model.addAllAttributes(response);
	}
	
	@RequestMapping(value = { "/{vendor}/file/getPreviewLink.*" })
	public void getPreviewLink(	@PathVariable String vendor,
								final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseResult response = service.getPreviewLink(params);
		
		model.addAllAttributes(response);
	}
	
	@RequestMapping(value = { "/{vendor}/file/getThumbnail.*" })
	public void getThumbnail(	@PathVariable String vendor,
								final HttpServletRequest request, final HttpServletResponse response,
								final ModelMap model) throws ApiException, IOException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		service.getThumbnail(params, response);
	}
}
