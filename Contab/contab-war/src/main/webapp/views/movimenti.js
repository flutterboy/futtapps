'use strict';

angular.module('contabilita.movimenti', [
	'ngAnimate', 
	'ui.bootstrap', 
	'ui.grid',
	'ngPDFViewer'
])

.controller('cercaMovimentiController', ['$rootScope', '$scope', '$http', 'dataBroadcaster', function($rootScope, $scope, $http, dataBroadcaster){
	$scope.direzioneConfig = $rootScope.direzioneConfig;
	
	$scope.minDate = new Date(1880, 1, 1);
	$scope.maxDate = new Date(2015, 12, 31);
	$scope.cm = {};
	$scope.dal = {
		opened: false,
		dateOptions: {formatYear: 'yy', startingDay: 1},
		disable: function (date, mode){
			return (mode === 'day' && (date.getDay() === 0 || date.getDay() === 6));
		},
		open: function(evt){this.opened = true}
	};
	$scope.al = {
		opened: false,
		dateOptions: {formatYear: 'yy', startingDay: 1},
		disable: function (date, mode){
			return (mode === 'day' && (date.getDay() === 0 || date.getDay() === 6));
		},
		open: function(evt){this.opened = true}
	};
	
	
	$scope.cercaMovs = {da : new Date(2015, 10, 10), al : new Date(2015, 10, 10)}; 
	$scope.cercaMovimenti = function(){
		var da = ($scope.cm.dal == null || $scope.cm.dal.length == 0) ? 'x' : $scope.cm.dal;
		var al = ($scope.cm.al == null || $scope.cm.al.length == 0) ? 'x' : $scope.cm.al;
		var dir = ($scope.cm.dir == null || $scope.cm.dir.length == 0 || $scope.cm.dir == 'Qualsiasi') ? 'x' : $scope.cm.dir;
		if (da != 'x')
			da = cu.dateToString(da, 'dd-MM-yyyy');
		if (al != 'x')
			al = cu.dateToString(al, 'dd-MM-yyyy');
		
		cu.get({
			url: 'disp/rest/get/' + da + '/' + al + '/' + dir,
			success: function (res){
				dataBroadcaster.send('ricercaMovimentiResult', res);
			},
			dataBroadcaster: dataBroadcaster,
			$http: $http2015
		});
		
	}
	
	$scope.resetSearch = function(){
		$scope.cm.dal = null;
		$scope.cm.al = null;
		$scope.cm.dir = null;
	}
	
}])

