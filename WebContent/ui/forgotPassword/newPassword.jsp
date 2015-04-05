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
<body class="top_zero">
	<jsp:include page="../resources/jsp/navigation_black.jsp" flush="false">
		<jsp:param name="section" value="forgotpassword"/>
	</jsp:include>
	
	<div class="container">
		<div class="row">
			<div class="col-md-8 col-md-offset-2 contents">
				<div class="row">
					<ul class="nav nav-tabs nav-justified steps">
						<li class="disabled"><a>Step 1</a></li>
						<li class="disabled"><a>Step 2</a></li>
						<li class="active"><a>Step 3</a></li>
						<li class="disabled"><a>Step 4</a></li>
					</ul>
				</div>
					
				<div class="page-header">
				  <h1>step3. <small>새 비밀번호 입력</small></h1>
				</div>
					
				<p><strong>${email}</strong> 계정의 새 비밀번호를 입력하세요.</p>
				<br/>
				
				<form id="forgotPasswordForm" action="<%=request.getContextPath()%>/forgotPasswordComplete.do" method="post">
					<input type="hidden" name="email" id="email" value="${email}">
					<br>
					<div class="form-group">
					    <label for="newPassword">새 비밀번호</label>
					    <input type="password" class="form-control" id="newPassword" name="newPassword" class="form-control" placeholder="새 비밀번호 입력">
				  	</div>
			   		<div class="form-group">
					    <label for="confirmPassword">비밀번호 확인</label>
					    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="비밀번호 재입력">
				 	</div>
					<br>
					 <div class="row">
				    	<div class="col-md-6">
					    	<input class="btn btn-lg btn-primary btn-block" type="button" value="변경하기">
					    	</br>
				    	</div>
				    	<div class="col-md-6">
							<input class="btn btn-lg btn-default btn-block" type="reset" value="새로입력">
							</br>
						</div>
			    	</div>
				</form>
				
		    	<div id="forgotPasswordCompleteForm" role-content="mail" style="display:none;">
					<!-- <h1>step 4. 비밀번호 변경 완료</h1> -->
					<div class="row">
						<div class="col-md-5">
		   					<div class="thumbnail signup" style="border:none;">
								<img src="<%=request.getContextPath()%>/ui/resources/img/mail/passwordComplete.png">
							</div>
						</div>
						<div class="col-md-7 sendTxt">
							<br/>
							<h4>${email}님<br/><br/> 비밀번호가 정상적으로 변경되었습니다.</h4><br/>
							<p class="text-right"><a href="<%= request.getContextPath() %>/signin" class="btn btn-success btn-lg">로그인 하기</a></p>
						    <br/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../resources/jsp/footer.jsp" flush="false" />
</body>
</html>
<script type="text/javascript" src="<%= request.getContextPath() %>/ui/forgotPassword/newPassword.js"></script>
