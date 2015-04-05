$(document).ready(function () {
	var error = document.getElementById('exception').value;
	if(error != null && error != ''){
		var errorMsg = document.getElementById('msg').value;
		if(errorMsg != null && errorMsg != ''){
			alertify.error(document.getElementById('msg').value);
		}
	}

});