.controller('tabellaMovimentiController', ['$rootScope', '$scope', '$http', '$uibModal', 'dataBroadcaster', 'uiGridConstants', function($rootScope, $scope, $http, $uibModal, dataBroadcaster, uiGridConstants){
	
	var gridButtons = '<div class="ui-grid-cell-contents ng-binding ng-scope"><button type="button" class="btn btn-primary btn-xs" ng-click="grid.appScope.dettaglioMovimento(row.entity)"><span class="glyphicon glyphicon-zoom-in"></span></button>&nbsp;&nbsp;<button type="button" class="btn btn-warning btn-xs" ng-click="grid.appScope.modificaMovimento(row.entity)" ng-disabled="row.entity.idPadre != null"><span class="glyphicon glyphicon-edit"></span></button>&nbsp;&nbsp;<button class="btn btn-danger btn-xs" ng-click="grid.appScope.eliminaMovimento(row.entity)" ng-disabled="row.entity.idPadre != null"><span class="glyphicon glyphicon-trash"></span></button></div';
	
	var dateSort = function (a, b){
		var dateA = cu.stringToDate(a);
		var dateB = cu.stringToDate(b);
		if (a == b)
			return 0;
		if (a > b)
			return 1;
		return -1;
	}
	
	$scope.getTableStyle= function() {
        return {
            height: (15 * $scope.gridOptions.rowHeight + $scope.gridOptions.headerRowHeight) + "px"
        };
    };
	
	$scope.gridOptions = {
		enableFiltering: true,
		onRegisterApi: function (gridApi){
			$scope.gridApi = gridApi;
		},
		enableHorizontalScrollbar: false,
		data: [],
		columnDefs: [
		             {
		            	 field:'data', 
		            	 displayName: 'Data', 
		            	 width: 100, 
		            	 sortingAlgorithm: dateSort,
		            	 type: 'string', 
		            	 cellClass: cu.movimentiTableCellClass,
		            	 filters: [
									{
										   noTerm: true,
										   placeholder: 'dopo il',
										   condition: function (searchTerm, cellValue){
											   if (searchTerm == null || searchTerm.length == 0)
												   return true;
											   var st = searchTerm.replace(/\\/gi, '');
											   try {
												   var searchTermDate = cu.stringToDate(st);
												   var cellDate = cu.stringToDate(cellValue);
												   return (cellDate >= searchTermDate);
											   }catch(error){
												   return false;
											   }
										   }
									},
		            	           {
		            	        	   noTerm: true,
		            	        	   term: cu.dateToString(new Date()),
		            	        	   placeholder: 'prima del',
		            	        	   condition: function (searchTerm, cellValue){
		            	        		   if (searchTerm == null || searchTerm.length == 0)
		            	        			   return true;
		            	        		   var st = searchTerm.replace(/\\/gi, '');
		            	        		   try {
		            	        			   var searchTermDate = cu.stringToDate(st);
			            	        		   var cellDate = cu.stringToDate(cellValue);
			            	        		   return (cellDate <= searchTermDate);
		            	        		   }catch(error){
		            	        			   return false;
		            	        		   }
		            	        	   }
		            	           }
		            	          ]
		            	 
		            }, 
		            {
		            	field:'direzione', 
		            	displayName: 'Direzione', 
		            	width: 110, 
		            	type: 'string', 
		            	cellClass: cu.movimentiTableCellClass,
		            	filters: [{noTerm: true, type: uiGridConstants.filter.SELECT, selectOptions: $rootScope.direzioneConfig.value}]
		            },
		            {
		            	field:'target', 
		            	displayName: 'Target', 
		            	width: 200, 
		            	type: 'string', 
		            	cellClass: cu.movimentiTableCellClass,
		            	filters: [{noTerm: true, type: uiGridConstants.filter.SELECT, selectOptions: $rootScope.targetConfig.value}]
		            },
		            {
		            	field:'importo', 
		            	displayName: 'Importo', 
		            	width: 80, 
		            	type: 'number', 
		            	cellClass: cu.movimentiTableCellClass,
		            	filters: [
		            	           {condition: uiGridConstants.filter.GREATER_THAN, placeholder: 'più di'},
		            	           {condition: uiGridConstants.filter.LESS_THAN, placeholder: 'meno di'}
		            	         ]
		            },
		            {
		            	field:'descrizione', 
		            	displayName: 'Descrizione', 
		            	type: 'string', 
		            	cellClass: cu.movimentiTableCellClass
		            },
		            {
		            	name: ' ', 
		            	width: 105, 
		            	enableSorting: false, 
		            	enableColumnMenu: false, 
		            	cellTemplate: gridButtons,
		            	enableFiltering: false
		            }
		           ]
	};
	
	cu.get({
		url: 'disp/rest/getAllMovimenti',
		success: function (res){
			$scope.gridOptions.data = res;
		},
		dataBroadcaster: dataBroadcaster,
		$http: $http
	});
	
	$scope.$on('ricercaMovimentiResult', function(event, res){
		$scope.gridOptions.data = dataBroadcaster.getData();
	});
	$scope.$on('nuovoMovimentoInserito', function(event, res){
		$scope.gridOptions.data = dataBroadcaster.getData();
	});
	$scope.$on('movimentoModificato', function(event, res){
		$scope.gridOptions.data = dataBroadcaster.getData();
	});
	$scope.$on('movimentoEliminato', function(event, res){
		$scope.gridOptions.data = dataBroadcaster.getData();
	});
	
	$scope.modificaMovimento = function(m){
		$scope.conf = {view: false, edit: true, newz: false, m: m};
		$uibModal.open({
				animation: true,
				templateUrl: 'views/gestioneMovimento.html',
				controller: 'movimentoModalInstanceController',
				size: 'lg',
				resolve: {
					conf: function (){
						return $scope.conf;
					}
				}
		});
	}
	
	$scope.dettaglioMovimento = function(m){
		$scope.conf = {view: true, edit: false, newz: false, m: m};
		$uibModal.open({
				animation: true,
				templateUrl: 'views/gestioneMovimento.html',
				controller: 'movimentoModalInstanceController',
				size: 'lg',
				resolve: {
					conf: function (){
						return $scope.conf;
					}
				}
		});
	}
	$scope.eliminaMovimento = function(m){
		var url = 'disp/rest/delete/' + m.id + '/01-01-1990/31-12-2020';
		cu.get({
			url: url,
			dataBroadcaster: dataBroadcaster,
			$http: $http,
			success: function (res){
				dataBroadcaster.send('movimentoEliminato', res);
			},
			error: function(res){
				dataBroadcaster.send('movimentoEliminato', res);
			}
		});
	}
}])

.controller('nuovoMovimentoController', ['$scope', '$uibModal', function($scope, $uibModal){
	$scope.openNuovoMovimento = function(){
		$scope.conf = {view: false, edit: false, newz: true};
		$uibModal.open({
				animation: true,
				templateUrl: 'views/gestioneMovimento.html',
				controller: 'movimentoModalInstanceController',
				size: 'lg',
				resolve: {
					conf: function (){
						return $scope.conf;
					}
				}
		});
	}
	
}])

