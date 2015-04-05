
UI = {
	version : 0.1
};

UI.Box = OOPS.extend({
	vendor : '',
	renderTo : '',
	active : false,
	contents : '',
	button : {},
	_constructor_: function(config) {
		$.extend(this, config);
		this.load();
	},
	load : function() {
		this.template = '';
		if(this.active) {
			this.template = this.getActiveTemplate();
		} else {
			this.template = this.getInactiveTemplate();
		}
		
		if(this.renderTo != '') {
			this.template.appendTo(this.renderTo);
		}
	}
});

UI.CloudBox = UI.Box.extend({
	percentage : 0,
	getInactiveTemplate : function(){
		var self = this;
		var container = $('<div class="col-md-4">');
		var thumbnail = $('<div class="thumbnail status">');
		$('<img src="'+contextPath+'/ui/resources/img/vendors/'+this.vendor+'_gray.png" alt="'+this.vendor+'">').appendTo(thumbnail);
		
		var caption = $('<div class="caption">');
		caption.html(this.contents);
		caption.appendTo(thumbnail);
		
		var buttonHolder = $('<div class="text-center"></div>');
		var button = $('<a class="btn '+this.button.type+' btn-lg" role-vendor="'+this.vendor+'">'+this.button.text+'</a>');
		button.appendTo(buttonHolder);
		buttonHolder.appendTo(thumbnail);
		
		button.on('click', function(){
			self.button.click.call(self);
		});
		
		
		thumbnail.appendTo(container);
		
		return container;
	},
	getActiveTemplate : function(){
		var self = this;
		var container = $('<div class="col-md-4">');
		var thumbnail = $('<div class="thumbnail status">');
		$('<img src="'+contextPath+'/ui/resources/img/vendors/'+this.vendor+'.png" alt="'+this.vendor+'">').appendTo(thumbnail);
		
		var caption = $('<div class="caption">');
		$('<h3 class="text-center">'+clouds[this.vendor].serviceName+'</h3>').appendTo(caption);
		$('<p>').html(this.contents).appendTo(caption);
		$('<div class="progress"><div class="progress-bar" role="progressbar" aria-valuenow="'+this.percentage+'" aria-valuemin="0" aria-valuemax="100" style="width: '+this.percentage+'%;"><span class="sr-only">'+this.percentage+'% Complete</span></div></div>').appendTo(caption);
		caption.appendTo(thumbnail);
		
		var buttonHolder = $('<div class="text-center"></div>');
		var button = $('<a class="btn '+this.button.type+' btn-lg" role-vendor="'+this.vendor+'">'+this.button.text+'</a>');
		button.appendTo(buttonHolder);
		buttonHolder.appendTo(thumbnail);
		
		button.on('click', function(){
			self.button.click.call(self);
		});
		
		
		thumbnail.appendTo(container);
		
		return container;
	}
});

UI.SocialBox = UI.Box.extend({
	getInactiveTemplate : function(){
		var self = this;
		var container = $('<div class="col-md-4">');
		var thumbnail = $('<div class="thumbnail status">');
		$('<img src="'+contextPath+'/ui/resources/img/vendors/'+this.vendor+'_gray.png" alt="'+this.vendor+'">').appendTo(thumbnail);
		
		var caption = $('<div class="caption">');
		caption.html(this.contents);
		caption.appendTo(thumbnail);
		
		var buttonHolder = $('<div class="text-center"></div>');
		var button = $('<a class="btn '+this.button.type+' btn-lg" role-vendor="'+this.vendor+'">'+this.button.text+'</a>');
		button.appendTo(buttonHolder);
		buttonHolder.appendTo(thumbnail);
		
		button.on('click', function(){
			self.button.click.call(self);
		});
		
		
		thumbnail.appendTo(container);
		
		return container;
	},
	getActiveTemplate : function(){
		var self = this;
		var container = $('<div class="col-md-4">');
		var thumbnail = $('<div class="thumbnail status">');
		$('<img src="'+contextPath+'/ui/resources/img/vendors/'+this.vendor+'.png" alt="'+this.vendor+'">').appendTo(thumbnail);
		
		var caption = $('<div class="caption">');
		$('<h3 class="text-center">'+socials[this.vendor].serviceName+'</h3>').appendTo(caption);
		$('<p>').html(this.contents).appendTo(caption);
		caption.appendTo(thumbnail);
		
		var buttonHolder = $('<div class="text-center"></div>');
		var button = $('<a class="btn '+this.button.type+' btn-lg" role-vendor="'+this.vendor+'">'+this.button.text+'</a>');
		button.appendTo(buttonHolder);
		buttonHolder.appendTo(thumbnail);
		
		button.on('click', function(){
			self.button.click.call(self);
		});
		
		
		thumbnail.appendTo(container);
		
		return container;
	}
});

