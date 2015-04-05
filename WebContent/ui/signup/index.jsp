<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><!DOCTYPE html><html><head><jsp:include page="../resources/jsp/variable.jsp" flush="false" /><jsp:include page="../resources/jsp/header.jsp" flush="false" /></head><body class="top_zero">	<jsp:include page="../resources/jsp/navigation_black.jsp" flush="false">		<jsp:param name="section" value="signup"/>	</jsp:include>		<div class="container">		<div class="row">			<div class="col-md-8 col-md-offset-2 contents">								<div class="row">					<ul class="nav nav-tabs nav-justified steps">						<li class="active"><a>Step 1</a></li>						<li class="disabled"><a>Step 2</a></li>						<li class="disabled"><a>Step 3</a></li>					</ul>				</div>								<div class="page-header">				  <h1>step1. <small>기본정보 입력</small></h1>				</div>								<form id="signupForm">					<div class="form-group">						<label for="email">이메일</label> 						<input type="email" class="form-control" name="email" id="email" placeholder="이메일 입력"/>					</div>					<div class="form-group">						<label for="password">비밀번호</label> 						<input type="password" class="form-control" id="password" name="password" placeholder="비밀번호"/>					</div>					<div class="form-group">						<label for="confirmPassword">비밀번호 재입력</label> 						<input type="password" class="form-control" name="confirmPassword" placeholder="비밀번호 재입력"/>					</div>					<p>						등록를 클릭하는 것으로, 						<a class="pointer" target="_blank" href="<%=request.getContextPath()%>/legal">개인정보 보호정책</a>						에 동의하는 것으로 간주합니다.					</p>					<br/>										<div class="row">						<div class="col-md-6">							<a href="<%=request.getContextPath()%>/" class="btn btn-lg btn-default btn-block">취소</a> </br>						</div>						<div class="col-md-6">							<input id="register" class="btn btn-lg btn-primary btn-block" type="button" value="등록"> </br>						</div>					</div>				</form>							</div>		</div>	</div>	<jsp:include page="../resources/jsp/footer.jsp" flush="false" /></body></html><script type="text/javascript" src="<%= request.getContextPath() %>/ui/signup/signup.js"></script>