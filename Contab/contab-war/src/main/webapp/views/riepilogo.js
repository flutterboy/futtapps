'use strict';

angular.module('contabilita.riepilogo', [
	'ngAnimate', 
	'ui.bootstrap', 
	'ui.grid', 
	'ngPDFViewer',
	'chart.js'
])

.controller('riepilogoController', ['$rootScope', '$scope', '$http', 'dataBroadcaster', 'uiGridConstants', function($rootScope, $scope, $http, dataBroadcaster, uiGridConstants) {

	$scope.chartLabels = [];
	$scope.chartSeries = ['Saldo Globale'];
	$scope.chartData = [[]];

	var columnDefs = [
		{name: 'data', displayName: 'Data', cellClass: cu.movimentiTableCellClass, type: 'string', cellTooltip: cellTooltip},
		{name: 'importo', displayName: 'Importo', cellClass: cu.movimentiTableCellClass, type: 'string', cellTooltip: cellTooltip}
	];

	$scope.ultimiMovimentiGridOptions = {
		data: $scope.ultimiMovimenti,
		columnDefs: columnDefs,
		enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER
	};

	$scope.prossimeUsciteGridOptions = {
		data: $scope.prossimeUscite,
		columnDefs: columnDefs,
		enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER
	};

	cu.get({
		$http: $http,
		dataBroadcaster: dataBroadcaster,
		url: 'disp/rest/getSaldi',
		success: function (res){
			$scope.saldo = res.globale;
			$scope.saldoA30 = res.globaleA30;
		}
	});

	cu.get({
		$http: $http,
		dataBroadcaster: dataBroadcaster,
		url: 'disp/rest/getUltimiMovimenti',
		success: function (res){
			$scope.ultimiMovimentiGridOptions.data = res;
		}
	});

	cu.get({
		$http: $http,
		dataBroadcaster: dataBroadcaster,
		url: 'disp/rest/getProssimeUscite',
		success: function (res){
			$scope.prossimeUsciteGridOptions.data = res;
		}
	});

	cu.get({
		$http: $http,
		dataBroadcaster: dataBroadcaster,
		url: 'disp/rest/calcolaAndamento',
		success: function (res){
			var x = 0;
			for (x = 0; x < res.length; x++){
				$scope.chartLabels.push(res[x].data);
				$scope.chartData[0].push(res[x].importo);
			}
		}
	});

	$scope.saldoOpts = {
		pointHitDetectionRadius: 0,
		pointDot: false
	};

	$scope.onChartClick = function (points, evt) {
		console.log(points, evt);
	};
	
}]);

function cellTooltip(row, col) {
	return row.entity.target + ' - ' + row.entity.descrizione;
}