UI.Store = OOPS.extend({
	url : '',
	root : 'rows',
	rows : '',
	loaded : false,
	data : '',
	type:'',
	traditional : false,
	global : false,
	totalCountProperty : 'totalCount',
	options : {
//		start : 0,
//		limit : 20
	},
	lastOptions : this.options,
	ajax: {},
	autoLoad : true,
	_constructor_ : function(config){
		$.extend(this, config);
		if (this.autoLoad){
			this.load.call(this, config);
		}
	},
	on : function(event, callback) {
		$(this).on(event, callback);
	},
	setUrl : function(url){
		this.url = url;
	},
	setData : function(data){
		this.data = data;
	},
	load : function(config){
		var self = this;
		
		if (config){
			$.extend(this.options, config.options);
			this.lastOptions = this.options;
		}
		
			if (!self.loaded) {
				
				var params = self.options || {};
				var options = {
					type: self.type || "POST",
					dataType : 'json',
					url: self.url,
					data : params,
					traditional : self.traditional,
					success: function(data){
						self.data = data;
						self.rows = data[self.root] || [];
						self.vendor = data.vendor;
						self.loaded = true;
						if ($.isFunction(self.options.callback)){
							self.options.callback.call(self, data);
						}
						$(self).trigger('load', [self, data]);
					},
					error: UI.AjaxErrorHandler,
					complete : function(xhr, status){
					},
					global : self.global
				};
				
				$.ajax(options);
				
			} 
	},
	reload : function(options){
		this.loaded = false;
		this.load({
			options : options
		});
	},
	getUrl : function(){
		return this.url;
	},
	getRoot : function(){
		return this.root;
	},
	getRows : function(){
		return this.rows;
	},
	getData : function(){
		return this.data;
	},
	getVendor : function(){
		return this.vendor;
	},
	getTotalCount : function(){
		return this.data[this.totalCountProperty];
	},
	getLimit : function(){
		return this.options['limit'];
	}
});

