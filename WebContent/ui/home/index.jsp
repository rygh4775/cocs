<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false"%>

<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="../resources/jsp/variable.jsp" flush="false" />
	<jsp:include page="../resources/jsp/header.jsp" flush="false" /> 
	
	<jsp:include page="./upload.jsp" flush="false" />
	<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ui/home/upload.css" />
	
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/home/actions.home.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/home/actions.list.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/home/actions.search.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/ui/home/common.js"></script>
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
			<ul class="nav navbar-nav">
				<li class="navbar-form">
					<select id="searchSelector" multiple="multiple"></select>
				</li>
			</ul>
		    
		    <div class="navbar-form navbar-left" role="search">
				<div class="input-group" style="max-width:230px;" >
					<input id="keyword" type="text" class="form-control" placeholder="파일 검색" autocomplete='off'> 
					<span class="input-group-btn">
						<button id="search_btn" class="btn btn-default" type="button">
							<span class="glyphicon glyphicon-search" />
						</button>
					</span>
				</div>
			</div>
		    
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">${login_user} <b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="<%=request.getContextPath()%>/account">설정</a></li>
						<li class="divider"></li>
<!-- 						<li class="dropdown-header">Nav header</li> -->
						<li><a href="<%=request.getContextPath()%>/signout.do">로그아웃</a></li>
					</ul>
				</li>
			</ul>
		</div>
	</nav>
	<!-- 맨위에 바 -->
	
	<div class="col-md-2 hidden-sm hidden-xs" role="side-bar">
		<div id="left_treeGrid" class="bs-sidebar affix" role="complementary">
		</div>
	</div>

	<div id="home" class="col-md-10" role="view" style="display:none">
		
		<div class="row">
			<ul class="nav nav-tabs">
			  <li class="active"><a href="#cloudTab" data-toggle="tab" id="cloudTabButton">클라우드</a></li>
			  <li ><a href="#socialTab" data-toggle="tab" id="socialTabButton">소셜 네트워크</a></li>
			</ul>
		</div>
		
		<div class="tab-content">
		  <div class="tab-pane active" id="cloudTab">
		  	<div class="row text-center">
				<h3>나의 클라우드</h3><span class="text-muted">(무료제공 용량기준)</span>
			</div>
			<div class="row text-center" id="allQuota"></div>
			<div class="row" id="vendors"></div>
		  </div>
		  <div class="tab-pane" id="socialTab">
		  	<br/>
		  	<form class="panel panel-default" id="postForm" role="form" enctype="multipart/form-data" action="javascript:;" method="post" accept-charset="utf-8">
		  		<div class="panel-heading">
		  			<div class="panel-title clearfix">
		  				<div class="pull-left">새 글 작성하기</div>
		  				<div class="pull-right">
		  					작성 대상 : <span id="targetSocials"></span>
					  	</div>
		  			</div>
		  			
				</div>
		  		<div class="panel-body">
		  			<textarea id="postTextarea" name="contents" class="form-control" rows="3" placeholder="글, 사진을 한번의 클릭으로 여러 소셜 네트워크에 등록할 수 있습니다." autocomplete="off"></textarea>
					<br/>
					<div id="preview" class="thumbnail" style="position:relative; height: 100px; width: 100px; display:none;">
			  			<img id="addedFile" style="position:absolute; height:100%; width:100%; left: 0px; top: 0px;"/>
			  			<span id="cancelFile" class="glyphicon glyphicon-remove-circle pointer" style="position:absolute; right: -12px; top: -12px; height: 20px; width: 20px;"></span>
					</div>
		  			<br/>
		  			<div class="clearfix">
				  		<div class="pull-left">
				  			<span class="btn btn-default fileinput-button"> 
								<i class="glyphicon glyphicon-camera"></i> <span>사진 추가</span>
								<input id="addFile" type="file" name="file">
							</span>
						</div>
				  		<div class="pull-left">
				  			&nbsp;&nbsp;
				  			<span id="selectFile" class="btn btn-default"> 
								<i class="glyphicon glyphicon-cloud-download"></i> <span>사진 추가</span>
							</span>
						</div>
				  		<div class="pull-right">
				  			<button id="postAll" class="btn btn-success"><span class="glyphicon glyphicon-edit"></span>&nbsp;작성 하기</button>
						</div>
					</div>
		  		</div>
		  	</form>
		  	<div class="text-center" style="margin-top:60px;">
				<div class="row" id="socialList"></div>
			</div>
		  </div>
		</div>
		
	</div>
<!-- 	<div id="quota" class="col-md-10" role="view" style="display:none"> -->
		
<!-- 		<div class="row text-center"> -->
<!-- 			<h3>나의 클라우드</h3><span class="text-muted">(무료제공 용량기준)</span> -->
<!-- 		</div> -->
<!-- 		<div class="row" id="allQuota"> -->
<!-- 		</div> -->
<!-- 		<div class="row" id="vendors"> -->
<!-- 		</div> -->
<!-- 	</div> -->
	
	<div id="list" class="col-md-10" role="view" style="display:none">
		<div id="grid_top">
			<div class="col-md-12" style="margin-bottom:10px;">
				<span class="btn btn-success fileinput-button"> 
					<i class="glyphicon glyphicon-cloud-upload"></i> <span>업로드</span>
				 <input id="fileupload" type="file" multiple="" name="files[]">
				</span>
				<button type="button" class="btn btn btn-primary" id="createNewFolder">
					<span class="glyphicon glyphicon-folder-open"></span> 새폴더
				</button>
				<span id="dynamicButtons"></span>
			</div>
			<div class="col-md-12">
				<ol id="path" class="breadcrumb"></ol>
			</div>
		</div>
		<div class="col-md-12" id="grid" style="overflow:auto;"></div>
	</div>
		
	<div id="search" class="col-md-10" role="view" style="display:none">
		<div id="searchGrid_top">
			<h4 id="searchCountText"></h4>
		</div>
		
		<div class="col-md-12" id="searchGrid" style="overflow:auto;"></div>
	</div>
</body>
</html>

<div class="modal fade" id="modalTree" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				<h4 class="modal-title">Modal title</h4>
			</div>
			<div class="modal-body">
				<div id="modalContentTree"></div>

			</div>
			<div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		        <button type="button" class="btn btn-primary" >Save changes</button>
     		 </div>
		</div>
	</div>
</div>

<div id="context-menu">
  <ul class="dropdown-menu" role="menu"></ul>
</div>