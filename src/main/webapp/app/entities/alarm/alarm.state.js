(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('alarm', {
            parent: 'entity',
            url: '/alarm?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'safeworkingApp.alarm.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alarm/alarms.html',
                    controller: 'AlarmController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('alarm');
                    $translatePartialLoader.addPart('alarmType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('alarm-detail', {
            parent: 'alarm',
            url: '/alarm/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'safeworkingApp.alarm.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/alarm/alarm-detail.html',
                    controller: 'AlarmDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('alarm');
                    $translatePartialLoader.addPart('alarmType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Alarm', function($stateParams, Alarm) {
                    return Alarm.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'alarm',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('alarm-detail.edit', {
            parent: 'alarm-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alarm/alarm-dialog.html',
                    controller: 'AlarmDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Alarm', function(Alarm) {
                            return Alarm.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('alarm.new', {
            parent: 'alarm',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alarm/alarm-dialog.html',
                    controller: 'AlarmDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                insertDate: null,
                                image: null,
                                imageContentType: null,
                                backGroundImage: null,
                                backGroundImageContentType: null,
                                imageFile: null,
                                alarmType: null,
                                alarmTypeValue: null,
                                falseAlarm: null,
                                processed: null,
                                note: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('alarm', null, { reload: 'alarm' });
                }, function() {
                    $state.go('alarm');
                });
            }]
        })
        .state('alarm.edit', {
            parent: 'alarm',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alarm/alarm-dialog.html',
                    controller: 'AlarmDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Alarm', function(Alarm) {
                            return Alarm.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('alarm', null, { reload: 'alarm' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('alarm.delete', {
            parent: 'alarm',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/alarm/alarm-delete-dialog.html',
                    controller: 'AlarmDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Alarm', function(Alarm) {
                            return Alarm.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('alarm', null, { reload: 'alarm' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
