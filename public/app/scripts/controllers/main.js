'use strict';

var mybox = angular.module('mybox');

mybox.controller('FrontPageController', 
		function($scope, $route, feedService, ngDialog, userInfo, viewService) {
	$scope.userInfo = userInfo;
	$scope.products = feedService.getFeedProduct.get();
	$scope.open = function (product) {
		$scope.product = product;
		viewService.viewProduct.get({id:product.id});
		ngDialog.open({
			template: '/assets/app/views/mybox/product-dialog.html',
			className: 'ngdialog-theme-plain custom-width ',
			controller: 'ProductController',
			scope: $scope                
		});
	};
});

mybox.controller('ProductPageController', 
		function($scope, $route, $http, likeService, userService, productService, product, userInfo) {
	$scope.product = product;
	$scope.userInfo = userInfo;
});

mybox.controller('ProductController', 
		function($scope, $route, $http, likeService, userService, productService) {
	console.log($scope.product);
	$scope.collections = [];
	$scope.open = false;
	$scope.product = productService.getProductInfo.get({id:$scope.product.id});
	if($scope.product.oid = $scope.userInfo.id){
		$scope.collections = userService.getUserCollection.get({id:$scope.userInfo.id});
	}
	$scope.like_product = function(id) {
		likeService.likeProduct.get({id:id});
		$scope.product.isLike = !$scope.product.isLike;
		$scope.product.nol++;
	}
	$scope.unlike_product = function(id) {
		likeService.unLikeProduct.get({id:id});
		$scope.product.isLike = !$scope.product.isLike;
		$scope.product.nol--;
	}
	$scope.colectionValue = "";
	$scope.collection = {};
	$scope.addCollection = function(text) {
		$scope.collection = {
				"cn" :  text,
				"id" : 0,
		}
		$scope.collections.push($scope.collection);
		$scope.open = false;
	}
	$scope.selectValue = function(collection){
		$scope.collection = collection;
		$scope.colectionValue = collection.cn;
		$scope.open = false;
	}
	
	$scope.add_product_to_collection = function(){
		var data = {
				"product_id" : $scope.product.id,
				"collectionId":  $scope.collection.id,
				"collectionName":  $scope.collection.cn,
		};
		$http.post('/product/add-to-collection', data) 
		.success(function(response) {
			console.log(response);
		});
	}
	
	$scope.onFollowUser = function() {
		followService.followUser.get({id:$scope.product.oid});
		$scope.product.ifu = !$scope.product.ifu;
	}
	$scope.onUnFollowUser = function() {
		followService.unFollowUser.get({id:$scope.product.oid});
		$scope.product.ifu = !$scope.product.ifu;
	}
});

mybox.controller('CommentOnProductController', 
		function($scope, $route, $http, likeService) {
	$scope.formData = {};

	$scope.comArray=[];
	
	$scope.submit = function() {
		var data = {
				"product_id" : $scope.product.id,
				"commentText" : $scope.formData.comment,
		};
		
		console.log($scope.formData.comment);
		console.log($scope.userInfo);
	
		$http.post('/product/comment', data) 
		.success(function(response) {
			console.log(response);
			 $scope.comArray.push({
			        comment:$scope.formData.comment,
			        name: $scope.userInfo.displayName,
			        id: $scope.userInfo.id
			        
		    });
			 $scope.formData.comment="";
		});
	}

});


mybox.controller('ProfileController', 
		function($scope, $route, profileUser, userService, userInfo, followService) {
	$scope.userInfo = userInfo;
	$scope.user = profileUser;
	$scope.products = userService.getUserProduct.get({id:profileUser.id});
	$scope.collections = userService.getUserCollection.get({id:profileUser.id});
	$scope.onFollowUser = function() {
		followService.followUser.get({id:profileUser.id});
		$scope.user.ifu = !$scope.user.ifu;
		$scope.user.n_fr++;
	}
	$scope.onUnFollowUser = function() {
		followService.unFollowUser.get({id:profileUser.id});
		$scope.user.ifu = !$scope.user.ifu;
		$scope.user.n_fr--;
	}

});


mybox.controller('CreateCollectionController', 
		function($scope, $route, $http, usSpinnerService) {
	$scope.formData = {};
	$scope.createCollection = function() {
		console.log($scope.formData);
		usSpinnerService.spin('loading...');
		$http.post('/create-collection', $scope.formData)
		.success(function(data){
			usSpinnerService.stop('loading...');
		});
	}
});

mybox.controller('CreateProductController',function($scope, $location, $http, $upload, $validator, usSpinnerService, userInfo){
	$scope.userInfo = userInfo;
	$scope.formData = {};
	$scope.selectedFiles =[];
	$scope.submitBtn = "建立社群";
	$scope.submit = function() {
		console.log($scope.formData);
		$validator.validate($scope, 'formData')
		.success(function () {
			usSpinnerService.spin('載入中...');
			$upload.upload({
				url: '/create-product',
				method: 'POST',
				file: $scope.selectedFiles,
				data: $scope.formData,
				fileFormDataName: 'photo'
			}).progress(function(evt) {
				$scope.submitBtn = "請稍候...";
				usSpinnerService.stop('載入中...');
			}).success(function(data, status, headers, config) {
				$scope.submitBtn = "完成";
				usSpinnerService.stop('loading...');
			}).error(function(data, status, headers, config) {
				if( status == 505 ) {
					$scope.uniqueName = true;
					usSpinnerService.stop('載入中...');
					$scope.submitBtn = "再試一次";
				}  
			});
		})
		.error(function () {
			prompt("建立社群失敗。請重試");
		});
	}

	$scope.onFileSelect = function($files) {
		$scope.selectedFiles.push($files[0]);
		$scope.formData.photo = 'photo';
	}

});



