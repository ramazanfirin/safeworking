(function() {
    'use strict';
    angular
        .module('safeworkingApp')
        .factory('Alarm', Alarm);

    Alarm.$inject = ['$resource', 'DateUtils'];

    function Alarm ($resource, DateUtils) {
        var resourceUrl =  'api/alarms/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.insertDate = DateUtils.convertDateTimeFromServer(data.insertDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