UI.Grid = OOPS.extend({
	store : '',
	renderTo : '',
	columns : '',
	buttons : [],
	scrollPaging : false,
	checkable : true,
	transObject : '',
	noRecordsMsg : '이 폴더는 비어있습니다.',
	_constructor_ : function(config){
		var self = this;
		$.extend(this, config);
		this.loaded = false;
//		if(!this.store.autoLoad){
//			self.render(null);
//		}
		$(this.store).on('load', function(e, store){
			self.render(store);
		});
	},
	getSelectedItems : function(){
		var self = this;
		var rows = this.store.getRows();
		var selected = [];
		var checkedItems = $(self.renderTo + ' input:checked');
		for(var i=0; i < checkedItems.length ; i++){
			if (i == 0 && 'on' == checkedItems[i].value) continue;
			selected.push(rows[checkedItems[i].value]);
		}
		return selected;
	},
	setColumns : function(columns){
		this.columns = columns;
	},
	render : function(store){
		var self = this;
		var rows = store ? store.getRows() : [];
		rows = rows || [];
		
		var table = $('<table class="table table-hover">');
		
		var thead = $('<thead>');
		thead.appendTo(table);
		
		var tbody = $('<tbody>');
		tbody.appendTo(table);
		if(this.scrollPaging && this.loaded) {
			tbody = this.tbody;
		}
		this.tbody = tbody;
		
		
		
		var tableMenu = $('<tr id="tableMenu" class="active">');
		tableMenu.appendTo(thead);
		
		if (this.checkable){
			var checkTh = $('<th width="3%">');
			checkTh.appendTo(tableMenu);
			var label = $('<label>');
			label.appendTo(checkTh);
			var allCheckbox = $('<input type="checkbox">');
			allCheckbox.appendTo(label);
			
			allCheckbox.on('click', function(){
				if(allCheckbox.is(":checked")) {
					$('tr', tbody).addClass('success');
					$("input:checkbox", self.renderTo).each(function() {
						$(this).prop('checked', true).change();
					});
				} else {
					$('tr', tbody).removeClass('success');
					$("input:checkbox", self.renderTo).each(function() {
						$(this).prop('checked', false).change();
					});
				}
			});
		}
		
		for(var index in this.columns){
			var column = this.columns[index];
			var th = $('<th width="'+column.width+'">');
			th.css('text-align', 'center');
			th.text(column.name).appendTo(tableMenu);
			if (column.addClass){
				th.addClass(column.addClass);
			}
		}
		
		if (rows.length == 0){
			
			if(this.scrollPaging && this.loaded) {
				return;
			}
			var div = $("<div>");
			div.css({
				position:"absolute",
				textAlign:'center',
				padding:"40px 0 5px",
				left:"0px",
				top:"50px",
				width:"100%",
				height:"100px"
				
				});
			div.html('<strong>'+this.noRecordsMsg+'</strong>');
			div.appendTo(tbody);
		} else {
			for(var index in rows){
				var row = rows[index];
				var tr = $("<tr data-id=" + row.id+ " is-file="+ row.isFile +">");
	            tr.attr('draggable', 'true');
	            
				tr.appendTo(tbody);
				tr.on('click', function(e){
					if($(e.target).children().is("input:checkbox")) return;
					
					if(!e.metaKey) {
						$('tr', tbody).not($(this)).removeClass('success');
						$("input:checkbox", self.renderTo).each(function() {
							$(this).prop('checked', false).change();
						});
					};
					
					if($(this).hasClass('success')) {
						$(this).removeClass('success');
						$(':checkbox', $(this)).prop('checked', false).change();
					} else {
						$(this).addClass('success');
						$(':checkbox', $(this)).prop('checked', true).change();
					}
				});
				tr.on('contextmenu', function(e){
					if(!$(this).hasClass('success')) {
						$(this).addClass('success');
						$(':checkbox', $(this)).prop('checked', true).change();
					}
				});
				
				if (this.checkable){
					var td = $('<td class="inputCheck" style="width: 3%">');
					var checkbox = $('<input type="checkbox" value="'+index+'">');
					checkbox.appendTo(td);
					td.appendTo(tr);
					
					checkbox.on('click', function(e){
						var tr = $(this).closest('tr');
						if($(this).is(':checked')) {
							if(!tr.hasClass('success')) {
								tr.addClass('success');
							}
						} else {
							tr.removeClass('success');
						}
						e.stopPropagation();
					});
					
					checkbox.change(function(){
						$(self).trigger('checkbox.change',[self.getSelectedItems()]);
					});
					
					td.on('click', function(){
						$(this).find(':checkbox').trigger('click');
					});
				}
				
				for(var index in this.columns){
					var column = this.columns[index];
					
					var td = $("<td>");
					if (column.align){
						td.css('text-align', column.align);
					}
					if (column.width){
						td.css('width', column.width);
					}
					if (column.addClass){
						td.addClass(column.addClass);
					}
					
					var value = row[column.id];
					if (column.render instanceof Function){
						value = column.render.call(this, value, row, store.getVendor(), td, rows);
					}
					
					if (value instanceof Object){
						if ($.isFunction(value.appendTo)){
							value.appendTo(td);	
						} else {
							td.html(value+'');	
						}
						
					} else {
						td.html(value+'');
					}
					
					td.appendTo(tr);
					
				}
				
				//tr.wrapInner( "<div draggable='true'></div>" );
				tr.on({
//					dragstart :  function(event){
//						var target =  $('input:checked');
//						target.siblings().removeClass('success');
//						target.addClass('success');
//						var activeTr = $(this).parent('tbody').children('tr');
//						
//					    
//
//						$.each(activeTr, function(idx){
//							var isFile = rows[idx].isFile;
//							if(!isFile){
//								$(this).addClass('active');
//							}
//						});
//					},
//					dragenter : function(event){
//						event.preventDefault();
//						var e = event.originalEvent;
////					    var tooltip = $('<div><p>abc</p></div>');
////					    console.log('tooltip : ', tooltip);
////					    $('table').append(tooltip);
////					    tooltip.css({top : e.x , left : e.y, position : absolute});
//						var activeTr = $(this).parent('tbody').children('tr');
//						var files = event.originalEvent.dataTransfer.files;
//						$.each(activeTr, function(idx){
//							var isFile = rows[idx].isFile;
//							if(!isFile){
//								$(this).addClass('active');
//							}
//						});
//						$(this).addClass('danger');
//						$(this).siblings().removeClass('danger').popover('hide');
//						var targetIdx = $('tbody[data-target="#context-menu"] > tr').index($('tbody > tr.danger'));
//						if(rows[targetIdx].isFile){
//							$(this).removeClass('danger');
//						}
//						console.log('dragenter');
//					},
//					dragleave : function(event){
//						event.preventDefault();
//						$(this).removeClass('danger');
//					},
					drop : function(event){
						event.stopPropagation();
						event.preventDefault();
						var files = event.originalEvent.dataTransfer.files;
						var targetIdx = $('tbody[data-target="#context-menu"] > tr').index($('tbody > tr.danger'));
						var sendList = $('tbody > tr.success');
						$('tbody > tr').removeClass('active').removeClass('danger');
						var activeTr = $(this).parent('tbody').children('tr');
						$.each(activeTr, function(idx){
							$(this).removeClass('active');
						});
						var sendObj = new Array();
						console.log('targetIdx : ', targetIdx);
						if(targetIdx == -1){ // 파일일경우
							
							if(files.length){ // 파일 있으면 현재 경로에 업로드
								for(var i=0; i < files.length; i++){
									self.uploadFile(event, files[i], '');
								}
							}
//							else{ //파일없으면(드래그앤 드롭 일 경우) 노티
//								utils.noty.error('폴더를 선택하세요.');
//							}
						}else{ // 폴더에 파일 업로드
							if(files.length){ // 파일일 경우 폴더에 업로드
								for(var i=0; i < files.length; i++){
									self.uploadFile(event, files[i], rows[targetIdx]);
								}
							}else{ // 내부로 파일 이동
								for(var i=0; i < sendList.length; i++){
									var sendIdx = $('tbody > tr').index(sendList[i]);
									sendObj.push(rows[sendIdx]);
								}
								self.onDrop(sendObj, rows[targetIdx]);
							}
						}
						
					}
				});
				
			}
		}
		
		if(!this.scrollPaging || !this.loaded) {
			$(this.renderTo).html(table);
		}
		
		table.floatThead({
			scrollContainer: function(table){
				return table.closest(self.renderTo);
			}
		});
		
		if(this.contextMenu instanceof UI.ContextMenu) {
			tbody.contextmenu({
		        target: this.contextMenu.target,
		        before: function(e) { 
		        	
		        	var ul = this.getMenu().find("ul");
		        	ul.empty();
		        	
		        	var download = $('<li style="cursor:pointer;"><a class="download"><span class="glyphicon glyphicon-cloud-download"></span>&nbsp;&nbsp;다운로드</a></li>');
		        	var rename = $('<li style="cursor:pointer;"><a class="rename"><span class="glyphicon glyphicon-edit"></span>&nbsp;&nbsp;이름 변경</a></li>');
		        	var remove = $('<li style="cursor:pointer;"><a class="remove"><span class="glyphicon glyphicon-minus-sign"></span>&nbsp;&nbsp;삭제</a></li>');
		        	var move = $('<li style="cursor:pointer;"><a class="move"><span class="glyphicon glyphicon-arrow-right"></span>&nbsp;&nbsp;이동</a></li>');
		        	var copy = $('<li style="cursor:pointer;"><a class="copy"><span class="glyphicon glyphicon-transfer"></span>&nbsp;&nbsp;복사</a></li>');
		        	
		        	var selectedItems = self.getSelectedItems();
		        	
		        	var hasFolder = false;
	        		for ( var idx in selectedItems) {
	    				var selectedItem = selectedItems[idx];
	    				if(!selectedItem.isFile){
	    					hasFolder = true;
	    				}
	    			}
		        	if(!hasFolder) {
		        		this.getMenu().find("ul").append(download);
		        	}
		        	
		        	if(selectedItems.length == 1) {
		        		this.getMenu().find("ul").append(rename);
		        	}
		        	
		        	this.getMenu().find("ul").append(remove);
		        	this.getMenu().find("ul").append(move);
		        	this.getMenu().find("ul").append(copy);
		        	
		        	return true;
	            },
		        onItem: function(e, item) {
		        	if($(item).hasClass('download')) {
		        		self.contextMenu.addEvent.download.call(self, self.getSelectedItems());
		        	} else if($(item).hasClass('rename')) {
		        		self.contextMenu.addEvent.rename.call(self, self.getSelectedItems());
		        	} else if($(item).hasClass('remove')) {
		        		self.contextMenu.addEvent.remove.call(self, self.getSelectedItems());
		        	} else if($(item).hasClass('move')) {
		        		self.contextMenu.addEvent.move.call(self, self.getSelectedItems());
		        	} else if($(item).hasClass('copy')) {
		        		self.contextMenu.addEvent.copy.call(self, self.getSelectedItems());
		        	};
		        }
		     });
		}
		this.loaded = true;
	}
});

