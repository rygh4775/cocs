<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="-1" />
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
		
	<meta name="title" content="Cloud of Clouds" />
	<meta name="author" content="" />
	<meta name="description" content="" />
	<meta name="copyright" content="2013 CLOUD OF CLOUDS. All rights reserved." />
  	<!--[if lt IE 9]>
    
     <script src="<%= request.getContextPath() %>/ui/resources/js/css3-mediaqueries.js"></script>
     <script src="<%= request.getContextPath() %>/ui/resources/js/respond.js"></script>
    <script src="<%= request.getContextPath() %>/ui/resources/js/respond.matchmedia.addListener.min.js"></script>
    <script src="<%= request.getContextPath() %>/ui/resources/js/html5shiv.js"></script>
    <![endif]-->
	
	<!--fivicon -->
	<link rel="shortcut icon" href="<%=request.getContextPath()%>/ui/resources/logo/favicon.ico" type="image/x-icon">
	<link rel="icon" href="<%=request.getContextPath()%>/ui/resources/logo/favicon.ico" type="image/x-icon">
	
	
	<title>COCS - Cloud of Clouds</title>
	
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/normalize.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/bootstrap.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/jquery.spin.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/jquery.jqplot.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/bootstrap-multiselect.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/bootstrap-select.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/alertify.core.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/alertify.bootstrap.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/magnific-popup.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/jquery.treegrid.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/ladda-themeless.min.css" />

	<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/jquery.fileupload.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/jquery.fileupload-ui.css" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/jqtree.css" />
	
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/resources/css/override.css" />
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/prototypes.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery-1.10.1.min.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/bootstrap.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/underscore.js"></script>
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.validate.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.validate.message_ko.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/additional-methods.js"></script>
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.spin.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/history.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/history.adapter.jquery.js"></script>
<%-- 	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.pnotify.custom.js"></script> --%>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.tmpl.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.floatThead.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.magnific-popup.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.tree.custom.js"></script>
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/google.jsapi.min.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/google.corechart.min.js"></script>
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/bootbox.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/bootstrap-multiselect.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/bootstrap-select.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/bootstrap-contextmenu.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/spin.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/ladda.js"></script>
	
	
	<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.ui.widget.js"></script>
	<!-- The Templates plugin is included to render the upload/download listings -->
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/tmpl.min.js"></script>
	<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.iframe-transport.js"></script>
	<!-- The basic File Upload plugin -->
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.fileupload.js"></script>
	<!-- The File Upload processing plugin -->
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.fileupload-process.js"></script>
	<!-- The File Upload validation plugin -->
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.fileupload-validate.js"></script>
	<!-- The File Upload user interface plugin -->
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.fileupload-ui.js"></script>
	<!-- The Scroll plugin -->
<!-- 	<script type="text/javascript" src="http://blueimp.github.io/JavaScript-Canvas-to-Blob/js/canvas-to-blob.min.js"></script> -->
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/load-image.min.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.fileupload-image.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.bootpag.custom.min.js"></script>
	
	<%-- <script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.fileupload-jquery-ui.js"></script> --%>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/alertify.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/oop.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/actions.js"></script>
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/utils.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/oops.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/UI.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/common.js"></script>
