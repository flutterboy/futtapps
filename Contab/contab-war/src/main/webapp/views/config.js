'use strict';

angular.module('contabilita.config', [
	'ui.bootstrap', 
	'ui.grid',
	'ui.grid.edit'
])

.controller('configController', ['$scope', '$http', '$rootScope', '$uibModal', 'dataBroadcaster', function($scope, $http, $rootScope, $uibModal, dataBroadcaster){
	
	$scope.configs = $rootScope.allConfigs;
	
	for (var i = 0; i < $scope.configs.length; i++)
		enrichConfig($rootScope, $scope, $scope.configs[i], $http, dataBroadcaster);
	
	$scope.elimina = function(o){
		for (var e = 0; e < $scope.configs.length; e++){
			if ($scope.configs[e].id == o.config){
				for (var j = 0; j < $scope.configs[e].value.length; j++){
					if ($scope.configs[e].value[j].value == o.value){
						$scope.configs[e].value.splice(j, 1);
						cu.post({
							method: 'POST',
							url: 'disp/rest/saveConfig',
							data: angular.toJson($scope.configs[e]),
							$http: $http,
							dataBroadcaster: dataBroadcaster,
							success: function (res){
										for (var z = 0; z < $scope.configs.length; z++){
											if ($scope.configs[z].id == res.id){
												$scope.configs[z].gridOptions.data = res.value;
												break;
											}
										}
									}
						});
						return;
					}
				}
			}
		}
	};
	
	$scope.nuovo = function(id){
		for (i = 0; i < $scope.configs.length; i++){
			if ($scope.configs[i].id == id){
				if ($scope.configs[i].value == null)
					$scope.configs[i].value = [];
				$scope.configs[i].value.push({config: id, label: '', value: ''});
				$scope.configs[i].gridOptions.data = $scope.configs[i].value;
				$rootScope.allConfigs = $scope.configs;
				break;
			}
		}
	};
	
	$scope.nuovoConfig = function(){
		$uibModal.open({
			animation: true,
			templateUrl: 'views/nuovoConfig.html',
			controller: 'nuovoConfigModalInstanceController',
			size: 'md'
		});
	};
	
	$scope.eliminaConfig = function(id){
		cu.get({
			url: 'disp/rest/eliminaConfig/' + id,
			dataBroadcaster: dataBroadcaster,
			$http: $http,
			success: function (res){
				for (var i = 0; i < $scope.configs.length; i++){
					if ($scope.configs[i].id == id){
						$scope.configs.splice(i, 1);
						$rootScope.allConfigs = $scope.configs;
						break;
					}
				}
			},
			error: function(res){
				dataBroadcaster.send('erroreEliminazioneConfig', res);
			}
		});
	};
	
	$scope.$on('nuovoConfigCreato', function(event, res){
		var newConfig = dataBroadcaster.getData();
		enrichConfig($rootScope, $scope, newConfig, $http, dataBroadcaster);
		$scope.configs.push(newConfig);
		$rootScope.allConfigs = $scope.configs;
	});
	
}])

.controller('nuovoConfigModalInstanceController', ['$rootScope', '$scope', '$uibModalInstance', '$http', 'dataBroadcaster', function($rootScope, $scope, $uibModalInstance, $http, dataBroadcaster){
	$scope.nc = {};
	
	$scope.conferma = function(){
		$uibModalInstance.close();
		cu.post({
			url: 'disp/rest/saveConfig',
			dataBroadcaster: dataBroadcaster,
			data: $scope.nc,
			$http: $http,
			success: function (res){
				dataBroadcaster.send('nuovoConfigCreato', res);
			},
			error: function(res){
				dataBroadcaster.send('erroreCreazioneConfig', res);
			}
		});
	};
	
	$scope.annulla = function(){
		$uibModalInstance.close();
	};
	
}]);

function enrichConfig(rScope, scope, config, http, dataBroadcaster){
	if (config.value == null)
		config.value = [];
	var deleteButton = '<div class="ui-grid-cell-contents ng-binding ng-scope"><button class="btn btn-danger btn-xs" ng-click="grid.appScope.elimina(row.entity)"><span class="glyphicon glyphicon-trash"></span></button></div>';
	config.gridOptions = {
			data: config.value,
			onRegisterApi : function(gridApi){
				scope.gridApi = gridApi;
				gridApi.edit.on.afterCellEdit(scope, function(rowEntity, colDef, newValue, oldValue){
					if (newValue == oldValue)
						return;
					var entityId = rowEntity.config;
					for (var j = 0; j < scope.configs.length; j++){
						if (scope.configs[j].id == entityId){
							saveConfigAfterCellEdit(scope, scope.configs[j], http, dataBroadcaster);
							break;
						}
					}
				});
			},
			columnDefs: [
			              {name: 'label', displayName: 'Nome', type: 'string', width: '45%'},
			              {name: 'value', displayName: 'Valore', type: 'string', width: '45%'},
			              {name: ' ', width: '10%', enableSorting: false, enableColumnMenu: false, cellTemplate: deleteButton, enableCellEdit: false}
			             ]
		};
}

function saveConfigAfterCellEdit(scope, config, http, dataBroadcaster){
	config.value = config.gridOptions.data;
	cu.post({
		method: 'POST',
		url: 'disp/rest/saveConfig',
		data: angular.toJson(config),
		$http: http,
		dataBroadcaster: dataBroadcaster,
		success: function (res){
					for (var z = 0; z < scope.configs.length; z++){
						if (scope.configs[z].id == res.id){
							scope.configs[z].gridOptions.data = res.value;
							break;
						}
					}
				}
	});
}