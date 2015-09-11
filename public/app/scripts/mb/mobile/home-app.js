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
      .when('/', {
        templateUrl: '/assets/app/views/mobile/home/home.html'
      })
      //.when('/my-magazine', {
      //  templateUrl: '/assets/app/views/mobile/home/my-magazine.html'
      //})
      .when('/about',{
    	templateUrl: '/assets/app/views/mobile/home/about-me.html',
    	controller: 'UserAboutController'
      })
      .when('/about/:tab',{
        templateUrl: '/assets/app/views/mobile/home/about-me.html',
        controller: 'UserAboutController'
      })
      .when('/profile/:id',{
    	templateUrl: '/assets/app/views/mobile/home/profile-page.html',
    	controller : 'UserProfileController'  
      })
      .when('/communities-discover',{
        templateUrl: '/assets/app/views/mobile/home/communities-discover-page.html'
      })
      .when('/communities-discover/:tab',{
        templateUrl: '/assets/app/views/mobile/home/communities-discover-page.html'
      })
      .when('/community/:id',{
        templateUrl: '/assets/app/views/mobile/home/community-page.html',
        controller : 'CommunityPageController'  
      })
      .when('/community/:id/:tab',{
    	templateUrl: '/assets/app/views/mobile/home/community-page.html',
    	controller : 'CommunityPageController'  
      })
      .when('/post-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/mobile/home/post-landing-page.html',
        controller : 'PostLandingController'  
      })
      .when('/qna-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/mobile/home/qna-landing-page.html',
        controller : 'QnALandingController'  
      })
      .when('/business/community/:id',{
        templateUrl: '/assets/app/views/mobile/home/business-community-page.html',
        controller : 'BusinessCommunityPageController'  
      })
      .when('/business/community/:id/:tab',{
        templateUrl: '/assets/app/views/mobile/home/business-community-page.html',
        controller : 'BusinessCommunityPageController'  
      })
      .when('/business-post-landing/id/:id/communityId/:communityId',{
        templateUrl: '/assets/app/views/mobile/home/business-post-landing-page.html',
        controller : 'PostLandingController'  
      })
      .when('/message-list',{
    	templateUrl: '/assets/app/views/mobile/home/message-list.html',
    	controller: 'UserConversationController'  
      })
      .when('/start-conversation/:id',{
    	templateUrl: '/assets/app/views/mobile/home/message.html',
    	controller: 'UserConversationController'  
      })
      .when('/open-conversation/:id',{
    	templateUrl: '/assets/app/views/mobile/home/message.html',
    	controller: 'UserConversationController'  
      })
      .when('/game',{
        templateUrl: '/assets/app/views/mobile/home/game-page.html',
        controller: 'GameController'
      })
      .when('/game-gift/:id',{
        templateUrl: '/assets/app/views/mobile/home/game-gift-page.html',
        controller: 'GameGiftController'
      })
      .when('/game-rules',{
        templateUrl: '/assets/app/views/mobile/home/game-rules-page.html'
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