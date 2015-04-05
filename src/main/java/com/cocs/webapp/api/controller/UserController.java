package com.cocs.webapp.api.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cocs.handler.ResponseResult;
import com.cocs.service.ClientManager;
import com.cocs.service.IService;
import com.cocs.service.Vendors;
import com.cocs.webapp.api.exception.ApiException;

@Controller
public class UserController {
	
	@Autowired
	ClientManager clientManager;
	
	@RequestMapping(value = { "/{vendor}/user/getInfo.*" })
	public void getInfo(	@PathVariable String vendor,
							final HttpSession session, final ModelMap model) throws ApiException {
		IService service = Vendors.valueOf(vendor).getService().setClient(clientManager);
		
		ResponseResult response = service.getUserInfo();
		
		model.addAllAttributes(response);
	}

}
