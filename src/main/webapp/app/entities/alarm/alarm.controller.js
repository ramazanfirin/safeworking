(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .controller('AlarmController', AlarmController);

    AlarmController.$inject = ['$state', 'DataUtils', 'Alarm', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'Person', '$uibModal', 'Device', 'Camera'];

    function AlarmController($state, DataUtils, Alarm, ParseLinks, AlertService, paginationConstants, pagingParams, Person, $uibModal, Device, Camera) {

        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.openFile = openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.currentSearch = pagingParams.search;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.loadPersons = loadPersons;
        vm.loadDevices = loadDevices;
        vm.loadCameras = loadCameras;
        vm.selectedPersonId = null;
        vm.selectedDateFilter = null;
        vm.selectedDeviceId = null;
        vm.selectedCameraId = null;
        vm.selectedAlarmType = null;
        vm.selectedFalseAlarm = null;
        vm.selectedProcessed = null;
        vm.persons = [];
        vm.devices = [];
        vm.cameras = [];
        vm.alarmTypes = [
            { id: "CALL", name: "safeworkingApp.AlarmType.CALL" },
            { id: "WATCH_PHONE", name: "safeworkingApp.AlarmType.WATCH_PHONE" }
        ];
        vm.falseAlarmOptions = [
            { id: "true", name: "safeworkingApp.alarm.trueFalse.true" },
            { id: "false", name: "safeworkingApp.alarm.trueFalse.false" }
        ];
        vm.processedOptions = [
            { id: "true", name: "safeworkingApp.alarm.trueFalse.true" },
            { id: "false", name: "safeworkingApp.alarm.trueFalse.false" }
        ];
        vm.dateFilters = [
            { id: "today", name: "Bugün" },
            { id: "yesterday", name: "Dün" },
            { id: "last7days", name: "Son 7 Gün" },
            { id: "last30days", name: "Son 30 Gün" },
            { id: "thisMonth", name: "Bu Ay" },
            { id: "lastMonth", name: "Geçen Ay" }
        ];

        init();

        function init() {
            loadAll();
            loadPersons();
            loadDevices();
            loadCameras();
        }

        function openFile(contentType, data) {
            if (!data) {
                AlertService.error('Resim verisi bulunamadı');
                return;
            }

            // Eğer data zaten base64 formatında değilse, base64'e çevir
            var base64Data = data;
            if (!data.startsWith('data:')) {
                base64Data = 'data:' + contentType + ';base64,' + data;
            }

            // Modal aç
            $uibModal.open({
                animation: true,
                template: '<div class="modal-header"><h3 class="modal-title">Resim Görüntüle</h3><button type="button" class="close" ng-click="close()"><span>&times;</span></button></div><div class="modal-body" style="text-align:center;padding:0;"><div style="padding:2rem;"><img ng-src="' + base64Data + '" style="max-width:100%;max-height:80vh;" ng-load="adjustModalSize()"></div></div>',
                controller: ['$scope', '$uibModalInstance', '$timeout', function($scope, $uibModalInstance, $timeout) {
                    $scope.close = function() {
                        $uibModalInstance.close();
                    };
                    
                    $scope.adjustModalSize = function() {
                        $timeout(function() {
                            var modalBody = document.querySelector('.image-modal .modal-body');
                            var img = modalBody.querySelector('img');
                            if (img) {
                                var modalDialog = document.querySelector('.image-modal .modal-dialog');
                                modalDialog.style.width = (img.naturalWidth + 60) + 'px'; // 60px padding için
                                modalDialog.style.maxWidth = '90vw'; // Maksimum genişlik sınırı
                            }
                        }, 100); // Resmin yüklenmesi için kısa bir bekleme
                    };
                }],
                windowClass: 'image-modal'
            });
        }

        function loadPersons() {
            Person.query(function(result) {
                vm.persons = result;
            });
        }

        function loadDevices() {
            Device.query(function(result) {
                vm.devices = result;
            });
        }

        function loadCameras() {
            Camera.query(function(result) {
                vm.cameras = result;
            });
        }

        function sort() {
            var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            if (vm.predicate !== 'id') {
                result.push('id');
            }
            return result;
        }

        function loadAll () {
            var params = {
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            };

            if (vm.selectedPersonId) {
                params.personId = vm.selectedPersonId;
            }

            if (vm.selectedDateFilter) {
                var today = new Date();
                var filterDate = new Date();
                filterDate.setDate(today.getDate() - parseInt(vm.selectedDateFilter.id));
                params.fromDate = filterDate.toISOString();
            }

            Alarm.query(params, onSuccess, onError);
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.alarms = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        function search() {
            if (!vm.page) {
                vm.page = 1;
            }
            var data = {
                page: vm.page - 1,
                size: vm.itemsPerPage,
                sort: vm.predicate + "," + (vm.reverse ? "asc" : "desc")
            };

            if (vm.selectedPersonId) {
                data.personId = vm.selectedPersonId;
            }
            if (vm.selectedAlarmType) {
                data.alarmType = vm.selectedAlarmType;
            }
            if (vm.selectedFalseAlarm) {
                data.falseAlarm = vm.selectedFalseAlarm === "true";
            }
            if (vm.selectedProcessed) {
                data.processed = vm.selectedProcessed === "true";
            }
            if (vm.selectedDateFilter) {
                var today = new Date();
                var startDate = new Date();
                switch(vm.selectedDateFilter) {
                    case "today":
                        startDate.setHours(0, 0, 0, 0);
                        data.startDate = startDate.toISOString();
                        data.endDate = today.toISOString();
                        break;
                    case "yesterday":
                        startDate.setDate(startDate.getDate() - 1);
                        startDate.setHours(0, 0, 0, 0);
                        data.startDate = startDate.toISOString();
                        data.endDate = new Date(startDate.setHours(23, 59, 59, 999)).toISOString();
                        break;
                    case "last7days":
                        startDate.setDate(startDate.getDate() - 7);
                        data.startDate = startDate.toISOString();
                        data.endDate = today.toISOString();
                        break;
                    case "last30days":
                        startDate.setDate(startDate.getDate() - 30);
                        data.startDate = startDate.toISOString();
                        data.endDate = today.toISOString();
                        break;
                    case "thisMonth":
                        startDate.setDate(1);
                        startDate.setHours(0, 0, 0, 0);
                        data.startDate = startDate.toISOString();
                        data.endDate = today.toISOString();
                        break;
                    case "lastMonth":
                        startDate.setMonth(startDate.getMonth() - 1);
                        startDate.setDate(1);
                        startDate.setHours(0, 0, 0, 0);
                        data.startDate = startDate.toISOString();
                        var lastDayOfMonth = new Date(startDate.getFullYear(), startDate.getMonth() + 1, 0);
                        lastDayOfMonth.setHours(23, 59, 59, 999);
                        data.endDate = lastDayOfMonth.toISOString();
                        break;
                }
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.alarms = data;
                vm.page = 1;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }

            Alarm.search(data, onSuccess, onError);
        }
    }
})();
