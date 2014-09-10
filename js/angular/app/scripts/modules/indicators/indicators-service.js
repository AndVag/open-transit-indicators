'use strict';

angular.module('transitIndicators')
.factory('OTIIndicatorsService',
        ['$q', '$http', '$resource',
        function ($q, $http, $resource) {

    var otiIndicatorsService = {};
    var nullVersion = 0;

    otiIndicatorsService.Indicator = $resource('/api/indicators/:id/ ', {id: '@id'}, {
        'update': {
            method: 'PATCH',
            url: '/api/indicators/:id/ '
        }
    });

    /**
     * Thin wrapper for Indicator used in the controller for setting the map properties
     */
    otiIndicatorsService.IndicatorConfig = function (config) {
        this.version = config.version || nullVersion;
        this.type = config.type;
        this.sample_period = config.sample_period;
        this.aggregation = config.aggregation;
    };

    /**
     * Create windshaft urls for leaflet map
     *
     * @param indicator: otiIndicatorsService.Indicator instance
     * @param filetype: String, either png or utfgrid
     */
    otiIndicatorsService.getIndicatorUrl = function (filetype) {
        var url = otiIndicatorsService.getWindshaftHost();
        url += '/tiles/transit_indicators/{version}/{type}/{sample_period}/{aggregation}' +
               '/{z}/{x}/{y}';
        url += (filetype === 'utfgrid') ? '.grid.json?interactivity=value' : '.png';
        return url;
    };

    /**
     * Get the current indicator version
     *
     * @param callback: function to call after request is made, has a single argument 'version'
     */
    otiIndicatorsService.getIndicatorVersion = function (callback) {
        $http.get('/api/indicator-version/').success(function (data) {
            var version = data.current_version || nullVersion;
            callback(version);
        }).error(function (error) {
            console.error('getIndicatorVersion:', error);
            callback(nullVersion);
        });
    };

    otiIndicatorsService.getIndicatorTypes = function () {
        var dfd = $q.defer();
        $http.get('/api/indicator-types/').success(function (data) {
            dfd.resolve(data);
        }).error(function (error) {
            console.error('OTIIndicatorService.getIndicatorTypes', error);
            dfd.resolve({});
        });
        return dfd.promise;
    };

    otiIndicatorsService.getIndicatorAggregationTypes = function () {
        var dfd = $q.defer();
        $http.get('/api/indicator-aggregation-types/').success(function (data) {
            dfd.resolve(data);
        }).error(function (error) {
            console.error('OTIIndicatorService.getIndicatorAggregationTypes', error);
            dfd.resolve({});
        });
        return dfd.promise;
    };

    otiIndicatorsService.getSamplePeriodTypes = function () {
        var dfd = $q.defer();
        $http.get('/api/sample-period-types/').success(function (data) {
            dfd.resolve(data);
        }).error(function (error) {
            console.error('OTIIndicatorService.getSamplePeriodTypes', error);
            dfd.resolve({});
        });
        return dfd.promise;
    };

    return otiIndicatorsService;
}]);
