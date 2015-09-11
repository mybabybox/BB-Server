'use strict';

angular.module('minibean', [
  'infinite-scroll',
  'ngResource',
  'ngRoute',
  'xeditable',
  'ui.bootstrap',
  'ui.bootstrap.tpls',
  'angularFileUpload',
  'ui.bootstrap.datetimepicker',
  'validator',
  'validator.rules',
  'angularSpinner',
  'truncate',
  'ui.tinymce',
  'ui.utils',
  'ngSanitize',
  'angularMoment',
  'wu.masonry',
  'pasvaz.bindonce'
])
  .config(function ($routeProvider, $locationProvider) {
    $routeProvider
      .when('/',{
        templateUrl: '/assets/app/views/mobile/schools/schools.html', 
        controller : 'ShowSchoolsController' 
      })
      .when('/pn',{
        templateUrl: '/assets/app/views/mobile/schools/pns.html',
        controller : 'ShowSchoolsController' 
      })
      .when('/pn/district/:districtId',{
        templateUrl: '/assets/app/views/mobile/schools/pns.html',
        controller : 'ShowSchoolsController' 
      })
      .when('/pn/newsfeed',{
        templateUrl: '/assets/app/views/mobile/schools/pn-newsfeed-page.html',
        controller : 'SchoolsNewsfeedController' 
      })
      .when('/pn/ranking',{
        templateUrl: '/assets/app/views/mobile/schools/pn-ranking-page.html',
        controller : 'SchoolsRankingController' 
      })
      .when('/pn/:id',{
        templateUrl: '/assets/app/views/mobile/schools/pn-page.html',
        controller : 'PNPageController' 
      })
      .when('/kg',{
        templateUrl: '/assets/app/views/mobile/schools/kgs.html',
        controller : 'ShowSchoolsController' 
      })
      .when('/kg/district/:districtId',{
        templateUrl: '/assets/app/views/mobile/schools/kgs.html',
        controller : 'ShowSchoolsController' 
      })
      .when('/kg/newsfeed',{
        templateUrl: '/assets/app/views/mobile/schools/kg-newsfeed-page.html',
        controller : 'SchoolsNewsfeedController' 
      })
      .when('/kg/ranking',{
        templateUrl: '/assets/app/views/mobile/schools/kg-ranking-page.html',
        controller : 'SchoolsRankingController' 
      })
      .when('/kg/:id',{
        templateUrl: '/assets/app/views/mobile/schools/kg-page.html',
        controller : 'KGPageController' 
      })
      .when('/error', {
    	  templateUrl: '/assets/app/views/error-page.html',
      })
      .otherwise({
          redirectTo: '/'
      });
    $locationProvider
      .html5Mode(false)
      .hashPrefix('!');
  })
  .run(function(editableOptions) {
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
  });

//
// noCache for browser
//

var minibean = angular.module('minibean');

var URL_IGNORE = [
    "template", 
    "assets", 
    "image", 
    "photo", 
    "modal"
];

minibean.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('noCacheInterceptor');
    }]).factory('noCacheInterceptor', function () {
            return {
                request: function (config) {
                    //console.log(config.method + " " + config.url);
                    if(config.method=='GET'){
                        var url = config.url.toLowerCase();
                        var containsUrlIgnore = false;
                        for (var i in URL_IGNORE) {
                            if (url.indexOf(URL_IGNORE[i]) != -1) {
                                containsUrlIgnore = true;
                            }
                        }
                        if (!containsUrlIgnore) {
                            var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                            config.url = config.url+separator+'noCache=' + new Date().getTime();
                            //console.log(config.method + " " + config.url);
                        }
                    }
                    return config;
               }
           };
    });
    
//minibean.config(['$httpProvider', function($httpProvider) {
//    if (!$httpProvider.defaults.headers.get) {
//        $httpProvider.defaults.headers.get = {};    
//    }
//    $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';
//    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache'; 
//    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
//}]);