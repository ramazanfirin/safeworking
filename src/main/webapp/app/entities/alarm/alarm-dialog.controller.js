(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .controller('AlarmDialogController', AlarmDialogController);

    AlarmDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Alarm', 'Device', 'Person', 'Camera'];

    function AlarmDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Alarm, Device, Person, Camera) {
        var vm = this;

        vm.alarm = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.devices = Device.query();
        vm.people = Person.query();
        vm.cameras = Camera.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.alarm.id !== null) {
                Alarm.update(vm.alarm, onSaveSuccess, onSaveError);
            } else {
                Alarm.save(vm.alarm, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('safeworkingApp:alarmUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.insertDate = false;

        vm.setImage = function ($file, alarm) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        alarm.image = base64Data;
                        alarm.imageContentType = $file.type;
                    });
                });
            }
        };

        vm.setBackGroundImage = function ($file, alarm) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        alarm.backGroundImage = base64Data;
                        alarm.backGroundImageContentType = $file.type;
                    });
                });
            }
        };

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
