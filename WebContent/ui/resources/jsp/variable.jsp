<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<% pageContext.setAttribute("timestamp",System.currentTimeMillis(),PageContext.REQUEST_SCOPE); %>
<script type="text/javascript">

	window.baseDateFormat ='yyyy-MM-dd HH:mm';
	
	window.serverName  = '${request.serverName}';
	window.contextPath = '<%= request.getContextPath()%>';
	window.title = 'COCS - Cloud of Clouds';
	window.login_user = '${login_user}';
	window.oauth_provider = '${oauth_provider}';
	
	window.api = contextPath + '/api/';
	
	window.Vendors = [<c:forEach items="${vendors.list}" var="vendor" varStatus="status">"${vendor}"<c:if test="${not status.last}">, </c:if></c:forEach>];
	
	window.activeCloudList = [<c:forEach items="${vendors.activeCloudList}" var="cloud" varStatus="status">"${cloud}"<c:if test="${not status.last}">, </c:if></c:forEach>];
	window.inactiveCloudList = [<c:forEach items="${vendors.inactiveCloudList}" var="cloud" varStatus="status">"${cloud}"<c:if test="${not status.last}">, </c:if></c:forEach>];
	
	window.clouds = {
			'google' : {
				"serviceName" : "Google Drive",
				"settingsUrl" : "https://drive.google.com/settings",
				"description" : "구글 드라이브(Google Drive)는 구글에서 제공하는 클라우드 기반 협업도구이자 파일저장/공유 서비스입니다.",
				"freeQuota" : 16106127360
			},
			'dropbox' : {
				"serviceName" : "Dropbox",
				"settingsUrl" : "https://www.dropbox.com/account#settings",
				"description" : "드롭박스(Dropbox 드랍박스)는 Dropbox, Inc.가 제공하는, 파일 동기화와 클라우드 컴퓨팅을 이용한 웹 기반의 파일 공유 서비스입니다.",
				"freeQuota" : 3221225472
			}
	}
	
	window.activeSocialList = [<c:forEach items="${vendors.activeSocialList}" var="social" varStatus="status">"${social}"<c:if test="${not status.last}">, </c:if></c:forEach>];
	window.inactiveSocialList = [<c:forEach items="${vendors.inactiveSocialList}" var="social" varStatus="status">"${social}"<c:if test="${not status.last}">, </c:if></c:forEach>];
	
	window.socials = {
			'facebook' : {
				"serviceName" : "Facebook",
				"settingsUrl" : "https://www.facebook.com/settings",
				"description" : "페이스북(Facebook)은 활동 사용자들 가운데 가장 많이 이용되는 소셜 네트워크 서비스입니다.",
				"postInfo" : "가장 기억에 남는 게시물, 사진, 이벤트를 타임라인에서 공유하세요."
			},
			'twitter' : {
				"serviceName" : "Twitter",
				"settingsUrl" : "https://twitter.com/settings",
				"description" : "트위터(Twitter)는 무료 소셜 네트워킹 겸 마이크로블로그 서비스입니다.",
				"postInfo" : "각 트윗은 140자 이하의 공간에 맞춰집니다. 기발한 착상, 뉴스 헤드라인 또는 시기적절한 논평에 꼭 맞는 크기이지요."
			}
	}
	
</script>