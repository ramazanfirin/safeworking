(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .controller('CameraDetailController', CameraDetailController);

    CameraDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Camera', 'Device'];

    function CameraDetailController($scope, $rootScope, $stateParams, previousState, entity, Camera, Device) {
        var vm = this;

        vm.camera = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('safeworkingApp:cameraUpdate', function(event, result) {
            vm.camera = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
