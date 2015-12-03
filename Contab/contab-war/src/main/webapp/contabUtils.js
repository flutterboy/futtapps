(function() {

	var cu = function(obj) {
		if (obj instanceof cu) 
			return obj;
		if (!(this instanceof cu)) 
			return new cu(obj);
		this.cuwrapped = obj;
	};
	
	if (typeof exports !== 'undefined') {
		if (typeof module !== 'undefined' && module.exports) {
			exports = module.exports = cu;
		}
		exports.cu = cu;
	} else {
		this.cu = cu;
	}
	
	cu.datesToStrings = {
		'dd-MM-yyyy': function(date){
			var day = date.getDate();
			var mon = (date.getMonth() + 1);
			if (day < 10)
				day = '0' + day;
			if (mon < 10)
				mon = '0' + mon;
			return day + '-' + mon + '-' + date.getFullYear();
		},
		'dd/MM/yyyy': function(date){
			var day = date.getDate();
			var mon = (date.getMonth() + 1);
			if (day < 10)
				day = '0' + day;
			if (mon < 10)
				mon = '0' + mon;
			return day + '/' + mon + '/' + date.getFullYear();
		},
		"dd-MM-yyyy'T'HH:mm:ss.SSS'Z'" : function (date){
			var res = cu.datesToStrings['dd-MM-yyyy'](date);
			res += 'T';
			
			var hh = date.getHours();
			if (hh < 10)
				hh = '0' + hh;
			res += hh + ':';
			
			var mm = date.getMinutes();
			if (mm < 10)
				mm = '0' + mm;
			res += mm + ':';
			
			var ss = date.getSeconds();
			if (ss < 10)
				ss = '0' + ss;
			res += ss + '.';
			
			var sss = date.getMilliseconds();
			if (sss < 10)
				sss = '00' + sss;
			else if (sss < 100)
				sss = '0' + sss;
			res += sss + 'Z';
			
			return res;
		}
	};
	
	cu.stringsToDates = {
		'dd-MM-yyyy': function(str){
			if (str == undefined || str == null || str.length != 10)
				return null;
			var dateParts = str.split('-');
			return new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);
		},
		'dd/MM/yyyy': function(str){
			if (str == undefined || str == null || str.length != 10)
				return null;
			var dateParts = str.split('/');
			return new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);
		},
		"dd-MM-yyyy'T'HH:mm:ss.SSS'Z'" : function (date){
			if (str == undefined || str == null || str.length != 24)
				return null;
			var d = cu.removeFirstZero(str.substring(0, 2));
			var mo = cu.removeFirstZero(str.substring(3, 5)) - 1;
			var y = str.substring(6, 10);
			var h = cu.removeFirstZero(str.substring(11, 13));
			var mi = cu.removeFirstZero(str.substring(14, 16));
			var s = cu.removeFirstZero(str.substring(17, 19));
			var ss = str.substring(20, 23);
			if (ss.substring(0, 1) == '0' && ss.substring(1, 2) == '0')
				ss = ss.substring(2, 3);
			else if (ss.substring(0, 1) == '0')
				ss = ss.substring(1, 3);
			var date = new Date(y, m, d, h, mi, s, ss);
			return date;
		}
	};
	
	cu.removeFirstZero = function(str){
		var res = str;
		if (str.substring(0, 1) == '0')
			res = str.sbstring(1, 2);
		return res;
	};
	
	cu.dateToString = function (date, pattern){
		if (date == undefined || date == null)
			return null;
		var p = pattern;
		if (pattern == null || pattern == undefined)
			p = 'dd/MM/yyyy';
		var func = cu.datesToStrings[p];
		return func(date);
	};
	
	cu.get = function (c){
		var wait = true;
		if (c.wait != undefined && c.wait != null && c.wait == false)
			wait = false;
		if (wait)
			c.dataBroadcaster.wait();
		c.$http.get(c.url).success(function (res){
			if (c.success != undefined)
				c.success(res);
			if (wait)
				c.dataBroadcaster.unwait();
			if (c.infoMessage != undefined)
				c.dataBroadcaster.showInfo({message: c.infoMessage});
		}).error(function (res){
			var errInfo = res;
			if (wait)
				c.dataBroadcaster.unwait();
			if (res == null)
				errInfo = {message: 'Errore Sconosciuto!'};
			if (!c.showError)
				c.dataBroadcaster.showError(errInfo);
			if (c.error != undefined)
				c.error(res);
		});
	}
	
	cu.post = function (c){
		var wait = true;
		if (c.wait != undefined && c.wait != null && c.wait == false)
			wait = false;
		var headers = {};
		if (c.headers && c.headers != null)
			headers = c.headers;
		if (wait)
			c.dataBroadcaster.wait();
		c.$http({
			method: 'POST',
			url: c.url,
			data: c.data,
			headers: headers
		}).success(function (res){
			if (c.success != undefined)
				c.success(res);
			if (wait)
				c.dataBroadcaster.unwait();
			if (c.infoMessage != undefined)
				c.dataBroadcaster.showInfo({message: c.infoMessage});
		}).error(function (res){
			var errInfo = res;
			if (wait)
				c.dataBroadcaster.unwait();
			if (res == null)
				errInfo = {message: 'Errore Sconosciuto!'};
			if (!c.showError)
				c.dataBroadcaster.showError(errInfo);
			if (c.error != undefined)
				c.error(res);
		});
	}
	
	cu.stringToDate = function(str, pattern){
		if (str == undefined || str == null)
			return null;
		var p = pattern;
		if (pattern == null || pattern == undefined)
			p = 'dd/MM/yyyy';
		var func = cu.stringsToDates[p];
		return func(str);
	};
	
	cu.clone = function (obj){
		var copy = null;

		if (null == obj || "object" != typeof obj) 
			return obj;
		if (obj instanceof Date) {
			copy = new Date();
			copy.setTime(obj.getTime());
			return copy;
		}
		if (obj instanceof Array) {
			copy = [];
			for (var i = 0, len = obj.length; i < len; i++) {
				copy[i] = cu.clone(obj[i]);
			}
			return copy;
		}
		if (obj instanceof Object) {
			copy = {};
			for (var attr in obj) {
				if (obj.hasOwnProperty(attr)) copy[attr] = cu.clone(obj[attr]);
			}
			return copy;
		}
		throw new Error("Unable to copy obj! Its type isn't supported.");
	};
	
	cu.movimentiTableCellClass = function (grid, row, col, rowRenderIndex, colRenderIndex) {
		var oggi = new Date();
		var data = cu.stringToDate(row.entity.data);
		var direzione = row.entity.direzione.toLowerCase();
		if (data.getTime() > oggi.getTime())
			return direzione + 'Futura';
		return direzione;
	}
	
	if (typeof define === 'function' && define.amd) {
		define('cutils', [], function() {
			return cu;
		});
	}
}.call(this));
