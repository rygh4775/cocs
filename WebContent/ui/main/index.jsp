<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.cocs.common.Env"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../resources/jsp/variable.jsp" flush="false" />
<jsp:include page="../resources/jsp/header.jsp" flush="false" />
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/main/main.css"/>

</head>
<body>
	<div class="navbar navbar-inner navbar-fixed-top" role="navigation">
      <div class="container">
          <a class="navbar-brand" href="<%=request.getContextPath()%>/">Cloud Of Coluds</a>
          <a class="navbar-brand pull-right pointer" id="signin_button">로그인</a>
      </div>
    </div>
	<!-- 중앙 섹션 -->
    <!-- Full Page Image Header Area -->
    <div id="top" class="header">
    
        <div class="vert-text">
        	<div class="container">
           		<div class="row">
           			 <div class="col-md-10 col-md-offset-1 text-center">
           			 	<div class="row">
           			 		<div class="col-md-4" style="text-align: center;">
           			 			<img src="<%=request.getContextPath()%>/ui/resources/img/index_img/cocs_logo.png" style="max-width: 100%"/>
           			 		</div>
           			 		<div class="col-md-8 text-left main-text">
           			 			<br/>
							    <h2 class="media-heading" style="color: #FFF">
								    모든 클라우드를 </br>
			       				 	하나의 클라우드 처럼 쓰는</br>
			       				 	놀라운 경혐을 시작하십시오.</br>
							    </h2>
							    <hr>
							    <a class="btn btn-lg btn-success pull-right" href="<%=request.getContextPath()%>/signup" role="button">가입 하기</a>
							    <br/><br/><br/><br/><br/>
						  	</div>
           			 	</div>
              		 </div>
           		</div>
           	</div>
		</div>
    </div>
    <!-- /Full Page Image Header Area -->
	 <!-- Support -->
    <div class="support">
        <div class="container">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <h2><span style="color:#EB6864">C</span>loud <span style="color:#EB6864">O</span>f <span style="color:#EB6864">C</span>loud<span style="color:#EB6864">S</span>는 클라우드 통합과 소셜 네트워크를 연결해주는 서비스 입니다.</h2>
                </div>
            </div>
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <h1 style="color:#999;">We Support</h1>
            	<hr/>
                </div>
            </div>
            <div class="row">
                <div class="col-md-8 col-md-offset-2 text-center">
                    <div class="service-item">
                    	<div class="row">
           			 		<div class="col-md-4">
           			 			<img src="<%=request.getContextPath()%>/ui/resources/img/vendors/dropbox.png"
						style="width: 100px; height: 100px" />
           			 		</div>
           			 		<div class="col-md-8 text-left">
		                        <h4>DropBox</h4>
		                        <p>드롭박스(Dropbox 드랍박스)는 Dropbox, Inc.가 제공하는, 파일 동기화와 클라우드 컴퓨팅을 이용한 웹 기반의 파일 공유 서비스입니다.</p>
						  	</div>
           			 	</div>
                    </div>
                </div>
                <div class="col-md-8 col-md-offset-2 text-center">
                    <div class="service-item">
                    	<div class="row">
           			 		<div class="col-md-4">
           			 			<img src="<%=request.getContextPath()%>/ui/resources/img/vendors/google.png"
						style="width: 100px; height: 100px" />
           			 		</div>
           			 		<div class="col-md-8 text-left">
		                        <h4>Google Drive</h4>
		                        <p>구글 드라이브(Google Drive)는 구글에서 제공하는 클라우드 기반 협업도구이자 파일저장/공유 서비스입니다.</p>
						  	</div>
           			 	</div>
                    </div>
                </div>
                <div class="col-md-8 col-md-offset-2 text-center">
                    <div class="service-item">
                    	<div class="row">
           			 		<div class="col-md-4">
           			 			<img src="<%=request.getContextPath()%>/ui/resources/img/vendors/facebook.png"
						style="width: 100px; height: 100px" />
           			 		</div>
           			 		<div class="col-md-8 text-left">
		                        <h4>Facebook</h4> 
		                        <p>페이스북(Facebook)은 활동 사용자들 가운데 가장 많이 이용되는 소셜 네트워크 서비스입니다.</p>
						  	</div>
           			 	</div>
                    </div>
                </div>
                <div class="col-md-8 col-md-offset-2 text-center">
                    <div class="service-item">
                    	<div class="row">
           			 		<div class="col-md-4">
           			 			<img src="<%=request.getContextPath()%>/ui/resources/img/vendors/twitter.png"
						style="width: 100px; height: 100px" />
           			 		</div>
           			 		<div class="col-md-8 text-left">
		                        <h4>Twitter</h4>
		                        <p>트위터(Twitter)는 무료 소셜 네트워킹 겸 마이크로블로그 서비스입니다.</p>
						  	</div>
           			 	</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- /Support -->
    
    
        <!-- Services -->
    <div id="services" class="services">
        <div class="container">
            <div class="row">
                <div class="col-md-4 col-md-offset-4 text-center">
                    <h2>Our Services</h2>
                    <hr>
                </div>
            </div>
            <div class="row">
                <div class="col-md-4 text-center">
                    <div class="service-item">
                    <!--class="img-circle" 이미지 써클 있었음  -->
                        <img  src="<%=request.getContextPath()%>/ui/resources/img/index_img/icon_001.png"   
						style="width: 150px; height: 150px" />
                        <h4><strong>분리된 공간을 하나 처럼</strong></h4>
                        <p>모든 클라우드를 하나의 클루우드처럼 쓸 수 있습니다.  더 이상 이곳저곳 로그인할 필요가 없어집니다. 하나의 공간에서 편리하게 관리하세요.</p>
                    </div>
                </div>
                <div class="col-md-4 text-center">
                    <div class="service-item">
                    	<img  src="<%=request.getContextPath()%>/ui/resources/img/index_img/icon_002.png"
						style="width: 150px; height: 150px" />
                        <h4><strong>언제 어디서나 자유롭게</strong></h4>
                        <p>COCS를 통해 파일 등을 올려 놓고 다른 컴퓨터와 휴대폰, 태블릿에서 해당 파일을 관리하세요.  인터넷만 연결된다면 우주 어디에서도 사용할 수 있습니다.</p>
                    </div>
                </div>
                <div class="col-md-4 text-center">
                    <div class="service-item">
                        <img src="<%=request.getContextPath()%>/ui/resources/img/index_img/icon_003.png"
						style="width: 150px; height: 150px" />
                        <h4><strong>안심할 수 있는 서비스</strong></h4>
                        <p>여러분의 개인 정보를 저장하지 않습니다.  COCS는 보안을 위한 OAuth 표준 인증 방식을 따르기 때문에 여러분의 개인 정보를 액세스 할 수 없습니다.</p>
                    </div>
                </div>
                <br>
                <div class="col-md-4 text-center">
                    <div class="service-item">
                        <img src="<%=request.getContextPath()%>/ui/resources/img/index_img/icon_004.png"
						style="width: 150px; height: 150px" />
                        <h4><strong>스마트한 검색</strong></h4>
                        <p>한번의 검색으로 원하는 결과를 얻으세요.모든 클라우드를 대상으로 동시 검색을 지원하기 때문에 자료 검색 시간을 단축 할 수 있습니다.</p>
                    </div>
                </div>
                <div class="col-md-4 text-center">
                    <div class="service-item">
                        <img src="<%=request.getContextPath()%>/ui/resources/img/index_img/icon_005.png"
						style="width: 150px; height: 150px" />
                        <h4><strong>부족하지 않은 저장 공간</strong></h4>
                        <p>더 이상 한정된 공간으로 고민하실 필요가 없습니다. 클라우드를 모아 프리미엄 수준의 대용량 저장공간을 활용 할 수 있습니다.</p>
                    </div>
                </div>
                <div class="col-md-4 text-center">
                    <div class="service-item">
                        <img  src="<%=request.getContextPath()%>/ui/resources/img/index_img/icon_006.png"
						style="width: 150px; height: 150px" />
                        <h4><strong>사진을 SNS로 공유</strong></h4>
                        <p>원하는 사진을 즉시 SNS로 공유 할 수 있습니다.  페이스북, 트위터 등에 쉽고 빠르게 업로드 하세요.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- /Services -->
    
    <!-- Callout -->
    <div class="callout">
        <div class="vert-text">
        </div>
    </div>
    <!-- /Callout -->
    
	<jsp:include page="../resources/jsp/footer.jsp" flush="false" />	