/*minibean.controller('PrivacySettingsController', function($scope, $http, userSettingsService, usSpinnerService) {

    $scope.privacyFormData = userSettingsService.privacySettings.get();
    $scope.privacySettingsSaved = false;
    $scope.updatePrivacySettings = function() {
        usSpinnerService.spin('loading...');
        return $http.post('/save-privacy-settings', $scope.privacyFormData)
            .success(function(data){
                $scope.privacySettingsSaved = true;
                $scope.get_header_metaData();
                usSpinnerService.stop('loading...');
            }).error(function(data, status, headers, config) {
                prompt(data);
            });
    }
});

minibean.controller('EdmSettingsController', function($scope, $http, userSettingsService, usSpinnerService) {

    $scope.edmFormData = userSettingsService.edmSettings.get();
    $scope.edmSettingsSaved = false;
    $scope.updateEdmSettings = function() {
        usSpinnerService.spin('loading...');
        return $http.post('/save-edm-settings', $scope.edmFormData)
            .success(function(data){
                $scope.edmSettingsSaved = true;
                $scope.get_header_metaData();
                usSpinnerService.stop('loading...');
            }).error(function(data, status, headers, config) {
                prompt(data);
            });
    }
});

minibean.controller('UserAboutController',function($routeParams, $scope, $http, userAboutService, locationService, profilePhotoModal, usSpinnerService) {

	$scope.get_header_metaData();

	$scope.selectNavBar('HOME', -1);

	var tab = $routeParams.tab;

    $scope.selectedSubTab = 1;
	if (tab == 'activities' || tab == undefined) {
		$scope.selectedTab = 1;
	} else if (tab == 'communities') {
		$scope.selectedTab = 2;
	} else if (tab == 'myCommunities') {
		$scope.selectedTab = 2;
	} else if (tab == 'friends') {
        $scope.selectedTab = 3;
    } else if (tab == 'bookmarks') {
        $scope.selectedTab = 4;
    } else {
        $scope.selectedTab = 1;
    }

	$scope.profileImage = "/image/get-profile-image";
	$scope.coverImage = "/image/get-cover-image";
	$scope.userAbout = userAboutService.UserAbout.get();

	$scope.genders = DefaultValues.genders;
	$scope.parentBirthYears = DefaultValues.parentBirthYears;
	$scope.childBirthYears = DefaultValues.childBirthYears;
    $scope.locations = locationService.allDistricts.get();

    $scope.profileDataSaved = false;
	$scope.updateUserProfileData = function() {
        if ($("#signup-info").valid()) {
            var formData = {
                "parent_firstname"	: $scope.userAbout.firstName,
                "parent_lastname"  	: $scope.userAbout.lastName,
                "parent_displayname": $scope.userAbout.displayName,
                "parent_aboutme" 	: $scope.userAbout.userInfo.aboutMe,
                "parent_birth_year" : $scope.userAbout.userInfo.birthYear,
                "parent_location" 	: $scope.userAbout.userInfo.location.id
            };

            usSpinnerService.spin('loading...');
    		return $http.post('/updateUserProfileData', formData)
                .success(function(data){
                    $scope.profileDataSaved = true;
                    $scope.get_header_metaData();
                    usSpinnerService.stop('loading...');
                }).error(function(data, status, headers, config) {
                    prompt(data);
                });
        }
	}

	$scope.isProfileOn = true; 
	$scope.isCoverOn = !$scope.isProfileOn;
	$scope.openProfilePhotoModal = function() {
		PhotoModalController.url = "image/upload-profile-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal',
			 controller: PhotoModalController
		},function() {
			$scope.profileImage = $scope.profileImage + "?q="+ Math.random();
		});
		PhotoModalController.isProfileOn = true;
	}

	$scope.openCoverPhotoModal = function() {
		PhotoModalController.url = "image/upload-cover-photo";
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal',
			 controller: PhotoModalController
		},function() {
			$scope.coverImage = $scope.coverImage + "?q="+ Math.random();
		});
		PhotoModalController.isProfileOn = false;
	}
});

minibean.controller('EditCommunityController',function($scope,$q, $location,$routeParams, $http, usSpinnerService, iconsService, editCommunityPageService, $upload, profilePhotoModal){

	$scope.submitBtn = "儲存";
	$scope.community = editCommunityPageService.EditCommunityPage.get({id:$routeParams.id}, 
		function(response) {
		},
		function(rejection) {
			if(rejection.status === 500) {
				$location.path('/error');
			}
			return $q.reject(rejection);
		}
	);

	$scope.community.typ = DefaultValues.communityType;

	$scope.icons = iconsService.getCommunityIcons.get();

	$scope.select_icon = function(img, text) {
		$scope.icon_chosen = img;
		$scope.icon_text = text;
		$scope.isChosen = true;
		$scope.community.icon = img;
	}

	$scope.updateGroupProfileData = function(data) {
		usSpinnerService.spin('loading...');
		return $http.post('/updateGroupProfileData', $scope.community)
            .success(function(data){
    			$scope.submitBtn = "完成";
    			usSpinnerService.stop('loading...');
    		});
	}

	$scope.openGroupCoverPhotoModal = function(id) {
		PhotoModalController.url = "image/upload-cover-photo-group/"+id;
		profilePhotoModal.OpenModal({
			 templateUrl: 'change-profile-photo-modal',
			 controller: PhotoModalController
		},function() {

		});
	}

});

minibean.controller('CreateCommunityController',function($scope, $location, $http, $upload, $validator, iconsService, usSpinnerService){

	$scope.formData = {};
	$scope.selectedFiles =[];
	$scope.submitBtn = "建立社群";
	$scope.submit = function() {
		 $validator.validate($scope, 'formData')
		    .success(function () {
		    	usSpinnerService.spin('載入中...');
		    	$upload.upload({
					url: '/createCommunity',
					method: 'POST',
					file: $scope.selectedFiles[0],
					data: $scope.formData,
					fileFormDataName: 'cover-photo'
				}).progress(function(evt) {
					$scope.submitBtn = "請稍候...";
					usSpinnerService.stop('載入中...');
			    }).success(function(data, status, headers, config) {
			    	$scope.submitBtn = "完成";
			    	usSpinnerService.stop('loading...');
			    	if ($scope.formData.communityType == 'BUSINESS') {
			    	    $location.path('/business/community/'+data);
			    	} else {
			    	    $location.path('/community/'+data);
			    	}
			    	$("#myModal").modal('hide');
			    }).error(function(data, status, headers, config) {
			    	if( status == 505 ) {
			    		$scope.uniqueName = true;
			    		usSpinnerService.stop('載入中...');
			    		$scope.submitBtn = "再試一次";
			    	}  
			    });
		    })
		    .error(function () {
		        prompt("建立社群失敗。請重試");
		    });
	}

	$scope.icons = iconsService.getCommunityIcons.get();

	$scope.select_icon = function(img, text) {
		$scope.icon_chosen = img;
		$scope.icon_text = text;
		$scope.isChosen = true;
		$scope.formData.icon = img;
	}

	$scope.onFileSelect = function($files) {
		$scope.selectedFiles = $files;
		$scope.formData.photo = 'cover-photo';
	}

});

///////////////////////// Suggested Friends Widget Service Start //////////////////////////////////

minibean.controller('SuggestedFriendsUtilityController',function($scope, unFriendService, usSpinnerService, sendInvitation, friendsService, userInfoService, $http){

	$scope.result = friendsService.SuggestedFriends.get();
	$scope.isLoadingEnabled = false;
	$scope.send_invite = function(id) {
		$scope.isLoadingEnabled = true;
		this.invite = sendInvitation.inviteFriend.get({id:id}, function(data) {
			$scope.isLoadingEnabled = false;
			angular.forEach($scope.result.friends, function(request, key){
				if(request.id == id) {
					request.isP = true;
				}
			});
		});
	}

	$scope.un_friend = function(id) {
		$scope.isLoadingEnabled = true;
		this.unFriendHim = unFriendService.doUnfriend.get({id:id}, function(data) {
			$scope.isLoadingEnabled = false;
			angular.forEach($scope.result.friends, function(request, key){
				if(request.id == id) {
					request.isF = true;
				}
			});
		});
	}

});

minibean.controller('CommunityMembersController',function($scope, $routeParams, membersWidgetService, $http){

	var id = $routeParams.id;
    if ($scope._commId != undefined) {
    	id = $scope._commId;		// set in pn page
    }

    // paged filtered data
    $scope.pagedMembers = [];

    $scope.currentPage = 1;
    $scope.itemsPerPage = DefaultValues.DEFAULT_MEMBERS_PER_PAGE;
    $scope.setMemberPage = function(page) {
        var begin = ((page - 1) * $scope.itemsPerPage);
        var end = begin + $scope.itemsPerPage;
        $scope.pagedMembers = $scope.result.members.slice(begin, end);
        $scope.currentPage = page;
    }

	$scope.result = membersWidgetService.NewCommunityMembers.get({id:id}, 
	   function() {
	       $scope.setMemberPage(1);
	   } 
	);
	$scope.showMembers = true;
	$scope.showAdmin = false;
	$scope.getAllMembers = function() {
		$scope.showMembers = true;
		$scope.showAdmin = false;
	}
	$scope.getAdmin = function() {
		$scope.showMembers = false;
		$scope.showAdmin = true;
	}

});

minibean.controller('RecommendedCommunityWidgetController',function($scope, usSpinnerService, sendJoinRequest, unJoinedCommunityWidgetService, userInfoService, $http){

	$scope.result = unJoinedCommunityWidgetService.UnJoinedCommunities.get();
	$scope.send_request = function(id) {
        this.invite = sendJoinRequest.sendRequest.get({id:id},
			function(data) {
                angular.forEach($scope.result.communities, function(request, key) {
                    if(request.id == id) {
                        request.isP = true;
                    }
                });
            }
        );
    }

});

minibean.controller('MyFriendsUtilityController',function($scope, userInfoService, friendsService, $http){

    $scope.result = friendsService.MyFriendsForUtility.get();

});

minibean.controller('UserFriendsUtilityController',function($scope, $routeParams, userInfoService, friendsService, $http){

    $scope.result = friendsService.UserFriendsForUtility.get({id:$routeParams.id});

});

minibean.controller('MyFriendsController',function($scope, friendsService, $http){

	$scope.result = friendsService.MyFriends.get();

});

minibean.controller('UserFriendsController',function($scope, $routeParams, friendsService, $http){

    $scope.result = friendsService.UserFriends.get({id:$routeParams.id});

});

minibean.controller('CommunitiesDiscoverController',function($scope, $routeParams, usSpinnerService, communitiesDiscoverService, sendJoinRequest){

    $scope.get_header_metaData();

    // cache
    $scope.communities = [];
    $scope.communityCategoriesMap = [];

    $scope.topicCommunityCategoriesMap = [];
    $scope.zodiacYearCommunities = [];
    $scope.zodiacYearMonthCommunityCategoriesMap = [];
    $scope.districtCommunities = [];
    $scope.otherCommunities = [];

    $scope.setSelectedSubTab = function (tab) {
        if ($scope.selectedSubTab == tab)
            return;

        $scope.selectedSubTab = tab;

        if (tab == 1) {
            if ($scope.zodiacYearCommunities.length == 0) {
                $scope.communities = [];
                usSpinnerService.spin('loading...');
                communitiesDiscoverService.ZodiacYearCommunities.get(
                    function(data) {
                        $scope.zodiacYearCommunities = data.communities;
                        $scope.communities = $scope.zodiacYearCommunities;
                        usSpinnerService.stop('loading...');
                    }
                );
            } else {
                $scope.communities = $scope.zodiacYearCommunities;
            } 
        } else if (tab == 2) {
            if ($scope.zodiacYearMonthCommunityCategoriesMap.length == 0) {
                $scope.communityCategoriesMap = []; 
                usSpinnerService.spin('loading...');
                communitiesDiscoverService.getZodiacYearMonthCommunityCategoriesMap.get({indexOnly:false}, 
                    function(data) {
                        $scope.zodiacYearMonthCommunityCategoriesMap = data; 
                        $scope.communityCategoriesMap = $scope.zodiacYearMonthCommunityCategoriesMap;
                        usSpinnerService.stop('loading...');
                    }
                );
            } else {
                $scope.communityCategoriesMap = $scope.zodiacYearMonthCommunityCategoriesMap;
            }
        } else if (tab == 3) {
            if ($scope.districtCommunities.length == 0) {
                $scope.communities = []; 
                usSpinnerService.spin('loading...');
                communitiesDiscoverService.DistrictCommunities.get(
                    function(data) {
                        $scope.districtCommunities = data.communities;
                        $scope.communities = $scope.districtCommunities;
                        usSpinnerService.stop('loading...');
                    }
                );
            } else {
                $scope.communities = $scope.districtCommunities;
            }
        } else if (tab == 4) {
            if ($scope.topicCommunityCategoriesMap.length == 0) {
                $scope.communityCategoriesMap = [];
                usSpinnerService.spin('loading...');
                communitiesDiscoverService.getSocialCommunityCategoriesMap.get({indexOnly:false}, 
                    function(data) {
                        $scope.topicCommunityCategoriesMap = data;
                        $scope.communityCategoriesMap = $scope.topicCommunityCategoriesMap;
                        usSpinnerService.stop('loading...');
                    }
                );
            } else {
                $scope.communityCategoriesMap = $scope.topicCommunityCategoriesMap;
            }
        } else if (tab == 5) {
            if ($scope.otherCommunities.length == 0) {
                $scope.communities = [];
                usSpinnerService.spin('loading...');
                communitiesDiscoverService.OtherCommunities.get(
                    function(data) {
                        $scope.otherCommunities = data.communities;
                        $scope.communities = $scope.otherCommunities;
                        usSpinnerService.stop('loading...');
                    }
                );    
            } else {
                $scope.communities = $scope.otherCommunities;
            }
        }
    }

    var tab = $routeParams.tab;
    if (tab == 'topic' || tab == undefined) {
        $scope.setSelectedSubTab(4);
    } else if (tab == 'zodiacYear') {
        $scope.setSelectedSubTab(1);
    } else if (tab == 'zodiacYearMonth') {
        $scope.setSelectedSubTab(2);
    } else if (tab == 'district') {
        $scope.setSelectedSubTab(3);
    } else {
        $scope.setSelectedSubTab(4);
    }

    $scope.joinCommunity = function(id) {
        usSpinnerService.spin('loading...');
        this.invite = sendJoinRequest.sendRequest.get({id:id},
            function(data) {
                if ($scope.selectedSubTab == 4 || $scope.selectedSubTab == 2) {
                    angular.forEach($scope.communityCategoriesMap, function(communityCategoryMap, key){
                        angular.forEach(communityCategoryMap.communities, function(community, key){
                            if(community.id == id) {
                                community.isP = true;
                                return;
                            }
                        });
                    });
                } else {
                    angular.forEach($scope.communities, function(community, key){
                        if(community.id == id) {
                            community.isP = true;
                            return;
                        }
                    });
                }
                usSpinnerService.stop('loading...');
            }
        );
    }

});

minibean.controller('CommunityWidgetController',function($scope, $routeParams, usSpinnerService, communityWidgetService, sendJoinRequest){

	$scope.myAdminCommunities = [];
	$scope.myAdminBusinessCommunities = [];
    $scope.myJoinedCommunities = [];
    $scope.myLikedBusinessCommunities = [];
	$scope.myCommunities = communityWidgetService.MyCommunities.get(
        function(data) {
            angular.forEach(data.communities, function(community, key) {
                if (community.isO) {
                    if (community.tp == 'BUSINESS') {
                        $scope.myAdminBusinessCommunities.push(community);
                    } else {
                        $scope.myAdminCommunities.push(community);
                    }
                } else {
                    if (community.tp == 'BUSINESS') {
                        $scope.myLikedBusinessCommunities.push(community);
                    } else {
                        $scope.myJoinedCommunities.push(community);
                    }
                }
            })
        }
	);

	$scope.send_request = function(id) {
		usSpinnerService.spin('loading...');
		this.invite = sendJoinRequest.sendRequest.get({id:id},
			function(data) {
				angular.forEach($scope.result.communities, function(request, key){
					if(request.id == id) {
						request.isP = true;
					}
				});
				usSpinnerService.stop('loading...');
			}
		);
	}

});

minibean.controller('UserCommunityWidgetController',function($scope, communityWidgetService){

	$scope.sysCommunities = [];
	$scope.myCommunities = [];
	$scope.myBusinessCommunities = [];
	$scope.result = communityWidgetService.MyCommunities.get({}, 
        	function(data) {
                angular.forEach($scope.result.communities, function(request, key){
                    if (request.sys) {
                        $scope.sysCommunities.push(request);
                    } else if (request.tp == 'BUSINESS') {
                        $scope.myBusinessCommunities.push(request);
                    } else {
                        $scope.myCommunities.push(request);
                    }
                });
            }
	);

	$scope.selectedTab = 1;

});

minibean.controller('CommunityWidgetByUserController',function($scope, $routeParams, usSpinnerService, sendJoinRequest, communityJoinService, communityWidgetByUserService){

    $scope.userJoinedCommunities = [];
    $scope.userLikedBusinessCommunities = [];
    $scope.userCommunitiesResult = communityWidgetByUserService.UserCommunities.get({id:$routeParams.id}, 
        function(data) {
            angular.forEach(data.communities, function(community, key) {
                if (community.tp == 'BUSINESS') {
                    $scope.userLikedBusinessCommunities.push(community);
                } else {
                    $scope.userJoinedCommunities.push(community);
                }
            })
        }
    );

	$scope.send_request = function(id) {
		usSpinnerService.spin('loading...');
		this.invite = sendJoinRequest.sendRequest.get({id:id},
				function(data) {
					angular.forEach($scope.userCommunitiesResult.communities, function(community, key){
						if(community.id == id) {
							community.isP = true;
						}
					});
					usSpinnerService.stop('loading...');
				}
		);
	}

});

minibean.controller('UserProfileController',function($scope, $routeParams, $location, profileService, friendsService, sendInvitation, unFriendService){

    $scope.get_header_metaData();

	$scope.$watch($routeParams.id, function (navigateTo) {
		if($routeParams.id  == $scope.userInfo.id){
			 $location.path("/about/activities");
		}
	});

	$scope.isLoadingEnabled = false;
	$scope.selectedTab = 1;
	$scope.selectedSubTab = 1; 

	$scope.navigateTo = function (navigateTo) {
		$scope.active = navigateTo;
		if(navigateTo === 'friends') {
			$scope.friends = friendsService.UserFriends.get({id:$routeParams.id});
		}

	}
	$scope.send_invite = function(id) {
		$scope.isLoadingEnabled = true;
		this.invite = sendInvitation.inviteFriend.get({id:id}, function(data) {
			$scope.isLoadingEnabled = false;
			$scope.profile.isP = true;
		});
	}

	$scope.un_friend = function(id) {
		$scope.isLoadingEnabled = true;
		this.unFriendHim = unFriendService.doUnfriend.get({id:id}, function(data) {
			$scope.isLoadingEnabled = false;
			$scope.profile.isf = false;
		});
	}

	$scope.active = "about";
	$scope.profile = profileService.Profile.get({id:$routeParams.id});
});

minibean.controller('SearchPageController', function($scope, $routeParams, communityPageService, $http, communitySearchPageService, usSpinnerService){

	$scope.highlightText="";
	$scope.highlightQuery = "";
	$scope.community = communityPageService.Community.get({id:$routeParams.id}, function(){
		usSpinnerService.stop('loading...');
	});

	$scope.$watch('search_trigger', function(query) {
	       if(query != undefined) {
	    	   $scope.search_and_highlight(query);
	       }
	   });

	var offset = 0;
	var searchPost = true;
	$scope.search_and_highlight = function(query) {
		if ($scope.isBusy) return;
		var id = $routeParams.id;
		$scope.isBusy = true;
		if(searchPost){
			$scope.community.searchPosts = [];
			searchPost = false;
		}

		communitySearchPageService.GetPostsFromIndex.get({community_id : id , query : query, offset:offset}, function( data ) {
			var posts = data;

			if(posts.length == 0) {
				$scope.community.searchPosts.length=0;
				$scope.noresult = "No Results Found";
			}
			if(data.length < DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT) {
				offset = -1;
				$scope.community.searchPosts.length=0;
				$scope.isBusy = false;
			}

			for (var i = 0; i < posts.length; i++) {
				$scope.community.searchPosts.push(posts[i]);
		    }
			$scope.isBusy = false;
			offset++;
			$scope.highlightText = query;
		});
	};

});

minibean.controller('PostLandingController', function($scope, $routeParams, $http, $upload, $timeout, $validator, 
		postFactory, postLandingService, communityPageService, postManagementService, usSpinnerService) {

	$scope.get_header_metaData();

	$scope.selectNavBar('HOME', -1);

    $scope.$on('$viewContentLoaded', function() {
        usSpinnerService.spin('loading...');
    });

    $scope.community = communityPageService.Community.get({id:$routeParams.communityId});

    $scope.post = postLandingService.postLanding.get({id:$routeParams.id,communityId:$routeParams.communityId}, 
    	function(data) {
    		$scope.noResult = false;
        	usSpinnerService.stop('loading...');

        	writeMetaTitleDescription(data.ptl, data.pt);
    	}, 
    	function(rejection) {
    		$scope.noResult = true;
    		usSpinnerService.stop('loading...');
		}
    );

    //
    // Below is copied completely from CommunityPageController
    // for js functions to handle comment, comment photo, like, bookmark etc
    //

    $scope.isLoadingEnabled = false;

    $scope.postPhoto = function() {
        $("#post-photo-id").click();
    }

    $scope.showMore = function(id) {
    	var posts = [ $scope.post ];
        postFactory.showMore(id, posts);
    }

    $scope.get_all_comments = function(id) {
    	var posts = [ $scope.post ];
        postFactory.getAllComments(id, posts, $scope);
    }

    $scope.deletePost = function(postId) {
    	var posts = [ $scope.post ];
        postFactory.deletePost(postId, posts);
    }

    $scope.deleteComment = function(commentId, post) {
        postFactory.deleteComment(commentId, post);
    }

    $scope.select_emoticon_comment = function(code, index) {
        postFactory.selectCommentEmoticon(code, index);
    }

    $scope.comment_on_post = function(id, commentText) {
        // first convert to links
        //commentText = convertText(commentText);

        var data = {
            "post_id" : id,
            "commentText" : commentText,
            "withPhotos" : $scope.commentSelectedFiles.length != 0
        };
        var post_data = data;

        usSpinnerService.spin('loading...');
        $http.post('/community/post/comment', data) 
            .success(function(response) {
            	var posts = [ $scope.post ];
                angular.forEach(posts, function(post, key){
                    if(post.id == data.post_id) {
                        post.n_c++;
                        post.ut = new Date();
                        var comment = {"oid" : $scope.userInfo.id, "d" : response.text, "on" : $scope.userInfo.displayName,
                                "isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c, "id" : response.id};
                        post.cs.push(comment);

                        if($scope.commentSelectedFiles.length == 0) {
                            return;
                        }

                        $scope.commentSelectedFiles = [];
                        $scope.commentDataUrls = [];

                        // when post is done in BE then do photo upload
                        //log($scope.commentTempSelectedFiles.length);
                        for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
                            usSpinnerService.spin('loading...');
                            $upload.upload({
                                url : '/image/uploadCommentPhoto',
                                method: $scope.httpMethod,
                                data : {
                                    commentId : response.id
                                },
                                file: $scope.commentTempSelectedFiles[i],
                                fileFormDataName: 'comment-photo'
                            }).success(function(data, status, headers, config) {
                                $scope.commentTempSelectedFiles.length = 0;
                                if(post.id == post_data.post_id) {
                                    angular.forEach(post.cs, function(cmt, key){
                                        if(cmt.id == response.id) {
                                            cmt.hasImage = true;
                                            if(cmt.imgs) {

                                            } else {
                                                cmt.imgs = [];
                                            }
                                            cmt.imgs.push(data);
                                        }
                                    });
                                }
                            }).error(function(data, status, headers, config) {
                                prompt("回載圖片失敗。請重試");
                            });
                        }
                    }
                });
            }).error(function(data, status, headers, config) {
                prompt("回覆失敗。請重試");
            });
            usSpinnerService.stop('loading...');    
    }

    $scope.remove_image_from_comment = function(index) {
        $scope.commentSelectedFiles.splice(index, 1);
        $scope.commentTempSelectedFiles.splice(index, 1);
        $scope.commentDataUrls.splice(index, 1);
    }

    $scope.like_post = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.like_post(post_id, posts);
    }

    $scope.unlike_post = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.unlike_post(post_id, posts);
    }

    $scope.like_comment = function(post_id, comment_id) {
    	var posts = [ $scope.post ];
        postFactory.like_comment(post_id, comment_id, posts);
    }

    $scope.unlike_comment = function(post_id, comment_id) {
    	var posts = [ $scope.post ];
        postFactory.unlike_comment(post_id, comment_id, posts);
    }

    $scope.bookmarkPost = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.bookmarkPost(post_id, posts);
    }

    $scope.unBookmarkPost = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.unBookmarkPost(post_id, posts);
    }

    $scope.commentPhoto = function(post_id) {
        $("#comment-photo-id").click();
        $scope.commentedOnPost = post_id ;
    } 

    $scope.commentSelectedFiles = [];
    $scope.commentTempSelectedFiles = [];
    $scope.commentDataUrls = [];

    $scope.onCommentFileSelect = function($files) {
        //log($scope.commentSelectedFiles.length);
        if($scope.commentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
            $scope.commentTempSelectedFiles = [];
        }

        $scope.commentSelectedFiles.push($files);
        //log($scope.commentSelectedFiles);
        $scope.commentTempSelectedFiles.push($files);
        for ( var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.commentDataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }

});

minibean.controller('QnALandingController', function($scope, $routeParams, $http, $timeout, $upload, $validator, 
		postFactory, qnaLandingService, communityPageService, postManagementService, usSpinnerService) {

    $scope.get_header_metaData();

    $scope.selectNavBar('HOME', -1);

    $scope.$on('$viewContentLoaded', function() {
        usSpinnerService.spin('loading...');
    });

    $scope.community = communityPageService.Community.get({id:$routeParams.communityId});

    $scope.post = qnaLandingService.qnaLanding.get({id:$routeParams.id,communityId:$routeParams.communityId}, 
    	function(data) {
    		$scope.noResult = false;
    		usSpinnerService.stop('loading...');

            writeMetaTitleDescription(data.ptl, data.pt);
    	},
    	function(rejection) {
    		$scope.noResult = true;
    		usSpinnerService.stop('loading...');
		}
    );

    //
    // Below is copied completely from CommunityQnAController
    // for js functions to handle comment, comment photo, like, bookmark etc
    //

    $scope.deletePost = function(postId) {
    	var posts = [ $scope.post ];
        postFactory.deletePost(postId, posts);
        $scope.noResult = true;
    }

    $scope.deleteComment = function(commentId, post) {
        postFactory.deleteComment(commentId, post);
    }

    $scope.postPhoto = function() {
        $("#QnA-photo-id").click();
    }

    $scope.showMore = function(id) {
    	var posts = [ $scope.post ];
        postFactory.showMore(id, posts);
    }

    $scope.get_all_answers = function(id) {
    	var posts = [ $scope.post ];
        postFactory.getAllComments(id, posts, $scope);
    }

    $scope.get_all_comments = function(id) {
    	var posts = [ $scope.post ];
        postFactory.getAllComments(id, posts, $scope);
    }

    // !!!NOTE: Since we reuse qna-bar.html for landing page, and qna-bar.html is 
    // being used in home-news-feed-section.html, "view all" is using 
    // CommunityPageController.get_all_comments() instead of 
    // CommunityQnAController.get_all_answers(). Hence we need to define 
    // below to make get_all_comments() available in QnALandingController

    $scope.qnaCommentPhoto = function(post_id) {
        $("#qna-comment-photo-id").click();
        $scope.commentedOnPost = post_id ;
    };

    $scope.qnaCommentSelectedFiles = [];
    $scope.qnaTempCommentSelectedFiles = [];
    $scope.qnaCommentDataUrls = [];

    $scope.onQnACommentFileSelect = function($files) {
        //log($scope.qnaCommentSelectedFiles.length);
        if($scope.qnaCommentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
            $scope.qnaTempCommentSelectedFiles = [];
        }

        $scope.qnaCommentSelectedFiles.push($files);
        //log($scope.qnaCommentSelectedFiles);
        $scope.qnaTempCommentSelectedFiles.push($files);
        for (var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.qnaCommentDataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }

    $scope.remove_image_from_qna_comment = function(index) {
        $scope.qnaCommentSelectedFiles.splice(index, 1);
        $scope.qnaTempCommentSelectedFiles.splice(index, 1);
        $scope.qnaCommentDataUrls.splice(index, 1);
    }

    $scope.select_emoticon_comment = function(code, index) {
        postFactory.selectCommentEmoticon(code, index);
    }

    $scope.answer_to_question = function(question_post_id, answerText) {
        // first convert to links
        //answerText = convertText(answerText);

        var data = {
            "post_id" : question_post_id,
            "answerText" : answerText,
            "withPhotos" : $scope.qnaCommentSelectedFiles.length != 0
        };
        var post_data = data;

        usSpinnerService.spin('loading...');
        $http.post('/communityQnA/question/answer', data) 
            .success(function(response) {
            	var posts = [ $scope.post ];
                angular.forEach(posts, function(post, key){
                    if(post.id == data.post_id) {
                        post.n_c++;
                        post.ut = new Date();
                        var answer = {"oid" : $scope.userInfo.id, "d" : response.text, "on" : $scope.userInfo.displayName, 
                                "isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c, "id" : response.id};
                        answer.isO = true;
                        answer.n = post.n_c;
                        post.cs.push(answer);

                        if($scope.qnaCommentSelectedFiles.length == 0) {
                            return;
                        }

                        $scope.qnaCommentSelectedFiles = [];
                        $scope.qnaCommentDataUrls = [];

                        // when post is done in BE then do photo upload
                        //log($scope.qnaTempCommentSelectedFiles.length);
                        for(var i=0 ; i<$scope.qnaTempCommentSelectedFiles.length ; i++) {
                            usSpinnerService.spin('loading...');
                            $upload.upload({
                                url : '/image/uploadCommentPhoto',
                                method: $scope.httpMethod,
                                data : {
                                    commentId : response.id
                                },
                                file: $scope.qnaTempCommentSelectedFiles[i],
                                fileFormDataName: 'comment-photo'
                            }).success(function(data, status, headers, config) {
                                $scope.qnaTempCommentSelectedFiles.length = 0;
                                if(post.id == post_data.post_id) {
                                    angular.forEach(post.cs, function(cmt, key){
                                        if(cmt.id == response.id) {
                                            cmt.hasImage = true;
                                            if(cmt.imgs) {

                                            } else {
                                                cmt.imgs = [];
                                            }
                                            cmt.imgs.push(data);
                                        }
                                    });
                                }
                            });
                        }
                    }
                    usSpinnerService.stop('loading...');
                });
            });
    }

    $scope.want_answer = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.want_answer(post_id, posts);
    }

    $scope.unwant_answer = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.unwant_answer(post_id, posts);
    }

    $scope.like_post = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.like_post(post_id, posts);
    }

    $scope.unlike_post = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.unlike_post(post_id, posts);
    }

    $scope.like_comment = function(post_id, comment_id) {
    	var posts = [ $scope.post ];
        postFactory.like_comment(post_id, comment_id, posts);
    }

    $scope.unlike_comment = function(post_id, comment_id) {
    	var posts = [ $scope.post ];
        postFactory.unlike_comment(post_id, comment_id, posts);
    }

    $scope.bookmarkPost = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.bookmarkPost(post_id, posts);
    }

    $scope.unBookmarkPost = function(post_id) {
    	var posts = [ $scope.post ];
        postFactory.unBookmarkPost(post_id, posts);
    }
});

minibean.controller('CommunityPageController', function($scope, $routeParams, $interval, profilePhotoModal, 
		pkViewFactory, communityPageService, communityJoinService, pkViewService, searchMembersService, usSpinnerService){

    $scope.get_header_metaData();

    $scope.selectNavBar('HOME', -1);

    $scope.selectedTab = 1;
    $scope.selectedSubTab = 1;
    var tab = $routeParams.tab;
    if(tab == 'question'){
        $scope.selectedSubTab = 1;
    } else if(tab == 'sharing'){
        $scope.selectedSubTab = 1;      // sharing tab now removed
    } else if(tab == 'members'){
        $scope.selectedTab = 2;
    } else if(tab == 'details'){
        $scope.selectedTab = 3;
    }

    // pkview slider
    $scope.renderPromo2Slider = function() {
        var opts = {
            arrowsNav: false,
            arrowsNavAutoHide: false,
            fadeinLoadedSlide: false,
            controlsInside: false,
            controlNavigationSpacing: 0,
            controlNavigation: 'bullets',
            imageScaleMode: 'none',
            imageAlignCenter: false,
            loop: true,
            transitionType: 'move',
            keyboardNavEnabled: false,
            navigateByClick: false,
            block: {
                delay: 400
            },
            autoPlay: {
                enabled: true,
                pauseOnHover: true,
                stopAtAction: false,
                delay: 5000
            }
        };
        if ($('#promo2-slider').length > 0) {
            var promo2Slider = $('#promo2-slider').royalSlider(opts);
        }
    }
    $scope.pkviews = pkViewService.communityPKViews.get({community_id:$routeParams.id},
        function(data) {
            if (data.length > 0) {
                $interval($scope.renderPromo2Slider, 1500, 1);
            }
        }
    );

    $scope.redVote = function(pkview) {
        if (!$scope.userInfo.isLoggedIn) {
            $scope.popupLoginModal();
            return;
        }
        pkViewFactory.redVote(pkview);
    }

    $scope.blueVote = function(pkview) {
        if (!$scope.userInfo.isLoggedIn) {
            $scope.popupLoginModal();
            return;
        }
        pkViewFactory.blueVote(pkview);
    }

    $scope.$on('$viewContentLoaded', function() {
        usSpinnerService.spin('loading...');
    });

    $scope.community = communityPageService.Community.get({id:$routeParams.id}, 
        function(data){
            usSpinnerService.stop('loading...');
        }
    );

    communityPageService.isNewsfeedEnabled.get({community_id:$routeParams.id}, 
        function(data) {
            $scope.newsfeedEnabled = data.newsfeedEnabled; 
        }
    );

    $scope.toggleNewsfeedEnabled = function(community_id) {
        communityPageService.toggleNewsfeedEnabled.get({community_id:community_id}, 
            function(data) {
                $scope.newsfeedEnabled = data.newsfeedEnabled; 
            }
        );
    }

    $scope.coverImage = "/image/get-cover-community-image-by-id/" + $routeParams.id;

    $scope.openGroupCoverPhotoModal = function(id) {
        PhotoModalController.url = "image/upload-cover-photo-group/"+id;
        profilePhotoModal.OpenModal({
             templateUrl: 'change-profile-photo-modal',
             controller: PhotoModalController
        },function() {
            $scope.coverImage = $scope.coverImage + "?q="+ Math.random();
        });
    }

    $scope.nonMembers = [];
    $scope.search_unjoined_users = function(comm_id, query) {
        if(query.length >1){
            $scope.nonMembers = searchMembersService.getUnjoinedUsers.get({id : comm_id, query: query});
        }
    }

    $scope.send_invite_to_join = function(group_id, user_id) {
        searchMembersService.sendInvitationToNonMember.get({group_id : group_id, user_id: user_id}, function() {
            angular.forEach($scope.nonMembers, function(member, key){
                if(member.id == user_id) {
                    $scope.nonMembers.splice($scope.nonMembers.indexOf(member),1);
                }
            });
        });
    }

    $scope.send_join = function(id) {
        usSpinnerService.spin('loading...');
        this.send_join_request = communityJoinService.sendJoinRequest.get({id:id}, function(data) {
            usSpinnerService.stop('loading...');
            $scope.community.isP = $scope.community.typ == 'CLOSE' ?  true : false;
            $scope.community.isM = $scope.community.typ == 'OPEN'? true : false;
        });
    }

    $scope.leave_community = function(id) {
        usSpinnerService.spin('loading...');
        this.leave_this_community = communityJoinService.leaveCommunity.get({id:id}, function(data) {
            usSpinnerService.stop('loading...');
            $scope.community.isM = false;
        });
    }

});

minibean.controller('CommunityPostController', function($scope, $routeParams, $http, $upload, $timeout, profilePhotoModal,
		postFactory, communityPageService, postManagementService, communityJoinService, usSpinnerService){

    var firstBatchLoaded = false;
    var time = 0;
    var noMore = false;

	$scope.posts = communityPageService.InitialPosts.get({id:$routeParams.id}, function(){
        //log("===> get first batch posts completed");
        firstBatchLoaded = true;
        usSpinnerService.stop('loading...');
    });

    $scope.nextPosts = function() {
        //log("===> nextPosts:isBusy="+$scope.isBusy+"|time="+time+"|firstBatchLoaded="+firstBatchLoaded+"|noMore="+noMore);
        if ($scope.isBusy) return;
        if (!firstBatchLoaded) return;
        if (noMore) return;
        $scope.isBusy = true;
        if(angular.isObject($scope.posts.posts) && $scope.posts.posts.length > 0) {
            time = $scope.posts.posts[$scope.posts.posts.length - 1].ut;
            //log("===> set time:"+time);
        }
        communityPageService.NextPosts.get({id:$routeParams.id,time:time}, function(data){
            var posts = data;
            if(data.length == 0) {
                noMore = true;
            }

            for (var i = 0; i < posts.length; i++) {
                $scope.posts.posts.push(posts[i]);
            }
            $scope.isBusy = false;
        });
    }

    $scope.deletePost = function(postId) {
        postFactory.deletePost(postId, $scope.posts.posts);
    }

    $scope.deleteComment = function(commentId, post) {
        postFactory.deleteComment(commentId, post);
    }

	$scope.postPhoto = function() {
		$("#post-photo-id").click();
	}

	$scope.get_all_comments = function(id) {
        postFactory.getAllComments(id, $scope.posts.posts, $scope);
    }

    $scope.selectedFiles = [];
    $scope.tempSelectedFiles = [];
    $scope.dataUrls = [];

    $scope.onFileSelect = function($files) {
        if($scope.selectedFiles.length == 0) {
            $scope.tempSelectedFiles = [];
        }

        $scope.selectedFiles.push($files);
        $scope.tempSelectedFiles.push($files);
        for (var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.dataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }

	$scope.select_emoticon_comment = function(code, index) {
        postFactory.selectCommentEmoticon(code, index);
    }

	$scope.comment_on_post = function(id, commentText) {
        // first convert to links
        //commentText = convertText(commentText);

		var data = {
			"post_id" : id,
			"commentText" : commentText,
			"withPhotos" : $scope.commentSelectedFiles.length != 0
		};
		var post_data = data;

		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(response) {
				angular.forEach($scope.posts.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var comment = {"oid" : $scope.posts.lu, "d" : response.text, "on" : $scope.posts.lun,
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c, "id" : response.id};
						post.cs.push(comment);

						if($scope.commentSelectedFiles.length == 0) {
							return;
						}

						$scope.commentSelectedFiles = [];
						$scope.commentDataUrls = [];

						// when post is done in BE then do photo upload
						//log($scope.commentTempSelectedFiles.length);
						for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadCommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : response.id
								},
								file: $scope.commentTempSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.commentTempSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == response.id) {
											cmt.hasImage = true;
											if(cmt.imgs) {

											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
						}
					}
                });
                usSpinnerService.stop('loading...');	
            });
	}

	$scope.remove_image_from_comment = function(index) {
		$scope.commentSelectedFiles.splice(index, 1);
		$scope.commentTempSelectedFiles.splice(index, 1);
		$scope.commentDataUrls.splice(index, 1);
	}

	$scope.post_on_community = function(id, postText) {
        // first convert to links
		//postText = convertText(postText);

		usSpinnerService.spin('loading...');
		var data = {
			"community_id" : id,
			"postText" : postText,
			"withPhotos" : $scope.selectedFiles.length != 0
		};

		$scope.postText="";

		$http.post('/community/post', data) // first create post with post text.
			.success(function(post_id) {
				usSpinnerService.stop('loading...');
				$('.postBox').val('');
				$scope.postText = "";
				var post = {"oid" : $scope.posts.lu, "pt" : postText, "cid" : $scope.community.id, "cn" : $scope.community.n, "ci" : $scope.community.icon, 
                        "isLike" : false, "nol" : 0, "p" : $scope.posts.lun, "t" : new Date(), "n_c" : 0, "id" : post_id, "cs": []};
                $scope.posts.posts.unshift(post);

				if($scope.selectedFiles.length == 0) {
					return;
				}

				$scope.selectedFiles = [];
				$scope.dataUrls = [];

				// when post is done in BE then do photo upload
				for(var i=0 ; i<$scope.tempSelectedFiles.length ; i++) {
					usSpinnerService.spin('loading...');
					$upload.upload({
						url : '/image/uploadPostPhoto',
						method: $scope.httpMethod,
						data : {
							postId : post_id
						},
						file: $scope.tempSelectedFiles[i],
						fileFormDataName: 'post-photo'
					}).success(function(data, status, headers, config) {
						usSpinnerService.stop('loading...');
						angular.forEach($scope.posts.posts, function(post, key){
							if(post.id == post_id) {
								post.hasImage = true;
								if(post.imgs) { 
								} else {
									post.imgs = [];
								}
								post.imgs.push(data);
                            }
                        });
                    });
                }
            });
	}

	$scope.remove_image = function(index) {
		$scope.selectedFiles.splice(index, 1);
		$scope.tempSelectedFiles.splice(index, 1);
		$scope.dataUrls.splice(index, 1);
	}

    $scope.like_post = function(post_id) {
        postFactory.like_post(post_id, $scope.posts.posts);
    }

    $scope.unlike_post = function(post_id) {
        postFactory.unlike_post(post_id, $scope.posts.posts);
    }

    $scope.like_comment = function(post_id, comment_id) {
        postFactory.like_comment(post_id, comment_id, $scope.posts.posts);
    }

    $scope.unlike_comment = function(post_id, comment_id) {
        postFactory.unlike_comment(post_id, comment_id, $scope.posts.posts);
    }

    $scope.bookmarkPost = function(post_id) {
        postFactory.bookmarkPost(post_id, $scope.posts.posts);
    }

    $scope.unBookmarkPost = function(post_id) {
        postFactory.unBookmarkPost(post_id, $scope.posts.posts);
    }

    $scope.commentPhoto = function(post_id) {
        $("#comment-photo-id").click();
        $scope.commentedOnPost = post_id ;
    } 

	$scope.commentPhoto = function(post_id) {
		$("#comment-photo-id").click();
		$scope.commentedOnPost = post_id ;
	} 

	$scope.commentSelectedFiles = [];
	$scope.commentTempSelectedFiles = [];
	$scope.commentDataUrls = [];

	$scope.onCommentFileSelect = function($files) {
		//log($scope.commentSelectedFiles.length);
		if($scope.commentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.commentTempSelectedFiles = [];
		}

		$scope.commentSelectedFiles.push($files);
		//log($scope.commentSelectedFiles);
		$scope.commentTempSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.commentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}

});

minibean.controller('CommunityQnAController',function($scope, postFactory, postManagementService, communityQnAPageService, usSpinnerService ,$timeout, $routeParams, $http,  $upload, $validator){

    var firstBatchLoaded = false;
    var time = 0;
    var noMore = false;

    var id = $routeParams.id;
    if ($scope._commId != undefined) {
    	id = $scope._commId;		// set in pn page
    }

    $scope.QnAs = communityQnAPageService.InitialQuestions.get({id:id}, function(){
        firstBatchLoaded = true;
        usSpinnerService.stop('loading...');
    });

    $scope.nextPosts = function() {
        //log("===> nextPosts:isBusy="+$scope.isBusy+"|time="+time+"|firstBatchLoaded="+firstBatchLoaded+"|noMore="+noMore);
        if ($scope.isBusy) return;
        if (!firstBatchLoaded) return;
        if (noMore) return;
        $scope.isBusy = true;
        if(angular.isObject($scope.QnAs.posts) && $scope.QnAs.posts.length > 0) {
            time = $scope.QnAs.posts[$scope.QnAs.posts.length - 1].ut;
            //log("===> set time:"+time);
        }
        communityQnAPageService.NextQuestions.get({id:id,time:time}, function(data){
            var posts = data;
            if(data.length == 0) {
                noMore = true;
            }

            for (var i = 0; i < posts.length; i++) {
                $scope.QnAs.posts.push(posts[i]);
            }
            $scope.isBusy = false;
        });

    }

	$scope.postPhoto = function() {
		$("#QnA-photo-id").click();
	}

    $scope.showMore = function(id) {
        postFactory.showMore(id, $scope.QnAs.posts);
    }

	$scope.get_all_answers = function(id) {
        postFactory.getAllComments(id, $scope.QnAs.posts, $scope);
    }

    $scope.get_all_comments = function(id) {
        postFactory.getAllComments(id, $scope.QnAs.posts, $scope);
    }

    $scope.select_emoticon = function(code) {
        postFactory.selectEmoticon(code);
    }

    $scope.deletePost = function(postId) {
        postFactory.deletePost(postId, $scope.QnAs.posts);
    }

    $scope.deleteComment = function(commentId, post) {
        postFactory.deleteComment(commentId, post);
    }

    $scope.select_emoticon_comment = function(code, index) {
        postFactory.selectCommentEmoticon(code, index);
    }

	// Right now community-qna-bar.html > qna-bar.html is using CommunityQnAController
	// and home-news-feed.html > qna-bar.html is using CommunityPageController
	// and qna-bar.html is calling get_all_comments() instead of get_all_answers() 
	// such that it works in all places
	// Assign the dummy get_all_comments here... needs refactoring... 

	$scope.qnaCommentPhoto = function(post_id) {
		$("#qna-comment-photo-id").click();
		$scope.commentedOnPost = post_id ;
	};

	$scope.qnaCommentSelectedFiles = [];
	$scope.qnaTempCommentSelectedFiles = [];
	$scope.qnaCommentDataUrls = [];

	$scope.onQnACommentFileSelect = function($files) {
		//log($scope.qnaCommentSelectedFiles.length);
		if($scope.qnaCommentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.qnaTempCommentSelectedFiles = [];
		}

		$scope.qnaCommentSelectedFiles.push($files);
		//log($scope.qnaCommentSelectedFiles);
		$scope.qnaTempCommentSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.qnaCommentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}

    $scope.QnASelectedFiles = [];
    $scope.tempSelectedFiles = [];
    $scope.dataUrls = [];

    $scope.onQnAFileSelect = function($files) {
        if($scope.QnASelectedFiles.length == 0) {
            $scope.tempSelectedFiles = [];
        }

        $scope.QnASelectedFiles.push($files);
        $scope.tempSelectedFiles.push($files);
        for (var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.dataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }

    $scope.remove_image = function(index) {
        $scope.QnASelectedFiles.splice(index, 1);
        $scope.dataUrls.splice(index, 1);
    }

	$scope.ask_question_community = function(id, questionTitle, questionText) {
        // first convert to links
        //questionText = convertText(questionText);

		var data = {
			"community_id" : id,
			"questionTitle" : questionTitle,
			"questionText" : questionText,
			"withPhotos" : $scope.QnASelectedFiles.length != 0
		};

		usSpinnerService.spin('loading...');
		$http.post('/communityQnA/question/post', data) // first create post with question text.
			.success(function(response) {
				usSpinnerService.stop('loading...');
				$('.postBox').val('');
				var post = {"oid" : $scope.QnAs.lu, "ptl" : questionTitle, "pt" : response.text, "cid" : $scope.community.id, "cn" : $scope.community.n, 
						"isLike" : false, "showM": (response.showM == 'true'), "nol" : 0, "p" : $scope.QnAs.lun, "t" : new Date(), "n_c" : 0, "id" : response.id, "cs": []};
				$scope.QnAs.posts.unshift(post);

				if($scope.QnASelectedFiles.length == 0) {
					return;
				}

				$scope.QnASelectedFiles = [];
				$scope.dataUrls = [];

				// when post is done in BE then do photo upload
				for(var i=0 ; i<$scope.tempSelectedFiles.length ; i++) {
					usSpinnerService.spin('loading...');
					$upload.upload({
						url : '/image/uploadPostPhoto',
						method: $scope.httpMethod,
						data : {
							postId : response.id
						},
						file: $scope.tempSelectedFiles[i],
						fileFormDataName: 'post-photo'
					}).success(function(data, status, headers, config) {
						usSpinnerService.stop('loading...');
						angular.forEach($scope.QnAs.posts, function(post, key){
							if(post.id == response.id) {
								post.hasImage = true;
								if(post.imgs) { 
								} else {
									post.imgs = [];
								}
								post.imgs.push(data);
							}
						});
					});
				}
            });
	}

	$scope.remove_image_from_qna_comment = function(index) {
		$scope.qnaCommentSelectedFiles.splice(index, 1);
		$scope.qnaTempCommentSelectedFiles.splice(index, 1);
		$scope.qnaCommentDataUrls.splice(index, 1);
	}

	$scope.answer_to_question = function(question_post_id, answerText) {
		// first convert to links
        //answerText = convertText(answerText);

        var data = {
			"post_id" : question_post_id,
			"answerText" : answerText,
			"withPhotos" : $scope.qnaCommentSelectedFiles.length != 0
		};
		var post_data = data;

		usSpinnerService.spin('loading...');
		$http.post('/communityQnA/question/answer', data) 
			.success(function(response) {
				angular.forEach($scope.QnAs.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var answer = {"oid" : $scope.QnAs.lu, "d" : response.text, "on" : $scope.QnAs.lun, 
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c, "id" : response.id};
                        answer.isO = true;
                        answer.n = post.n_c;
                        post.cs.push(answer);

						if($scope.qnaCommentSelectedFiles.length == 0) {
							return;
						}

						$scope.qnaCommentSelectedFiles = [];
						$scope.qnaCommentDataUrls = [];

						// when post is done in BE then do photo upload
						//log($scope.qnaTempCommentSelectedFiles.length);
						for(var i=0 ; i<$scope.qnaTempCommentSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadCommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : response.id
								},
								file: $scope.qnaTempCommentSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.qnaTempCommentSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == response.id) {
											cmt.hasImage = true;
											if(cmt.imgs) {

											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
                        }
				    }
				    usSpinnerService.stop('loading...');
                });
            });
	}

    $scope.want_answer = function(post_id) {
        postFactory.want_answer(post_id, $scope.QnAs.posts);
    }

    $scope.unwant_answer = function(post_id) {
        postFactory.unwant_answer(post_id, $scope.QnAs.posts);
    }

	$scope.like_post = function(post_id) {
		postFactory.like_post(post_id, $scope.QnAs.posts);
	}

	$scope.unlike_post = function(post_id) {
		postFactory.unlike_post(post_id, $scope.QnAs.posts);
	}

	$scope.like_comment = function(post_id, comment_id) {
		postFactory.like_comment(post_id, comment_id, $scope.QnAs.posts);
	}

	$scope.unlike_comment = function(post_id, comment_id) {
		postFactory.unlike_comment(post_id, comment_id, $scope.QnAs.posts);
	}

	$scope.bookmarkPost = function(post_id) {
        postFactory.bookmarkPost(post_id, $scope.QnAs.posts);
	}

	$scope.unBookmarkPost = function(post_id) {
		postFactory.unBookmarkPost(post_id, $scope.QnAs.posts);
	}
});

minibean.controller('ArticleSliderController', function($scope, $routeParams, $interval, usSpinnerService, articleService){

    var catId = $routeParams.catId;
    if (catId == undefined) {
       catId = 0;
    }
    $scope.resultSlider = articleService.SixArticles.get({category_id:catId}, function() {
        $scope.changeSliderImage($scope.resultSlider.la[0].id);
        $interval($scope.autoRollSlider, 10, 1);    // init auto roll
        $interval($scope.autoRollSlider, DefaultValues.AUTO_SCROLL_INTERVAL/3, 1);   // first roll  
        $scope.resumeSliderRoll();   
    });

    $scope.changeSliderImage = function(article_id) {
        angular.forEach($scope.resultSlider.la, function(element, key){
            if(element.id == article_id) {
                $scope.image_source = element.img_url;
                $scope.description = element.lds;
                $scope.title = element.nm;
                $scope.article_id = element.id;
                $scope.article_cat_id = element.ct.id;
            }
            $('#slider_item_'+element.id).removeClass('hovered');
        })
        angular.forEach($scope.resultSlider.ra, function(element, key){
            if(element.id == article_id) {
                $scope.image_source = element.img_url;
                $scope.description = element.lds;
                $scope.title = element.nm;
                $scope.article_id = element.id;
                $scope.article_cat_id = element.ct.id;
            }
            $('#slider_item_'+element.id).removeClass('hovered');
        })
        $('#slider_item_'+article_id).addClass('hovered');
    };

    $scope.stopSliderRoll = function() {
        if (angular.isDefined($scope.stopSliderRollTimer)) {
            $interval.cancel($scope.stopSliderRollTimer);
            $scope.stopSliderRollTimer = undefined;
            //log('stop article auto roll timer');
        }
    }

    $scope.resumeSliderRoll = function() {
        if (angular.isDefined($scope.stopSliderRollTimer)) {
            return;
        }
        $scope.initAutoRollState();
        $scope.stopSliderRollTimer = $interval($scope.autoRollSlider, DefaultValues.AUTO_SCROLL_INTERVAL);
        //log('start article auto roll timer');
    }

    $scope.$on('$destroy', function() {
        // avoid duplicate timer start
        $scope.stopSliderRoll();
    });

    var left = true;
    var index = 0;
    var leftCount;
    var rightCount;
    $scope.initAutoRollState = function() {
        leftCount = $scope.resultSlider.la.length;
        rightCount = $scope.resultSlider.ra.length;
    }

    $scope.autoRollSlider = function() {
        var article_id = $scope.resultSlider.la[index].id;
        if (!left) {
            article_id = $scope.resultSlider.ra[index].id;
        }
        $scope.changeSliderImage(article_id);

        // switch side
        if (left) {
            if (index == leftCount - 1) {
                left = !left;
                index = 0;
            } else {
                index++;
            }
        } else {
            if (index == rightCount - 1) {
                left = !left;
                index = 0;
            } else {
                index++;
            }
        }
        //log(left+":"+index);
    }
});

minibean.controller('PKViewPageController',function($scope, $route, $location, $http, $timeout, $routeParams, pkViewFactory, pkViewService, likeFrameworkService, usSpinnerService){

    $scope.redExpandText = '只看紅豆豆意見';
    $scope.blueExpandText = '只看藍豆豆意見';
    $scope.redExpanded = false;
    $scope.blueExpanded = false;
    $scope.toggleRedExpand = function() {
        $scope.redExpanded = !$scope.redExpanded;
        if ($scope.redExpanded) {
            $('.col-l').show();
            $('.col-l').width('100%');
            $('.col-r').hide();
            $scope.redExpandText = '全部意見';
        } else {
            $('.col-l').show();
            $('.col-l').width('50%');
            $('.col-r').show();
            $('.col-r').width('49%');
            $scope.redExpandText = '只看紅豆豆意見';
        }
    }
    $scope.toggleBlueExpand = function() {
        $scope.blueExpanded = !$scope.blueExpanded;
        if ($scope.blueExpanded) {
            $('.col-l').hide();
            $('.col-r').show();
            $('.col-r').width('100%');
            $scope.blueExpandText = '全部意見';
        } else {
            $('.col-l').show();
            $('.col-l').width('50%');
            $('.col-r').show();
            $('.col-r').width('49%');
            $scope.blueExpandText = '只看藍豆豆意見';
        }
    }

    $scope.showPKView = true;
    $scope.commentsPreviewNum = DefaultValues.COMMENTS_PREVIEW_COUNT;

    $scope.pkview = pkViewService.pkViewInfo.get({id:$routeParams.id}, 
        function(data) {
            if ($scope.pkview.id == null) {
                $scope.showPKView = false;
            }

            writeMetaTitleDescription(data.ptl, data.pt);
        },
    	function(rejection) {
        	$location.path('/pkview/show');
		}
    );

    $scope.redVote = function(pkview) {
        if (!$scope.userInfo.isLoggedIn) {
            $scope.popupLoginModal();
            return;
        }
        pkViewFactory.redVote(pkview);
    }

    $scope.blueVote = function(pkview) {
        if (!$scope.userInfo.isLoggedIn) {
            $scope.popupLoginModal();
            return;
        }
        pkViewFactory.blueVote(pkview);
    }

    $scope.deleteComment = function(commentId, attr) {
        pkViewFactory.deleteComment(commentId, attr, $scope.pkview);
    }

    $scope.like_pkview = function(pkview_id) {
        pkViewFactory.like_pkview(pkview_id, $scope.pkview);
    }

    $scope.unlike_pkview = function(pkview_id) {
        pkViewFactory.unlike_pkview(pkview_id, $scope.pkview);
    }

    $scope.bookmarkPKView = function(pkview_id) {
        pkViewFactory.bookmarkPKView(pkview_id, $scope.pkview);
    }

    $scope.unBookmarkPKView = function(pkview_id) {
        pkViewFactory.unBookmarkPKView(pkview_id, $scope.pkview);
    }

    $scope.like_comment = function(comment_id, attr) {
        pkViewFactory.like_comment(comment_id, attr, $scope.pkview);
    }

    $scope.unlike_comment = function(comment_id, attr) {
        pkViewFactory.unlike_comment(comment_id, attr, $scope.pkview);
    }

    $scope.select_emoticon_comment = function(code, attr) {
        pkViewFactory.selectCommentEmoticon(code, attr);
    }

    $scope.comment_to_pkview = function(pkview_id, commentText, attribute) {
        // first convert to links
        //commentText = convertText(commentText);

        var data = {
            "pkview_id" : pkview_id,
            "commentText" : commentText,
            "attribute" : attribute
        };
        var pkview_data = data;

        usSpinnerService.spin('loading...');
        $http.post('/community/pkview/comment', data) 
            .success(function(response) {
                $scope.pkview.n_c++;
                $scope.pkview.ut = new Date();
                var comment = {"oid" : $scope.userInfo.id, "d" : response.text, "on" : $scope.userInfo.displayName, 
                        "isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : $scope.pkview.n_c, "id" : response.id, "attr" : response.attribute};
                comment.isO = true;
                comment.n = $scope.pkview.n_c;
                $scope.pkview.cs.push(comment);
                if (response.attribute == 'YES') {
                    $scope.pkview.red_cs.unshift(comment);
                    $scope.pkview.n_rc++;
                } else if (response.attribute == 'NO') {
                    $scope.pkview.blue_cs.unshift(comment);
                    $scope.pkview.n_bc++;
                }
                usSpinnerService.stop('loading...');
            });
    }
});

minibean.controller('CampaignPageController',function($scope, $route, $location, $http, $routeParams, likeFrameworkService, campaignService, usSpinnerService){

    $scope.showCampaign = true;

    $scope.announcedWinners = [];

    var id = -1;    // my profile newsfeed
    if($routeParams.id != undefined){
        id = $routeParams.id;
    }

    $scope.campaign = campaignService.campaignInfo.get({id:id}, 
        function(data) {
            if ($scope.campaign.id == null || ($scope.campaign.cs == 'NEW' && !$scope.userInfo.isE)) {
                $scope.showCampaign = false;
            }
            if ($scope.campaign.cs == 'ANNOUNCED' || $scope.campaign.cs == 'CLOSED') {
                $scope.announcedWinners = campaignService.campaignAnnouncedWinners.get({id:id});
            }

            writeMetaTitleDescription(data.nm, data.ds, $scope.formatToExternalUrl(data.im));
        }, 
        function(rejection) {
        	$location.path('/campaign/show');
		}
    );

    $scope.popupCampaignNotStartModal = function() {
        bootbox.dialog({
            message: 
                "<div style='margin:0 20px;'>" + 
                "<div style='height:25px;margin-top:10px;font-size:15px;'>此活動尚未開始</div>" +
                "<div style='height:25px;margin-top:0px;font-size:15px;'>請於活動開始時間再參加</div>" +
                "</div>",
            title: "參加活動",
            className: "campaign-login-modal",
        });
    }

    $scope.formData = {};
    $scope.joinCampaign = function(campaignId) {
        $scope.formData.campaignId = campaignId;

        $scope.errorCampaignNotExist = false;        
        $scope.errorJoinedAlready = false;
        $scope.errorMissingContact = false;
        $scope.errorStatus = false;
        usSpinnerService.spin('loading...');
        return $http.post('/join-campaign', $scope.formData).success(function(data){
            usSpinnerService.stop('loading...');
            if (data.success) {
                $('#joinCampaignModal').modal('hide');
                prompt("<div><b>你已成功參加此活動!</b></div>", "bootbox-default-prompt game-bootbox-prompt", 1800);
                $scope.campaign.isJoined = true;
            } else {
                $scope.messages = data.messages;
                $scope.errorStatus = true;
            }
        }).error(function(data, status, headers, config) {
            if(status == 599){
                usSpinnerService.stop('loading...');
                window.location = '/my#!/login';
            } else if(status == 500){
                usSpinnerService.stop('loading...');
                $scope.errorCampaignNotExist = true;
                //alert("沒有此活動");
            } else if(status == 501){
                usSpinnerService.stop('loading...');
                $scope.errorJoinedAlready = true;
                //alert("你之前已加活動。活動只可參加一次。");
            } else if(status == 502){
                usSpinnerService.stop('loading...');
                $scope.errorMissingContact = true;
                //alert("你之前已加活動。活動只可參加一次。");
            }
        });
    }

    $scope.withdrawCampaign = function(campaignId) {
        var formData = {
            "campaignId" : campaignId
        };

        $scope.errorCampaignNotExist = false;        
        usSpinnerService.spin('loading...');
        return $http.post('/withdraw-campaign', formData).success(function(data){
            usSpinnerService.stop('loading...');
            $('#withdrawCampaignModal').modal('hide');
            $scope.campaign.isJoined = false;
        }).error(function(data, status, headers, config) {
            if(status == 599){
                usSpinnerService.stop('loading...');
                window.location = '/my#!/login';
            } else if(status == 500){
                usSpinnerService.stop('loading...');
                $scope.errorCampaignNotExist = true;
                //alert("沒有此活動");
            }
        });
    }

    $scope.like_campaign = function(campaign_id) {
        likeFrameworkService.hitLikeOnCampaign.get({campaign_id:campaign_id}, 
            function(data) {
                $scope.campaign.nol++;
                $scope.campaign.isLike=true;
            });
    }

    $scope.unlike_campaign = function(campaign_id) {
        likeFrameworkService.hitUnlikeOnCampaign.get({campaign_id:campaign_id}, 
            function(data) {
                $scope.campaign.nol--;
                $scope.campaign.isLike=false;
            });
    }
});

minibean.controller('ArticlePageController',function($scope, $routeParams, articleFactory, usSpinnerService, articleService, tagwordService){

    $scope.selectNavBar($scope.getArticleCategoryGroup($routeParams.catId), $routeParams.catId);

    $scope.defaultCollapseCount = DefaultValues.TAGWORD_LIST_COLLAPSE_COUNT;

    // tag words
    $scope.hotArticlesTagwords = tagwordService.HotArticlesTagwords.get();
    $scope.soonMomsTagwords = tagwordService.SoonMomsTagwords.get();

    $scope.hotArticles = articleService.HotArticles.get({category_id:$routeParams.catId});
    $scope.recommendedArticles = articleService.RecommendedArticles.get({category_id:$routeParams.catId});
    //$scope.newArticles = articleService.NewArticles.get({category_id:$routeParams.catId});

    $scope.article = articleService.ArticleInfo.get({id:$routeParams.id}, 
        function(data) {
            $scope.relatedResult = articleService.getRelatedArticles.get({id:$routeParams.id, category_id:data.ct.id});

            var title = data.nm + " - " + data.ct.name;
            writeMetaTitleDescription(title, data.lds, $scope.formatToExternalUrl(data.img_url));

            // render mobile scroll nav bar
            if ($scope.userInfo.isMobile) {
                $scope.renderNavSubBar();
            }
        },
        function(rejection) {
        	$location.path('/article/show/0');
		}
	);

    $scope.like_article = function(article_id) {
        articleFactory.like_article(article_id, $scope.article);
    }

    $scope.unlike_article = function(article_id) {
        articleFactory.unlike_article(article_id, $scope.article);
    }

    $scope.bookmarkArticle = function(article_id) {
        var articles = [ $scope.article ];
        articleFactory.bookmarkArticle(article_id, articles);
    }

    $scope.unBookmarkArticle = function(article_id) {
        var articles = [ $scope.article ];
        articleFactory.unBookmarkArticle(article_id, articles);
    }
});

minibean.controller('PNPageController',function($scope, $routeParams, schoolsFactory, schoolsService, myBookmarksService, communityPageService, usSpinnerService) {

    $scope.selectNavBar('SCHOOLS', 0);

    var id = $routeParams.id;
    usSpinnerService.spin('loading...');
    $scope.pn = schoolsService.pnInfo.get({id:id},
    	function(data) {
    		var commId = data.commId;
    		$scope.community = communityPageService.Community.get({id:commId}, function(data){
    	        usSpinnerService.stop('loading...');
    	    });

    		$scope._commId = commId;		// to be used in CommunityQnAController and CommunityMembersController
    		$scope.selectedTab = 1;

    		var meta = getMetaForSchool(data);
    		writeMetaTitleDescription(meta.title, meta.description, $scope.formatToExternalUrl("/assets/app/images/schools/pn_promo.png"));
		}
    );

    $scope.bookmarkedSchools = myBookmarksService.bookmarkedPNs.get();

    // search by name
    $scope.maxSchoolsSearchCount = DefaultValues.MAX_SCHOOLS_SEARCH_COUNT;
    $scope.resetSearch = function() {
    	$scope.searchTerm = '';
		$scope.searchResults = [];
		$("#schools-searchfield").val('');
        $("#schools-searchfield").trigger('input');
        $scope.searchMode = false;
    }
    $scope.resetSearch();

    $scope.searchByName = function(schoolQuery) {
		if(schoolQuery != undefined && schoolQuery.length > 0 && $scope.searchTerm != schoolQuery) {
			$scope.searchMode = true;
			$scope.searching = true;
			$scope.searchTerm = schoolQuery;

			$scope.searchResults = schoolsService.searchPNsByName.get({query:schoolQuery},
				function(data) {
					$scope.searching = false;
				}
			);
		}
		if (schoolQuery == null || schoolQuery.length == 0) {
			$scope.resetSearch();
		}
	}

    // bookmark
    $scope.bookmarkPN = function(id) {
    	var schools = [ $scope.pn ];
    	if ($scope.searchMode) {
    		schools = $scope.searchResults;
    	}
    	schoolsFactory.bookmarkPN(id, schools, $scope.bookmarkedSchools);	
    }

    $scope.unBookmarkPN = function(id) {
    	var schools = [ $scope.pn ];
    	if ($scope.searchMode) {
    		schools = $scope.searchResults;
    	}
    	schoolsFactory.unBookmarkPN(id, schools, $scope.bookmarkedSchools);
    }

});

minibean.controller('KGPageController',function($scope, $routeParams, schoolsFactory, schoolsService, myBookmarksService, communityPageService, usSpinnerService) {

    $scope.selectNavBar('SCHOOLS', 1);

    var id = $routeParams.id;
    usSpinnerService.spin('loading...');
    $scope.kg = schoolsService.kgInfo.get({id:id},
    	function(data) {
    		var commId = data.commId;
    		$scope.community = communityPageService.Community.get({id:commId}, function(data){
    	        usSpinnerService.stop('loading...');
    	    });

    		$scope._commId = commId;		// to be used in CommunityQnAController and CommunityMembersController
    		$scope.selectedTab = 1;

    		var title = data.n;
    		if (data.ne && data.ne != undefined) {
    			title += ' ' + data.ne;
    		}

    		var meta = getMetaForSchool(data);
    		writeMetaTitleDescription(meta.title, meta.description, $scope.formatToExternalUrl("/assets/app/images/schools/kg_promo.png"));
		}
    );

    $scope.bookmarkedSchools = myBookmarksService.bookmarkedKGs.get();

    // search by name
    $scope.maxSchoolsSearchCount = DefaultValues.MAX_SCHOOLS_SEARCH_COUNT;
    $scope.resetSearch = function() {
    	$scope.searchTerm = '';
		$scope.searchResults = [];
		$("#schools-searchfield").val('');
        $("#schools-searchfield").trigger('input');
        $scope.searchMode = false;
    }
    $scope.resetSearch();

    $scope.searchByName = function(schoolQuery) {
		if(schoolQuery != undefined && schoolQuery.length > 0 && $scope.searchTerm != schoolQuery) {
			$scope.searchMode = true;
			$scope.searching = true;
			$scope.searchTerm = schoolQuery;

			$scope.searchResults = schoolsService.searchKGsByName.get({query:schoolQuery},
				function(data) {
					$scope.searching = false;
				}
			);
		}
		if (schoolQuery == null || schoolQuery.length == 0) {
			$scope.resetSearch();
		}
	}

    // bookmark
    $scope.bookmarkKG = function(id) {
    	var schools = [ $scope.kg ];
    	if ($scope.searchMode) {
    		schools = $scope.searchResults;
    	}
    	schoolsFactory.bookmarkKG(id, schools, $scope.bookmarkedSchools);	
    }

    $scope.unBookmarkKG = function(id) {
    	var schools = [ $scope.kg ];
    	if ($scope.searchMode) {
    		schools = $scope.searchResults;
    	}
    	schoolsFactory.unBookmarkKG(id, schools, $scope.bookmarkedSchools);
    }

});

minibean.controller('SchoolsNewsfeedController',function($scope, $location, postFactory, frontpageService, usSpinnerService) {

	if ($location.path().indexOf('/pn') > -1) {
		$scope.isPN = true;
    	$scope.selectNavBar('SCHOOLS', 0);
    } else if ($location.path().indexOf('/kg') > -1) {
    	$scope.isKG = true;
    	$scope.selectNavBar('SCHOOLS', 1);
    } else {
    	$scope.isPN = true;
    	$scope.selectNavBar('SCHOOLS', 0);
    }

	$scope.newsFeeds = { posts: [] };

	var noMore = false;
	var offset = 0;
	$scope.nextNewsFeeds = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		$scope.isBusy = true;

		if ($scope.selectedNavSubBar == 0) {	// PN
			frontpageService.pnNewsFeeds.get({offset:offset},
				function(data){
					var posts = data.posts;
					if(posts.length == 0) {
						noMore = true;
					}

					for (var i = 0; i < posts.length; i++) {
						$scope.newsFeeds.posts.push(posts[i]);
				    }
				    $scope.isBusy = false;
					offset++;
				}
			);
		} else if ($scope.selectedNavSubBar == 1) {		// KG
			frontpageService.kgNewsFeeds.get({offset:offset},
				function(data){
					var posts = data.posts;
					if(posts.length == 0) {
						noMore = true;
					}

					for (var i = 0; i < posts.length; i++) {
						$scope.newsFeeds.posts.push(posts[i]);
				    }
				    $scope.isBusy = false;
					offset++;
				}
			);
		}
	}

	// first batch
	$scope.nextNewsFeeds();

	$scope.bookmarkPost = function(post_id) {
        postFactory.bookmarkPost(post_id, $scope.newsFeeds.posts);
    }

    $scope.unBookmarkPost = function(post_id) {
        postFactory.unBookmarkPost(post_id, $scope.newsFeeds.posts);
    }
});

minibean.controller('SchoolsRankingController',function($scope, $routeParams, $location, $interval, schoolsService, usSpinnerService) {

    if ($location.path().indexOf('/pn') > -1) {
    	$scope.selectNavBar('SCHOOLS', 0);
    } else if ($location.path().indexOf('/kg') > -1) {
    	$scope.selectNavBar('SCHOOLS', 1);
    } else {
    	$scope.selectNavBar('SCHOOLS', 0);
    }

	if ($scope.selectedNavSubBar == 0) {	// PN
		$scope.topViewedSchools = schoolsService.topViewedPNs.get({},
			function(data) {
				$interval($scope.gotoRanking, 100, 1);
    		}
		);
		$scope.topBookmarkedSchools = schoolsService.topBookmarkedPNs.get({},
			function(data) {
				$interval($scope.gotoRanking, 100, 1);
			}
		);
	} else if ($scope.selectedNavSubBar == 1) {		// KG
		$scope.topViewedSchools = schoolsService.topViewedKGs.get({},
			function(data) {
				$interval($scope.gotoRanking, 100, 1);
    		}
		);
		$scope.topBookmarkedSchools = schoolsService.topBookmarkedKGs.get({},
			function(data) {
				$interval($scope.gotoRanking, 100, 1);
			}
		);
	}

	$scope.gotoRanking = function() {
		if ($scope.userInfo.isMobile) {
			if ($location.path().indexOf('top-viewed') > -1) {
				$scope.gotoId('schools-ranking-top-viewed');
			} else if ($location.path().indexOf('top-bookmarked') > -1) {
				$scope.gotoId('schools-ranking-top-bookmarked');
		    } else if ($location.path().indexOf('top-discussed') > -1) {
				$scope.gotoId('schools-ranking-top-discussed');
		    }
		}

		// HACK for admin
		if ($scope.userInfo.isE) {
			if ($scope.selectedNavSubBar == 0) {	// PN
				$scope.topDiscussedSchools = schoolsService.topDiscussedPNs.get();
			} else if ($scope.selectedNavSubBar == 1) {		// KG
				$scope.topDiscussedSchools = schoolsService.topDiscussedKGs.get();
			}
		}
	}

});

minibean.controller('BookmarkedSchoolsController',function($scope, myBookmarksService) {
	if ($scope.selectedNavSubBar == 0) {	// PN
		$scope.bookmarkedSchools = myBookmarksService.bookmarkedPNs.get();
	} else if ($scope.selectedNavSubBar == 1) {	// KG
		$scope.bookmarkedSchools = myBookmarksService.bookmarkedKGs.get();
	}
});

minibean.controller('ShowSchoolsController',function($scope, $routeParams, $location, $filter, schoolsFactory, schoolsService, myBookmarksService, locationService, usSpinnerService) {

    $scope.get_header_metaData();

    if ($location.path().indexOf('/pn') > -1) {
    	$scope.selectNavBar('SCHOOLS', 0);
    } else if ($location.path().indexOf('/kg') > -1) {
    	$scope.selectNavBar('SCHOOLS', 1);
    } else {
    	$scope.selectNavBar('SCHOOLS', 0);
    }

    $scope.initSchools = function() {
    	$scope.selectedDistrictId = $routeParams.districtId;
    	if ($scope.selectedDistrictId == undefined) {
    		if ($scope.userInfo.isLoggedIn) {
    			$scope.selectedDistrictId = $scope.userInfo.location.id;
    		} else { 
    			$scope.selectedDistrictId = $scope.districts[0].id;
    		}
		}
	    if ($scope.selectedNavSubBar == 0) {	// PN
	    	$scope.schools = schoolsService.pnsByDistrict.get({district_id:$scope.selectedDistrictId});
	    	$scope.bookmarkedSchools = myBookmarksService.bookmarkedPNs.get();
	    } else if ($scope.selectedNavSubBar == 1) {		// K
	    	$scope.schools = schoolsService.kgsByDistrict.get({district_id:$scope.selectedDistrictId});
	    	$scope.bookmarkedSchools = myBookmarksService.bookmarkedKGs.get();
	    }
	    $scope.filteredSchools = $scope.schools;
    }

    $scope.districts = locationService.allDistricts.get({},
    	function(data) {
    		$scope.initSchools();
    	}
    );

    // search by name
    $scope.maxSchoolsSearchCount = DefaultValues.MAX_SCHOOLS_SEARCH_COUNT;
    $scope.resetSearch = function() {
    	$scope.searchTerm = '';
		$scope.searchResults = [];
		$("#schools-searchfield").val('');
        $("#schools-searchfield").trigger('input');
        $scope.searchMode = false;
    }
    $scope.resetSearch();

    $scope.searchByName = function(schoolQuery) {
		if(schoolQuery != undefined && schoolQuery.length > 0 && $scope.searchTerm != schoolQuery) {
			$scope.searchMode = true;
			$scope.searching = true;
			$scope.searchTerm = schoolQuery;
			if ($scope.selectedNavSubBar == 0) {
				$scope.searchResults = schoolsService.searchPNsByName.get({query:schoolQuery},
					function(data) {
						$scope.searching = false;
					}
				);
			} else if ($scope.selectedNavSubBar == 1) {
				$scope.searchResults = schoolsService.searchKGsByName.get({query:schoolQuery},
					function(data) {
						$scope.searching = false;
					}
				);
			}
		}
		if (schoolQuery == null || schoolQuery.length == 0) {
			$scope.resetSearch();
		}
	}

    // bookmark
    $scope.bookmarkPN = function(id) {
    	var schools = $scope.filteredSchools;
    	if ($scope.searchMode) {
    		schools = $scope.searchResults;
    	}
    	schoolsFactory.bookmarkPN(id, schools, $scope.bookmarkedSchools);	
    }

    $scope.unBookmarkPN = function(id) {
    	var schools = $scope.filteredSchools;
    	if ($scope.searchMode) {
    		schools = $scope.searchResults;
    	}
    	schoolsFactory.unBookmarkPN(id, schools, $scope.bookmarkedSchools);
    }

    $scope.bookmarkKG = function(id) {
    	var schools = $scope.filteredSchools;
    	if ($scope.searchMode) {
    		schools = $scope.searchResults;
    	}
    	schoolsFactory.bookmarkKG(id, schools, $scope.bookmarkedSchools);	
    }

    $scope.unBookmarkKG = function(id) {
    	var schools = $scope.filteredSchools;
    	if ($scope.searchMode) {
    		schools = $scope.searchResults;
    	}
    	schoolsFactory.unBookmarkKG(id, schools, $scope.bookmarkedSchools);
    }

    // remember all filters user set
    $scope.couponFilter = {'cp':'all'};
    $scope.orgTypeFilter = {'orgt':'all'};
    $scope.curTypeFilter = {'curt':'all'};
    $scope.classtimesFilter = {'ct':'all'};
    $scope.orgFilter = {'org':'all'};
    $scope.setFilter = function(key, value) {
    	if (key == 'cp') {
    		$scope.couponFilter = {'cp':value};
    	} else if (key == 'orgt') {
    		$scope.orgTypeFilter = {'orgt':value};
    	} else if (key == 'curt') {
    		$scope.curTypeFilter = {'curt':value};
    	} else if (key == 'ct') {
    		$scope.classtimesFilter = {'ct':value};
    	} else if (key == 'org') {
    		$scope.orgFilter = {'org':value};
    	}
    }
    $scope.applySchoolFilter = function(key, value) {
    	$scope.setFilter(key, value);

    	// filter one by one
    	$scope.filtering = true;
    	$scope.filteredSchools = $scope.schools;
    	if ($scope.couponFilter.cp != 'all') {
    		$scope.filteredSchools = $filter('objFilter')($scope.filteredSchools, $scope.couponFilter);
    	}
    	if ($scope.orgTypeFilter.orgt != 'all') {
    		$scope.filteredSchools = $filter('objFilter')($scope.filteredSchools, $scope.orgTypeFilter);
    	}
    	if ($scope.curTypeFilter.curt != 'all') {
    		$scope.filteredSchools = $filter('objFilter')($scope.filteredSchools, $scope.curTypeFilter, true);
    	}
    	if ($scope.classtimesFilter.ct != 'all') {
    		$scope.filteredSchools = $filter('objFilter')($scope.filteredSchools, $scope.classtimesFilter);
    	}
    	if ($scope.orgFilter.org != 'all') {
    		$scope.filteredSchools = $filter('objFilter')($scope.filteredSchools, $scope.orgFilter);
    	}
    	$scope.filtering = false;
	}
});

minibean.controller('ShowArticlesController',function($scope, $routeParams, articleFactory, articleService, tagwordService, usSpinnerService) {

    $scope.get_header_metaData();

    $scope.defaultCollapseCount = DefaultValues.TAGWORD_LIST_COLLAPSE_COUNT;
    var tagwordRequest = false;
    if ($routeParams.tagwordId != undefined) {
        var tagwordId = $routeParams.tagwordId; 
        tagwordRequest = true;
        $scope.selectNavBar($routeParams.catGroup, -1);
    } else {
        var catId = $routeParams.catId;
        if (catId == undefined) {
           catId = 0;
        }
        $scope.selectNavBar($scope.getArticleCategoryGroup(catId), catId);
    }

    // tag words
    $scope.hotArticlesTagwords = tagwordService.HotArticlesTagwords.get({}, 
        function(data) {
            if (tagwordRequest && $routeParams.catGroup == 'HOT_ARTICLES' || 
                tagwordRequest && $routeParams.catGroup == 'FRONTPAGE') {
                angular.forEach(data, function(tagword, key){
                    if(tagword.id == tagwordId) {
                        $scope.tagword = tagword;
                    }
                })
            }
        });
    $scope.soonMomsTagwords = tagwordService.SoonMomsTagwords.get({}, 
        function(data) {
            if (tagwordRequest && $routeParams.catGroup == 'SOON_TO_BE_MOMS_ARTICLES') {
                angular.forEach(data, function(tagword, key){
                    if(tagword.id == tagwordId) {
                        $scope.tagword = tagword;
                    }
                })
            }
        });

    // utilities
    if (!tagwordRequest) {
    	$scope.hotArticles = articleService.HotArticles.get({category_id:catId});
    	$scope.recommendedArticles = articleService.RecommendedArticles.get({category_id:catId});
    	$scope.newArticles = articleService.NewArticles.get({category_id:catId});
    }

	$scope.articlesScrollOffset = 0;
	var noMore = false;
	$scope.get_result = function() {
		usSpinnerService.spin('loading...');
		$scope.isBusy = true;
		if (tagwordRequest) {
            tagwordService.ClickTagword.get({id:tagwordId});
            $scope.result = articleService.ArticlesByTagword.get({tagword_id:tagwordId, offset: $scope.articlesScrollOffset}, 
                function(data) {
                    postGetResults(data);
                    usSpinnerService.stop('loading...');
                });
        } else {
            $scope.result = articleService.ArticleCategorywise.get({category_id:catId, offset: $scope.articlesScrollOffset}, 
                function(data) {
                    postGetResults(data);
                    usSpinnerService.stop('loading...');
                });
        }
    };

    $scope.result = [];
    $scope.get_result();

    $scope.next_result = function() {
        if ($scope.isBusy) return;
        if (noMore) return;
        $scope.isBusy = true;
        usSpinnerService.spin('loading...');
        if (tagwordRequest) {
            articleService.ArticlesByTagword.get({tagword_id:tagwordId, offset: $scope.articlesScrollOffset},
                function(data){
                    postNextResults(data);
                    usSpinnerService.stop('loading...');
                });
        } else {
            articleService.ArticleCategorywise.get({category_id:catId, offset: $scope.articlesScrollOffset},
                function(data){
                    postNextResults(data);
                    usSpinnerService.stop('loading...');
                });
        }
    }

    var postGetResults = function(data) {
        var count = 1;
        $scope.articlesScrollOffset++;
        angular.forEach($scope.result, function(element, key){
            if(count == 1) {
                $scope.desc = element.ds;
                $scope.article1 = element;
            }
            if(count == 2) {
                $scope.article2 = element;
            }
            if(count == 3) {
                $scope.article3 = element;
            }
            count++;
        })
        if ($scope.result.length == 0) {
            noMore = true;
        }
        $scope.categoryImage = $scope.result[0].category_url;
        $scope.categoryName = $scope.result[0].ct.name;
        if(catId == 0) {
            $scope.allCategory = true;
            $scope.oneCategory = false;
        }
        else
        {
            $scope.allCategory = false;
            $scope.oneCategory = true;
        }
        $scope.isBusy = false;

        // render mobile scroll nav bar
        if ($scope.userInfo.isMobile) {
            $scope.renderNavSubBar();
        }
    }

    var postNextResults = function(data) {
        var posts = data;
        if(posts.length == 0) {
            noMore = true;
        }

        for (var i = 0; i < posts.length; i++) {
            $scope.result.push(posts[i]);
        }
        $scope.isBusy = false;
        $scope.articlesScrollOffset++;
    }

    $scope.bookmarkArticle = function(article_id) {
        articleFactory.bookmarkArticle(article_id, $scope.result);
    }

    $scope.unBookmarkArticle = function(article_id) {
        articleFactory.unBookmarkArticle(article_id, $scope.result);
    }
});

minibean.controller('MyMagazineNewsFeedController', function($scope, postFactory, postManagementService, $upload, $http, usSpinnerService, myMagazineNewsFeedService) {

    $scope.get_header_metaData();

    $scope.newsFeeds = { posts: [] };

    $scope.deletePost = function(postId) {
        postFactory.deletePost(postId, $scope.newsFeeds.posts);
    }

    $scope.deleteComment = function(commentId, post) {
        postFactory.deleteComment(commentId, post);
    }

    $scope.get_all_comments = function(id) {
        postFactory.getAllComments(id, $scope.newsFeeds.posts, $scope);
    }

    var noMore = false;
    var offset = 0;
    $scope.nextNewsFeeds = function() {
        if ($scope.isBusy) return;
        if (noMore) return;
        $scope.isBusy = true;
        myMagazineNewsFeedService.NewsFeeds.get({offset:offset},
            function(data){
                var posts = data.posts;
                if(posts.length == 0) {
                    noMore = true;
                }

                for (var i = 0; i < posts.length; i++) {
                    $scope.newsFeeds.posts.push(posts[i]);
                }
                $scope.isBusy = false;
                offset++;
            }
        );
    }

    $scope.like_post = function(post_id) {
        postFactory.like_post(post_id, $scope.newsFeeds.posts);
    }

    $scope.unlike_post = function(post_id) {
        postFactory.unlike_post(post_id, $scope.newsFeeds.posts);
    }

    $scope.like_comment = function(post_id, comment_id) {
        postFactory.like_comment(post_id, comment_id, $scope.newsFeeds.posts);
    }

    $scope.unlike_comment = function(post_id, comment_id) {
        postFactory.unlike_comment(post_id, comment_id, $scope.newsFeeds.posts);
    }

    $scope.bookmarkPost = function(post_id) {
        postFactory.bookmarkPost(post_id, $scope.newsFeeds.posts);
    }

    $scope.unBookmarkPost = function(post_id) {
        postFactory.unBookmarkPost(post_id, $scope.newsFeeds.posts);
    }
});

minibean.controller('NewsFeedController', function($scope, postFactory, postManagementService, $timeout, $upload, $http, usSpinnerService, newsFeedService) {

    $scope.get_header_metaData();

    $scope.selectNavBar('HOME', -1);

	$scope.newsFeeds = { posts: [] };

    $scope.showMore = function(id) {
        postFactory.showMore(id, $scope.newsFeeds.posts);
	}

    $scope.get_all_comments = function(id) {
        postFactory.getAllComments(id, $scope.newsFeeds.posts, $scope);
    }

	$scope.deletePost = function(postId) {
        postFactory.deletePost(postId, $scope.newsFeeds.posts);
    }

    $scope.deleteComment = function(commentId, post) {
        postFactory.deleteComment(commentId, post);
    }

    $scope.select_emoticon_comment = function(code, index) {
        postFactory.selectCommentEmoticon(code, index);
    }

	$scope.comment_on_post = function(id, commentText) {
        // first convert to links
        //commentText = convertText(commentText);

		var data = {
			"post_id" : id,
			"commentText" : commentText,
			"withPhotos" : $scope.commentSelectedFiles.length != 0
		};
		var post_data = data;

		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(response) {
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var comment = {"oid" : $scope.userInfo.id, "d" : response.text, "on" : $scope.userInfo.displayName,
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c, "id" : response.id};
						post.cs.push(comment);

						if($scope.commentSelectedFiles.length == 0) {
							return;
						}

						$scope.commentSelectedFiles = [];
						$scope.commentDataUrls = [];

						// when post is done in BE then do photo upload
						//log($scope.commentTempSelectedFiles.length);
						for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadCommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : response.id
								},
								file: $scope.commentTempSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.commentTempSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == response.id) {
											cmt.hasImage = true;
											if(cmt.imgs) {

											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
                        }
                    }
                });
                usSpinnerService.stop('loading...');	
            });
	}

    $scope.want_answer = function(post_id) {
        postFactory.want_answer(post_id, $scope.newsFeeds.posts);
    }

    $scope.unwant_answer = function(post_id) {
        postFactory.unwant_answer(post_id, $scope.newsFeeds.posts);
    }

    $scope.like_post = function(post_id) {
        postFactory.like_post(post_id, $scope.newsFeeds.posts);
    }

    $scope.unlike_post = function(post_id) {
        postFactory.unlike_post(post_id, $scope.newsFeeds.posts);
    }

    $scope.like_comment = function(post_id, comment_id) {
        postFactory.like_comment(post_id, comment_id, $scope.newsFeeds.posts);
    }

    $scope.unlike_comment = function(post_id, comment_id) {
        postFactory.unlike_comment(post_id, comment_id, $scope.newsFeeds.posts);
    }

    $scope.bookmarkPost = function(post_id) {
        postFactory.bookmarkPost(post_id, $scope.newsFeeds.posts);
    }

    $scope.unBookmarkPost = function(post_id) {
        postFactory.unBookmarkPost(post_id, $scope.newsFeeds.posts);
    }

	var noMore = false;
	var offset = 0;
	$scope.nextNewsFeeds = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		$scope.isBusy = true;
		newsFeedService.NewsFeeds.get({offset:offset},
			function(data){
				var posts = data.posts;
				if(posts.length == 0) {
					noMore = true;
				}

				for (var i = 0; i < posts.length; i++) {
					$scope.newsFeeds.posts.push(posts[i]);
			    }
			    $scope.isBusy = false;
				offset++;
			}
		);
	}

 *//*** QnA Community ***//*

	$scope.qnaCommentPhoto = function(post_id) {
		$("#qna-comment-photo-id").click();
		$scope.commentedOnPost = post_id ;
	};

	$scope.qnaCommentSelectedFiles = [];
	$scope.qnaTempCommentSelectedFiles = [];
	$scope.qnaCommentDataUrls = [];

	$scope.onQnACommentFileSelect = function($files) {
		//log($scope.qnaCommentSelectedFiles.length);
		if($scope.qnaCommentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.qnaTempCommentSelectedFiles = [];
		}

		$scope.qnaCommentSelectedFiles.push($files);
		//log($scope.qnaCommentSelectedFiles);
		$scope.qnaTempCommentSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.qnaCommentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	};

	$scope.answer_to_question = function(question_post_id, answerText) {
		// first convert to links
        //answerText = convertText(answerText);

        var data = {
			"post_id" : question_post_id,
			"answerText" : answerText,
			"withPhotos" : $scope.qnaCommentSelectedFiles.length != 0
		};
		var post_data = data;

		usSpinnerService.spin('loading...');
		$http.post('/communityQnA/question/answer', data) 
			.success(function(response) {
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var answer = {"oid" : $scope.userInfo.id, "d" : response.text, "on" : $scope.userInfo.displayName, 
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c, "id" : response.id};
                        answer.isO = true;
                        answer.n = post.n_c;
						post.cs.push(answer);

						if($scope.qnaCommentSelectedFiles.length == 0) {
							return;
						}

						$scope.qnaCommentSelectedFiles = [];
						$scope.qnaCommentDataUrls = [];

						// when post is done in BE then do photo upload
						//log($scope.qnaTempCommentSelectedFiles.length);
						for(var i=0 ; i<$scope.qnaTempCommentSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadCommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : response.id
								},
								file: $scope.qnaTempCommentSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.qnaTempCommentSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == response.id) {
											cmt.hasImage = true;
											if(cmt.imgs) {

											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
    					}
    				}
    				usSpinnerService.stop('loading...');
    			});
            });
	}

	$scope.remove_image_from_qna_comment = function(index) {
		$scope.qnaCommentSelectedFiles.splice(index, 1);
		$scope.qnaTempCommentSelectedFiles.splice(index, 1);
		$scope.qnaCommentDataUrls.splice(index, 1);
	}

  *//** images **//*

	$scope.commentPhoto = function(post_id) {
		$("#comment-photo-id").click();
		$scope.commentedOnPost = post_id;
	}
	$scope.commentSelectedFiles = [];
	$scope.commentTempSelectedFiles = [];
	$scope.commentDataUrls = [];

	$scope.onCommentFileSelect = function($files) {
		//log($scope.commentSelectedFiles.length);
		if($scope.commentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.commentTempSelectedFiles = [];
		}

		$scope.commentSelectedFiles.push($files);
		//log($scope.commentSelectedFiles);
		$scope.commentTempSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.commentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}

	$scope.remove_image_from_comment = function(index) {
		$scope.commentSelectedFiles.splice(index, 1);
		$scope.commentTempSelectedFiles.splice(index, 1);
		$scope.commentDataUrls.splice(index, 1);
	}

});

minibean.controller('UserNewsFeedController', function($scope, $routeParams, $timeout, $upload, postFactory, postManagementService, $http, usSpinnerService, userNewsFeedService) {

	$scope.get_header_metaData();

	$scope.newsFeeds = { posts: [] };

	$scope.showMore = function(id) {
        postFactory.showMore(id, $scope.newsFeeds.posts);
    }

    $scope.get_all_comments = function(id) {
        postFactory.getAllComments(id, $scope.newsFeeds.posts, $scope);
    }

    $scope.deletePost = function(postId) {
        postFactory.deletePost(postId, $scope.newsFeeds.posts);
    }

    $scope.deleteComment = function(commentId, post) {
        postFactory.deleteComment(commentId, post);
    }

    $scope.select_emoticon_comment = function(code, index) {
        postFactory.selectCommentEmoticon(code, index);
    }

	$scope.comment_on_post = function(id, commentText) {
        // first convert to links
        //commentText = convertText(commentText);

		var data = {
			"post_id" : id,
			"commentText" : commentText,
			"withPhotos" : $scope.commentSelectedFiles.length != 0
		};
		var post_data = data;

		usSpinnerService.spin('loading...');
		$http.post('/community/post/comment', data) 
			.success(function(response) {
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var comment = {"oid" : $scope.userInfo.id, "d" : response.text, "on" : $scope.userInfo.displayName,
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c, "id" : response.id};
						post.cs.push(comment);

						if($scope.commentSelectedFiles.length == 0) {
							return;
						}

						$scope.commentSelectedFiles = [];
						$scope.commentDataUrls = [];

						// when post is done in BE then do photo upload
						//log($scope.commentTempSelectedFiles.length);
						for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadCommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : response.id
								},
								file: $scope.commentTempSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.commentTempSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == response.id) {
											cmt.hasImage = true;
											if(cmt.imgs) {

											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
    					}
    				}
    				usSpinnerService.stop('loading...');	
    			});
    		});
	}

	$scope.qnaCommentPhoto = function(post_id) {
		$("#qna-comment-photo-id").click();
		$scope.commentedOnPost = post_id ;
	}

	$scope.qnaCommentSelectedFiles = [];
	$scope.qnaTempCommentSelectedFiles = [];
	$scope.qnaCommentDataUrls = [];

	$scope.onQnACommentFileSelect = function($files) {
		//log($scope.qnaCommentSelectedFiles.length);
		if($scope.qnaCommentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.qnaTempCommentSelectedFiles = [];
		}

		$scope.qnaCommentSelectedFiles.push($files);
		//log($scope.qnaCommentSelectedFiles);
		$scope.qnaTempCommentSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.qnaCommentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	};

	$scope.answer_to_question = function(question_post_id, answerText) {
		// first convert to links
        //answerText = convertText(answerText);

        var data = {
			"post_id" : question_post_id,
			"answerText" : answerText,
			"withPhotos" : $scope.qnaCommentSelectedFiles.length != 0
		};
		var post_data = data;

		usSpinnerService.spin('loading...');
		$http.post('/communityQnA/question/answer', data) 
			.success(function(response) {
				angular.forEach($scope.newsFeeds.posts, function(post, key){
					if(post.id == data.post_id) {
						post.n_c++;
						post.ut = new Date();
						var answer = {"oid" : $scope.userInfo.id, "d" : response.text, "on" : $scope.userInfo.displayName, 
								"isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c, "id" : response.id};
                        answer.isO = true;
                        answer.n = post.n_c;
                        post.cs.push(answer);

						if($scope.qnaCommentSelectedFiles.length == 0) {
							return;
						}

						$scope.qnaCommentSelectedFiles = [];
						$scope.qnaCommentDataUrls = [];

						// when post is done in BE then do photo upload
						//log($scope.qnaTempCommentSelectedFiles.length);
						for(var i=0 ; i<$scope.qnaTempCommentSelectedFiles.length ; i++) {
							usSpinnerService.spin('loading...');
							$upload.upload({
								url : '/image/uploadCommentPhoto',
								method: $scope.httpMethod,
								data : {
									commentId : response.id
								},
								file: $scope.qnaTempCommentSelectedFiles[i],
								fileFormDataName: 'comment-photo'
							}).success(function(data, status, headers, config) {
								$scope.qnaTempCommentSelectedFiles.length = 0;
								if(post.id == post_data.post_id) {
									angular.forEach(post.cs, function(cmt, key){
										if(cmt.id == response.id) {
											cmt.hasImage = true;
											if(cmt.imgs) {

											} else {
												cmt.imgs = [];
											}
											cmt.imgs.push(data);
										}
									});
								}
							});
    					}
    				}
    				usSpinnerService.stop('loading...');
    			});
    		});
	}

	$scope.remove_image_from_qna_comment = function(index) {
		$scope.qnaCommentSelectedFiles.splice(index, 1);
		$scope.qnaTempCommentSelectedFiles.splice(index, 1);
		$scope.qnaCommentDataUrls.splice(index, 1);
	}

   *//**images **//*

	$scope.commentPhoto = function(post_id) {
		$("#comment-photo-id").click();
		$scope.commentedOnPost = post_id;
	}
	$scope.commentSelectedFiles = [];
	$scope.commentTempSelectedFiles = [];
	$scope.commentDataUrls = [];

	$scope.onCommentFileSelect = function($files) {
		//log($scope.commentSelectedFiles.length);
		if($scope.commentSelectedFiles.length == DefaultValues.POST_PHOTO_UPLOAD) {
			$scope.commentTempSelectedFiles = [];
		}

		$scope.commentSelectedFiles.push($files);
		//log($scope.commentSelectedFiles);
		$scope.commentTempSelectedFiles.push($files);
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			if (window.FileReader && $file.type.indexOf('image') > -1) {
				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.commentDataUrls.push(e.target.result);
						});
					}
				}(fileReader, i);
			}
		}
	}

	$scope.remove_image_from_comment = function(index) {
		$scope.commentSelectedFiles.splice(index, 1);
		$scope.commentTempSelectedFiles.splice(index, 1);
		$scope.commentDataUrls.splice(index, 1);
	}

	$scope.want_answer = function(post_id) {
        postFactory.want_answer(post_id, $scope.newsFeeds.posts);
    }

    $scope.unwant_answer = function(post_id) {
        postFactory.unwant_answer(post_id, $scope.newsFeeds.posts);
    }

    $scope.like_post = function(post_id) {
        postFactory.like_post(post_id, $scope.newsFeeds.posts);
    }

    $scope.unlike_post = function(post_id) {
        postFactory.unlike_post(post_id, $scope.newsFeeds.posts);
    }

    $scope.like_comment = function(post_id, comment_id) {
        postFactory.like_comment(post_id, comment_id, $scope.newsFeeds.posts);
    }

    $scope.unlike_comment = function(post_id, comment_id) {
        postFactory.unlike_comment(post_id, comment_id, $scope.newsFeeds.posts);
    }

    $scope.bookmarkPost = function(post_id) {
        postFactory.bookmarkPost(post_id, $scope.newsFeeds.posts);
    }

    $scope.unBookmarkPost = function(post_id) {
        postFactory.unBookmarkPost(post_id, $scope.newsFeeds.posts);
    }

	var noMoreC = false;
	var offsetC = 0;
	var noMoreP = false;
	var offsetP = 0;

	$scope.setSelectedSubTab = function (tab) {
        if ($scope.selectedSubTab == tab)
            return;

		$scope.selectedSubTab = tab;
		$scope.newsFeeds = { posts: [] };
		$scope.isBusyP = false;
		$scope.isBusyC = false;
		noMoreC = false;
		offsetC = 0;
		noMoreP = false;
		offsetP = 0;
	}

    var id = -1;    // my profile newsfeed
    if($routeParams.id != undefined){
        id = $routeParams.id;
    }

	// nextNewsFeeds section starts
	$scope.nextNewsFeeds = function() {
		if ($scope.isBusyP) return;
		if (noMoreP) return;
		$scope.isBusyP = true;
			userNewsFeedService.NewsFeedsPosts.get({offset:offsetP,id:id},
				function(data){

					var posts = data.posts;
					if(posts.length == 0) {
						noMoreP = true;
						$scope.isBusyP = false;
					}

					for (var i = 0; i < posts.length; i++) {
						$scope.newsFeeds.posts.push(posts[i]);
				    }
				    $scope.isBusyP = false;
					offsetP++;
				}
			);
	}
	// nextNewsFeeds section ends

	$scope.nextNewsFeedsComments = function() {
		if ($scope.isBusyC) return;
		if (noMoreC) return;
		$scope.isBusyC = true;
		userNewsFeedService.NewsFeedsComments.get({offset:offsetC,id:id},
			function(data){

				var posts = data.posts;
				if(posts.length == 0) {
					noMoreC = true;
					$scope.isBusyC = false;
				}

				for (var i = 0; i < posts.length; i++) {
					$scope.newsFeeds.posts.push(posts[i]);
			    }
			    $scope.isBusyC = false;
				offsetC++;
			}
		);
	}

});

minibean.controller('MyBookmarksController', function($scope, postFactory, myBookmarksService, bookmarkService, postManagementService, $http, usSpinnerService) {

    $scope.bookmarkSummary = myBookmarksService.bookmarkSummary.get();

	$scope.posts = { posts: [] };

	$scope.articles = { article: [] };

	$scope.selectedSubTab = 1;

	$scope.showMore = function(id) {
        postFactory.showMore(id, $scope.posts.posts);
    }

    $scope.get_all_comments = function(id) {
        postFactory.getAllComments(id, $scope.posts.posts, $scope);
    }

	$scope.deletePost = function(postId) {
        postFactory.deletePost(postId, $scope.posts.posts);
    }

	$scope.deleteComment = function(commentId, post) {
        postFactory.deleteComment(commentId, post);
    }

	$scope.unBookmarkPost = function(post_id) {
		bookmarkService.unbookmarkPost.get({post_id:post_id}, function(data) {
			angular.forEach($scope.posts.posts, function(post, key){
				if(post.id == post_id) {
					post.isBookmarked = false;
					$scope.posts.posts.splice($scope.posts.posts.indexOf(post),1);
					if (post.type == 'QUESTION')
    					$scope.bookmarkSummary.qc--;
                    else if (post.type == 'SIMPLE')
                        $scope.bookmarkSummary.pc--;
				}
			})
		});
	}

	$scope.unBookmarkArticle = function(article_id) {
		bookmarkService.unbookmarkArticle.get({article_id:article_id}, function(data) {
			angular.forEach($scope.articles.article, function(article, key){
				if(article.id == article_id) {
					article.isBookmarked = false;
					$scope.articles.article.splice($scope.articles.article.indexOf(article),1);
                    $scope.bookmarkSummary.ac--;
				}
			})
		});
	}

	$scope.unBookmarkPKView = function(article_id) {
		bookmarkService.unbookmarkPKView.get({pkview_id:pkview_id}, function(data) {
            angular.forEach($scope.pkviews.pkview, function(pkview, key){
                if(pkview.id == pkview_id) {
                    pkview.isBookmarked = false;
                    $scope.pkviews.pkview.splice($scope.pkviews.pkview.indexOf(pkview),1);
                    $scope.bookmarkSummary.pkc--;
                }
            })
        });
    }

	$scope.want_answer = function(post_id) {
        postFactory.want_answer(post_id, $scope.posts.posts);
    }

    $scope.unwant_answer = function(post_id) {
        postFactory.unwant_answer(post_id, $scope.posts.posts);
    }

    $scope.like_post = function(post_id) {
        postFactory.like_post(post_id, $scope.posts.posts);
    }

    $scope.unlike_post = function(post_id) {
        postFactory.unlike_post(post_id, $scope.posts.posts);
    }

    $scope.like_comment = function(post_id, comment_id) {
        postFactory.like_comment(post_id, comment_id, $scope.posts.posts);
    }

    $scope.unlike_comment = function(post_id, comment_id) {
        postFactory.unlike_comment(post_id, comment_id, $scope.posts.posts);
    }

	var offset = 0;
	var noMore = false;
	$scope.nextPosts = function() {
		if ($scope.isBusy) return;
		if (noMore) return;
		$scope.isBusy = true;
		myBookmarksService.bookmarkedPosts.get({offset:offset},
            function(data){
    			var posts = data;
    			if(data.length == 0) {
    				noMore = true;
    			}

    			for (var i = 0; i < posts.length; i++) {
    				$scope.posts.posts.push(posts[i]);
    		    }
    			$scope.isBusy = false;
    			offset++;
    		});
	}

	var offsetA = 0;
	var noMoreA = false;
	$scope.nextArticles = function() {
		if ($scope.isBusyA) return;
		if (noMoreA) return;
		$scope.isBusyA = true;
		myBookmarksService.bookmarkedArticles.get({offsetA:offsetA},
            function(data){
    			var articles = data;
    			if(data.length == 0) {
    				noMoreA = true;
    			}

    			for (var i = 0; i < articles.length; i++) {
    				$scope.articles.article.push(articles[i]);
    		    }
    			$scope.isBusyA = false;
    			offsetA++;
    		});
	}

	var offsetP = 0;
    var noMoreP = false;
    $scope.nextPKViews = function() {
        if ($scope.isBusyP) return;
        if (noMoreP) return;
        $scope.isBusyP = true;
        myBookmarksService.bookmarkedPKViews.get({offsetP:offsetP},
            function(data){
                var pkviews = data;
                if(data.length == 0) {
                    noMoreP = true;
                }

                for (var i = 0; i < pkviews.length; i++) {
                    $scope.pkviews.pkview.push(pkviews[i]);
                }
                $scope.isBusyP = false;
                offsetP++;
            });
    }
});

minibean.controller('UserConversationController',function($scope, $http, $filter, $timeout, $upload, $location, $routeParams, $sce, searchFriendService, usSpinnerService, getMessageService, conversationService) {

	$scope.get_header_metaData();

    $scope.selectNavBar('HOME', -1);

    $scope.loading = false;

    $scope.messageText = "";

    $scope.select_emoticon = function(code) {
        $scope.messageText += " " + code + " ";
        //$("#message-inputfield").val($("#message-inputfield").val() + " " + code + " ");
        $("#message-inputfield").focus();
        $("#message-inputfield").trigger('input');    // need this to populate jquery val update to ng-model
    }

	if ($location.path().indexOf('/message-list') > -1) {
		$scope.loading = true;
		$scope.conversations = conversationService.allConversations.get(function(){
			if($scope.conversations.length > 0){
				if (!$scope.userInfo.isMobile) {
					$scope.getMessages($scope.conversations[0].id, $scope.conversations[0].uid);
				}
			}
			$scope.loading = false;
		});
	} else if ($location.path().indexOf('/start-conversation') > -1){
		if ($scope.userInfo.id == $routeParams.id) {
            prompt("不可發私人訊息給自己");
        } else {
        	$scope.loading = true;
    		$scope.conversations = conversationService.startConversation.get({id: $routeParams.id},function(){
    			$scope.getMessages($scope.conversations[0].id, $scope.conversations[0].uid);
    			$scope.loading = false;
    		});
        }
	} else if ($location.path().indexOf('/open-conversation') > -1) {
		if ($scope.userInfo.id == $routeParams.id) {
            prompt("不可發私人訊息給自己");
        } else {
        	$scope.loading = true;
    		$scope.conversations = conversationService.openConversation.get({id: $routeParams.id},function(){
    			$scope.getMessages($scope.conversations[0].id, $scope.conversations[0].uid);
    			$scope.loading = false;
    		});
        }
	}

	$scope.messages = [];
	$scope.receiverId;
	$scope.currentConversation;

	var offset = 0;

	$scope.search_friend = function(query) {
		if(query != undefined && query.trim() != '') {
			$scope.searchResult = searchFriendService.userSearch.get({q:query});
		}
	}

	$scope.searchReset = function() {
	   $scope.searchResult = [];
	}

	$scope.sendPhoto = function() {
        $("#send-photo-id").click();
    }

    $scope.selectedFiles = [];
    $scope.dataUrls = [];
    $scope.tempSelectedFiles = [];

    $scope.onFileSelect = function($files) {
        if($scope.selectedFiles.length == 0) {
            $scope.tempSelectedFiles = [];
        }

        $scope.selectedFiles.push($files);
        $scope.tempSelectedFiles.push($files);
        for (var i = 0; i < $files.length; i++) {
            var $file = $files[i];
            if (window.FileReader && $file.type.indexOf('image') > -1) {
                var fileReader = new FileReader();
                fileReader.readAsDataURL($files[i]);
                var loadFile = function(fileReader, index) {
                    fileReader.onload = function(e) {
                        $timeout(function() {
                            $scope.dataUrls.push(e.target.result);
                        });
                    }
                }(fileReader, i);
            }
        }
    }

    $scope.remove_image = function(index) {
    	$scope.selectedFiles = [];
        $scope.dataUrls = [];
        $scope.tempSelectedFiles = [];
    }

	$scope.startConversation = function(uid) {
        if ($scope.userInfo.id == uid) {
            prompt("不可發私人訊息給自己");
            return;
        }

		$scope.receiverId = uid;
		usSpinnerService.spin('loading...');
		conversationService.startConversation.get({id: uid},
			function(data){
				$scope.conversations = data;
				$scope.selectedIndex = 0;
				$scope.getMessages($scope.conversations[0].id, $scope.conversations[0].uid);
				usSpinnerService.stop('loading...');
			});
	}

	$scope.deleteConversation = function(cid) {
		usSpinnerService.spin('loading...');
		conversationService.deleteConversation.get({id: cid},
			function(data){
				$scope.conversations = data;
				$scope.selectedIndex = 0;
				$scope.messages = 0;
				$scope.noMore = false;
				usSpinnerService.stop('loading...');
			});
	}

	$scope.openConversation = function(uid) {
		$location.path('/open-conversation/'+uid);
	}

    $scope.loadMore = false;
    $scope.getMessages = function(cid, uid) {
        offset = 0;
        $scope.receiverId = uid;
        $scope.currentConversation = cid;
        usSpinnerService.spin('loading...');
        getMessageService.getMessages.get({id: cid,offset: offset},
            function(data){
                $scope.loadMore = true;
                $scope.messages = data.message;
                $scope.unread_msg_count = data.counter;
                usSpinnerService.stop('loading...');
                if($scope.messages.length < DefaultValues.CONVERSATION_MESSAGE_COUNT){
                    $scope.loadMore = false;
                }
                offset++;
                $timeout(function(){
                	if ($scope.userInfo.isMobile) {
                		$scope.gotoId('message-input-box');
                	} else {
	                    var objDiv = document.getElementById('message-area');
	                    objDiv.scrollTop = objDiv.scrollHeight;
                	}
                });
            });
    }

	$scope.selectedIndex = 0;  
	$scope.setSelectedIndex = function($index) {
		$scope.selectedIndex = $index ;
	}

	$scope.nextMessages = function() {
        usSpinnerService.spin('loading...');
        getMessageService.getMessages.get({id: $scope.currentConversation,offset: offset},
            function(data){
                $scope.loadMore = true;
                var objDiv = document.getElementById('message-area');
                var height = objDiv.scrollHeight;
                var messages = data.message;
                $scope.unread_msg_count = data.counter;
                for (var i = 0; i < messages.length; i++) {
                    $scope.messages.push(messages[i]);
                }
                if(data.message.length < DefaultValues.CONVERSATION_MESSAGE_COUNT){
                    $scope.loadMore = false;
                }
                usSpinnerService.stop('loading...');
                offset++;
                $timeout(function(){
                    var objDiv = document.getElementById('message-area');
                    objDiv.scrollTop = objDiv.scrollHeight - height;
                });
            });
    }

	$scope.sendMessage = function(msgText) {
        // first convert to links
        //msgText = convertText(msgText);

		var data = {
			"receiver_id" : $scope.receiverId,
			"msgText" : msgText,
			"withPhotos" : $scope.selectedFiles.length != 0
		};
		usSpinnerService.spin('loading...');
		$http.post('/message/sendMsg', data) 
			.success(function(messagedata) {
				$scope.messages = messagedata.message;
				$scope.conversations = conversationService.allConversations.get();
				usSpinnerService.stop('loading...');	

				$timeout(function(){
        			var objDiv = document.getElementById('message-area');
        			objDiv.scrollTop = objDiv.scrollHeight;
        	    });

				if($scope.selectedFiles.length == 0) {
                    return;
                }

                $upload.upload({
                    url: '/image/sendMessagePhoto',
                    method: $scope.httpMethod,
                    data: {
                    	messageId : $scope.messages[0].id
                    },
                    file: $scope.tempSelectedFiles[0],
                    fileFormDataName: 'send-photo'
                }).success(function(data, status, headers, config) {
                    usSpinnerService.stop('loading...');
                    angular.forEach($scope.messages, function(message, key){
                        if(message.id == $scope.messages[0].id) {
                        	message.hasImage = true;
                            message.imgs = data;
                        }
                    });
                    $timeout(function(){
            			var objDiv = document.getElementById('message-area');
            			objDiv.scrollTop = objDiv.scrollHeight;
            	    });
            	    $scope.remove_image(0);
                });
            });
	}

	$scope.currentHeader = "";
	$scope.createDateHeader = function(msgDate) {
		var date = $filter('date')(new Date(msgDate), 'dd/MM/yyyy');
	    var showHeader = (date != $scope.currentHeader); 
	    $scope.currentHeader = date;
	    return showHeader;
	}

});

minibean.controller('MagazineNewsFeedController', function($scope, $upload, $http, $routeParams,  
    postFactory, postManagementService, magazineNewsFeedService, iconsService, usSpinnerService) {

    $scope.get_header_metaData();

    var cat = $routeParams.cat;
    if (cat == undefined) {
        cat = 0;
    }
    $scope.selectNavBar('MAGAZINE', cat);

    $scope.newsFeeds = { posts: [] };

    var noMore = false;
    var offset = 0;
    $scope.nextNewsFeeds = function() {
        if ($scope.isBusy) return;
        if (noMore) return;
        $scope.isBusy = true;
        magazineNewsFeedService.NewsFeeds.get({offset:offset,cat:cat},
            function(data){
                var posts = data.posts;
                if(posts.length == 0) {
                    noMore = true;
                }
                for (var i = 0; i < posts.length; i++) {
                    $scope.newsFeeds.posts.push(posts[i]);
                }
                $scope.isBusy = false;
                offset++;

                // render mobile scroll nav bar
                if ($scope.userInfo.isMobile) {
                    $scope.renderNavSubBar();
                }
            }
        );
    }

    //
    // Post management
    //

    $scope.get_all_comments = function(id) {
        postFactory.getAllComments(id, $scope.posts.posts, $scope);
    }

    $scope.deletePost = function(postId) {
        postFactory.deletePost(postId, $scope.newsFeeds.posts);
    }

    $scope.deleteComment = function(commentId, post) {
        postFactory.deleteComment(commentId, post);
    }

    $scope.like_post = function(post_id) {
        postFactory.like_post(post_id, $scope.newsFeeds.posts);
    }

    $scope.unlike_post = function(post_id) {
        postFactory.unlike_post(post_id, $scope.newsFeeds.posts);
    }

    $scope.like_comment = function(post_id, comment_id) {
        postFactory.like_comment(post_id, comment_id, $scope.newsFeeds.posts);
    }

    $scope.unlike_comment = function(post_id, comment_id) {
        postFactory.unlike_comment(post_id, comment_id, $scope.newsFeeds.posts);
    }

    $scope.bookmarkPost = function(post_id) {
        postFactory.bookmarkPost(post_id, $scope.newsFeeds.posts);
    }

    $scope.unBookmarkPost = function(post_id) {
        postFactory.unBookmarkPost(post_id, $scope.newsFeeds.posts);
    }

    $scope.comment_on_post = function(id, commentText) {
        // first convert to links
        //commentText = convertText(commentText);

        var data = {
            "post_id" : id,
            "commentText" : commentText,
            "withPhotos" : $scope.commentSelectedFiles.length != 0
        };
        var post_data = data;
        usSpinnerService.spin('loading...');
        $http.post('/community/post/comment', data) 
            .success(function(comment_id) {
                $('.commentBox').val('');

                $scope.commentText = "";
                angular.forEach($scope.newsFeeds.posts, function(post, key){
                        if(post.id == data.post_id) {
                            post.n_c++;
                            post.ut = new Date();
                            var comment = {"oid" : $scope.newsFeeds.lu, "d" : commentText, "on" : $scope.newsFeeds.lun,
                                    "isLike" : false, "nol" : 0, "cd" : new Date(), "n_c" : post.n_c,"id" : comment_id};
                            post.cs.push(comment);

                            if($scope.commentSelectedFiles.length == 0) {
                                return;
                            }

                            $scope.commentSelectedFiles = [];
                            $scope.commentDataUrls = [];

                            // when post is done in BE then do photo upload
                            //log($scope.commentTempSelectedFiles.length);
                            for(var i=0 ; i<$scope.commentTempSelectedFiles.length ; i++) {
                                usSpinnerService.spin('loading...');
                                $upload.upload({
                                    url : '/image/uploadCommentPhoto',
                                    method: $scope.httpMethod,
                                    data : {
                                        commentId : comment_id
                                    },
                                    file: $scope.commentTempSelectedFiles[i],
                                    fileFormDataName: 'comment-photo'
                                }).success(function(data, status, headers, config) {
                                    $scope.commentTempSelectedFiles.length = 0;
                                    if(post.id == post_data.post_id) {
                                        angular.forEach(post.cs, function(cmt, key){
                                            if(cmt.id == comment_id) {
                                                cmt.hasImage = true;
                                                if(cmt.imgs) {

                                                } else {
                                                    cmt.imgs = [];
                                                }
                                                cmt.imgs.push(data);
                                            }
                                        });
                                    }
                                });
                        }
                    }
            });
            usSpinnerService.stop('loading...');    
        });
    };
});

    */