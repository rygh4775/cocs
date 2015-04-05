$(document).ready(function () {
	$('#signin_button').on('click', function(){
		if(login_user == '') {
			$('#signinModal').modal({
				keyboard : false
			});
		} else {
			location.href = contextPath + '/home';
		}
	})
});