<div class="modal fade" id="signinModal" tabindex="-1" role="dialog" aria-labelledby="signinModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="signinModalLabel">로그인</h4>
      </div>
      <div class="modal-body">
       <form class="form-signin" id="signinForm" method="POST" action="<%=request.getContextPath()%>/doSignin.do">
			<div class="form-group">
				<input type="text" name="email" class="form-control" placeholder="이메일 주소" autofocus>
			</div>
			<div class="form-group">
			    <input type="password" name="password" class="form-control" placeholder="비밀번호">
			</div>
			<div class="clearfix">
				<div class="pull-left">
					<p><a href="<%=request.getContextPath()%>/signup">회원가입</a></p>
					<p><a href="<%=request.getContextPath()%>/forgotPassword">비밀번호 찾기</a></p>
				</div>
				<div class="pull-right">
					<button type="submit" class="btn btn-lg btn-primary">로그인</button>
				</div>
			</div>
			<hr/>
			<div class="text-center">
				<a href="<%=request.getContextPath()%>/authorize.do?oauth_provider=facebook"><img src="<%=request.getContextPath()%>/ui/resources/img/login/facebook.png"/></a>
			</div>
			<br/>
			<div class="text-center">
				<a href="<%=request.getContextPath()%>/authorize.do?oauth_provider=naver"><img src="<%=request.getContextPath()%>/ui/resources/img/login/naver.png"/></a>
			</div>
		</form>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
	</div>
</body>
</html>
<script type="text/javascript" src="<%= request.getContextPath() %>/ui/main/main.js"></script>