UI.ContextMenu = OOPS.extend({
	target : '',
	_constructor_ : function(config){
		var self = this;
		$.extend(this, config);
	}
});

UI.SearchGrid = OOPS.extend({
	stores : [],
	renderTo : '',
	columns : '',
	checkable : '',
	buttons : [],
	checkable : true,
	noRecordsMsg : 'No Data found',
	_constructor_ : function(config){
		var self = this;
		$.extend(this, config);
//		if(!this.store.autoLoad){
//			self.render(null);
//		}
		
		if(this.stores == '') {
			var holder = $('<div class="text-center" style="margin-top:60px;">');
			
			var h1 = $('<h1>');
			h1.text('등록된 클라우드가 존재하지 않습니다.');
			h1.appendTo(holder);
			
			$('<h4><a href="'+contextPath+'">여기</a>로 이동하여 클라우드를 등록 할 수 있습니다.</h4>').appendTo(holder);
			
			$(this.renderTo).html(holder);
		} else {
			for ( var idx in this.stores) {
				var store = this.stores[idx];
				$(store).on('load', function(e, store){
					self.render(store);
				});
			}
		}
	},
	setColumns : function(columns){
		this.columns = columns;
	},
	rendered : false,
	render : function(store){
		var self = this;
		var rows = store ? store.getRows() : [];
		rows = rows || [];
		
		if(this.rendered) {
			for(var index in rows){
				var row = rows[index];
				
				var tr = $("<tr>");
				tr.appendTo(self.tbody);
				
				if (this.checkable){
					$('<td style="width: 3%"><label><input type="checkbox"></label></td>').appendTo(tr);
				}
				
				for(var index in this.columns){
					var column = this.columns[index];
					
					var td = $("<td>");
					if (column.align){
						td.css('text-align', column.align);
					}
					if(column.addClass){
						td.addClass(column.addClass);
					}
					var value = row[column.id];
					if (column.render instanceof Function){
						value = column.render.call(this, value, row, store.getVendor(), td);
					}
					
					if (value instanceof Object){
						if ($.isFunction(value.appendTo)){
							value.appendTo(td);	
						} else {
							td.html(value+'');	
						}
						
					} else {
						td.html(value+'');
					}
					
					td.appendTo(tr);
				}
			}
			return;
		}
		
		var table = $('<table class="table table-hover">');
		
		var thead = $('<thead>');
		thead.appendTo(table);
		
		var tableMenu = $('<tr id="tableMenu" class="active">');
		tableMenu.appendTo(thead);
		
		if (this.checkable){
			var checkTh = $('<th width="3%">');
			checkTh.appendTo(tableMenu);
			var label = $('<label>');
			label.appendTo(checkTh);
			var allCheckbox = $('<input type="checkbox">');
			allCheckbox.appendTo(label);
			
			allCheckbox.on('click', function(){
				if(allCheckbox.is(":checked")) {
					$("input:checkbox", self.renderTo).each(function() {
						this.checked = true;
					});
				} else {
					$("input:checkbox", self.renderTo).each(function() {
						this.checked = false;
					});
				}
			});
		}
		
		for(var index in this.columns){
			var column = this.columns[index];
			var th = $('<th width="'+column.width+'">');
			th.css('text-align', 'center');
			th.text(column.name).appendTo(tableMenu);
			if(column.addClass){
				th.addClass(column.addClass);
			}
		}
		
		var tbody = $('<tbody>');
		this.tbody = tbody;
		tbody.appendTo(table);
		for(var index in rows){
			var row = rows[index];
			
			var tr = $("<tr>");
			tr.appendTo(tbody);
			
			if (this.checkable){
				$('<td style="width: 3%"><label><input type="checkbox"></label></td>').appendTo(tr);
			}
			
			for(var index in this.columns){
				var column = this.columns[index];
				
				var td = $("<td>");
				if (column.align){
					td.css('text-align', column.align);
				}
				if (column.width){
					td.css('width', column.width);
				}
				if(column.addClass){
					td.addClass(column.addClass);
				}
				
				var value = row[column.id];
				if (column.render instanceof Function){
					value = column.render.call(this, value, row, store.getVendor(), td);
				}
				
				if (value instanceof Object){
					if ($.isFunction(value.appendTo)){
						value.appendTo(td);	
					} else {
						td.html(value+'');	
					}
					
				} else {
					td.html(value+'');
				}
				
				td.appendTo(tr);
			}
		}
		
		$(this.renderTo).html(table);
		
		table.floatThead({
			scrollContainer: function(table){
				return table.closest(self.renderTo);
			}
		});
		
		this.rendered = true;
	}
});

