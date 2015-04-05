<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%><%@ page import="com.cocs.common.Env"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><!DOCTYPE html><html><head><jsp:include page="../resources/jsp/variable.jsp" flush="false" /><jsp:include page="../resources/jsp/header.jsp" flush="false" /></head><body class="top_zero">	<jsp:include page="../resources/jsp/navigation_black.jsp" flush="false">		<jsp:param name="section" value="forgotpassword"/>	</jsp:include>		<div class="container">		<div class="row">			<div class="col-md-8 col-md-offset-2 contents">					<!-- <h1>step 1. 기본정보 입력</h1> -->				<div class="row">					<ul class="nav nav-tabs nav-justified steps">						<li class="active"><a>Step 1</a></li>						<li class="disabled"><a>Step 2</a></li>						<li class="disabled"><a>Step 3</a></li>						<li class="disabled"><a>Step 4</a></li>					</ul>				</div>								<div class="page-header">				  <h1>step1. <small>이메일 입력</small></h1>				</div>																<form id="forgotForm">					<p>COCS를 사용할 때에 이용하시는 이메일 주소를 입력해 주시기 바랍니다.</p>					<p>비밀번호를 초기화 할 수 있도록 이메일을 전송해드리겠습니다.</p>					<br/>					<div class="form-group">					    <label for="email">이메일</label>					    <input type="email" class="form-control" name="email" id="email" placeholder="이메일 입력">				  	</div>				  	<div class="form-group" style="display: none;">					    <label for="email">이메일</label>					    <input type="email" class="form-control" name="test" id="test" placeholder="이메일 입력">				  	</div>							    <br/>			    <div class="row">			    	<div class="col-md-6">				    	<input class="btn btn-lg btn-primary btn-block" type="button" value="전송">				    	</br>			    	</div>			    	<div class="col-md-6">						<input class="btn btn-lg btn-default btn-block" type="reset" value="새로입력">						</br>					</div>		    	</div>	    		  </form>		    			    	<div id="forgotSendForm" role-content="mail" style="display:none;">					<!-- <h1>step 2. 이메일 인증</h1> -->					<div class="row">						<div class="col-md-5">							<div class="thumbnail signup" style="border:none;">								<img src="<%=request.getContextPath()%>/ui/resources/img/mail/sentemail.png">		   					</div>							</div>						<div class="col-md-7 sendTxt">							<h3>이메일이 전송 되었습니다.</h3>						    <br/>  							<p>COCS는 회원님의 정보를 안전하게 보호하고자 이메일을 통하여 비밀번호를 변경해 드리고 있습니다.</p>						    <br/>						</div>					</div>				</div>	  		</div>	  	</div>	</div>	<jsp:include page="../resources/jsp/footer.jsp" flush="false" /></body></html><script type="text/javascript" src="<%= request.getContextPath() %>/ui/forgotPassword/forgotPassword.js"></script>