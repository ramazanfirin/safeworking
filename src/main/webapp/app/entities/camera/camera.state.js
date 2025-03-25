(function() {
    'use strict';

    angular
        .module('safeworkingApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('camera', {
            parent: 'entity',
            url: '/camera',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'safeworkingApp.camera.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/camera/cameras.html',
                    controller: 'CameraController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('camera');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('camera-detail', {
            parent: 'camera',
            url: '/camera/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'safeworkingApp.camera.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/camera/camera-detail.html',
                    controller: 'CameraDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('camera');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Camera', function($stateParams, Camera) {
                    return Camera.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'camera',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('camera-detail.edit', {
            parent: 'camera-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/camera/camera-dialog.html',
                    controller: 'CameraDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Camera', function(Camera) {
                            return Camera.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('camera.new', {
            parent: 'camera',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/camera/camera-dialog.html',
                    controller: 'CameraDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                username: null,
                                password: null,
                                rtspUrl: null,
                                channel: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('camera', null, { reload: 'camera' });
                }, function() {
                    $state.go('camera');
                });
            }]
        })
        .state('camera.edit', {
            parent: 'camera',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/camera/camera-dialog.html',
                    controller: 'CameraDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Camera', function(Camera) {
                            return Camera.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('camera', null, { reload: 'camera' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('camera.delete', {
            parent: 'camera',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/camera/camera-delete-dialog.html',
                    controller: 'CameraDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Camera', function(Camera) {
                            return Camera.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('camera', null, { reload: 'camera' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
