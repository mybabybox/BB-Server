'use strict';

var babybox = angular.module('babybox');

babybox.service('feedService',function($resource){
    this.getFeedProduct = $resource(
            '/get-all-feed-products',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

babybox.service('productService',function($resource){
    this.getProductInfo = $resource(
            '/post/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    this.getSimilarProduct = $resource(
            '/get-all-similar-products',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

babybox.service('collecctionService',function($resource){
    this.getFeedProduct = $resource(
            '/get-collection-by-user',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

babybox.service('userService',function($resource){
    this.getUserProduct = $resource(
            '/get-user-products/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', isArray:true}
            }
    );
    
    this.getUserCollection = $resource(
            '/get-user-collections/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', isArray:true}
            }
    );
});

babybox.service('followService',function($resource){
    this.followUser = $resource(
            '/follow-user/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
    this.unFollowUser = $resource(
            '/unfollow-user/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

babybox.service('likeService',function($resource){
    this.likeProduct = $resource(
            '/like-post/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
    this.unLikeProduct = $resource(
            '/unlike-post/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

babybox.service('viewService',function($resource){
    this.viewProduct = $resource(
            '/view-product/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
});




/*
babybox.service('adminService',function($resource){
    this.campaignJoiners = $resource(
            '/admin/get-campaign-joiners/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.pkViewVoters = $resource(
            '/get-pkview-voters/:id/:yes_no',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

babybox.service('frontpageService',function($resource){
    this.pnNewsFeeds = $resource(
            '/get-pnnewsfeeds/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}}
            }
    );
    this.kgNewsFeeds = $resource(
            '/get-kgnewsfeeds/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}}
            }
    );
    this.hotNewsFeeds = $resource(
            '/get-hotnewsfeeds/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}}
            }
    );
    this.hotCommunities = $resource(
            '/get-hotcommunities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
    this.frontpageTopics = $resource(
            '/get-frontpage-topics',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET',isArray:true}
            }
    );
    this.sliderTopics = $resource(
            '/get-slider-topics',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET',isArray:true}
            }
    );
    this.promoTopics = $resource(
            '/get-promo-topics',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET',isArray:true}
            }
    );
    this.promo2Topics = $resource(
            '/get-promo2-topics',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET',isArray:true}
            }
    );
    this.featuredTopics = $resource(
            '/get-featured-topics',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET',isArray:true}
            }
    );
    this.gameTopics = $resource(
            '/get-game-topics',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET',isArray:true}
            }
    );
});

babybox.service('headerBarMetadataService',function($resource){
	this.headerBardata = $resource(
			'/get-headerBar-data',
	        {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
 	);
});

babybox.service('userSettingsService',function($resource){
    this.privacySettings = $resource(
            '/get-privacy-settings',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.edmSettings = $resource(
            '/get-edm-settings',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );    
});

babybox.service('announcementsService',function($resource) {
    this.getGeneralAnnouncements = $resource(
            '/get-general-announcements',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    
    this.getTopAnnouncements = $resource(
            '/get-top-announcements',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

babybox.service('todayWeatherInfoService',function($resource) {
    this.getTodayWeatherInfo = $resource(
            '/get-today-weather-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('gameService',function($resource) {
    this.signInForToday = $resource(
            '/sign-in-for-today',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.gameAccount = $resource(
            '/get-gameaccount',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.gameTransactions = $resource(
            '/get-game-transactions/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{offset:'@offset'},isArray:true}
            }
    );
    this.latestGameTransactions = $resource(
            '/get-latest-game-transactions',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.allGameGifts = $resource(
            '/get-all-game-gifts',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.gameGiftInfo = $resource(
            '/get-game-gift-info/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.redeemGameGift = $resource(
            '/redeem-game-gift/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('locationService',function($resource){
    this.allDistricts = $resource(
            '/get-all-districts',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

babybox.service('postLandingService',function($resource){
    this.postLanding = $resource(
            '/post-landing/:id/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{id:'@id',communityId:'@communityId'}}
            }
    );
});

babybox.service('qnaLandingService',function($resource){
    this.qnaLanding = $resource(
            '/qna-landing/:id/:communityId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{id:'@id',communityId:'@communityId'}}
            }
    );
});

babybox.service('searchService',function($resource){
    this.userSearch = $resource(
            '/user-search?query=:q',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{q:'@q'}, isArray:true}
            }
    );
});

babybox.service('sendInvitation',function($resource){
    this.inviteFriend = $resource(
            '/send-invite?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

babybox.service('unFriendService',function($resource){
    this.doUnfriend = $resource(
            '/un-friend?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

babybox.service('applicationInfoService',function($resource){
    this.ApplicationInfo = $resource(
            '/get-application-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

babybox.service('userInfoService',function($resource){
    this.UserInfo = $resource(
            '/get-user-info',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
    
    this.UserTargetProfile = $resource(
            '/get-user-target-profile',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
    this.CompleteHomeTour = $resource(
            '/complete-home-tour',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

babybox.service('acceptFriendRequestService',function($resource){
    this.acceptFriendRequest = $resource(
            '/accept-friend-request?friend_id=:id&notify_id=:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id',notify_id:'@notify_id'}, isArray:true}
            }
    );
});

babybox.service('acceptJoinRequestService',function($resource){
    this.acceptJoinRequest = $resource(
            '/accept-join-request/:member_id/:group_id/:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
            }
    );
    
    this.acceptInviteRequest = $resource(
            '/accept-invite-request/:member_id/:group_id/:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
            }
    );
});

babybox.service('notificationMarkReadService',function($resource){
    this.markAsRead = $resource(
            '/mark-as-read/:notify_ids',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{notify_ids:'@notify_ids'}, isArray:true}
            }
    );

    this.ignoreIt = $resource(
            '/ignore-it/:notify_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{member_id:'@member_id',group_id:'@group_id',notify_id:'@notify_id'}, isArray:true}
            }
    );
});

babybox.service('userAboutService',function($resource){
    this.UserAbout = $resource(
            '/about-user',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('profilePhotoModal',function( $modal){
    
    this.OpenModal = function(arg, successCallback) {
        this.instance = $modal.open(arg);
        this.onSuccess = successCallback;
    }
    
    this.CloseModal = function() {
        this.instance.dismiss('close');
        this.onSuccess();
    }
});

babybox.service('editCommunityPageService',function($resource){
    this.EditCommunityPage = $resource(
            '/edit-community/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

babybox.service('membersWidgetService',function($resource){
    this.NewCommunityMembers = $resource(
            '/get-new-community-members/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('unJoinedCommunityWidgetService',function($resource){
    this.UnJoinedCommunities = $resource(
            '/get-unjoined-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('sendJoinRequest',function($resource){
    this.sendRequest = $resource(
            '/send-request?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

babybox.service('friendsService',function($resource){
    this.MyFriendsForUtility = $resource(
            '/get-my-friends-for-utility',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.UserFriendsForUtility = $resource(
            '/get-user-friends-for-utility/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
    
    this.MyFriends = $resource(
            '/get-all-my-friends',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    
    this.UserFriends = $resource(
            '/get-all-user-friends/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
    
    this.SuggestedFriends = $resource(
            '/get-suggested-friends',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('sendJoinRequest',function($resource){
    this.sendRequest = $resource(
            '/send-request?id=:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{id:'@id'}}
            }
    );
});

babybox.service('communitiesDiscoverService',function($resource){
    this.getSocialCommunityCategoriesMap = $resource(
            '/get-social-community-categories-map?indexOnly=:indexOnly',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{indexOnly:'@indexOnly'},isArray:true}
            }
    );
    this.getZodiacYearMonthCommunityCategoriesMap = $resource(
            '/get-zodiac-year-month-community-categories-map?indexOnly=:indexOnly',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',params:{indexOnly:'@indexOnly'},isArray:true}
            }
    );
    this.ZodiacYearCommunities = $resource(
            '/get-zodiac-year-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.DistrictCommunities = $resource(
            '/get-district-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.OtherCommunities = $resource(
            '/get-other-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.getAllBusinessCommunityCategories = $resource(
            '/get-all-business-community-categories',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.getAllSocialCommunityCategories = $resource(
            '/get-all-social-community-categories',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

babybox.service('communityWidgetService',function($resource){
    this.MyCommunities = $resource(
            '/get-my-communities',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('communityWidgetByUserService',function($resource){
    this.UserCommunities = $resource(
            '/get-user-communities/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('profileService',function($resource){
    this.Profile = $resource(
            '/profile/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

babybox.service('communitySearchPageService',function($resource){
    this.GetPostsFromIndex = $resource(
            '/searchForPosts/index/:query/:community_id/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{community_id:'@community_id',query:'@query',offset:'@offset'},isArray:true}
            }
    );
});

babybox.service('communityPageService',function($resource){
    this.Community = $resource(
            '/community/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
    this.InitialPosts = $resource(
            '/community/posts/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
    this.NextPosts = $resource(
            '/community/posts/next/:id/:time',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id',time:'@time'},isArray:true}
            }
    );
    
    this.isNewsfeedEnabled = $resource(
            '/is-newsfeed-enabled-for-community/:community_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{community_id:'@community_id'}}
            }
    );
    
    this.toggleNewsfeedEnabled = $resource(
            '/toggle-newsfeed-enabled-for-community/:community_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{community_id:'@community_id'}}
            }
    );
});

babybox.service('postManagementService',function($resource){
    this.deletePost = $resource(
            '/delete-post/:postId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{postId:'@postId'}}
            }
    );
    
    this.deleteComment = $resource(
            '/delete-comment/:commentId',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{commentId:'@commentId'}}
            }
    );
    
    this.postBody = $resource(
            '/get-post-body/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
    this.allComments = $resource(
            '/comments/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'},isArray:true}
            }
    );
});

babybox.service('communityJoinService',function($resource){
    this.sendJoinRequest = $resource(
            '/community/join/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    
    this.leaveCommunity = $resource(
            '/community/leave/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

babybox.service('iconsService',function($resource){
    this.getCommunityIcons = $resource(
            '/image/getCommunityIcons',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
    
    this.getEmoticons = $resource(
            '/image/getEmoticons',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get' ,isArray:true}
            }
    );
});

babybox.service('searchMembersService',function($resource){
    this.getUnjoinedUsers = $resource(
            '/getAllUnjoinedMembers/:id/:query',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id',query : '@query'}, isArray:true}
            }
    );
    
    this.sendInvitationToNonMember = $resource(
            '/inviteToCommunity/:group_id/:user_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{group_id:'@group_id',user_id : '@user_id'}, isArray:true}
            }
    );
});

babybox.service('bookmarkService', function($resource) {
    this.bookmarkPost = $resource(
            '/bookmark-post/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.unbookmarkPost = $resource(
            '/unbookmark-post/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.bookmarkArticle = $resource(
            '/bookmark-article/:article_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{article_id:'@article_id'}}
            }
    );
    
    this.unbookmarkArticle = $resource(
            '/unbookmark-article/:article_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{article_id:'@article_id'}}
            }
    );
    
    this.bookmarkPKView = $resource(
            '/bookmark-pkview/:pkview_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{pkview_id:'@pkview_id'}}
            }
    );
    
    this.unbookmarkPKView = $resource(
            '/unbookmark-pkview/:pkview_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{pkview_id:'@pkview_id'}}
            }
    );
    
    this.bookmarkPN = $resource(
            '/bookmark-pn/:pn_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{pn_id:'@pn_id'}}
            }
    );
    
    this.unbookmarkPN = $resource(
            '/unbookmark-pn/:pn_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{pn_id:'@pn_id'}}
            }
    );
    
    this.bookmarkKG = $resource(
            '/bookmark-kg/:kg_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{pn_id:'@kg_id'}}
            }
    );
    
    this.unbookmarkKG = $resource(
            '/unbookmark-kg/:kg_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{pn_id:'@kg_id'}}
            }
    );
});
    
babybox.service('likeFrameworkService', function($resource) {

    this.hitWantAnswerOnQnA = $resource(
            '/want-ans/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitUnwantAnswerOnQnA = $resource(
            '/unwant-ans/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitLikeOnPost = $resource(
            '/like-post/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitUnlikeOnPost = $resource(
            '/unlike-post/:post_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitLikeOnComment = $resource(
            '/like-comment/:comment_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitUnlikeOnComment = $resource(
            '/unlike-comment/:comment_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{post_id:'@post_id'}}
            }
    );
    
    this.hitLikeOnArticle = $resource(
            '/like-article/:article_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{article_id:'@article_id'}}
            }
    );
    
    this.hitUnlikeOnArticle = $resource(
            '/unlike-article/:article_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{article_id:'@article_id'}}
            }
    );
    
    this.hitLikeOnCampaign = $resource(
            '/like-campaign/:campaign_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{campaign_id:'@campaign_id'}}
            }
    );
    
    this.hitUnlikeOnCampaign = $resource(
            '/unlike-campaign/:campaign_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{campaign_id:'@campaign_id'}}
            }
    );
    
    this.hitLikeOnPKView = $resource(
            '/like-pkview/:pkview_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{pkview_id:'@pkview_id'}}
            }
    );
    
    this.hitUnlikeOnPKView = $resource(
            '/unlike-pkview/:pkview_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{pkview_id:'@pkview_id'}}
            }
    );
});

babybox.service('communityQnAPageService',function($resource){
    this.InitialQuestions = $resource(
            '/communityQnA/questions/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    this.NextQuestions = $resource(
    		'/communityQnA/questions/next/:id/:time',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id',time:'@time'},isArray:true}
            }
    );
});

babybox.service('tagwordService',function($resource){
    this.HotArticlesTagwords = $resource(
            '/get-hot-articles-tagwords',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.SoonMomsTagwords = $resource(
            '/get-soon-moms-tagwords',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.ClickTagword = $resource(
            '/click-tagword/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
});

babybox.service('pkViewService',function($resource){
    this.pkViewInfo = $resource(
            '/get-pkview-info/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get', params:{id:'@id'}}
            }
    );
    this.communityPKViews = $resource(
            '/get-comm-pkviews/:community_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.yesVotePKView = $resource(
            '/yesvote-pkview/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.noVotePKView = $resource(
            '/novote-pkview/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});
    
babybox.service('campaignService',function($resource){
    this.campaignInfo = $resource(
            '/get-campaign-info/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.campaignAnnouncedWinners = $resource(
            '/get-campaign-announced-winners/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

babybox.service('schoolsService',function($resource){
	this.pnInfo = $resource(
            '/get-pn-info/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
	this.searchPNsByName = $resource(
            '/search-pns-by-name/:query',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.pnsByDistrict = $resource(
            '/get-pns-by-district/:district_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.topViewedPNs = $resource(
            '/get-top-viewed-pns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.topDiscussedPNs = $resource(
            '/get-top-discussed-pns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.topBookmarkedPNs = $resource(
            '/get-top-bookmarked-pns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.bookmarkedPNs = $resource(
            '/get-bookmarked-pns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.formReceivedPNs = $resource(
            '/get-form-received-pns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.appliedPNs = $resource(
            '/get-applied-pns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.interviewedPNs = $resource(
            '/get-interviewed-pns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.offeredPNs = $resource(
            '/get-offered-pns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.kgInfo = $resource(
            '/get-kg-info/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
	this.searchKGsByName = $resource(
            '/search-kgs-by-name/:query',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.kgsByDistrict = $resource(
            '/get-kgs-by-district/:district_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.topViewedKGs = $resource(
            '/get-top-viewed-kgs',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.topDiscussedKGs = $resource(
            '/get-top-discussed-kgs',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.topBookmarkedKGs = $resource(
            '/get-top-bookmarked-kgs',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.bookmarkedKGs = $resource(
            '/get-bookmarked-kgs',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

babybox.service('articleService',function($resource){
    this.AllArticleCategories = $resource(
            '/get-all-article-categories',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.NewArticles = $resource(
            '/get-new-Articles/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.HotArticles = $resource(
            '/get-hot-Articles/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.RecommendedArticles = $resource(
            '/get-recommended-Articles/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );  
    this.ArticleCategorywise = $resource(
            '/get-Articles-Categorywise/:category_id/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.ArticlesByTagword = $resource(
            '/get-Articles-TagWise/:tagword_id/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.SixArticles = $resource(
            '/get-Six-Articles/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
    this.getRelatedArticles = $resource(
            '/get-Related-Articles/:id/:category_id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
    this.ArticleInfo = $resource(
            '/get-article-info/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('showImageService',function($resource){
    this.getImage = $resource(
            '/get-image-url/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get'}
            }
    );
});

babybox.service('articleCommentsService',function($resource){
    this.comments = $resource(
            '/ArticleComments/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'get',isArray:true}
            }
    );
});

babybox.service('myMagazineNewsFeedService',function($resource){
    this.NewsFeeds = $resource(
            '/get-my-businessfeeds/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}}
            }
    );
});

babybox.service('newsFeedService',function($resource){
    this.NewsFeeds = $resource(
            '/get-newsfeeds/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}}
            }
    );
});
      
babybox.service('userNewsFeedService',function($resource){

    this.NewsFeedsPosts = $resource(
            '/get-user-newsfeeds-posts/:offset/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset',id:'@id'}}
            }
    );
    
    this.NewsFeedsComments = $resource(
            '/get-user-newsfeeds-comments/:offset/:id',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset',id:'@id'}}
            }
    );
});

babybox.service('myBookmarksService',function($resource){
    this.bookmarkedPosts = $resource(
            '/get-bookmarked-posts/:offset',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset'}, isArray:true}
            }
    );
    
    this.bookmarkedArticles = $resource(
            '/get-bookmarked-articles/:offsetA',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offsetA:'@offsetA'}, isArray:true}
            }
    );
    
    this.bookmarkedPKViews = $resource(
            '/get-bookmarked-pkviews/:offsetP',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offsetA:'@offsetP'}, isArray:true}
            }
    );
    
    this.bookmarkedPNs = $resource(
            '/get-bookmarked-pns',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', isArray:true}
            }
    );
    
    this.bookmarkedKGs = $resource(
            '/get-bookmarked-kgs',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', isArray:true}
            }
    );
    
    this.bookmarkSummary = $resource(
            '/get-bookmark-summary',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET'}
            }
    );
});

babybox.service('conversationService',function($resource){
    this.allConversations = $resource(
        '/get-all-conversations',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get',isArray:true}
        }
    );
    
    this.startConversation = $resource(
        '/start-conversation/:id',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get', isArray:true}
        }
    );
    
    this.openConversation = $resource(
        '/open-conversation/:id',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get', isArray:true}
        }
    );
    
    this.deleteConversation = $resource(
        '/delete-conversation/:id',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get', isArray:true}
        }
    );
});

babybox.service('getMessageService',function($resource){
    this.getMessages = $resource(
        '/get-messages/:id/:offset',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'get'}
        }
    );
});

babybox.service('searchFriendService',function($resource){
    this.userSearch = $resource(
        '/user-friend-search?query=:q',
        {alt:'json',callback:'JSON_CALLBACK'},
        {
            get: {method:'GET', params:{q:'@q'}, isArray:true}
        }
    );
});

babybox.service('magazineNewsFeedService',function($resource){
    this.NewsFeeds = $resource(
            '/get-businessfeeds/:offset/:cat',
            {alt:'json',callback:'JSON_CALLBACK'},
            {
                get: {method:'GET', params:{offset:'@offset',cat:'@cat'}}
            }
    );
});
*/