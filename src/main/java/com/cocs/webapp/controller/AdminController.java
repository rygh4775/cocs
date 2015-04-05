package com.cocs.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.cocs.server.dao.UsersDAO;
import com.cocs.server.dao.UsersDAO.ResponseUsers;
import com.cocs.webapp.intercepter.UserLoginIntercepter;

@Controller
public class AdminController {
	
	@Autowired
	UsersDAO users;
	
	@RequestMapping(value = { "/admin" })
	public ModelAndView account(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("admin/index");
	}
	
	@RequestMapping(value = { "/admin/**" })
	public ModelAndView account2(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("admin/index");
	}
	
	@RequestMapping(value = { "/admin/getUsers.*" })
	public void getUsers(HttpSession session,
							@RequestParam(value="oauthProvider",required=false,defaultValue="") String oauthProvider,
							@RequestParam(value="firstKey",required=false,defaultValue="") String fisrtKey,
							@RequestParam(value="rowCount",required=false, defaultValue="10") Integer rowCount,
							final HttpServletRequest request, final ModelMap model) throws Throwable {
		String userId = (String) session.getAttribute(UserLoginIntercepter.LOGIN_USER);
		
		ResponseUsers result = users.getAllUsers(oauthProvider, fisrtKey, rowCount);
		model.addAllAttributes(result);
	}
}