.controller('movimentoModalInstanceController', ['$rootScope', '$scope', '$uibModalInstance', '$http', 'dataBroadcaster', 'conf', function($rootScope, $scope, $uibModalInstance, $http, dataBroadcaster, conf){
	$scope.direzioneConfig = $rootScope.direzioneConfig;
	$scope.targetConfig = $rootScope.targetConfig;
	
	$scope.viewDoc = '';
	$scope.docText = '';
	$scope.pdfUrl = null;
	$scope.imageUrl = null;
	$scope.conf = conf;
	if ($scope.conf.newz)
		$scope.m = {};
	else
		$scope.m = cu.clone($scope.conf.m);
	
	showDocument(null, $scope, $http, dataBroadcaster);
	
	$scope.toggleView = function(){
		$scope.conf.view = true;
		$scope.conf.edit = false;
	}
	
	$scope.toggleEdit = function(){
		$scope.conf.view = false;
		$scope.conf.edit = true;
	}
	
	$scope.toggleDelete = function(){
		if ($scope.conf.newz)
			return;
		$uibModalInstance.close();
		var url = 'disp/rest/delete/' + $scope.m.id + '/' + $scope.m.direzione + '/01-01-1990/31-12-2020';
		cu.get({
			url: url,
			dataBroadcaster: dataBroadcaster,
			$http: $http,
			success: function (res){
				dataBroadcaster.send('movimentoEliminato', res);
			},
			error: function(res){
				dataBroadcaster.send('movimentoEliminato', res);
			}
		});
	}
	
	$scope.date = {
		opened: false,
		dateOptions: {formatYear: 'yy', stratingDay: 1},
		open: function(evt){this.opened = true}
	};
	$scope.mDt = new Date();
	if ($scope.m.data)
		$scope.mDt = cu.stringToDate($scope.m.data, 'dd/MM/yyyy');
	
	$scope.conferma = function () {
		$uibModalInstance.close();
		if ($scope.conf.view)
			return;
		var url = 'disp/rest/saveMovimento/01-01-1990/31-12-2020';
		var evt = 'nuovoMovimentoInserito';
		if ($scope.conf.edit)
			evt = 'movimentoModificato';
		$scope.m.data = cu.dateToString($scope.mDt, "dd-MM-yyyy'T'HH:mm:ss.SSS'Z'"); 
		
		cu.post({
			url: url,
			dataBroadcaster: dataBroadcaster,
			$http: $http,
			data: angular.toJson($scope.m),
			success: function (res){
				dataBroadcaster.send(evt, res);
			},
			error: function(res){
				dataBroadcaster.send(evt, res);
			},
			infoMessage: 'Fatto!!!',
			errorMessage: 'Ho Fallito!!!'
		});
		
	};
	$scope.annulla = function () {
		$uibModalInstance.dismiss();
	};
	$scope.onFileSelect = function (el){
		showDocument(el.files[0], $scope, $http, dataBroadcaster);
	}
	
	$scope.downloadDocument = function(){
		$http({
			method: 'POST',
			url: 'convertDocument?mode=gen',
			data: 'file=' + encodeURIComponent($scope.m.documento.base64) + '&type=' + encodeURIComponent($scope.m.documento.tipo) + '&name=' + encodeURIComponent($scope.m.documento.nome),
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).success(function (res){
			window.location.href = 'convertDocument?mode=' + res;
		});
	}
}]);

function showDocument(file, $scope, $http, dataBroadcaster){
	if (file != null){
		var reader = new FileReader();
		var documento = {nome: file.name, lunghezza: file.size, tipo: file.type};
		reader.onload = function(readerEvt) {
			documento.base64 = btoa(readerEvt.target.result);
			$scope.m.documento = documento;
			createAndShowDocument($scope, $http, dataBroadcaster);
		};
		reader.readAsBinaryString(file);
	}else if ($scope.m.documento != null){
		createAndShowDocument($scope, $http, dataBroadcaster)
	}
}

function createAndShowDocument ($scope, $http, dataBroadcaster){
	$('#fileSelectLabel').val($scope.m.documento.nome);
	cu.post({
		method: 'POST',
		url: 'convertDocument?mode=gen',
		data: 'file=' + encodeURIComponent($scope.m.documento.base64) + '&type=' + encodeURIComponent($scope.m.documento.tipo) + '&name=' + encodeURIComponent($scope.m.documento.nome),
		headers: {'Content-Type': 'application/x-www-form-urlencoded'},
		$http: $http,
		dataBroadcaster: dataBroadcaster,
		success: function (res){
					var doc = $scope.m.documento;
					if (doc.tipo.indexOf('text') != -1){
						$scope.docText = atob(doc.base64);
						$scope.viewDoc = 'text';
					}else if (doc.tipo.indexOf('image') != -1){
						$scope.imageUrl = 'convertDocument?mode=' + res;
						$scope.viewDoc = 'image';
						$('#docImage').attr('style', 'max-width: 630px; max-height: 370px; margin-top: 10px;');
					}else if (doc.nome.indexOf('.pdf') != -1 && (doc.nome.indexOf('.pdf') + 4) == doc.nome.length){
						$scope.pdfUrl = 'convertDocument?mode=' + res;
						$scope.viewDoc = 'pdf';
					}else{
						$scope.docText = $scope.m.documento.nome;
						$scope.viewDoc = 'unknown';
					}
				},
		error: function(res){
					var i = 0;
					i = 1;
				}
	});
}
