'use strict';

angular.module('contabilita', [
  'ui.router',
  'ui.bootstrap',
  'ngAnimate',
  'ngResource',
  'angularSpinners',
  'contabilita.riepilogo',
  'contabilita.saldo',
  'contabilita.movimenti',
  'contabilita.statistiche',
  'contabilita.config'
])

.run(['$rootScope', '$state', '$stateParams', function($rootScope, $state, $stateParams){
	$rootScope.$state = $state;
	$rootScope.$stateParams = $stateParams;
	$rootScope.sections = [
	         {title:'Riepilogo', route:'index', cssClass: 'table', active: 'active'},
	  	     {title:'Saldo', route:'saldo', cssClass: 'desktop'},
	  	     {title:'Movimenti', route:'movimenti', cssClass: 'edit'},
	  	     {title:'Statistiche', route:'statistiche', cssClass: 'bar-chart-o'},
	  	     {title:'Config', route:'config', cssClass: 'wrench'},
	  	];
	$state.go('index');
}])

.config(['$stateProvider', '$urlRouterProvider', '$httpProvider', function($stateProvider, $urlRouterProvider, $httpProvider) {
	
	$urlRouterProvider.otherwise('/');
	
	$stateProvider
	.state('index', {
		url: 'riepilogo',
		templateUrl: 'views/riepilogo.html',
	})
	.state('saldo', {
		url: '/saldo',
		templateUrl: 'views/saldo.html'
	})
	.state('movimenti', {
		url: '/movimenti',
		templateUrl: 'views/movimenti.html'
	})
	.state('statistiche', {
		url: '/statistiche',
		templateUrl: 'views/statistiche.html'
	}).state('config', {
		url: '/config',
		templateUrl: 'views/config.html'
	});
	
}])

.factory('dataBroadcaster', function($rootScope){
	var service = {};
	service.data = false;
	service.showInfo = function(data){
		$rootScope.$broadcast('showInfoPopup', data);
	};
	service.showError = function(data){
		$rootScope.$broadcast('showErrorPopup', data);
	}
	service.broadcast = function(notify, data){
		$rootScope.$broadcast(notify, data);
	};
	service.send = function(notify, data){
		this.data = data;
		$rootScope.$broadcast(notify);
	};
	service.wait = function(){
		$rootScope.$broadcast('modalWait');
	};
	service.unwait = function(){
		$rootScope.$broadcast('modalUnwait');
	};
	service.getData = function(){
		return this.data;
	};
	return service;
})

.controller('appController', ['$scope', 'spinnerService', '$rootScope', '$http', 'dataBroadcaster', '$uibModal', function($scope, spinnerService, $rootScope, $http, dataBroadcaster, $uibModal){
	$scope.$on('modalWait', function(event, res){
		spinnerService.show('waitSpinner');
	});
	$scope.$on('modalUnwait', function(){
		spinnerService.hide('waitSpinner');
	});
	$scope.$on('showInfoPopup', function(event, data){
		openPopup($scope, $uibModal, data, 'success');
	});
	$scope.$on('showErrorPopup', function(event, data){
		openPopup($scope, $uibModal, data, 'danger');
	});
	$rootScope.allConfigs = [];
	$http({
		method: 'GET',
		url: 'disp/rest/readAllConfig',
		$http: $http,
		dataBroadcaster: dataBroadcaster
	}).success(function (res){
		$rootScope.allConfigs = res;
		for (var i = 0; i < res.length; i++){
			if (res[i].id == 'direzione')
				$rootScope.direzioneConfig = res[i];
			else if (res[i].id == 'target')
				$rootScope.targetConfig = res[i];
		}
		dataBroadcaster.showInfo({message: 'Pronti!'});
	}).error(function(res){
		var errInfo = res;
		if (res == null)
			errInfo = {message: 'Errore Sconosciuto!'};
		if (!c.showError)
			c.dataBroadcaster.showError(errInfo);
	});
	
}])

.controller('messagePopupController', ['$scope', '$uibModalInstance', '$sce', 'dataInfo', function($scope, $uibModalInstance, $sce, dataInfo){
	
	$scope.m = dataInfo;
	
	$scope.bindStackTrace = function (str){
		return $sce.trustAsHtml(str);
	}
	
	$scope.chiudi = function(){
		$uibModalInstance.close();
	}

}]);


function openPopup($scope, $uibModal, data, type) {
	var title = 'Informazione';
	var style = 'background-color:#def0da;color:#5a7444;';
	if (type == 'danger'){
		title = 'Errore';
		style = 'background-color:#f2dedf;color:#8d594e;';
	}
	$scope.dataInfo = {
		title : title,
		type : type,
		style: style,
		data: data
	};
	$uibModal.open({
		animation : true,
		templateUrl : 'messagePopup.html',
		controller : 'messagePopupController',
		size : 'md',
		resolve : {
			dataInfo : function() {
				return $scope.dataInfo;
			}
		}
	});
}