UI.FormDialog = OOPS.extend({
	title : '',
	columns : [],
	buttons : [],
	_constructor_ : function(config){
		$.extend(this, config);
		this.form = $('<form role="form">');
	},
	render : function(){
		var self = this;
		
		for ( var idx in this.columns) {
			var column = this.columns[idx];
			var group  = $('<div class="form-group">');
			group.appendTo(self.form);
			var label = $('<label for="'+column.id+'">');
			label.text(column.label);
			label.appendTo(group);
			var input = $('<input class="form-control">');
			input.appendTo(group);
			input.attr('type', column.type);
			input.attr('placeholder', column.placeholder);
			input.attr('name', column.name);
		}
		
		var dialogButtons = {};
		for ( var idx in this.buttons) {
			var button = this.buttons[idx];
			dialogButtons[idx] = button;
		}
		
		bootbox.dialog({
			message : self.form,
			title : self.title,
			buttons : dialogButtons 
		});
	},
	getForm : function(){
		return this.form;
	},
	show : function(){
		this.render();
	},
	close : function(){
		bootbox.hideAll();
	}
});

UI.AjaxErrorHandler = function(xhr, status, error){
	var result = $.parseJSON(xhr.responseText);
	if(xhr.status == 406) {
		if(result['redirect'] != '') {
			location.href = result['redirect'];
		} else {
			location.reload();
		}
	}
	
	if(xhr.status == 401){
		var message = result.vendor+' 인증이 더 이상 유효하지 않습니다.<br/>' + 
					'재인증을 하시려면 재인증을 클릭 해주세요. 인증취소를 할 경우 '+result.vendor+' 서비스를 이용하실 수 없습니다.';
		bootbox.dialog({
			message : message,
			title : '재인증 요청',
			buttons : {
				success: {
					label: "재인증",
					className: "btn-success",
					callback: function() {
						location.href = api + result.vendor + '/authorize.do';
					}
				},
				cancel: {
					label: "인증취소",
					className: "btn-danger",
					callback: function() {
						location.href = api + result.vendor + '/unauthorize.do';
						//need to redirect current view 
					}
				}
			}
		});
		
	}else{
		utils.noty.error(result.message);
	}
};

UI.MoreButtons = OOPS.extend({
	renderTo : '',
	_constructor_ : function(config){
		var self = this;
		$.extend(this, config);
	},
	evnets : {},
	render : function(items){
		var self = this;
		if(items.length < 1) {
	    	$(this.renderTo).html('');
		} else {
			var buttonGroup = $('<div class="btn-group">');

//			$('<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-list"></span> 더보기 <span class="caret"></span></button>').appendTo(buttonGroup);
			$('<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"> 더보기 <span class="caret"></span></button>').appendTo(buttonGroup);
			    
			var ul = $('<ul class="dropdown-menu" role="menu" style="z-index:9999;">');
			ul.appendTo(buttonGroup);
			
	    	var download = $('<li style="cursor:pointer;"><a class="download"><span class="glyphicon glyphicon-cloud-download"></span>&nbsp;&nbsp;다운로드</a></li>');
        	var rename = $('<li style="cursor:pointer;"><a class="rename"><span class="glyphicon glyphicon-edit"></span>&nbsp;&nbsp;이름 변경</a></li>');
        	var remove = $('<li style="cursor:pointer;"><a class="remove"><span class="glyphicon glyphicon-minus-sign"></span>&nbsp;&nbsp;삭제</a></li>');
        	var move = $('<li style="cursor:pointer;"><a class="move"><span class="glyphicon glyphicon-arrow-right"></span>&nbsp;&nbsp;이동</a></li>');
        	var copy = $('<li style="cursor:pointer;"><a class="copy"><span class="glyphicon glyphicon-transfer"></span>&nbsp;&nbsp;복사</a></li>');
	    	
	    	download.on('click', function(){
	    		self.events.download.call(self, items);
	    	});
	    	rename.on('click', function(){
	    		self.events.rename.call(self, items);
	    	});
	    	remove.on('click', function(){
	    		self.events.remove.call(self, items);
	    	});
	    	move.on('click', function(){
	    		self.events.move.call(self, items);
	    	});
	    	copy.on('click', function(){
	    		self.events.copy.call(self, items);
	    	});
	    	
	    	var hasFolder = false;
			for ( var idx in items) {
				var item = items[idx];
				if(!item.isFile){
					hasFolder = true;
				}
			}
	    	if(!hasFolder) {
	    		ul.append(download);
	    	}
	    	
	    	if(items.length == 1) {
	    		ul.append(rename);
	    	}
	    	
	    	ul.append(remove);
	    	ul.append(copy);
	    	ul.append(move);
	    	
	    	$(this.renderTo).html(buttonGroup);
		}
		
	}
});

