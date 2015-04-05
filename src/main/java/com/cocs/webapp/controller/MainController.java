package com.cocs.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cocs.common.EveryoneAccessable;

@Controller
public class MainController implements EveryoneAccessable{
	
	@RequestMapping(value = { "/main" })
	public ModelAndView veiw(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("main/index");
	}
	
	@RequestMapping(value = { "/main/**" })
	public ModelAndView veiw2(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("main/index");
	}
	
	@RequestMapping(value = { "/release" })
	public ModelAndView release(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("release/index");
	}
	
	@RequestMapping(value = { "/release/**" })
	public ModelAndView release2(final HttpServletRequest request, final ModelMap model) {
		return new ModelAndView("release/index");
	}
}
