$(document).ready(function () {	var forgotButton = $('#forgotForm .btn').eq(0).on('click', function(){		var validator = $('#forgotForm').validate({			rules: {				email: {					email: true,					required: true				}			},			highlight : function(element) {				$(element).css('margin-bottom', '0px');				$(element).closest('.form-group').removeClass('has-success').addClass('has-error');			},			success : function(element) {				$(element).prev().css('margin-bottom', '0px');				element.closest('.form-group').removeClass('has-error').addClass('has-success');			}		});				validator.form();		if (validator.errorList.length == 0){			$.ajax({				type: "POST",				url: contextPath + '/doForgotPassword.json',				data : $('#forgotForm').serialize(),				dataType : "json",				success: function(data) {					if(data.success) {						$('#forgotForm').hide();						$("#forgotSendForm").show();						$('.steps > li').eq(0).removeClass('active').addClass('disabled');						$('.steps > li').eq(1).removeClass('disabled').addClass('active');						$('.page-header').find('h1').text('step2. ').append('<small> 이메일 전송</small>');						var userEmail = $('#email').val(); 						$('#forgotSendForm .sendTxt p').eq(0).html('<strong>'+userEmail + "</strong>(으)로 이메일을 발송하였으니 확인 부탁드립니다.");											} else {						utils.noty.error(data.message);					}				},				error : UI.AjaxErrorHandler			});		}	});	$('#forgotForm').find('.form-control').keypress(function(event){		var keycode = (event.keyCode ? event.keyCode : event.which);		if(keycode == '13'){			forgotButton.click();		}		event.stopPropagation();	});	});