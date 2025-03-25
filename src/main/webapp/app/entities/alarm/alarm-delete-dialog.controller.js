(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .controller('AlarmDeleteController',AlarmDeleteController);

    AlarmDeleteController.$inject = ['$uibModalInstance', 'entity', 'Alarm'];

    function AlarmDeleteController($uibModalInstance, entity, Alarm) {
        var vm = this;

        vm.alarm = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Alarm.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
