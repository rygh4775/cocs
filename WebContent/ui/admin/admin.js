$(document).ready(function () {
	if("system" != login_user) {
		window.location = contextPath+'/home';
		return
	}
	
	var self = this;
	this.lastKey = '';
	var listStore = new UI.Store({
		url : contextPath + '/admin/getUsers.json',
		autoLoad : true,
		async: false,
		options : {
			rowCount : 20,
			callback : function(data){
				if(!$.isEmptyObject(arguments)) {
					self.lastKey = data.lastKey;
				}
			}
		}
	});
	
	listStore.on('load', function(e, store, data){
		$('#totalCount').html(data.totalCount);
	});
	
	$('select.selectpicker').selectpicker('val', '');
	$('select.selectpicker').on('change', function(){
		$('#loadingData').fadeOut();
		$('#userList').attr('scrollPagination', 'enabled');
		 
		var value = $(this).val();
		renderGrid();
		listStore.reload({
			oauthProvider :  value,
			firstKey : '',
			callback : function(data){
				if(!$.isEmptyObject(arguments)) {
					self.lastKey = data.lastKey;
				}
			}
		});
	});
	var scroll = $('#userList').scrollPagination({
		store : listStore,
		contentData : {
			// these are the variables you can pass to the request, for example: children().size() to know which page you are
		}, 
		scrollTarget : $(window), // who gonna scroll? in this example, the full window
		heightOffset : 10, // it gonna request when scroll is 10 pixels before the page ends
		beforeLoad : function(contentData){ // before load function, you can display a preloader div
			$('#loadingData').fadeInWithDelay();	
			contentData.firstKey = self.lastKey;
			$('#userList').attr('scrollPagination', 'disabled');
			return true;
		},
		afterLoad: function(data){ // after loading content, you can use this function to animate your new elements
			 if(self.lastKey != data.lastKey) {
				 $('#loadingData').fadeOut();
				 $('#userList').attr('scrollPagination', 'enabled');
			 } else {
				 $('#loadingData').fadeOut();
				 $('#noMoreResults').fadeIn();	
			 }
			 self.lastKey = data.lastKey;
		}
	});
	
	// code for fade in element by element
	$.fn.fadeInWithDelay = function(){
		var delay = 0;
		return this.each(function(){
			$(this).delay(delay).animate({opacity:1}, 200);
			delay += 100;
		});
	};
	
	var renderGrid = function(){
		window.tempGrid = new UI.Grid({
			renderTo : '#userList',
			store : listStore,
			scrollPaging : true,
			noRecordsMsg : '사용자가 존재하지 않습니다.',
			columns : [
	           {
	        	   id : 'id',
	        	   name : 'ID',
	        	   width : '*',
	        	   align : 'center',
	        	   render : function(value, row, vendor) {
	        		   var aTag = $('<a>');
	        		   aTag.text(value);
	        		   aTag.addClass('pointer');
	        		   aTag.on('click', function(e){
	        			   e.stopPropagation();
	        			   var form = $('<form class="form-horizontal" role="form">');
	        			   for ( var idx in row) {
	        				   var formGroup = $('<div class="form-group">');
	        				   $('<label class="col-sm-3 control-label">'+idx+'</label>').appendTo(formGroup);
	        				   var val = row[idx];
	        				   if(idx == 'created' || idx == 'modified') {
	        					   val = utils.convertTimestampToDate(parseInt(val));
	        				   }
	        				   $('<div class="col-sm-9"><p class="form-control-static">'+val+'</p></div>').appendTo(formGroup);
	        				   formGroup.appendTo(form);
	        			   }
	        			   bootbox.dialog({
	        					message : form,
	        					title : '사용자 정보',
	        					buttons : [
									{
										label: "확인",
										className: "btn-default"
									} 
								]
	        				});
	        		   });
	        		   return aTag;
	        	   }
	           },
	           {
	        	   id : 'created',
	        	   name : '생성 일자',
	        	   width : '20%',
	        	   align : 'center',
	        	   render : function(value, row) {
	        		   return utils.convertTimestampToDate(parseInt(value));
	        	   }
	           },
	           {
	        	   id : 'oauthProvider',
	        	   name : 'OAuth Provider',
	        	   width : '20%',
	        	   align : 'center',
	           }
	       ]
		});
	};
	
	renderGrid();
});