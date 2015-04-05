// Ajax activity indicator bound to ajax start/stop document events
//var loadingNoty;
$(document).ajaxStart(function(){
	$('<div style="z-index: 1051;" class="spin" data-spin>').appendTo('body');
	$('.spin').spin('show'); 
	$('<div class="modal-backdrop in" style="z-index: 1050;">').appendTo('body');
//	loadingNoty = $.pnotify({
//	    title: false,
//	    text: '로딩 중..',
//	    type: 'info',
//	    	sticker : false
//	});
}).ajaxStop(function(){
//	loadingNoty.pnotify_remove();
	$('.spin').spin('hide'); 
	$('.spin').remove();
	$('.modal-backdrop.in').remove();
});

$(document).ready(function () {
	$('.selectpicker').selectpicker();
});