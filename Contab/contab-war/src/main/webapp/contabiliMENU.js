'use strict';

angular.module('contabilita', [
  'ui.router',
  'ui.bootstrap',
  'contabilita.saldo',
  'contabilita.movimenti',
  'ngAnimate'
])

.run(['$rootScope', '$state', '$stateParams', function($rootScope, $state, $stateParams){
	$rootScope.$state = $state;
	$rootScope.$stateParams = $stateParams;
	$rootScope.mItems = [
	  	     {title:'Saldo', route:'saldo'},
	  	     {title:'Movimenti', route:'movimenti'},
	  	     {title:'Statistiche', route:'statistiche'},
	  	];
}])

.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
	
	$urlRouterProvider.otherwise('/');
	
	$stateProvider
	.state('saldo', {
		url: '/saldo',
		templateUrl: 'views/saldo.html',
//		controller:  ['$scope', '$http', function($scope, $http){
//			$scope.posta = function (pos){
//				var req = $http({
//					method: 'post',
//					url: 'http://localhost:8080/contab-war/disp/rest/fai/' + pos.q,
//					data: {"body":pos.body},
//				});
//				req.success(function (o){
//					alert(JSON.stringify(o.result));
//				});
//				req.error(function (o){
//					alert(JSON.stringify(o));
//				});
//			}
//		}]
	})
	.state('movimenti', {
		url: '/movimenti',
		templateUrl: 'views/movimenti.html'
	})
	.state('statistiche', {
		url: '/statistiche',
		templateUrl: 'views/statistiche.html'
	});
	
}]);
