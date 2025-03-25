(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .controller('CameraController', CameraController);

    CameraController.$inject = ['Camera'];

    function CameraController(Camera) {

        var vm = this;

        vm.cameras = [];

        loadAll();

        function loadAll() {
            Camera.query(function(result) {
                vm.cameras = result;
                vm.searchQuery = null;
            });
        }
    }
})();
