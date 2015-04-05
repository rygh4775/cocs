package com.cocs.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	
	@RequestMapping(value = { "/home" })
	public ModelAndView home(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("home/index");
	}
	
	@RequestMapping(value = { "/home/**" })
	public ModelAndView home2(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("home/index");
	}
}
