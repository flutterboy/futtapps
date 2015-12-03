'use strict';

angular.module('contabilita', [
  'ui.router',
  'ui.bootstrap',
  'contabilita.saldo',
  'contabilita.movimenti'
]).
config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
	$stateProvider.state('/', {
		url: '',
		views: {
			tabSaldo:{templateUrl:'views/saldo.html'},
			tabMovimenti:{templateUrl:'views/movimenti.html'},
			tabStatistiche:{templateUrl:'views/statistiche.html'},
		}
	});
	$urlRouterProvider.otherwise('/');
}])
.controller('tabsController', function ($rootScope, $scope, $state){
	$scope.tabs = [
	     {title:'Saldo', route:'tabSaldo', active: true},
	     {title:'Movimenti', route:'tabMovimenti', active: false},
	     {title:'Statistiche', route:'tabStatistiche', active: false},
	];
});