UI.VendorInfoBox = OOPS.extend({
	vendor : '',
	userName : '',
	serviceName : '',
	description : '',
	settingsUrl : '',
	button : {},
	_constructor_: function(config) {
		$.extend(this, config);
	},
	getElement : function(){
		var self = this;
		var container = $('<div class="container" style="margin:15px 0; border:2px solid gray; border-radius:10px;">');
		
		var imageHolder = $('<div class="col-md-3 thumbnail" style="border:none; margin-top:30px;">');
		imageHolder.appendTo(container);
		
		var image = $('<img src="'+contextPath+'/ui/resources/img/vendors/'+this.vendor+'.png" alt="'+this.vendor+'"/>');
		image.appendTo(imageHolder);
		
		var body = $('<div class="col-md-9">');
		body.appendTo(container);
		
		var contents = $('<div style="margin: 20px;">');
		contents.appendTo(body);
		$('<h3 class="media-heading">'+this.serviceName+'</h3><br/>').appendTo(contents);
		$('<p>'+this.description+'</p>').appendTo(contents);
		
		$('<hr/>').appendTo(contents);
		
		var button = $('<a class="pull-right btn btn-danger btn-lg">'+this.button.text+'</a>');
		button.appendTo(contents);
		button.on('click', function(){
			self.button.click.call(self);
		});
		
		$('<p>&nbsp;이름 : ' + this.userName + '</p>').appendTo(contents);
		
		var link = $('<button type="button" class="btn btn-link"><span class="glyphicon glyphicon-cog">&nbsp;</span>설정</button>');
		link.appendTo(contents);
		link.on('click', function(){
			window.open(self.settingsUrl);
		});
		
		return container;
	}
});

UI.PagingBar = OOPS.extend({
	pageSize : 20,
	store : '',
	renderTo : '',
	currentPage : 1,
	maxVisible : 5,
	start : 1,
	_constructor_  : function(config){
		var self = this;
		$.extend(this, config);
		$(this.store).on('load', function(e, store){
			self.render(store);
		});
	},
	render : function(store){
		var self = this;
		if(store == null) {
			store = self.store;
		}
		
		var pagingTotalCount = Math.ceil(store.getTotalCount()/store.lastOptions.rowCount);
		pagingTotalCount = (pagingTotalCount < 1) ? 1 : pagingTotalCount;
		
		var pagingBar = $('<div class="col-md-12 text-center">');
		pagingBar.attr('id', this.id || 'table-paging');
		
		var refresh = $('<span class="glyphicon glyphicon-refresh" style="cursor:pointer; top:-30px; right:20px;">');
		refresh.appendTo(pagingBar);
		refresh.on('click', function(e){
			store.reload();
		});
		
		pagingBar.bootpag({
			total: pagingTotalCount,
			page: self.currentPage,
			maxVisible: self.maxVisible
		}).on("page", function(event, num){
			self.currentPage = num;
//			store.reload({offset:(num-1)*store.getLimit()});
			store.reload({
				fisrtKey : store.getData().lastKey
			});
		});
		$(this.renderTo).html(pagingBar);
	}
	
});

