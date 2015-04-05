<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="../resources/jsp/variable.jsp" flush="false" />
	<jsp:include page="../resources/jsp/header.jsp" flush="false" /> 
</head>
<body>
	<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
	   		</button>
	   		
	   		<a href="<%=request.getContextPath()%>/home"><img alt="Cloud of Clouds" src="<%=request.getContextPath()%>/ui/resources/logo/cocs_logo_beta.png" class="pull-left" style="max-height:30px; margin:10px 20px;"/></a>
	   		
	 	</div>
	 	
	 	<!-- Collect the nav links, forms, and other content for toggling -->
  		<div class="collapse navbar-collapse">
		    
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">${login_user} <b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="<%=request.getContextPath()%>/home">홈으로</a></li>
						<li class="divider"></li>
<!-- 						<li class="dropdown-header">Nav header</li> -->
						<li><a href="<%=request.getContextPath()%>/signout.do">로그아웃</a></li>
					</ul>
				</li>
			</ul>
		</div>
	</nav>
 
	<div class="container contents">

		<div class="row">
			<ul class="nav nav-tabs">
			  <li><a href="#userTab" data-toggle="tab"  id="settings">사용자</a></li>
			  <li><a href="#etcTab" data-toggle="tab" id="mycloud">기타</a></li>
			</ul>
		</div>
		<div class="tab-content">
			<div class="tab-pane active" id="userTab">
				<br/>
				<label>OAuth Provider</label>&nbsp;&nbsp;
				<select class="selectpicker">
					<option value="" selected>all</option>
					<option value="default">default</option>
					<option value="facebook">facebook</option>
					<option value="naver">naver</option>
				</select>
				<p class="text-right">Total Count : <span id="totalCount" class="badge">42</span></p>
				<div id="userList"></div>
				<div id="loadingData" class="loading text-center" style="display: block;">데이터를 로딩 중입니다.</div>
				<div id="noMoreResults" class="loading text-center" style="display: none;">모든 데이터가 로드되었습니다.</div>
		  	</div>
		  	<div class="tab-pane" id="etcTab"></div>
		</div>
	</div>
</body>
</html>
<script type="text/javascript" src="<%= request.getContextPath() %>/ui/resources/js/jquery.scrollpagination.custom.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/ui/admin/admin.js"></script>