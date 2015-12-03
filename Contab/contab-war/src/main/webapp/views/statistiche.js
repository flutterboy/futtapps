'use strict';

angular.module('contabilita.statistiche', [
        'ngAnimate',
        'ui.bootstrap',
        'ui.grid',
        'chart.js'
    ])
    .controller('statisticheController', ['$rootScope', '$scope', '$http', 'dataBroadcaster', 'uiGridConstants', function($rootScope, $scope, $http, dataBroadcaster, uiGridConstants) {
        $scope.tabs = [
            {title:'Indici', active: true, templateUrl:'views/stats/indici.html'},
            {title:'Grafici', active: false, templateUrl:'views/stats/grafici.html'},
            {title:'Statistiche', active: false, templateUrl:'views/stats/statistiche.html'},
        ];

        $scope.saldoLabels = [];
        $scope.saldoSeries = ['Saldo'];
        $scope.saldo = [[]];

        $scope.entrateUsciteLabels = [];
        $scope.entrateUsciteSeries = ['Entrate', 'Uscite'];
        $scope.entrateUscite = [[],[]];

        $scope.indiceRisparmioLabels = [];
        $scope.indiceRisparmioSeries = ['Indice di Risparmio'];
        $scope.indiceRisparmio = [[]];

        var datePickerOptions = {
            val: null,
            opened: false,
            dateOptions: {formatYear: 'yy', startingDay: 1},
            open: function(evt){this.opened = true},
            reset: function(){this.val = null;}
        };

        $scope.saldoDal = cu.clone(datePickerOptions);
        $scope.saldoAl = cu.clone(datePickerOptions);

        $scope.aeuDal = cu.clone(datePickerOptions);
        $scope.aeuAl = cu.clone(datePickerOptions);

        $scope.airDal = cu.clone(datePickerOptions);
        $scope.airAl = cu.clone(datePickerOptions);

        cu.get({
            $http: $http,
            dataBroadcaster: dataBroadcaster,
            url: 'disp/rest/calcolaAndamento',
            success: function (res){
                var d = null;
                for (var x = 0; x < res.length; x++){
                    d = res[x].data;
                    //if (x == 0 || (x == (res.length - 1)) || ((d.substring(0, 2) % 10) == 0))
                        $scope.saldoLabels.push(d);
                    /*else
                        $scope.saldoLabels.push('');*/
                    $scope.saldo[0].push(res[x].importo);
                }
            }
        });

        cu.get({
            $http: $http,
            dataBroadcaster: dataBroadcaster,
            url: 'disp/rest/calcolaAndamentoEntrateUscite',
            success: function (res){
                var en = res.andamentoEntrate;
                var us = res.andamentoUscite;
                for (var x = 0; x < en.length; x++){
                    $scope.entrateUsciteLabels.push(en[x].data);
                    $scope.entrateUscite[0].push(en[x].importo);
                    $scope.entrateUscite[1].push(us[x].importo);
                }
            }
        });

        cu.get({
            $http: $http,
            dataBroadcaster: dataBroadcaster,
            url: 'disp/rest/getAndamentoIndiceDiRisparmio',
            success: function (res){
                for (var x = 0; x < res.length; x++){
                    $scope.indiceRisparmioLabels.push(res[x].data);
                    $scope.indiceRisparmio[0].push(res[x].importo);
                }
            }
        });

        $scope.chartsOpts = {
            pointHitDetectionRadius: 0,
            pointDot: false
        };

        $scope.saldoHover = function(){
            var i = o;
            i = 2;
        }

        $scope.cercaSaldo = function(){
            var dal = null;
            var al = null;
            if ($scope.saldoDal.val != null)
                dal = cu.dateToString($scope.saldoDal.val, 'dd-MM-yyyy');
            if ($scope.saldoAl.val != null)
                al = cu.dateToString($scope.saldoAl.val, 'dd-MM-yyyy');
            var url = 'disp/rest/calcolaAndamento';
            if (dal != null && al != null)
                url += '/' + dal + '/' + al;
            else if (dal != null)
                url += 'Dal/' + dal;
            else if (al != null)
                url += 'Al/' + al;
            cu.get({
                $http: $http,
                dataBroadcaster: dataBroadcaster,
                url: url,
                success: function (res){
                    $scope.saldoLabels = [];
                    $scope.saldoSeries = ['Saldo'];
                    $scope.saldo = [[]];
                    for (var x = 0; x < res.length; x++){
                        $scope.saldoLabels.push(res[x].data);
                        $scope.saldo[0].push(res[x].importo);
                    }
                }
            });
        };

        $scope.cercaAeu = function(){
            var dal = null;
            var al = null;
            if ($scope.aeuDal.val != null)
                dal = cu.dateToString($scope.aeuDal.val, 'dd-MM-yyyy');
            if ($scope.aeuAl.val != null)
                al = cu.dateToString($scope.aeuAl.val, 'dd-MM-yyyy');
            var url = 'disp/rest/calcolaAndamentoEntrateUscite';
            if (dal != null && al != null)
                url += '/' + dal + '/' + al;
            else if (dal != null)
                url += 'Dal/' + dal;
            else if (al != null)
                url += 'Al/' + al;
            cu.get({
                $http: $http,
                dataBroadcaster: dataBroadcaster,
                url: url,
                success: function (res){
                    var en = res.andamentoEntrate;
                    var us = res.andamentoUscite;
                    $scope.entrateUsciteLabels = [];
                    $scope.entrateUsciteSeries = ['Entrate', 'Uscite', 'Indice Risparmio'];
                    $scope.entrateUscite = [[],[]];
                    for (var x = 0; x < en.length; x++){
                        $scope.entrateUsciteLabels.push(en[x].data);
                        $scope.entrateUscite[0].push(en[x].importo);
                        $scope.entrateUscite[1].push(us[x].importo);
                    }
                }
            });
        };

        $scope.cercaAir = function(){
            var dal = null;
            var al = null;
            if ($scope.airDal.val != null)
                dal = cu.dateToString($scope.airDal.val, 'dd-MM-yyyy');
            if ($scope.airAl.val != null)
                al = cu.dateToString($scope.airAl.val, 'dd-MM-yyyy');
            var url = 'disp/rest/getAndamentoIndiceDiRisparmio';
            if (dal != null && al != null)
                url += '/' + dal + '/' + al;
            else if (dal != null)
                url += 'Dal/' + dal;
            else if (al != null)
                url += 'Al/' + al;
            cu.get({
                $http: $http,
                dataBroadcaster: dataBroadcaster,
                url: url,
                success: function (res){
                    $scope.indiceRisparmioLabels = [];
                    $scope.indiceRisparmioSeries = ['Indice di Risparmio'];
                    $scope.indiceRisparmio = [[]];
                    for (var x = 0; x < res.length; x++){
                        $scope.indiceRisparmioLabels.push(res[x].data);
                        $scope.indiceRisparmio[0].push(res[x].importo);
                    }
                }
            });
        };

    }]);
