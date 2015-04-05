package org.springframework.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.cocs.webapp.api.exception.ApiException;
import com.cocs.webapp.exception.UserLoginException;

public class CustomSimpleMappingExceptionResolver extends SimpleMappingExceptionResolver{
	public static final String DEFAULT_EXCEPTION_ATTRIBUTE = "exception";
	public static final String ERROR_CODE = "errorCode";
	public static final String ERROR_MESSAGE = "message";
	public static final String VENDOR = "vendor";
	
	public CustomSimpleMappingExceptionResolver() {
		super();
	}
	
	private String exceptionAttribute = DEFAULT_EXCEPTION_ATTRIBUTE;

	public void setMessageAttribute(String messageAttribute) {
		this.exceptionAttribute = messageAttribute;
	}

	public void setExceptionAttribute(String exceptionAttribute) {
		this.exceptionAttribute = exceptionAttribute;
	}
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		ModelAndView modelAndView = super.doResolveException(request, response, handler, ex);
		
		if (logger.isDebugEnabled()) {
			if(ex instanceof UserLoginException){
				logger.info("Login Exception '" + ex.getMessage()+ "'");
			} else {
				ex.printStackTrace();
			}
		}
		
//		ModelMap modelMap = new ModelMap(DefaultConstants.SUCCESS,false);
		
		if(ex instanceof ApiException){
			modelAndView = new ModelAndView();
			ApiException aex = (ApiException)ex;
			response.setStatus(aex.getStatusCode());
			modelAndView.getModelMap().addAttribute(VENDOR, aex.getVendor());
			modelAndView.getModelMap().addAttribute(ERROR_MESSAGE, aex.getMessage());
		} else if(ex instanceof UserLoginException){
			response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
		} else if (ex instanceof IllegalArgumentException){
			response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			modelAndView.getModelMap().addAttribute("redirect", request.getContextPath()+"/home");
			modelAndView.getModelMap().addAttribute(ERROR_MESSAGE, "잘못된 경로 접근입니다.");
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			modelAndView.getModelMap().addAttribute(ERROR_MESSAGE, ex.getMessage());
		}
		
		ex.printStackTrace();
		
		return modelAndView;
	}
}
