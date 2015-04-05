package com.cocs.webapp.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cocs.common.DefaultConstants;
import com.cocs.server.dao.UsersDAO;
import com.cocs.webapp.intercepter.UserLoginIntercepter;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;

@Controller
public class UsersController implements DefaultConstants {
	
	@Autowired
	UsersDAO users;
	
	@RequestMapping(value = { "/users/changePassword.*" })
	public void changePassword(HttpSession session,
							@RequestParam(value="password",required=false,defaultValue="") String password,
							@RequestParam(value="newPassword",required=false,defaultValue="") String newPassword,
							@RequestParam(value="confirmNewPassword",required=false,defaultValue="") String confirmNewPassword,
							final HttpServletRequest request, final ModelMap model) throws Throwable {
		String userId = (String) session.getAttribute(UserLoginIntercepter.LOGIN_USER);
		
		if(!newPassword.equals(confirmNewPassword)) {
			throw new Exception("새 비밀번호가 일치하지 않습니다.");
		}
		
		if(!users.equalsPassword(userId, password)) {
			throw new Exception("이전 비밀번호가 일치하지 않습니다.");
		}
		
		users.changePassword(userId, newPassword);
		model.put(SUCCESS, true);
	}
	
}
