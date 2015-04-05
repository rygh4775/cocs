Actions.define('home',{
	id: 'home',
	onLoad:function() {
		var self = this;
		if(History.getHash() == 'social') {
			$('#socialTabButton').tab('show');
		} else {
			$('#cloudTabButton').tab('show');
		}
		
		$("#addFile").change(function() {
			if (this.files && this.files[0]) {
				if(!this.files[0].type.startsWith('image/')) {
					utils.noty.error('이미지 파일을 추가해주세요.');
					self.removeFile();
				} else {
					var reader = new FileReader();
					reader.onload = function(e) {
						$('#addedFile').attr('src', e.target.result);
						self.addFile();
					};
					reader.readAsDataURL(this.files[0]);
				}
		   }
		});
		
		$('#selectFile').on('click', function(){
			if(activeCloudList.length == 0) {
				bootbox.dialog({
		    		  message : '등록된 클라우드가 존재하지 않습니다. <a href="'+contextPath+'/home">여기</a>로 이동하여 클라우드를 등록 할 수 있습니다.',
		    		  title : "클라우드로부터 사진 추가",
		    		  buttons: {
		    			  cancel: {
		    				  label: "취소",
		    				  className: "btn-default"
		    			  }
		    		  }
		    	  });
				return;
			}
			utils.list.selectFile(null, function(node) {
				if(node.thumbnailLink != '' && node.thumbnailLink != null) {
					$('#addedFile').attr('src', node.thumbnailLink);
				} else {
					$('#addedFile').attr('src', window.contextPath + '/api/' + node.vendor + '/file/getThumbnail.jpg?path=' + node.id);
				}
				$("form#postForm").data({cloudVendor : node.vendor});
				$("form#postForm").data({id : node.id});
				self.addFile();
			});
		});
		
		$('#cancelFile').on('click', function(){
			self.removeFile();
		});
	},
	viewController : function(id){
    	$('[role=view]').hide();
    	$('[role=view]#'+id).show();
    },
    addFile : function(){
    	$('#preview').show();
    },
    removeFile : function(){
    	$('#preview').hide();
    	$('#addFile').val('');
    	$("form#postForm").removeData();
    },
    show : function(){
    	this.viewController(this.id);
    	
    	this.renderCloudTab();
    	this.renderSocialTab();
    },
    renderCloudTab : function(){
    	var vendorsView = $('#vendors');
    	vendorsView.empty();
    	
//    	var quotaTotal = 0;
//    	var quotaUsed = 0;
    	
		var ajaxArray = [];
		
//		var quotaArray = [];
		
		for ( var idx in activeCloudList) {
			var activeVendor = activeCloudList[idx];
			var ajax = $.ajax({
				url: api + activeVendor +'/user/getInfo.json',
				type : 'post',
				success: function(data) {
					var result = data.result;
//					quotaTotal += result.quotaTotal;
//					quotaUsed += result.quotaUsed;
//					quotaArray.push([data.vendor, result.quotaUsed, '전체 용량 : ' + utils.bytesToSize(result.quotaTotal) + '<br/> 사용 용량 : ' + utils.bytesToSize(result.quotaUsed)]);
					new UI.CloudBox({
						renderTo : '#vendors',
						vendor : data.vendor,
						active : true,
						contents : '전체 용량 '+utils.bytesToSize(result.quotaTotal)+ ' 중 '+utils.bytesToSize(result.quotaUsed)+' 사용',
						percentage : (result.quotaUsed / result.quotaTotal) * 100,
						button : {
							text : '사용 하기',
							type : 'btn-success',
							click : function(){
								var data = {
										vendor : this.vendor,
										parentId : 'root',
										parentPath : '/'
								};
								History.pushState(data, window.title, window.contextPath + '/home/' + this.vendor);
							}
						}
					});
				},
				error : UI.AjaxErrorHandler
			});
			ajaxArray.push(ajax);
		}
			
		
		var quotaArray = [];
		
		for ( var idx in activeCloudList) {
			var activeVendor = activeCloudList[idx];
			quotaArray.push([clouds[activeVendor].serviceName, clouds[activeVendor].freeQuota, activeVendor]);
		}
		
		var allInactiveVendorFreeQuota = 0;
		for ( var idx in inactiveCloudList) {
			var inactiveCloud = inactiveCloudList[idx];
			allInactiveVendorFreeQuota += clouds[inactiveCloud].freeQuota;
		}
		quotaArray.push(['미사용', allInactiveVendorFreeQuota, 'unused']);
		
		var chartData = new google.visualization.DataTable();
		chartData.addColumn('string', 'ServiceName');
		chartData.addColumn('number', 'freeQuota');
		chartData.addColumn('string', 'ss');
		chartData.addRows(quotaArray);
		
		
		var options = {
//        		title: '나의 클라우드(무료제공 용량기준)',
        		titleTextStyle : {
        			fontSize: 20,
        			fontName: '<global-font-name>'
        		},
        		tooltip: {
        			text: 'percentage',
        			trigger: 'selection'
    			},
    			chartArea : {
    				width:"80%",
    				height:"65%",
    				left:"25%"
    			}
        };
        
        var chart = new google.visualization.PieChart(document.getElementById('allQuota'));
        
        chart.setAction({
			id: 'info',                  // An id is mandatory for all actions.
			text: '상세정보 이동',
			action: function(){
				var vendor = chartData.getValue(chart.getSelection()[0].row, 2);
				if('unused' == vendor) {
					
					var message =$('<div>');
					message.append('<p>&nbsp;아래의 클라우드를 등록하여 저장 공간을 늘릴 수 있습니다 :)</p>');
					for ( var idx in inactiveCloudList) {
						var inactiveCloud = inactiveCloudList[idx];
						var serviceName = clouds[inactiveCloud].serviceName;
						var redableQuota = utils.bytesToSize(clouds[inactiveCloud].freeQuota);
						message.append('<h3>&nbsp;'+serviceName+'&nbsp;<span class="label label-info">'+redableQuota+'무료</span></h3>');
					}
					message.append('<br/>');
					
					alertify.alert(message.html());
				} else {
					$('html,body').animate({scrollTop: $('[role-vendor="'+vendor+'"]').closest('.col-md-4').offset().top});
					$('.thumbnail.status').css('border', '1px solid #DDD');
					$('[role-vendor="'+vendor+'"]').closest('.thumbnail').css("border", "2px solid #5bc0de");
				}
			}
		});
        
        chart.draw(chartData, options);
        
		$.when.apply($, ajaxArray).done(function(e){
	        for ( var idx in inactiveCloudList) {
	        	var inactiveCloud = inactiveCloudList[idx];
	        	
	        	var contents = $('<div>');
	        	$('<h3 class="text-center">'+clouds[inactiveCloud].serviceName+'</h3>').appendTo(contents);
	        	$('<p>').text(clouds[inactiveCloud].description).appendTo(contents);
	        	
	        	new UI.CloudBox({
	        		renderTo : '#vendors',
					vendor : inactiveCloud,
					active : false,
					contents : contents,
					button : {
						text : '등록 하기',
						type : 'btn-info',
						click : function(){
							window.location = contextPath+'/api/'+this.vendor+'/authorize.do';
						}
					}
				});
	        }
		});
    },
    renderSocialTab : function(){
    	var self = this;
    	var targetSocials = $('#targetSocials');
    	var postAllButton = $('#postAll');
    	if(activeSocialList.length == 0) {
    		targetSocials.text('없음');
    		postAllButton.attr('disabled', 'disabled');
    	}
    	
    	$("form#postForm").submit(function(event){
    		event.preventDefault();
	
 		   var validator = $(this).validate({
					rules:{
						contents:{
							required : true,
							minlength : 5
						}
					},
					highlight: function(element, errorClass, validClass) {
						$(element).parents('.form-group').removeClass('has-success');
						$(element).parents('.form-group').addClass('has-error');
					},
					unhighlight: function(element, errorClass, validClass) {
						$(element).parents('.form-group').removeClass('has-error');
						$(element).parents('.form-group').addClass('has-success');
					}
 		   });
 		   
 		   	validator.form();
 			if (validator.errorList.length != 0) return false;
			var formData = new FormData($(this)[0]);
			formData.append('cloudVendor', $(this).data('cloudVendor') || '');
			formData.append('id', $(this).data('id') || '');
			formData.append('path', $(this).data('id') || '');
			
			for ( var idx in activeSocialList) {
    			var activeSocial = activeSocialList[idx];
    			$.ajax({
    				url: api + activeSocial +'/post/create.json',
    				type : 'post',
    				data : formData,
    				cache : false,
    				contentType : false,
    				processData : false,
    				global : false,
    				success : function(data) {
    					var cancelElementId = 'cancel' + data.vendor +'Post'; 
    					var cancleButton = '<a class="pointer" id="'+cancelElementId+'"><b>취소 하기</b></a>';
    					utils.noty.success(socials[data.vendor].serviceName + '에 성공적으로 작성 되었습니다.' + cancleButton, 10000);
    					$('#postForm')[0].reset();
    					self.removeFile();
    					$('#'+cancelElementId).on('click', function(){
    						$.ajax({
    		    				url: api + data.vendor +'/post/delete.json',
    		    				type : 'post',
    		    				data : {
    		    					id : data.id
    		    				},
    		    				global : false,
    		    				success : function(data) {
    		    					utils.noty.success(socials[data.vendor].serviceName + '에 작성을 정상적으로 취소 하였습니다.');
    		    				}
    		    			});
    					});
    				},
    				error : UI.AjaxErrorHandler
    			});
			}
			return false;
		});
    	
    	for ( var idx in activeSocialList) {
    		var activeSocial = activeSocialList[idx];
    		
    		$('<img style="margin-right:3px;" src="'+contextPath+'/ui/resources/img/vendors/'+activeSocial+'_29.png" alt="'+activeSocial+'">').appendTo(targetSocials);
    		(function(activeSocial){
    			new UI.SocialBox({
    				renderTo : '#socialList',
    				vendor : activeSocial,
    				active : true,
    				contents : socials[activeSocial].postInfo,
    				button : {
    					text : '작성 하기',
    					type : 'btn-success',
    					click : function(){
    						new UI.PostFormDialog({
    							title : '새 글 작성하기',
    							vendor : activeSocial,
    							formId : 'dialogForm',
    							buttons : [
    							           {
    							        	   label: "작성",
    							        	   className: "btn-primary",
    							        	   callback: function() {
    							        		   
    							        		   var validator = $('#dialogForm').validate({
	    							   					rules:{
	    							   						contents:{
	    							   							required : true,
	    							   							minlength : 5
	    							   						}
	    							   					},
	    							   					highlight: function(element, errorClass, validClass) {
	    							   						$(element).parents('.form-group').removeClass('has-success');
	    							   						$(element).parents('.form-group').addClass('has-error');
	    							   					},
	    							   					unhighlight: function(element, errorClass, validClass) {
	    							   						$(element).parents('.form-group').removeClass('has-error');
	    							   						$(element).parents('.form-group').addClass('has-success');
	    							   					}
    							        		   });
    							    		   
    							        		   validator.form();
    							        		   if (validator.errorList.length != 0) return false;
    							        		   
    							        		   var formData = new FormData($('#dialogForm')[0]);
    							        		   formData.append('cloudVendor', $('#dialogForm').data('cloudVendor') || '');
    							        		   formData.append('id', $('#dialogForm').data('id') || '');
    							        		   formData.append('path', $('#dialogForm').data('id') || '');
    							       			
    							        		   $.ajax({
    							       				url: api + activeSocial +'/post/create.json',
    							       				type : 'post',
    							       				data : formData,
    							       				cache : false,
    							       				contentType : false,
    							       				processData : false,
    							       				global : false,
    							       				success : function(data) {
    							       					var cancelElementId = 'cancel' + data.vendor +'Post'; 
    							       					var cancleButton = '<a class="pointer" id="'+cancelElementId+'"><b>취소 하기</b></a>';
    							       					utils.noty.success(socials[data.vendor].serviceName + '에 성공적으로 작성 되었습니다.' + cancleButton, 10000);
    							       					$('#postForm')[0].reset();
    							       					self.removeFile();
    							       					$('#'+cancelElementId).on('click', function(){
    							       						$.ajax({
    							       		    				url: api + data.vendor +'/post/delete.json',
    							       		    				type : 'post',
    							       		    				data : {
    							       		    					id : data.id
    							       		    				},
    							       		    				global : false,
    							       		    				success : function(data) {
    							       		    					utils.noty.success(socials[data.vendor].serviceName + '에 작성을 정상적으로 취소 하였습니다.');
    							       		    				}
    							       		    			});
    							       					});
    							       				},
    							       				error : UI.AjaxErrorHandler
    							       			});
    							        	   }
    							           },
    							           {
    							        	   label: "취소",
    							        	   className: "btn-default"
    							           }
							           ]
    						}).show();
    					}
    				}
    			});
    		})(activeSocial);
		}
    	
    	for ( var idx in inactiveSocialList) {
			var inactiveSocial = inactiveSocialList[idx];
			
			var contents = $('<div>');
        	$('<h3 class="text-center">'+socials[inactiveSocial].serviceName+'</h3>').appendTo(contents);
        	$('<p>').text(socials[inactiveSocial].description).appendTo(contents);
        	
        	(function(inactiveSocial){
				new UI.SocialBox({
					renderTo : '#socialList',
					vendor : inactiveSocial,
					active : false,
					contents : contents,
					button : {
						text : '등록 하기',
						type : 'btn-info',
						click : function(){
							window.location = api + inactiveSocial + '/authorize.do';
						}
					}
				});
        	})(inactiveSocial);
		}
    }
});