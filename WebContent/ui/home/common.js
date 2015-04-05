var page = window.page = {};
page.isBeingUploaded = false;
window.onbeforeunload=null;
window.onbeforeunload = function(e) {
	if(page.isBeingUploaded) {
		var e = e || window.event;
		  // For IE and Firefox prior to version 4
		  if (e) {
		    e.returnValue = '다른 페이지로 이동하면 업로드가 취소됩니다.';
		  }
		  // For Safari
		  return '다른 페이지로 이동하면 업로드가 취소됩니다.';
	}
};

window.fixSize = function(){
	$('#grid').css('height', window.innerHeight-(70+$('#grid_top').height()+20));
	$('#searchGrid').css('height', window.innerHeight-(70+$('#searchGrid_top').height()+30));
	$('#left_treeGrid').css('width', $('[role="side-bar"]').width());
	$('#left_treeGrid').css('height', window.innerHeight-70);
}
$(window).resize(function() {
	fixSize();
});

$(document).ready(function() {
//	left folder list
	var folderStore = new UI.Store({
		autoLoad : false,
		options : {
				parentId : 'root',
				parentPath : '/'
		}
	});
	
	var data = [];
	
	for ( var idx in activeCloudList) {
		var activeCloud = activeCloudList[idx];
		data.push({
			label : clouds[activeCloud].serviceName,
			value : activeCloud,
			vendor : activeCloud,
			children: [
	            {
	        	 label : 'loading...'  
	            }
            ]
		});
	}
	
	var leftTree = $('#left_treeGrid');
	
	leftTree.tree({data: data});
    
	leftTree.bind('tree.open', function(e) {
    	var selectedNode = e.node;
    	
    	var options = {
				vendor : selectedNode.vendor
		};
    	
    	folderStore.url = api + selectedNode.vendor +'/folder/getFolders.json';
    	
    	if(selectedNode.id == undefined) {
    		options.parentId = 'root'; 
    		options.parentPath = '/'; 
    	} else {
    		options.parentId = selectedNode.id; 
    		options.parentPath = selectedNode.id; 
    	}
    	
//    	store state
    	var nodeValueArray = [];
		nodeValueArray.push(selectedNode.value);
    	
    	var parentNode = selectedNode.parent;
		
		while (parentNode.name != undefined) {
			nodeValueArray.unshift(encodeURIComponent(parentNode.value));
			parentNode = parentNode.parent;
		}
    	History.storeState(History.createStateObject( options, window.title, window.contextPath + '/home/' + nodeValueArray.join('/')));
//    	store state
    	
    	options.callback = function(data){
			if(data != undefined) {
				var nodeData = [];
        		var rows = data['rows'];
        		for ( var idx in rows) {
        			var row = rows[idx];
        			var node = {
        					label : row.name,
        					iconClass : 'glyphicon glyphicon-folder-close',
        					value : row.name,
        					id : row.id || row.path,
        					children: [
        	    	            {
        	    	        	 label : 'loading...'  
        	    	            }
        		            ],
        		            vendor: folderStore.lastOptions.vendor
        			};
        			nodeData.push(node);
        		}
        		leftTree.tree('loadData', nodeData, selectedNode);
			}
		};
		
    	folderStore.reload(options);
    });
	
	leftTree.bind('tree.click', function(e) {
		var selectedNode = e.node;
		
		var nodeValueArray = [];
		nodeValueArray.push(selectedNode.value);
		
		var parentNode = selectedNode.parent;
		
		while (parentNode.name != undefined) {
			nodeValueArray.unshift(encodeURIComponent(parentNode.value));
			parentNode = parentNode.parent;
		}
		
		var data = {
				vendor : selectedNode.vendor,
				parentId : selectedNode.id || 'root',
				parentPath : selectedNode.id || '/'
		};
		
	    History.pushState(data, window.title, window.contextPath + '/home/' + nodeValueArray.join('/'));
	});
//	left folder list
	
//	URL parsing only here
	var url = History.getPageUrl();
	
	if(History.getHash() != '') {
		url = url.replace('#'+History.getHash(), '');
	}
	
	if(url.endsWith('home/')) {
		Actions.get('home').load();
		Actions.get('home').show();
	}else if(url.endsWith('search/')) {
		Actions.get('search').load();
		Actions.get('search').show();
	}else {
		Actions.get('list').load();
		Actions.get('list').show();
	}
	
	History.Adapter.bind(window, 'statechange', function() {
		var url = History.getPageUrl();
		
		if(History.getHash() != '') {
			url = url.replace('#'+History.getHash(), '');
		}
		
		if(url.endsWith('home/')) {
			Actions.get('home').load();
			Actions.get('home').show();
		}else if(url.endsWith('search/')) {
			Actions.get('search').load();
			Actions.get('search').show();
		}else {
			Actions.get('list').load();
			Actions.get('list').show();
		}
//		var state = History.getState();
//		History.log('statechange:', state.data, state.title, state.url);
	});
//	URL parsing only here
	
//	render search multiSelect nicely
	for ( var idx in activeCloudList) {
		var activeCloud = activeCloudList[idx];
		$('#searchSelector').append('<option value="'+activeCloud+'">'+clouds[activeCloud].serviceName+'</option>');
	}
	
	 $('#searchSelector').multiselect({
		 includeSelectAllOption : true,
		 selectAllText : '전체 파일 검색',
		 selectAllValue: 'all',
		 nonSelectedText : '검색 범위 설정'
	 });
	
	 $('option', '#searchSelector').each(function(element) {
		 $('#searchSelector').multiselect('select', $(this).val());
    });
//	render search multiSelect nicely	
	 
//	search event
	 $('#search_btn').on('click', function(){
		 var vendorArray = $('#searchSelector').val();
		 if($('#keyword').val() == '') {
			 return
		 }
		 	 
		 if(vendorArray == null) {
			 utils.noty.warn('적어도 하나의 클라우드가 선택되야 합니다.');
			 return
		 }
		 
		 vendorArray = jQuery.grep(vendorArray, function(value) {	//remove element from array
			  return value != 'all';
		 });
		 
		 if(vendorArray.length == 0) {
			 utils.noty.warn('적어도 하나의 클라우드가 선택되야 합니다.');
			 return
		 }
		 
		 var data = {
				 vendors: vendorArray,
				 keyword: $('#keyword').val()
		 };
		 History.pushState(data, window.title, window.contextPath + '/home/search');
		 $('div.navbar-collapse').collapse('hide');
	 });

	 $('#keyword').bind("keydown", function(e) {
		 if (e.keyCode == 13) { // enter key
			 $('#search_btn').trigger('click');
			 $('div.navbar-collapse').collapse('hide');
			 return false;
		 }
	 });
	 $('#keyword').focus();
//		search event
	 
	 fixSize();
});