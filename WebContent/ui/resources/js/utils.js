var utils = {};

utils.convertTimestampToDate = function(timestamp){
	return new Date(timestamp).format(baseDateFormat);
};

utils.noty = {
	error : function(text, wait){
//		$.pnotify({
//		    title: '에러',
//		    text: text,
//		    type: 'error',
//	    	sticker : false
//		});
		alertify.error(text, wait);
	},
	warn : function(text, wait){
		alertify.error(text, wait);
	},
	success : function(text, wait){
		alertify.success(text, wait);
	},
	info : function(text, wait){
		alertify.log(text, wait);
	},
};

utils.number_format = function ( number, decimals, dec_point, thousands_sep ) {
    // http://kevin.vanzonneveld.net
    // +   original by: Jonas Raoni Soares Silva (http://www.jsfromhell.com)
    // +   improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +     bugfix by: Michael White (http://crestidg.com)
    // +     bugfix by: Benjamin Lupton
    // +     bugfix by: Allan Jensen (http://www.winternet.no)
    // +    revised by: Jonas Raoni Soares Silva (http://www.jsfromhell.com)    
    // *     example 1: number_format(1234.5678, 2, '.', '');
    // *     returns 1: 1234.57     
 
    var n = number, c = isNaN(decimals = Math.abs(decimals)) ? 2 : decimals;
    var d = dec_point == undefined ? "," : dec_point;
    var t = thousands_sep == undefined ? "." : thousands_sep, s = n < 0 ? "-" : "";
    var i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
 
    return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
}

utils.bytesToSize = function (filesize) {
	if (filesize >= 1073741824) {
	     filesize = utils.number_format(filesize / 1073741824, 1, '.', '') + ' Gb';
	} else { 
		if (filesize >= 1048576) {
    		filesize = utils.number_format(filesize / 1048576, 1, '.', '') + ' Mb';
  	} else { 
			if (filesize >= 1024) {
   		filesize = utils.number_format(filesize / 1024, 0) + ' Kb';
 		} else {
   		filesize = utils.number_format(filesize, 0) + ' bytes';
			};
		};
	};
 return filesize;
};


