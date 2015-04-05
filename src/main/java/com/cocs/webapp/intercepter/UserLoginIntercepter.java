package com.cocs.webapp.intercepter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cocs.common.DefaultConstants;
import com.cocs.common.EveryoneAccessable;
import com.cocs.service.ClientManager;
import com.cocs.service.Vendors;
import com.cocs.webapp.exception.UserLoginException;

public class UserLoginIntercepter extends HandlerInterceptorAdapter implements DefaultConstants{
	
	@Autowired
	ClientManager clientManager;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession(false);
		
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			handler = handlerMethod.getBean();
		}
		
		if (handler instanceof EveryoneAccessable) {
			return true;
		}

		if (session != null) {
			String loginUser = (String) session.getAttribute(LOGIN_USER);
			String oauthProvider = (String) session.getAttribute(OAUTH_PROVIDER);
			if(StringUtils.isBlank(loginUser)) {
				throw new UserLoginException();
			}
			request.setAttribute(LOGIN_USER, loginUser);
			request.setAttribute(OAUTH_PROVIDER, oauthProvider);
			
			ArrayList<String> vendorList = new ArrayList<String>();
			for (Vendors vendor : Vendors.values()) {
				vendorList.add(vendor.toString());
			}
			
			ArrayList<String> activeCloudList = new ArrayList<String>();
			ArrayList<String> inactiveCloudList = new ArrayList<String>();
			
			if(clientManager.getGoogleClient() != null) {
				activeCloudList.add("google");
			} else {
				inactiveCloudList.add("google");
			}
			if(clientManager.getDropboxClient() != null){
				activeCloudList.add("dropbox");
			} else {
				inactiveCloudList.add("dropbox");
			}
			
			ArrayList<String> activeSocialList = new ArrayList<String>();
			ArrayList<String> inactiveSocialList = new ArrayList<String>();
			
			if(clientManager.getFacebookClient() != null) {
				activeSocialList.add("facebook");
			} else {
				inactiveSocialList.add("facebook");
			}
			if(clientManager.getTwitterClient() != null){
				activeSocialList.add("twitter");
			} else {
				inactiveSocialList.add("twitter");
			}
			
			Map<String, Object> vendors = new HashMap<String, Object>();
			vendors.put("list", vendorList);
			vendors.put("activeCloudList", activeCloudList);
			vendors.put("inactiveCloudList", inactiveCloudList);
			vendors.put("activeSocialList", activeSocialList);
			vendors.put("inactiveSocialList", inactiveSocialList);
			
			request.setAttribute("vendors", vendors);
		} else {
			throw new UserLoginException();
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		super.afterCompletion(request, response, handler, ex);
	}

}
