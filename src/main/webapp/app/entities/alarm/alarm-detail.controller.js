(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .controller('AlarmDetailController', AlarmDetailController);

    AlarmDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Alarm', 'Device', 'Person', 'Camera'];

    function AlarmDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Alarm, Device, Person, Camera) {
        var vm = this;

        vm.alarm = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('safeworkingApp:alarmUpdate', function(event, result) {
            vm.alarm = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
