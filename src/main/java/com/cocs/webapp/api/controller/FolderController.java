package com.cocs.webapp.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cocs.handler.Params;
import com.cocs.handler.ResponseRows;
import com.cocs.handler.ResponseSuccess;
import com.cocs.service.ClientManager;
import com.cocs.service.IService;
import com.cocs.service.Vendors;
import com.cocs.webapp.api.exception.ApiException;

@Controller
public class FolderController {
	
	@Autowired
	ClientManager clientManager;
	
	@RequestMapping(value = { "/{vendor}/folder/create.*" })
	public void createFile(	@PathVariable String vendor,
							final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseSuccess response = service.createFolder(params);
		
		model.addAllAttributes(response);
	}

	
	@RequestMapping(value = { "/{vendor}/folder/rename.*" })
	public void rename(	@PathVariable String vendor,
						final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseSuccess response = service.rename(params);
		
		model.addAllAttributes(response);
	}
	
	@RequestMapping(value = { "/{vendor}/folder/getFolders.*" })
	public void getFolders(	@PathVariable String vendor,
						final HttpServletRequest request, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		Params params = new Params(request.getParameterMap());
		ResponseRows result = service.getFolders(params);
		
		model.addAllAttributes(result);
	}
}
