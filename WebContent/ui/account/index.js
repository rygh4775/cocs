$(document).ready(function() {
	
//	first tab
	$('#changePassowrd_btn').on('click', function(){
		if(oauth_provider != 'default') {
			utils.noty.warn(oauth_provider + '을 이용하여 로그인 하였습니다.');
			return 
		}
		
		var dialog = new UI.FormDialog({
			title : '비밀번호 변경',
			columns : [
			           {
			        	   type : 'password',
			        	   id : 'password',
			        	   name : 'password',
			        	   label : '이전 비밀번호',
			        	   placeholder : '이전 비밀번호'
			           },
			           {
			        	   type : 'password',
			        	   id : 'newPassword',
			        	   name : 'newPassword',
			        	   label : '새 비밀번호',
			        	   placeholder : '새 비밀번호'
			           },
			           {
			        	   type : 'password',
			        	   id : 'confirmNewPassword',
			        	   name : 'confirmNewPassword',
			        	   label : '새 비밀번호 확인',
			        	   placeholder : '새 비밀번호 확인'
			           }
            ],
			buttons : [
		       {
		    	   label: "비밀번호 변경",
		    	   className: "btn-primary",
		    	   callback: function() {
		    		   var form = dialog.getForm();
		    		   var validator = form.validate({
							rules:{
								password:{
									required : true
								},
								newPassword:{
						    		required : true,
						    		minlength : 8
						    	},
						    	confirmNewPassword:{
									required : true,
									minlength : 8,
									equalTo: "input[name='newPassword']"
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
		    			if (validator.errorList.length == 0){
		    				$.ajax({
		    					type: "POST", 
		    					dataType: 'json',
		    					data: form.serialize(),
		    					url: contextPath +"/users/changePassword.json",
		    					success: function(data){
		    						if(data.success) {
		    							dialog.close();
		    						}
		    					},
		    					error: UI.AjaxErrorHandler
		    				});
		    			}
		    			return false;
					}
		       },
		       {
		    	   label: "취소",
		    	   className: "btn-default"
		       }
           ]
		});
		dialog.show();
	});
	
	$('#deleteAccount_btn').on('click', function(){
		$('#deleteAccountModal').modal('show');
		var form = $('form','#deleteAccountModal');
		if(oauth_provider != 'default') {
			form.find('.form-group:first').hide();
		}
		var deleteButton = $('.modal-footer','#deleteAccountModal').find('button:first');
		deleteButton.unbind('click');
		deleteButton.on('click', function(){
			
			var validator = form.validate({
				rules:{
					password:{
						required : true
					},
					reason:{
			    		required : true
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
			if (validator.errorList.length == 0){
				$.ajax({
					url: contextPath +'/account/delete.json',
					data : form.serialize(),
					type : 'post',
					success: function(data) {
						window.location = contextPath+'/signout.do';
					},
					error : UI.AjaxErrorHandler
				});
			}
			
		});
	});

//	second tab
	if(activeCloudList == '') {
		var holder = $('<div class="text-center" style="margin-top:60px;">');
		
		var h1 = $('<h1>');
		h1.text('등록된 클라우드가 존재하지 않습니다.');
		h1.appendTo(holder);
		
		$('<h4><a href="'+contextPath+'/home">여기</a>로 이동하여 클라우드를 등록 할 수 있습니다.</h4>').appendTo(holder);
		
		$('#mycloudTab').html(holder);
	} else {
		for (var idx in activeCloudList) {
			var vendor = activeCloudList[idx];
			(function(vendor){
				$.ajax({
					url: api + vendor +'/user/getInfo.json',
					global: false,
					type : 'post',
					success: function(data) {
						if(data.result) {
							var cloudInfo = new UI.VendorInfoBox({
								vendor : vendor,
								userName : data.result.name,
								serviceName : clouds[vendor].serviceName,
								description : clouds[vendor].description,
								settingsUrl : clouds[vendor].settingsUrl,
								button : {
									text : '인증 해지',
									type : 'btn-danger',
									click : function(){
										alertify.confirm('인증해지를 할 경우 '+clouds[vendor].serviceName+'를 이용하실 수 없습니다.<br/> 그래도 해지 하시겠습니까?', function (e) {
										    if (e) {
										    	window.location = contextPath+'/api/'+vendor+'/unauthorize.do';
										    }
										});
									}
								}
							});
							$('#mycloudTab').append(cloudInfo.getElement());
						}
					},
					error : UI.AjaxErrorHandler
				});
			})(vendor);
		}
	}
	
//	third tab
	if(activeSocialList == '') {
		var holder = $('<div class="text-center" style="margin-top:60px;">');
		
		var h1 = $('<h1>');
		h1.text('등록된 소셜 네트워크 서비스가 존재하지 않습니다.');
		h1.appendTo(holder);
		
		$('<h4><a href="'+contextPath+'/home#social">여기</a>로 이동하여 소셜 네트워크 서비스를 등록 할 수 있습니다.</h4>').appendTo(holder);
		
		$('#mysocialTab').html(holder);
	} else {
		for (var idx in activeSocialList) {
			var vendor = activeSocialList[idx];
			(function(vendor){
				$.ajax({
					url: api + vendor +'/user/getInfo.json',
					global: false,
					type : 'post',
					success: function(data) {
						if(data.result) {
							var socialInfo = new UI.VendorInfoBox({
								vendor : vendor,
								userName : data.result.name,
								serviceName : socials[vendor].serviceName,
								description : socials[vendor].description,
								settingsUrl : socials[vendor].settingsUrl,
								button : {
									text : '인증 해지',
									type : 'btn-danger',
									click : function(){
										alertify.confirm('인증해지를 할 경우 '+socials[vendor].serviceName+'를 이용하실 수 없습니다.<br/> 그래도 해지 하시겠습니까?', function (e) {
										    if (e) {
										    	window.location = contextPath+'/api/'+vendor+'/unauthorize.do';
										    }
										});
									}
								}
							});
							$('#mysocialTab').append(socialInfo.getElement());
						}
					},
					error : UI.AjaxErrorHandler
				});
			})(vendor);
		}
	}
	
});