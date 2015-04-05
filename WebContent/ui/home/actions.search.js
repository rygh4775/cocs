Actions.define('search',{
	id: 'search',
	onLoad:function() {
		
    },
	clear : function(){
    	$('[role=view]').hide();
    	$('[role=view]#'+this.id).show();
    	$('[role=view]#'+this.id).empty();
    },
    viewController : function(id){
    	$('[role=view]').hide();
    	$('[role=view]#'+id).show();
    },
    show : function(){
    	var self = this;
    	this.viewController(this.id);
    	var state = History.getState(),
    	data = state.data;
    	
		var stores = [];
		var totalCount = 0;
		for ( var idx in data.vendors) {
			var vendor = data.vendors[idx];
			var store = new UI.Store({
				url: api + vendor +'/file/search.json',
	    		options : {keyword : data.keyword}
	    	});
			store.on('load', function(e, self){
				totalCount += self.getTotalCount();
				console.log(self.lastOptions.keyword);
				$('#searchCountText').html('\''+self.lastOptions.keyword+'\' 검색 - <small>결과 '+totalCount+'개</small>').addClass('col-md-12');
				fixSize();
			});
			stores.push(store);
		};
		
		new UI.SearchGrid({
    		renderTo : '#searchGrid',
    		stores : stores,
    		columns : [
				{
					   id : 'iconInfo',
					   name : '',
					   width : '3%',
					   align : 'center',
					   render : function(value, row){
						   var vendor = row['vendor'];
//						   temp
						   if(vendor == 'dropbox') {
							   var iconPath = contextPath+"/ui/resources/dropboximg/" + value + ".gif";
							   return $('<img src="'+iconPath+'">');
						   } else {
							   return $('<img src="'+value+'">');
						   }
					   }
				},
	           {
	        	   id : 'name',
	        	   name : '이름',
	        	   width : '*',
	        	   align : 'left',
	        	   render : function(value, row, vendor, el){
	        		   el.css('word-break','break-all');
	        		   var span = $('<span>');
	        		   span.text(value);
	        		   span.addClass('pointer');
	        		   span.hover(function(){
	        			   span.css("color","#428BCA");
	        			   },function(){
        				   span.css("color","black");
        			   });
	        		   
	        		   if(row.isFile) {
	        			   span.on('click', function(e){
	        				   if(row.previewSupported) {
	        					   $.ajax({
										url: api + vendor +'/file/getPreviewLink.json',
										async : false,
										data : {
											path: row.path,
											id: row.id
										},
										type : 'post',
										success: function(data) {
											$.magnificPopup.open({
												type : (row.mimeType.startsWith('image')) ? 'image' : 'iframe',
												items : {
													src : data.result
												}
											});
										},
										error : UI.AjaxErrorHandler
	        					   });
	        				   } else {
	        					   var holder = $('<div class="text-center">');
	        					   $('<h4 class="text-muted text-center">미리보기가 지원되지 않는 형식의 파일 입니다.</h6>').appendTo(holder);
	        					   $('<br/>').appendTo(holder);
	        					   var downloadButton = $('<button type="button" class="btn btn-primary"><span class="glyphicon glyphicon-cloud-download"></span> 다운로드</button>');
	        					   downloadButton.appendTo(holder);
	        					   
	        					   downloadButton.on('click', function(){
	        						   var downloadURL= api + vendor +'/file/downloadFile.json';
	        		            		if (row.id) {
	        		            			downloadURL += '?id=' + row.id;
	        		            		} else if (row.path) {
	        		            			downloadURL += '?path=' + row.path;
	        		            		}
	        		            		location.href = downloadURL;
	        					   });
	        					   
	        					   $.magnificPopup.open({
	        						   items: {
	        							   src: holder,
	        							   type: 'inline'
	        						   }
	        						 });
	        				   }
	        			   });
	        		   }
	        		   
	        		   return span;
	        	   }
	           },
	           {
				   id : 'vendor',
				   name : '위치',
				   width : '15%',
				   align : 'center',
				   render : function(value){
					   return $('<img style="width:25px; height:25px"; src="'+contextPath+'/ui/resources/img/vendors/'+value+'_29.png" alt="'+clouds[value].serviceName+'">');
				   }
				},
	           {
	        	   id : 'modified',
	        	   name : '수정된 날짜',
	        	   width : '20%',
	        	   align : 'center',
	        	   addClass : 'hidden-xs',
	        	   render : function(value){
	        		   return utils.convertTimestampToDate(value);
	        	   }
	           }
           ]
    	});
    }
});