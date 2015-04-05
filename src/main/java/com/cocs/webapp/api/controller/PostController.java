package com.cocs.webapp.api.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cocs.common.FileResource;
import com.cocs.handler.Params;
import com.cocs.handler.ResponseSuccess;
import com.cocs.service.ClientManager;
import com.cocs.service.IService;
import com.cocs.service.Vendors;
import com.cocs.webapp.api.exception.ApiException;

@Controller
public class PostController {
	
	@Autowired
	ClientManager clientManager;
	
	@RequestMapping(value = { "/{vendor}/post/create.*" })
	public void create(	@PathVariable String vendor,
							final MultipartHttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		FileResource fileResource = null;
		
		MultipartFile file = null;
		Iterator<String> it = request.getFileNames();
		while(it.hasNext()){
			String fileId = it.next();
			file = request.getFile(fileId);
			if(StringUtils.isNotBlank(file.getOriginalFilename())){
				try {
					fileResource = new FileResource(file.getInputStream());
					fileResource.setName(file.getName());
					fileResource.setMimeType(file.getContentType());
				} catch (IOException e) {
					new RuntimeException(e);
				}
			}
		}
		
		Params params = new Params(request.getParameterMap());
		
		String cloudVendor = request.getParameter("cloudVendor");
		if(StringUtils.isNotBlank(cloudVendor)) {
			IService cloudService = Vendors.valueOf(cloudVendor).getService().setClient(clientManager);
			fileResource = cloudService.downloadFile(params);
		}
		
		ResponseSuccess response = service.createPost(params, fileResource);
		model.addAllAttributes(response);
		
		fileResource.deleteFile();
	}
	
	@RequestMapping(value = { "/{vendor}/post/delete.*" })
	public void delete(	@PathVariable String vendor,
			final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseSuccess response = service.deletePost(params);
		
		model.addAllAttributes(response);
	}

}
