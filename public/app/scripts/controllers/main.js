'use strict';

var babybox = angular.module('babybox');

babybox.controller('FrontPageController', 
		function($scope, $route, feedService, ngDialog, userInfo, viewService, $location, $anchorScroll, usSpinnerService) {
	usSpinnerService.spin('loading...');
	$scope.userInfo = userInfo;
	$scope.products = feedService.getFeedProduct.get(
	        function() {
	        	usSpinnerService.stop('loading...');
	        });
	$scope.gotoTop=function(){
		$location.hash('');
		$anchorScroll();
	};

	$scope.open = function (product) {
		$scope.product = product;
		viewService.viewProduct.get({id:product.id});
		ngDialog.open({
			template: '/assets/app/views/babybox/product-dialog.html',
			className: 'ngdialog-theme-plain custom-width ',
			controller: 'ProductController',
			scope: $scope                
		});
	};
});

babybox.controller('ProductPageController', 
		function($scope, $route, $http, likeService, userService, productService, product, userInfo) {
	$scope.product = product;
	$scope.userInfo = userInfo;
});

babybox.controller('ProductController', 
		function($scope, $route, $http, likeService, userService, productService, usSpinnerService, followService) {
	console.log($scope.product.imgs);
	$scope.imageUrl = "/image/get-original-post-image-by-id/"+$scope.product.images[0];
	$scope.collections = [];


	$scope.similarProducts = productService.getSimilarProduct.get();
	$scope.open = false;
	$scope.product = productService.getProductInfo.get({id:$scope.product.id});

	console.log($scope.imageUrl);
	$scope.setImgUrl = function(id){
		$scope.imageUrl= "/image/get-original-post-image-by-id/"+id;
	};

	if($scope.product.oid = $scope.userInfo.id){
		$scope.collections = userService.getUserCollection.get({id:$scope.userInfo.id});
	}
	$scope.like_product = function(id) {
		likeService.likeProduct.get({id:id});
		$scope.product.isLiked = !$scope.product.isLike;
		$scope.product.numLikes++;
	}
	$scope.unlike_product = function(id) {
		likeService.unLikeProduct.get({id:id});
		$scope.product.isLikeD = !$scope.product.isLike;
		$scope.product.numLikes--;
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
		usSpinnerService.spin('loading...');
		$http.post('/product/add-to-collection', data) 
		.success(function(response) {
			usSpinnerService.stop('loading...');
			console.log(response);
		});
	}

	$scope.onFollowUser = function() {
		followService.followUser.get({id:$scope.product.ownerId});
		$scope.product.ifu = !$scope.product.ifu;
	}
	$scope.onUnFollowUser = function() {
		followService.unFollowUser.get({id:$scope.product.ownerId});
		$scope.product.ifu = !$scope.product.ifu;
	}
});

babybox.controller('CommentOnProductController', 
		function($scope, $route, $http, likeService) {
	$scope.formData = {};

	$scope.comArray=[];

	$scope.submit = function() {
		var newCommentVM = {
				"postId" : $scope.product.id,
				"body" : $scope.formData.comment,
		};

		console.log($scope.userInfo);
		usSpinnerService.spin('loading...');
		$http.post('/comment/new', newCommentVM) 
		.success(function(response) {
			console.log(response);
			$scope.comArray.push({
				comment:$scope.formData.comment,
				name: $scope.userInfo.displayName,
				id: $scope.userInfo.id

			});
			$scope.formData.comment="";
			usSpinnerService.stop('loading...');
		});
	}

});


babybox.controller('ProfileController', 
		function($scope, $route, profileUser, userService, userInfo, followService, viewService, ngDialog) {
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

	$scope.open = function (product) {
		$scope.product = product;
		//viewService.viewProduct.get({id:product.id});
		ngDialog.open({
			template: '/assets/app/views/babybox/product-dialog.html',
			className: 'ngdialog-theme-plain custom-width ',
			controller: 'ProductController',
			scope: $scope                
		});
	};

});


babybox.controller('CreateCollectionController', 
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

babybox.controller('CreateProductController',function($scope, $location, $http, $upload, $validator, usSpinnerService, userInfo){
	$scope.userInfo = userInfo;
	$scope.formData = {};
	$scope.selectedFiles =[];
	$scope.submitBtn = "建立社群";
	$scope.submit = function() {
		console.log($scope.formData);		
		var newPostVM = {
				"catId" : $scope.formData.category,
				"title" : $scope.formData.name,
				"body" : $scope.formData.body,
				"price" : $scope.formData.price,
		};
		console.log(newPostVM);

		usSpinnerService.spin('loading...');
		$validator.validate($scope, 'formData')
		.success(function () {
			usSpinnerService.spin('載入中...');
			$upload.upload({
				url: '/web/post/new',
				method: 'POST',
				file: $scope.selectedFiles,
				data: newPostVM,
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



/*
babybox.controller('PrivacySettingsController', function($scope, $http, userSettingsService, usSpinnerService) {

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

babybox.controller('UserAboutController',function($routeParams, $scope, $http, userAboutService, locationService, profilePhotoModal, usSpinnerService) {

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

babybox.controller('EditCommunityController',function($scope,$q, $location,$routeParams, $http, usSpinnerService, iconsService, editCommunityPageService, $upload, profilePhotoModal){

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

babybox.controller('CreateCommunityController',function($scope, $location, $http, $upload, $validator, iconsService, usSpinnerService){

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

babybox.controller('UserProfileController',function($scope, $routeParams, $location, profileService, friendsService, sendInvitation, unFriendService){

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

babybox.controller('SearchPageController', function($scope, $routeParams, communityPageService, $http, communitySearchPageService, usSpinnerService){

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

babybox.controller('PostLandingController', function($scope, $routeParams, $http, $upload, $timeout, $validator, 
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

babybox.controller('QnALandingController', function($scope, $routeParams, $http, $timeout, $upload, $validator, 
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

babybox.controller('CommunityPageController', function($scope, $routeParams, $interval, profilePhotoModal, 
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

babybox.controller('CommunityPostController', function($scope, $routeParams, $http, $upload, $timeout, profilePhotoModal,
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

babybox.controller('CommunityQnAController',function($scope, postFactory, postManagementService, communityQnAPageService, usSpinnerService ,$timeout, $routeParams, $http,  $upload, $validator){

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

babybox.controller('UserConversationController',function($scope, $http, $filter, $timeout, $upload, $location, $routeParams, $sce, searchFriendService, usSpinnerService, getMessageService, conversationService) {

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
*/