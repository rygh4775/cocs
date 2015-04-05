<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="../resources/jsp/variable.jsp" flush="false" />
	<jsp:include page="../resources/jsp/header.jsp" flush="false" /> 
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/account/index.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/account/common.js"></script>
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
			  <li><a href="#settingsTab" data-toggle="tab"  id="settings">계정 정보</a></li>
			  <li><a href="#mycloudTab" data-toggle="tab" id="mycloud">내 클라우드</a></li>
			  <li><a href="#mysocialTab" data-toggle="tab" id="mysocial">내 소셜</a></li>
			</ul>
		</div>
		<div class="tab-content">
		  <div class="tab-pane active" id="settingsTab">
		  	
	  		<h3>이메일</h3> 
			<p><span class="glyphicon glyphicon-ok">&nbsp;</span>${login_user}</p>
			
			<h3>언어</h3> 
			<p><span class="glyphicon glyphicon-ok">&nbsp;</span>한국어<button type="button" class="btn btn-link">언어 변경(지원 예정)</button></p>
			
			<h3>비밀번호</h3>
			<button type="button" class="btn btn-link" id="changePassowrd_btn">비밀번호 변경</button>
			
			<hr/>
			<button type="button" class="btn btn-link" id="deleteAccount_btn">계정 삭제</button>
		  </div>
		  <div class="tab-pane" id="mycloudTab"></div>
		  <div class="tab-pane" id="mysocialTab">
<!-- 		  	<div class="text-center" style="margin-top:60px;"> -->
<!-- 				<h1>준비중입니다... 조금만 기다려주세요 :)</h1> -->
<!-- 			</div> -->
		  </div>
		</div>
	</div>
      
	<jsp:include page="../resources/jsp/footer.jsp" flush="false" />
</body>
</html>

<div id="context-menu">
  <ul class="dropdown-menu" role="menu"></ul>
</div>

<div class="modal fade" id="deleteAccountModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
	        	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        	<h4 class="modal-title" id="myModalLabel">계정 삭제</h4>
      		</div>
      		<div class="modal-body">
				<h4>COCS 계정을 삭제하시겠습니까? :(</h4>
				<p>
				COCS를 더 이상 이용하지 않으신다니 아쉽습니다!
				COCS에서 해결할 수 있는 문제라면 먼저 <a href="mailto:cocs.cloudofclouds@gmail.com">지원팀</a>에 문의해 보세요. 성심 성의껏 도와 드리겠습니다.
				</p>
				<hr>
				<form role="form">
					<div class="form-group">
						<label for="password">비밀번호를 입력하세요</label>
						<input type="password" class="form-control" name="password" id="password" placeholder="비밀번호">
					</div>
					<div class="form-group">
						<label for="reason">더 이상 사용하지 않으려는 이유는 무엇인가요?</label>
						<textarea class="form-control" name="reason" id="reason" rows="3" placeholder="자세히 설명해주시겠습니까?"></textarea>
					</div>
				</form>
			</div>
      		<div class="modal-footer">
        		<button type="button" class="btn btn-primary">계정 삭제</button>
        		<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
      		</div>
    	</div>
	</div>
</div>