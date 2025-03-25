'use strict';

describe('Controller Tests', function() {

    describe('Alarm Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockAlarm, MockDevice, MockPerson, MockCamera;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockAlarm = jasmine.createSpy('MockAlarm');
            MockDevice = jasmine.createSpy('MockDevice');
            MockPerson = jasmine.createSpy('MockPerson');
            MockCamera = jasmine.createSpy('MockCamera');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Alarm': MockAlarm,
                'Device': MockDevice,
                'Person': MockPerson,
                'Camera': MockCamera
            };
            createController = function() {
                $injector.get('$controller')("AlarmDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'safeworkingApp:alarmUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
