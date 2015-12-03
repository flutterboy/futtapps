'use strict';

angular.module('contabilita.saldo', [
	'ngAnimate',
	'ui.bootstrap',
	'ui.grid',
	'chart.js'
])

.controller('saldoController', ['$scope', '$http', 'dataBroadcaster', function($scope, $http, dataBroadcaster){

	$scope.saldoPanels = [
		{resultName: 'contanti', title: 'Contanti', cssClass: 'panel panel-primary', cssBodyClass: 'panel-body-info', importo: '0.0', importoSec: ''},
		{resultName: 'contoCorrente', title: 'Conto Corrente', cssClass: 'panel-c panel-warning-c', cssBodyClass: 'panel-body-warning', importo: '0.0', importoSec: ''},
		{resultName: 'cartaCredito', title: 'Residuo Carta di Credito', cssClass: 'panel-c panel-danger-c', cssBodyClass: 'panel-body-danger', importo: '0.0', importoSec: ''},
		{resultName: 'buoniPasto', title: 'Buoni Pasto', cssClass: 'panel-c panel-grey-c', cssBodyClass: 'panel-body-grey', importo: '0.0', importoSec: ''},
		{resultName: 'contoCorrenteComune', title: 'Conto Corrente Comune', cssClass: 'panel-c panel-success-c', cssBodyClass: 'panel-body-success', importo: '0.0', importoSec: ''},
		{resultName: 'globale', title: 'Globale', cssClass: 'panel-c panel-orange-c', cssBodyClass: 'panel-body-orange', importo: '0.0', importoSec: ''},
	];

	$scope.saldoAlPanels = cu.clone($scope.saldoPanels);

	$scope.charts = [
		{title: 'Riepilogo Senza Conto Corrente', labels: ['Contanti', 'Buoni Pasto', 'Conto Corrente Comune'], colours: ['#31708f', '#666666', '#5cb85c'], data:[]},
		{title: 'Riepilogo Totale', labels: ['Contanti', 'Buoni Pasto', 'Conto Corrente Comune', 'Conto Corrente'], colours: ['#31708f', '#666666', '#5cb85c', '#f0ad4e'], data:[]}
	];
	$scope.chartsAl = cu.clone($scope.charts);

	$scope.cercaSaldoAl = {
			minDate: new Date(1880, 1, 1),
			maxDate: new Date(2020, 12, 31),
			opened: false,
			dateOptions: {formatYear: 'yy', startingDay: 1},
			open: function(evt){this.opened = true},
			al: new Date()
	};
	
	cu.get({
		$http: $http,
		dataBroadcaster: dataBroadcaster,
		url: 'disp/rest/getSaldi',
		success: function (res){
			for (var i = 0; i < $scope.saldoPanels.length; i++){
				$scope.saldoPanels[i].importo = res[$scope.saldoPanels[i].resultName].importo;
				if ($scope.saldoPanels[i].resultName == 'globale')
					$scope.saldoPanels[i].importoSec = '(prev. a 30 gg: ' + res.globaleA30.importo + ' €)';
				else if ($scope.saldoPanels[i].resultName == 'contoCorrenteComune')
					$scope.saldoPanels[i].importoSec = '(totale: ' + (res.contoCorrenteComune.importo * 2) + ' €)';
			}
			$scope.charts[0].data = [res.contanti.importo, res.buoniPasto.importo, res.contoCorrenteComune.importo];
			$scope.charts[1].data = [res.contanti.importo, res.buoniPasto.importo, res.contoCorrenteComune.importo, res.contoCorrente.importo];
		}
	});
	
	$scope.calcolaSaldoAl = function(){
		cu.get({
			$http: $http,
			dataBroadcaster: dataBroadcaster,
			url: 'disp/rest/getSaldi/' + cu.dateToString($scope.cercaSaldoAl.al, 'dd-MM-yyyy'),
			success: function (res){
				for (var i = 0; i < $scope.saldoAlPanels.length; i++){
					$scope.saldoAlPanels[i].importo = res[$scope.saldoAlPanels[i].resultName].importo;
					if ($scope.saldoAlPanels[i].resultName == 'globale')
						$scope.saldoAlPanels[i].importoSec = '(prev. a 30 gg: ' + res.globaleA30.importo + ' €)';
					else if ($scope.saldoAlPanels[i].resultName == 'contoCorrenteComune')
						$scope.saldoAlPanels[i].importoSec = '(totale: ' + (res.contoCorrenteComune.importo * 2) + ' €)';
				}
				$scope.chartsAl[0].data = [res.contanti.importo, res.buoniPasto.importo, res.contoCorrenteComune.importo];
				$scope.chartsAl[1].data = [res.contanti.importo, res.buoniPasto.importo, res.contoCorrenteComune.importo, res.contoCorrente.importo];
			}
		});
	}
	
}]);