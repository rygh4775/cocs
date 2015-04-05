package com.cocs.webapp.api.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cocs.common.DefaultConstants;
import com.cocs.common.EveryoneAccessable;
import com.cocs.webapp.exception.UserLoginException;

public class UserTokenIntercepter extends HandlerInterceptorAdapter implements DefaultConstants{
	
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
			
		} else {
			throw new UserLoginException();
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}

}