utils.list = {
		download : function(items){
			if(items.length == 1) {
       		 var downloadURL= api + History.getState().data.vendor +'/file/downloadFile.json';
	       		if (items[0].id) {
	       			downloadURL += '?id=' + items[0].id;
	       		} else if (items[0].path) {
	       			downloadURL += '?path=' + items[0].path;
	       		}
	       		location.href = downloadURL;
	       	} else {
	       		var itemArray = [];
	       		var downloadURL= api + History.getState().data.vendor +'/file/downloadFiles.json';
	       		for ( var idx in items) {
						var item = items[idx];
						if (item.id) {
							itemArray.push(item.id);
	           		} else if (item.path) {
	           			itemArray.push(item.path);
	           		}
	       		}
	       		downloadURL += '?ids=' + itemArray.join(',');
	       		downloadURL += '&paths=' + itemArray.join(',');
	       		location.href = downloadURL;
	       	}
		},
		rename : function(items, callback){
         	var originalName = items[0].name;
     		
     		alertify.prompt('항목의 새 이름을 입력하세요.', function (e, newName) {
     		    if (e) {
					$.ajax({
						url: api + History.getState().data.vendor +'/file/rename.json',
						data : {
							name : newName,
							id : items[0].id,
							path: items[0].path
						},
						type : 'post',
						success: function(data) {
							utils.noty.success('\''+originalName+'\'이 \''+newName+ '\'(으)로 변경되었습니다.');
							if(callback instanceof Function) {
								callback.call(this, data);
							}
						},
						error : UI.AjaxErrorHandler
					});
     		    }
     		}, originalName);
     		
         },
         remove : function(items, callback){
	    	 alertify.confirm("해당 항목을 정말 삭제하시겠습니까?", function (e) {
	    		    if (e) {
	    		    	for ( var idx in items) {
							var item = items[idx];
							(function(item){
								$.ajax({
									url: api + History.getState().data.vendor +'/file/delete.json',
									data : {
										id : item.id,
										path: item.path
									},
									type : 'post',
									success: function(data) {
										utils.noty.success('\''+item.name+'\'이(가) 삭제되었습니다.');
										if(callback instanceof Function) {
											callback.call(this, data);
										}
									},
									error : UI.AjaxErrorHandler
								});
							})(item);
						}
	    		    }
    		});
         },
         move : function(items, callback){
        	 utils.list.copy_or_move(items, callback, 'move');
         },
         copy : function(items, callback){
        	 utils.list.copy_or_move(items, callback, 'copy');
         },
         copy_or_move : function(items, callback, mode){
        	var label = (mode == 'copy') ? '복사' : '이동';
        	 
			var targetFolderStore = new UI.Store({
				autoLoad : false,
				options : {
						parentId : 'root',
					parentPath : '/'
				}
			});
     		
     		var data = [];
     		
// 			var currentVendor =  History.getState().data.vendor;
// 			data.push({
// 				label : clouds[currentVendor].serviceName,
// 				value : currentVendor,
// 				vendor : currentVendor,
// 				children: [
// 		            {
// 		        	 label : 'loading...'  
// 		            }
// 	            ]
// 			});
     		
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
     		
     		var treeElement = $('<div>');
     		treeElement.tree({data: data});
     	    
     		treeElement.bind('tree.open', function(e) {
     	    	var selectedNode = e.node;
     	    	
     	    	var options = {
     					vendor : selectedNode.vendor
     			};
     	    	
     	    	targetFolderStore.url = api + selectedNode.vendor +'/folder/getFolders.json';
     	    	
     	    	if(selectedNode.id == undefined) {
     	    		options.parentId = 'root'; 
     	    		options.parentPath = '/'; 
     	    	} else {
     	    		options.parentId = selectedNode.id; 
     	    		options.parentPath = selectedNode.id; 
     	    	}
     	    	
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
     	        		            vendor: targetFolderStore.lastOptions.vendor
     	        			};
     	        			nodeData.push(node);
     	        		}
     	        		treeElement.tree('loadData', nodeData, selectedNode);
     				}
     			};
     			
     	    	targetFolderStore.reload(options);
     	    });
     		
     		bootbox.dialog({
     			message : treeElement,
     			title : label,
     			buttons: {
     				cancel: {
       			      label: "취소",
       			      className: "btn-default"
       			    },
     			    ok: {
     			      label: label,
     			      className: "btn-primary",
     			      callback: function() {
     			    	// 복사 or 이동할 object
     		     			var selectedNodes = treeElement.tree('getSelectedNodes');
     		     			if(selectedNodes.length == 0) {
     		     				utils.noty.error("복사할 대상의 폴더를 선택해주세요.");
     		     				return false;
     		     			}
     		     			
     		     			var selectedNode = selectedNodes[0];
 		 		     		for ( var idx in items) {
 		 						var item = items[idx];
 		 						(function(item){
// 		 							if(History.getState().data.vendor == selectedNode.vendor){
 		 								
 		 							// sendVendor 와 targetVendor 가 동일하면..
 	 		 							$.ajax({
 	 		 								url : api + History.getState().data.vendor +'/file/'+mode+'.json',
 	 		 								data : {
 	 		 									id : item.id,
 	 		 				     				path : item.path,
 	 		 				     				targetParentId : selectedNode.id || 'root',
 	 		 				          			targetParentPath : selectedNode.id || '',
 	 		 				          			targetVendor : selectedNode.vendor,
 	 		 				          			parentId : selectedNode.id || 'root',
	 		 				          			parentPath : selectedNode.id || ''
 	 		 								},
 	 		 								type: 'POST',
 	 		 								dataType: 'json',
 	 		 								success: function(data) {
 	 		 									utils.noty.success('\''+item.name+'\'이(가) '+selectedNode.name+'으(로) '+ label +' 되었습니다.');
 	 		 				     				if(callback instanceof Function) {
 	 		 				     					callback.call(this, data);
 	 		 				     				}
 	 		 								},
 	 		 								error : UI.AjaxErrorHandler
 	 		 							});
// 		 							}else{
// 		 								// sendVendor 와 targetVendor 가 상이할경우
//											$.ajax({
//	 	 		 								url : api + History.getState().data.vendor +'/file/other/'+mode+'.json',
//	 	 		 								data : {
//	 	 		 									id : item.id,
//	 	 		 				     				path : item.path,
//	 	 		 				     				vendor : History.getState().data.vendor,
//	 	 		 				     				targetParentId : selectedNode.id || 'root',
//	 	 		 				          			targetParentPath : selectedNode.id || '',
//	 	 		 				          			targetVendor : selectedNode.vendor
//	 	 		 								},
//	 	 		 								type: 'POST',
//	 	 		 								dataType: 'json',
//	 	 		 								success: function(data) {
//	 	 		 									utils.noty.success('\''+item.name+'\'이(가) '+selectedNode.name+'으(로) '+ label +' 되었습니다.');
//	 	 		 				     				if(callback instanceof Function) {
//	 	 		 				     					callback.call(this, data);
//	 	 		 				     				}
//	 	 		 								},
//	 	 		 								error : UI.AjaxErrorHandler
//	 	 		 							});
// 		 							}
 		 						})(item);
 		 					}
     			      }
     			    }
     			}
     		});
      },
      selectFile : function(items, callback){
    	  
    	  var fileStore = new UI.Store({
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
    	  
    	  var treeElement = $('<div>');
    	  treeElement.tree({data: data});
    	  
    	  treeElement.bind('tree.open', function(e) {
    	    	var selectedNode = e.node;
    	    	
    	    	var options = {
    					vendor : selectedNode.vendor
    			};
    	    	
    	    	fileStore.url = api + selectedNode.vendor +'/file/getFiles.json';
    	    	
    	    	if(selectedNode.id == undefined) {
    	    		options.parentId = 'root'; 
    	    		options.parentPath = '/'; 
    	    	} else {
    	    		options.parentId = selectedNode.id; 
    	    		options.parentPath = selectedNode.id; 
    	    	}
    	    	
    	    	options.callback = function(data){
    				if(data != undefined) {
    					var nodeData = [];
    	        		var rows = data['rows'];
    	        		for ( var idx in rows) {
    	        			var row = rows[idx];
    	        			if(row.isFile && !row.mimeType.startsWith('image/')) {
    	        				continue;
    	        			}
    	        			var children = [{label : 'loading...'}];
    	        			var iconClass = 'glyphicon glyphicon-folder-close';
    	        			if(row.isFile) {
    	        				children = [];
    	        				iconClass = '';
    	        			}
    	        			var node = {
    	        					label : row.name,
    	        					iconClass : iconClass,
    	        					value : row.name,
    	        					id : row.id || row.path,
    	        					children: children,
    	        		            vendor: fileStore.lastOptions.vendor,
    	        		            mimeType : row.mimeType,
    	        		            thumbnailLink : row.thumbnailLink
    	        			};
    	        			nodeData.push(node);
    	        		}
    	        		treeElement.tree('loadData', nodeData, selectedNode);
    				}
    			};
    			
    	    	fileStore.reload(options);
    	  });
    	  
    	  bootbox.dialog({
    		  message : treeElement,
    		  title : "클라우드로부터 사진 추가",
    		  buttons: {
    			  cancel: {
    				  label: "취소",
    				  className: "btn-default"
    			  },
    			  ok: {
    				  label: "선택",
    				  className: "btn-primary",
    				  callback: function() {
    					  var selectedNodes = treeElement.tree('getSelectedNodes');
    					  if(selectedNodes.length == 0) {
    						  utils.noty.error("업로드할 대상의 파일을 선택해주세요.");
    						  return false;
    					  }
    					  
    					  var selectedNode = selectedNodes[0];
    					  if(selectedNode.mimeType == undefined || !selectedNode.mimeType.startsWith('image/')) {
    						  utils.noty.error("이미지 파일을 선택해주세요.");
    						  return false;
    					  }
    					  
    					  if(callback instanceof Function) {
							  callback.call(this, selectedNode);
						  }
    				  }
    			  }
    		  }
    	  });
      }
}
