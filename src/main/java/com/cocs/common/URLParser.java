package com.cocs.common;

import javax.servlet.http.HttpServletRequest;

public class URLParser {
	
	public static String getURLWithContextPath(HttpServletRequest request) {
	    String scheme = request.getScheme();             // http
	    String serverName = request.getServerName();     // hostname.com
	    int serverPort = request.getServerPort();        // 80
	    String contextPath = request.getContextPath();   // /mywebapp
//	    String servletPath = req.getServletPath();   // /servlet/MyServlet
//	    String pathInfo = req.getPathInfo();         // /a/b;c=123
//	    String queryString = req.getQueryString();          // d=789

	    // Reconstruct original requesting URL
	    StringBuffer url =  new StringBuffer();
	    url.append(scheme).append("://").append(serverName);

	    if ((serverPort != 80) && (serverPort != 443)) {
	        url.append(":").append(serverPort);
	    }

	    url.append(contextPath);
//	    url.append(servletPath);

//	    if (pathInfo != null) {
//	        url.append(pathInfo);
//	    }
//	    if (queryString != null) {
//	        url.append("?").append(queryString);
//	    }
	    return url.toString();
	}
	
	public static String getResourceURL(HttpServletRequest request) {
		String url = getURLWithContextPath(request);
		url += "/ui/resources";
		return url;
	}
}