UI.PostFormDialog = UI.FormDialog.extend({
	title : '',
	formId : '',
	vendor : '',
	buttons : [],
	_constructor_ : function(config){
		$.extend(this, config);
		this.form = $('<form role="form" id="'+this.formId+'">');
	},
	render : function(){
		var self = this;
		var body = '<textarea name="contents" class="form-control" rows="3" placeholder="'+socials[this.vendor].serviceName+'에 등록할 수 있습니다." autocomplete="off"></textarea>'
				+	'<br/>'
				+	'<div id="preview" class="thumbnail" style="position:relative; height: 100px; width: 100px; display:none;">'
				+		'<img id="addedFile" style="position:absolute; height:100%; width:100%; left: 0px; top: 0px;"/>'
				+		'<span id="cancelFile" class="glyphicon glyphicon-remove-circle pointer" style="position:absolute; right: -12px; top: -12px; height: 20px; width: 20px;"></span>'
				+	'</div>'
				+	'<br/>'
				+	'<div class="clearfix">'
				+		'<div class="pull-left">'
				+			'<span class="btn btn-default fileinput-button">'
				+				'<i class="glyphicon glyphicon-camera"></i> <span>사진 추가</span>'
				+				'<input id="addFile" type="file" name="file">'
				+			'</span>'
				+		'</div>'	
				+		'<div class="pull-left">'
				+			'&nbsp;&nbsp;'
				+ 			'<span id="selectFile" class="btn btn-default">'
	  			+				'<i class="glyphicon glyphicon-cloud-download"></i> <span>사진 추가</span>'
	  			+			'</span>'
	  			+		'</div>'
	  			+	'</div>';
		self.form.html(body);
		
		$("#addFile", self.form).change(function() {
			if (this.files && this.files[0]) {
				if(!this.files[0].type.startsWith('image/')) {
					utils.noty.error('이미지 파일을 추가해주세요.');
					$('#preview', self.form).hide();
			    	$('#addFile', self.form).val('');
			    	self.form.removeData();
				} else {
					var reader = new FileReader();
					reader.onload = function(e) {
						$('#addedFile', self.form).attr('src', e.target.result);
						$('#preview', self.form).show();
					};
					reader.readAsDataURL(this.files[0]);
				}
		   }
		});
		
		$('#selectFile', self.form).on('click', function(){
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
					$('#addedFile', self.form).attr('src', node.thumbnailLink);
				} else {
					$('#addedFile', self.form).attr('src', window.contextPath + '/api/' + node.vendor + '/file/getThumbnail.jpg?path=' + node.id);
				}
				self.form.data({cloudVendor : node.vendor});
				self.form.data({id : node.id});
				$('#preview', self.form).show();
				$('#preview', self.form).show();
			});
		});
		
		var dialogButtons = {};
		for ( var idx in this.buttons) {
			var button = this.buttons[idx];
			dialogButtons[idx] = button;
		}
		
		bootbox.dialog({
			message : self.form,
			title : self.title,
			buttons : dialogButtons 
		});
	}
});

