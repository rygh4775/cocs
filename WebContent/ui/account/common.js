$(document).ready(function() {
//	URL parsing only here
	if(History.getPageUrl().endsWith('mycloud/')) {
		$('.nav-tabs #mycloud').tab('show');
	} else if(History.getPageUrl().endsWith('mysocial/')){
		$('.nav-tabs #mysocial').tab('show');
	} else {
		$('.nav-tabs #settings').tab('show');
	}
	
	$('.nav-tabs a').on('click', function(e){
		e.preventDefault();
		History.pushState(null, window.title, contextPath+'/account/'+$(this).attr('id'));
	});
});