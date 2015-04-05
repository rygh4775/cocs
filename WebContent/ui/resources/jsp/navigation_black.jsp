<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<nav class="navbar navbar-inverse navbar-static-top" role="navigation">
	<div class="navbar-header">
		<a href="<%=request.getContextPath()%>/">
			<img class="pull-left" style="max-height:50px; margin:0 20px;" src="<%=request.getContextPath()%>/ui/resources/img/logo/COCS05.png" alt="Cloud of Clouds">
		</a>
		<p class="navbar-text">
			<%="signup".equals(request.getParameter("section")) ? "회원 가입 진행중..." : "비밀번호 찾기 진행중..." %>
		</p>
	</div>
</nav>