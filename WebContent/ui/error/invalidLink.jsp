<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.cocs.common.Env"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../resources/jsp/variable.jsp" flush="false" />
<jsp:include page="../resources/jsp/header.jsp" flush="false" />
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/signin/signin.css" />

</head>
<body>
	<textarea id="exception" style="display: none">${exception}</textarea>
	<textarea id="msg" style="display: none">${exception.message}</textarea>

    <!-- /container -->
    <div class="container signform">
    	
    	<div class="row ">
    		<div class="col-md-8 col-md-offset-2">
    			<div class="row" style="margin-bottom:30px;">
    				<a class="navbar-brand" href="<%=request.getContextPath()%>">
		    			<img src="<%=request.getContextPath()%>/ui/resources/img/logo/bright_logo03.png" style="width: 180px; height: 80px;" />
    				</a>
    			</div>
    			<div class="row signin_body text-center">
    				<br/><br/>
					<h4>현재 페이지는 존재하지 않습니다.</h4>    			
					<p>주소를 잘못 입력하였거나, 해당 주소가 더 이상 존재하지 않을 수도 있습니다.</p>
					<br/><br/>
					<a href="<%=request.getContextPath()%>/" class="btn btn-success btn-lg active" role="button">홈으로 이동</a>
					<br/><br/>
    			</div>
    		</div>
    	</div>
	</div>
</body>
</html>
