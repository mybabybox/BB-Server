'use strict';

angular.module('babybox', [
  'ngResource',
  'ngRoute',
  'ui.bootstrap',
  'ui.bootstrap.tpls',
  'angularFileUpload',
  'validator',
  'validator.rules',
  'angularSpinner',
  'ui.utils'
])
  .config(function ($routeProvider, $locationProvider) {
  })
  

//
// noCache for browser
//

var babybox = angular.module('babybox');

var URL_IGNORE = [
    "tracking",
    "template", 
    "assets", 
    "image", 
    "photo", 
    "modal"
];

babybox.config(['$httpProvider', function($httpProvider) {
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
    
//babybox.config(['$httpProvider', function($httpProvider) {
//    if (!$httpProvider.defaults.headers.get) {
//        $httpProvider.defaults.headers.get = {};    
//    }
//    $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';
//    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache'; 
//    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
//}]);