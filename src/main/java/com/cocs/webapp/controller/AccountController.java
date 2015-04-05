package com.cocs.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.cocs.common.DefaultConstants;
import com.cocs.mail.MailSender;
import com.cocs.server.dao.UsersDAO;
import com.cocs.webapp.intercepter.UserLoginIntercepter;

@Controller
public class AccountController implements DefaultConstants{
	
	@Autowired
	UsersDAO users;
	
	@Autowired
	MailSender mailSender;
	
	@RequestMapping(value = { "/account" })
	public ModelAndView account(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("account/index");
	}
	
	@RequestMapping(value = { "/account/**" })
	public ModelAndView account2(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("account/index");
	}
	
	@RequestMapping(value = { "/account/delete.*" })
	public void changePassword(HttpSession session,
							@RequestParam(value="password",required=false,defaultValue="") String password,
							@RequestParam(value="reason",required=false,defaultValue="") String reason,
							final HttpServletRequest request, final ModelMap model) throws Throwable {
		String userId = (String) session.getAttribute(UserLoginIntercepter.LOGIN_USER);
		String oauthProvider = (String) session.getAttribute(UserLoginIntercepter.OAUTH_PROVIDER);
		
		if(oauthProvider.equals("default") && !users.equalsPassword(userId, password)) {
			throw new Exception("비밀번호가 일치하지 않습니다.");
		}
		
		users.deleteUser(userId, oauthProvider);
		
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<br/>Total Usercount  : " + users.getTotalCount(null));
		stringBuffer.append("<br/>ID/Email : " + userId);
		stringBuffer.append("<br/>OAuth Provider : " + oauthProvider);
		stringBuffer.append("<br/>Reason : " + reason);
		
		mailSender.send("cocs.cloudofclouds@gmail.com", "계정삭제 알림 메일", stringBuffer.toString());
		model.put(SUCCESS, true);
	}
}
