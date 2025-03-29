(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .controller('AlarmController', AlarmController);

    AlarmController.$inject = ['$state', 'DataUtils', 'Alarm', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'Person', '$uibModal'];

    function AlarmController($state, DataUtils, Alarm, ParseLinks, AlertService, paginationConstants, pagingParams, Person, $uibModal) {

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
        vm.selectedPersonId = null;
        vm.selectedDateFilter = null;
        vm.persons = [];
        vm.dateFilters = [
            { id: '1', name: 'Son 1 Gün' },
            { id: '7', name: 'Son 1 Hafta' },
            { id: '30', name: 'Son 1 Ay' }
        ];

        loadAll();
        loadPersons();

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
            vm.page = 1;
            
            // Özel arama parametreleri
            var searchParams = {
                page: 0,
                size: vm.itemsPerPage,
                sort: sort()
            };

            // Kişi filtresi
            if (vm.selectedPersonId) {
                searchParams.personId = vm.selectedPersonId;
            }

            // Tarih filtresi
            if (vm.selectedDateFilter) {
                var today = new Date();
                var filterDate = new Date();
                filterDate.setDate(today.getDate() - parseInt(vm.selectedDateFilter.id));
                searchParams.fromDate = filterDate.toISOString();
            }

            // Özel REST endpoint'i çağır
            Alarm.search(searchParams, function(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.alarms = data;
                vm.page = 1;
            }, function(error) {
                AlertService.error(error.data.message);
            });
        }
    }
})();
