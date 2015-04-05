Actions = new function() {
	var self = this;
	self.classes = {};
	self.actions = {};
	self.names = new Array();
	self.nextData = [];
	
	var define = function(name, obj) {
		var Action = Class.define({
			definedName : name,
			loaded : false,
			hidden : false,
			load : function(force) {
				if (force || !this.loaded) {
					this.loaded = true;
					this.onLoad();	
				}
				if(self.names[0] != name){
					self.names.unshift(name);
				}
			},
			onLoad : function() {
			},
			show : function() {			
			},
			getAction : function() {
				return this;
			},
			getPreAction : function() {
				return self.get(self.names[1]);
			},
			showPreAction : function() {
				self.get(self.names[1]).show.call(self.get(self.names[1]), self.names[0]);
				self.names.shift();
			},
			getNames : function(){
				return self.names;
			},
			removePreAction : function() {
				self.names.shift();
			}
		});
		self.classes[name] = Action.define(obj);

	};

	this.define = define;

	var get = function(name) {
		if (typeof self.actions[name] == 'undefined') {
			self.actions[name] = new self.classes[name]();
		}
		return self.actions[name];
	};

	this.get = get;
}();

$.template('smaple', '<div class="col-md-4"><div class="thumbnail status"><img src="${contextPath}/ui/resources/img/vendors/${vendor}.png" alt="${vendor}"><div class="caption"><h3 class="text-center">${vendor}</h3><p>${contents}</p><div class="progress"><div class="progress-bar" role="progressbar" aria-valuenow="${percentage}" aria-valuemin="0" aria-valuemax="100" style="width: ${percentage}%;"><span class="sr-only">${percentage}% Complete</span></div></div><p class="text-center"><a class="btn btn-success btn-lg showList" role-vendor="${vendor}">이용 하기</a></p></div></div></div>');