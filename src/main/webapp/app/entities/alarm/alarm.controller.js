(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .controller('AlarmController', AlarmController);

    AlarmController.$inject = ['$state', 'DataUtils', 'Alarm', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'Person'];

    function AlarmController($state, DataUtils, Alarm, ParseLinks, AlertService, paginationConstants, pagingParams, Person) {

        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.openFile = DataUtils.openFile;
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
