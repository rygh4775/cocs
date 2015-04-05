Actions.define('list',{
	id: 'list',
	onLoad:function() {
		var self = this;
    	var fileupload = $('#fileupload');
    	fileupload.fileupload({
	        dataType: 'json',
	        autoUpload : true,
	        sequentialUploads : true,
	        filesContainer : $('#uploadList'),
	        dropZone : $('#grid'),
	        global : false,
	        previewMaxWidth: 40,
            previewMaxHeight: 30
    	});
    	
    	fileupload.bind('fileuploadprogressall', function(e,data) {
			page.isBeingUploaded = (data.loaded == data.total)  ? false : true;

			if(page.isBeingUploaded) {
				var percentage = Math.floor(data.loaded / data.total * 100);
				$('.header_text').html('업로드 중... ('+ percentage+'%)');
				data.context.find('input').val(progress).change();
			} else {
				$('.header_text').html('업로드가 완료되었습니다.');
			}
		});
    	fileupload.bind('fileuploaddrop', function(e, data){
        	$('#uploadStatus').show();
        });
    	
    	fileupload.bind('fileuploadsend', function(e, data){
//    		console.log('data ', data.originalFiles);
//    		var reader = new FileReader(); // instance of the FileReader
//            reader.readAsDataURL(data.originalFiles[0]); // read the local file
//
//            reader.onloadend = function(){ // set image data as background of div
//            	
//            	$('.preview').find('img').css("src", this.result);
//            	console.log('this : ', $('.preview').find('img'));
//            }
            
        	$('#uploadStatus').show();
        });
    	
    	fileupload.bind('fileuploadfail', function(e, data){
//    		if(data.errorThrown == 'abort') {
//    			if(data.getNumberOfFiles() == 1) {
//    				page.isBeingUploaded = false;
//        			$('#close_uploadStatus').click();
//        		}
//    		}
    	});
    	
    	$(document).bind('drop dragover', function (e) {
    	    e.preventDefault();
    	});
    	
    	fileupload.bind('fileuploaddone', function(e, data){
    		self.listStore.reload();
    	});
    	
    	$('#close_uploadStatus').on('click', function(){
    		if(page.isBeingUploaded) {
				window.location.reload();
			} else {
				$("#uploadStatus").hide();
				$('#uploadList').empty();
			}
    	});
    	
    	$('#resize_uploadList').on('click', function(){
    		$("#uploadList").slideToggle();
		});
    	
    	$('#createNewFolder').on('click', function(){
    		
    		alertify.prompt('새 폴더의 이름을 입력하세요.', function (e, name) {
     		    if (e) {
     		    	var data = History.getState().data;
					data.name = name;
					
     		    	$.ajax({
						url: api + History.getState().data.vendor +'/folder/create.json',
						data : data,
						type : 'post',
						success: function(data) {
							utils.noty.success('폴더를 성공적으로 생성하였습니다.');
							self.listStore.reload();
						},
						error : UI.AjaxErrorHandler
					});
     		    }
     		});
    		
    	});
    	
    	var contextMenu = new UI.ContextMenu({
    		target : '#context-menu',
    		addEvent : {
	            download : function(items){
	            	utils.list.download(items);
	            },
	            rename : function(items){
	            	utils.list.rename(items, function(){
	            		self.listStore.reload();
	            	});
	            },
	            remove : function(items){
	            	utils.list.remove(items, function(){
	            		self.listStore.reload();
	            		
	            	});
	            },
	            move : function(items){
	            	utils.list.move(items, function(){
	            		self.listStore.reload();
	            	});
	            },
	            copy : function(items){
	            	utils.list.copy(items, function(){
	            		self.listStore.reload();
	            	});
	            }
    		}
    	});
    	
    	this.listStore = new UI.Store({
    		autoLoad : false,
    		global : true
    	});
    	
    	this.listStore.on('load', function(){
    		fixSize();
    	});
    	
		var listGrid = new UI.Grid({
    		renderTo : '#grid',
    		contextMenu : contextMenu,
    		store : self.listStore,
    		uploadFile : function(event, file, target){
    			var formSetting = {
     				parentId : target.id || History.getState().data.parentId,
          			parentPath : target.path || History.getState().data.parentPath,
          			vendor : History.getState().data.vendor
    			}
    			
    			var reader = new FileReader(); // instance of the FileReader
    			var tempUrl;
              reader.readAsDataURL(file); // read the local file
  
              reader.onloadend = function(){ // set image data as background of div
              	
              	//$('.preview').find('img').css("src", this.result);
            	  tempUrl = this;
              }
    			fileupload.fileupload('option', {
    	    		//url : tempUrl,
    	    		formData : formSetting
    	    		//add : {files : file}
    	    		
    	    	});
    	    	
    			fileupload.fileupload('add', {files : file});
    			
    			
    		},
    		onDrop : function(sendObj, targetObj, callback){
    			// 이동할 object
	     			var selectedNode = targetObj;
	     			console.log('selectedNode.id : ', selectedNode.id);
	     			var items = sendObj;
		     		for ( var idx in items) {
						var item = items[idx];
						(function(item){
							$.ajax({
								url : api + History.getState().data.vendor +'/file/move.json',
								data : {
									id : item.id,
				     				path : item.path,
				     				targetParentId : selectedNode.id || History.getState().data.parentId,
				          			targetParentPath : selectedNode.path || History.getState().data.parentPath,
				          			targetVendor : History.getState().data.vendor
								},
								type: 'POST',
								dataType: 'json',
								success: function(data) {
									utils.noty.success('\''+item.name+'\'이(가) '+selectedNode.name+'으(로) 이동 되었습니다.');
				     					//self.listStore.reload();
									window.location.reload();
								},
								error : UI.AjaxErrorHandler
							});
						})(item);
					}
    		},
    		columns : [
	           {
				   id : 'iconInfo',
				   name : '',
				   width : '3%',
				   align : 'center',
				   render : function(value, row, vendor){
//					   temp
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
	        	   render : function(value, row, vendor, el, rows){
	        		   el.css('word-break','break-all');
	        		   if(row.isDeleted) {
	        			   return $('<span class="text-muted">').text(value);
	        		   } else {
		        		   var span = $('<span draggable="true">');
		        		   span.text(value);
		        		   span.addClass('pointer');
		        		   span.hover(function(){
		        			   span.css("color","#428BCA");
		        			   },function(){
	        				   span.css("color","black");
	        			   });
		        		   
		        		   if(row.isFile) {
		        			   var currentSequence;
		        			   var sequence = 0;
		        			   var data = [];
		        			   
		        			   for ( var idx in rows) {
		        				   if(rows[idx].isFile) {
		        					   var type;
			        				   
			        				   var downloadLink= api + History.getState().data.vendor +'/file/downloadFile.json';
			        				   if (rows[idx].id) {
			        					   downloadLink += '?id=' + rows[idx].id;
			        				   } else if (rows[idx].path) {
			        					   downloadLink += '?path=' + rows[idx].path;
			        				   }
			        				   
			        				   if(rows[idx].previewSupported) {
			        					   type = rows[idx].mimeType.startsWith('image') ? 'image' : 'iframe'; 
			        				   } else {
			        					   type = 'inline';
			        				   } 
			        				   
			        				   if(row == rows[idx]) {
			        					   currentSequence = sequence;
			        				   }
			        				   
			        				   if(type == 'inline') {
			        					   data.push({
			        						   type : type,
			        						   title : rows[idx].name,
			        						   downloadLink_href : downloadLink,
			        						   cloudVendor : vendor,
			        						   id : rows[idx].id,
			        						   path : rows[idx].path
			        					   });
			        				   } else {
			        					   data.push({
			        						   src : rows[idx].previewLink,
			        						   type : type,
			        						   title : rows[idx].name,
			        						   downloadLink_href : downloadLink,
			        						   cloudVendor : vendor,
			        						   id : rows[idx].id,
			        						   path : rows[idx].path
			        					   });
			        				   }
			        				   
			        				   sequence++;
		        				   }
		        			   }
		        			   
		        			   new UI.Preview({
	        					   title : value,
	        					   element : span,
	        					   data : data,
	        					   sequence : currentSequence
	        				   });
		        		   } else {
		        			   span.on('click', function(e){
		        				   e.stopPropagation();
		        				   var data = History.getState().data;
		        				   data.parentId = row.id;
		        				   data.parentPath = row.path;
		        				   History.pushState(data, window.title, History.getPageUrl()+encodeURIComponent(value));
		        			   });
		        		   }
	        		   }
	        		   
	        		   return span;
	        	   }
	           },
	           {
	        	   id : 'modified',
	        	   name : '수정된 날짜',
	        	   width : '20%',
	        	   align : 'center',
	        	   addClass : 'hidden-xs',
	        	   render : function(value, row){
	        		   if(value == '') {
//	        			   value = row.created;
	        		   }
	        		   return utils.convertTimestampToDate(value);
	        	   }
	           },
	           {
	        	   id : 'size',
	        	   name : '크기',
	        	   width : '20%',
	        	   align : 'center',
	        	   addClass : 'hidden-xs',
	        	   render : function(value, row){
	        		   if(value == undefined || value == '') {
	        			   return '';
	        		   }
	        		   return new Number(value).formatBytes();
	        	   }
	           }
           ]
    	});
		
		var moreButtons = new UI.MoreButtons({
			renderTo : '#dynamicButtons',
			events : {
				download : function(items){
					utils.list.download(items);
				},
				rename : function(items){
					utils.list.rename(items, function(){
	            		self.listStore.reload();
	            	});
				},
				remove : function(items){
					utils.list.remove(items, function(){
	            		self.listStore.reload();
	            	});
				},
				move : function(items){
					utils.list.move(items, function(){
	            		self.listStore.reload();
	            	});
				},
				copy : function(items){
					utils.list.copy(items, function(){
	            		self.listStore.reload();
	            	});
				}
			}
		});
		
		$(listGrid).on('checkbox.change', function(e, selectedItems){
			moreButtons.render(selectedItems);
		});
    },
	viewController : function(id){
    	$('[role=view]').hide();
    	$('[role=view]#'+id).show();
    },
    show : function(){
    	var self = this;
    	this.viewController(this.id);
    	var state = History.getState(),
    	data = state.data,
    	vendor = data.vendor;
//    	if($.isEmptyObject(data)) {
//    		History.pushState(data, title, url, queue)
//    	}
    	var path = $('#path');
    	path.empty();
//    	getting path from URL
    	var pathArray = decodeURIComponent(History.getState().cleanUrl).replaceAll(History.getRootUrl().slice(0,-1)+contextPath+"/", '').split('/');

    	var stackedPath = '';
    	for ( var idx in pathArray) {
    		stackedPath += "/" + pathArray[idx];
    		var folderName = pathArray[idx];
    		if(idx == 0) {
    			folderName = "Home";
    		} else if(idx == 1) {
    			folderName = clouds[pathArray[idx]].serviceName;
    		}
    		
    		var folerLink;
    		if(idx == pathArray.length-1) {
    			folerLink = $('<li class="active" role-path='+stackedPath+'>'+folderName+'</li>');
    		} else {
    			folerLink = $('<li role-path='+stackedPath+'><a style="cursor:pointer">'+folderName+'</a></li>');
    		}
    		
    		folerLink.appendTo(path);
    		folerLink.on('click', function(){
    			var folderPath = $(this).attr('role-path');
    			var state = History.extractState(History.getRootUrl().slice(0,-1)+contextPath+folderPath);
    			History.pushState(state.data, state.title, state.cleanUrl);
    		});
		}
    	$('#fileupload').fileupload('option', {
    		url : api + vendor + '/file/upload.json',
    		formData : data
    	});
    	
    	this.listStore.url = api + vendor + '/file/getFiles.json';
    	this.listStore.reload(data);
    }
		
});