/*'use strict';

var babybox = angular.module('babybox');

babybox.controller('SlidingMenuController', function($scope, $routeParams, $location, userInfoService, articleService){
    
    //
    // sliding menu control
    // http://startbootstrap.com/templates/simple-sidebar/#
    //
    
    $scope.toggleMenu = function() {
        if ($("#wrapper").hasClass("toggled")) {
            $("#slider-menu-backdrop").removeClass("modal-backdrop");
        } else {
            $("#slider-menu-backdrop").addClass("modal-backdrop");
        }
        
        //e.preventDefault;
        $("#wrapper").toggleClass("toggled");
    }
    
    //
    // user info
    //
    
    //$scope.userInfo = userInfoService.UserInfo.get();
    //$scope.userTargetProfile = userInfoService.UserTargetProfile.get();
    
    $scope.set_background_image = function() {
        return { background: 'url(/image/get-thumbnail-cover-image-by-id/'+$scope.userInfo.id+') center center no-repeat'};
    } 
    
    //
    // article categories
    //
    
    $scope.hotArticleCategories = [];
    $scope.soonMomsArticleCategories = [];
    $scope.articleCategories = articleService.AllArticleCategories.get(
        function(data) {
            angular.forEach(data, function(category, key){
                if(category.gp == 'HOT_ARTICLES') {
                    $scope.hotArticleCategories.push(category);
                } else if (category.gp == 'SOON_TO_BE_MOMS_ARTICLES') {
                    $scope.soonMomsArticleCategories.push(category);
                }
            });
        }
    );
    
});*/