UI.Preview = OOPS.extend({
	title : '',
	buttons : [],
	element : '',
	data : [],
	key : 'preview',
	sequence : '',
	gallery : true,
	_constructor_ : function(config){
		$.extend(this, config);
		this.init();
	},
	postData : {},
	init : function(){
		var self = this;
		this.element.magnificPopup({ 
			   key: this.key, 
			   items: this.data,
			   preloader: true,
			   gallery: {
				   enabled: this.gallery 
			   },
			   overflowY : 'scroll',
			   closeOnBgClick : false,
//			   showCloseBtn : true,
			   inline: {
			     // Define markup. Class names should match key names.
			     markup: '<div>'+
			     			'<div class="col-md-offset-2 col-md-8 text-center">'+
			     				'<div class="mfp-close" style="color:#fff;"></div>'+
							     '<h4><span class="mfp-title" style="padding : 0px;"></span></h4>'+
							     '<br/><span class="text-muted">미리보기를 지원하지 않는 형식의 파일 입니다.</span>'+
							     '<br/><br/><br/><a role="button" class="btn btn-primary mfp-downloadLink"><span class="glyphicon glyphicon-cloud-download"></span> 다운로드</a>'+
						     '<div/>'+
						     '<br/><br/><div class="text-center">'+
						     	'<span class="mfp-counter text-center" style="position:relative;"></span>'+
					     	'</div>'+
			             '</div>'
			   },
			   image: {
				   markup:  '<div class="mfp-figure">'+
			             		'<div class="mfp-close"></div>'+
			             		'<div class="mfp-img"></div>'+
			             		'<div class="mfp-bottom-bar">'+
				             		'<div class="mfp-title"></div>'+
				             		'<div class="mfp-counter"></div>'+
			             		'</div>'+
				           '</div>',// Popup HTML markup. `.mfp-img` div will be replaced with img tag, `.mfp-close` by close button
				   cursor: null, // Class that adds zoom cursor, will be added to body. Set to null to disable zoom out cursor. 
//				   cursor: 'mfp-zoom-out-cur', // Class that adds zoom cursor, will be added to body. Set to null to disable zoom out cursor. 
				   
				   titleSrc: 'title', // Attribute of the target element that contains caption for the slide.
				   // Or the function that should return the title. For example:
				   // titleSrc: function(item) {
				   //   return item.el.attr('title') + '<small>by Marsel Van Oosten</small>';
				   // }

				   verticalFit: true, // Fits image in area vertically

				   tError: '<a href="%url%">현재 이미지</a>를 불러올 수 없습니다.' // Error message
//					   tError: '<a href="%url%">The image</a> could not be loaded.' // Error message
			   },
			   iframe: {
				   markup: '<div class="mfp-iframe-scaler">'+
				             '<div class="mfp-close"></div>'+
				             '<iframe class="mfp-iframe" frameborder="0" allowfullscreen></iframe>'+
				           '</div>', // HTML markup of popup, `mfp-close` will be replaced by the close button

				   patterns: {
				     youtube: {
				       index: 'youtube.com/', // String that detects type of video (in this case YouTube). Simply via url.indexOf(index).

				       id: 'v=', // String that splits URL in a two parts, second part should be %id%
				       // Or null - full URL will be returned
				       // Or a function that should return %id%, for example:
				       // id: function(url) { return 'parsed id'; } 

				       src: '//www.youtube.com/embed/%id%?autoplay=1' // URL that will be set as a source for iframe. 
				     },
				     vimeo: {
				       index: 'vimeo.com/',
				       id: '/',
				       src: '//player.vimeo.com/video/%id%?autoplay=1'
				     },
				     gmaps: {
				       index: '//maps.google.',
				       src: '%id%&output=embed'
				     }

				     // you may add here more sources

				   },

				   srcAction: 'iframe_src', // Templating object key. First part defines CSS selector, second attribute. "iframe_src" means: find "iframe" and set attribute "src".
			   },
			   callbacks: {
				   markupParse: function(template, values, item) {
			       // optionally apply your own logic - modify "template" element based on data in "values"
//			        console.log('Parsing:', template, values, item);
				   },
				   beforeOpen: function() {
					   if(self.sequence != '') {
						   $.magnificPopup.instance.goTo(self.sequence);
					   }
				   },
				   open: function() {
					   
					   if(this.currItem.data.type == 'image') {
						   this.container.append(self.getSocialListEl());
					   }
				   },
				   change: function() {
					   $('.social-list img').popover('show');
					   $('.social-list img').popover('toggle');
					   $('.social-list').remove();
					   
					   if(this.currItem.data.type == 'image') {
						   self.postData.id = this.currItem.data.id;
						   self.postData.path = this.currItem.data.path;
						   self.postData.cloudVendor = this.currItem.data.cloudVendor;
						   this.container.append(self.getSocialListEl());
					   }
				   }
			   }
		});
	},
	getSocialListEl: function(){
		var self = this;
		var socialList = $('<div class="social-list"></div>');
	   for ( var idx in activeSocialList) {
		   var activeSocial = activeSocialList[idx];
		   var img = $('<img data-vendor="'+activeSocial+'" class="pointer" style="padding:10px;" src="'+contextPath+'/ui/resources/img/vendors/'+activeSocial+'_29.png"/>');
		   img.appendTo(socialList);
		   
		   img.popover({
			   placement : 'bottom',
			   html : true, 
			   title : socials[activeSocial].serviceName + '로 사진 공유',
			   content : self.getPostBody(activeSocial) 
		   });
		   
		   img.on('click', function (e) {
			    $('img').not(this).popover('show');
			    $('img').not(this).popover('toggle');
			    
			    $('.post_button').on('click', function(){
			    	self.post($(this).attr('data-vendor'), $(this).siblings('textarea'));
			    });
			    $('.post_cancel_button').on('click', function(){
			    	$('.social-list img').popover('show');
					$('.social-list img').popover('toggle');
					$(this).siblings('textarea').val('');
			    });
			});
		   
//			   img.on('shown.bs.popover', function () {
//				   $('.social-list img').popover('hide');
////				   self.postData.vendor = $(this).attr('data-vendor');
//			   });
	   }
	   return socialList;
	},
	getPostBody : function(vendor){
		var self = this;
		var holder = $('<form id="'+vendor+'_form" role="form" enctype="multipart/form-data" action="javascript:;" method="post" accept-charset="utf-8">');
		var textarea = $('<textarea name="contents" class="form-control" rows="3" placeholder="'+socials[vendor].serviceName+'에 등록할 수 있습니다." autocomplete="off"; value="' + History.getRootUrl() + '"></textarea>');
		holder.append(textarea);
		holder.append('<br/>');
		var post = $('<button data-vendor="'+vendor+'" class="btn btn-success pull-right post_button ladda-button" data-style="zoom-out" style="margin-right:10px;"><span class="glyphicon glyphicon-edit"></span>&nbsp;작성</button>');
		var cancel = $('<button class="btn btn-default pull-right post_cancel_button">취소</button>');
		holder.append(cancel);
		holder.append(post);
		holder.append('<br/><br/>');
//		cancel.on('click', function(){
//			$('.social-list img').popover('show');
//			$('.social-list img').popover('toggle');
//		});
//		post.on('click', function(e){
//			self.post(vendor, textarea);
//		});
		return holder;
	},
	post : function(vendor, textarea){
		var self = this;
		
		var validator = $('#'+vendor+'_form').validate({
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
	   
		var formData = new FormData($('#'+vendor+'_form')[0]);
		formData.append('cloudVendor', self.postData.cloudVendor || '');
		formData.append('id', self.postData.id || '');
		formData.append('path', self.postData.path || '');
		formData.append('vendor', vendor);
		formData.append('contents', textarea.val());
		
		$.ajax({
			url: api + vendor +'/post/create.json',
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
//				$('#'+vendor+'_form')[0].reset();
				textarea.val('');
				$('.social-list img').popover('show');
				$('.social-list img').popover('toggle');
				$('#'+cancelElementId).on('click', function(){
					$.ajax({
	    				url: api + data.vendor +'/post/delete.json',
	    				type : 'post',
	    				data : {
	    					id : data.id
	    				},
	    				global : false,
	    				success : function(data) {
	    					$('.social-list img').popover('show');
	 					   	$('.social-list img').popover('toggle');
	    					utils.noty.success(socials[data.vendor].serviceName + '에 작성을 정상적으로 취소 하였습니다.');
	    				}
	    			});
				});
			},
			error : UI.AjaxErrorHandler,
			beforeSend : function() {
				self.l = Ladda.create($('.post_button').get()[0]);
				self.l.start();
			},
			complete : function(){
				self.l.stop();
			}
		});
	}
});