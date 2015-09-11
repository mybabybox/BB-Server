package mybox.shopping.social;

import java.util.HashMap;
import java.util.Map;

import models.Notification;
import models.Notification.NotificationType;
import models.PrimarySocialRelation;
import models.SocialRelation;
import play.libs.Json;

/**
 * Notification - Class for triggering all the different types of notifications.
 */
public class SocialActivity {
    // play url prefix
    private static final String MY_PREFIX = "/my#!";
    private static final String MAGAZINE_PREFIX = "/magazine#!";
    private static final String SCHOOL_PREFIX = "/schools#!";

    //////////////////////////////////////////////////
    // Url Helpers
    /*private static String resolveCommunityLandingUrl(Long commId, Community.CommunityType type,
                                                     TargetingType targetingType) {
        boolean isBizCommunity = type != null && type == Community.CommunityType.BUSINESS;
        if (isBizCommunity) {
            return MAGAZINE_PREFIX+"/business/community/"+commId;
        } else {
            if (targetingType != null && targetingType == TargetingType.PRE_NURSERY) {
                // Note: If community is shared by PN and KG, PN url will take precedence.
                return SCHOOL_PREFIX+"/pn/"+ CommunityMetaCache.getPNIdFromCommunity(commId);
            } else if (targetingType != null && targetingType == TargetingType.KINDY) {
                return SCHOOL_PREFIX+"/kg/"+ CommunityMetaCache.getKGIdFromCommunity(commId);
            } else {
                return MY_PREFIX+"/community/"+commId;
            }
        }
    }

    private static String resolvePostLandingUrl(Long postId, Long commId, Community.CommunityType type) {
        boolean isBizCommunity = type != null && type == Community.CommunityType.BUSINESS;
        if (isBizCommunity) {
            return MAGAZINE_PREFIX+"/business-post-landing/id/"+postId+"/communityId/"+commId;
        } else {
            return MY_PREFIX +"/post-landing/id/"+postId+"/communityId/"+commId;
        }
    }

    private static String resolveQnALandingUrl(Long postId, Long commId, Community.CommunityType type) {
        boolean isBizCommunity = type != null && type == Community.CommunityType.BUSINESS;
        if (isBizCommunity) {
            return MAGAZINE_PREFIX+"/business-post-landing/id/"+postId+"/communityId/"+commId;
        } else {
            return MY_PREFIX +"/qna-landing/id/"+postId+"/communityId/"+commId;
        }
    }

    private static String resolveCommunityLandingUrl(Long commId) {
        Community.CommunityType type = Community.getCommunityTypeById(commId);
        return resolveCommunityLandingUrl(commId, type, null);
    }

    private static boolean isBusinessCommunity(Community community) {
        return community != null && community.communityType == Community.CommunityType.BUSINESS;
    }

    private static boolean isSendToAllMembers(Community community) {
        return community.getTargetingType() != null &&
               (community.getTargetingType() == TargetingType.PRE_NURSERY ||
                community.getTargetingType() == TargetingType.KINDY);
    }*/
    //////////////////////////////////////////////////


    /**
     * Handle SocialRelation
     * @param socialAction
     */
	public static void handle(SocialRelation socialAction) {
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("actor", socialAction.actor);
		jsonMap.put("target", socialAction.target);
		
		if(socialAction.action != null) {
			switch (socialAction.action) {
                case MEMBER: {
                    Long commId = socialAction.target;

                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/get-mini-cover-community-image-by-id/"+commId);
                    //jsonMap.put("onClick", resolveCommunityLandingUrl(commId));
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.recipient = socialAction.actor;
                    notification.notificationType = NotificationType.COMM_JOIN_APPROVED;
                    notification.message = "你正在關注「"+socialAction.getTargetObject().name+"」";
                    notification.status = 0;
                    notification.save();
                }
				break;

                case FRIEND: {
                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/image/get-mini-image-by-id/"+socialAction.target);
                    jsonMap.put("onClick", MY_PREFIX +"/profile/" + socialAction.target );
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.recipient = socialAction.actor;
                    notification.notificationType = NotificationType.FRD_ACCEPTED;
                    notification.message = "你與 "+socialAction.getTargetObject().name+" 成為了朋友";
                    notification.status = 0;
                    notification.save();
                }
				break;
			}
		} 
		
		if (socialAction.actionType != null) {
			switch (socialAction.actionType) {
                case JOIN_REQUESTED: {
                    Long requesterId = socialAction.actor;

                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/image/get-mini-image-by-id/"+requesterId);
                    jsonMap.put("onClick", MY_PREFIX +"/profile/"+requesterId);
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.usersName = socialAction.getActorObject().name;
                    notification.recipient = socialAction.targetOwner;
                    notification.notificationType = NotificationType.COMM_JOIN_REQUEST;
                    notification.message = socialAction.getActorObject().name+" 想加入「" + socialAction.targetname+"」社群";
                    notification.save();
                }
				break;

                case FRIEND_REQUESTED: {
                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/image/get-mini-image-by-id/" + socialAction.actor);
                    jsonMap.put("onClick", MY_PREFIX +"/profile/"+socialAction.actor);
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.recipient = socialAction.target;
                    notification.notificationType = NotificationType.FRD_REQUEST;
                    notification.message = socialAction.getActorObject().name+" 想成為你的朋友";
                    notification.save();
                }
				break;

                case INVITE_REQUESTED: {
                    Long commId = socialAction.target;

                    Notification notification = new Notification();
                    notification.socialActionID = socialAction.id;
                    jsonMap.put("photo", "/get-mini-cover-community-image-by-id/"+commId);
                    //jsonMap.put("onClick", resolveCommunityLandingUrl(commId));
                    notification.URLs = Json.stringify(Json.toJson(jsonMap));
                    notification.recipient = socialAction.actor;
                    notification.notificationType = NotificationType.COMM_INVITE_REQUEST;
                    notification.message = "有人推薦「"+socialAction.targetname+"」社群給您";
                    notification.save();
                }
				break;
			}
		}
	}

    /**
     * Handle PrimarySocialRelation
     * @param socialAction
     */
	public static void handle(PrimarySocialRelation socialAction) {
		if(socialAction.action != null) {
			Map<String, Object> jsonMap = new HashMap<>();
			jsonMap.put("actor", socialAction.actor);
			jsonMap.put("target", socialAction.target);
			
			switch (socialAction.action) {
                /*case POSTED: {
                    Post post = Post.findById(socialAction.target);
                    Community community = post.community;
                    if (isBusinessCommunity(community)) {
                        break;
                    }

                    // fan out to friends of same social community only
                    List<Long> frdIds = FriendCache.getFriendsIds(socialAction.actor);

                    if (frdIds.size() > 0) {
                        String commLandingUrl = resolveCommunityLandingUrl(community.id, community.communityType, community.targetingType);
                        String postLandingUrl = resolvePostLandingUrl(post.id, community.id, community.communityType);
                        String msgEnd = " 在「"+community.name+"」發佈了分享";

                        List<User> frdMembers = community.getMembersIn(frdIds);
                        for(User user : frdMembers) {
                            Notification notification =
                                    Notification.getNotification(user.id, NotificationType.POSTED, community.id, SocialObjectType.COMMUNITY);

                            if(notification == null){
                                notification = new Notification();
                                String msgSubjects = notification.addToList(User.findById(socialAction.actor));  // post owner

                                notification.notificationType = NotificationType.POSTED;
                                notification.target = community.id;
                                notification.targetType = SocialObjectType.COMMUNITY;
                                notification.recipient = user.id;
                                notification.socialActionID = community.id;
                                jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                                jsonMap.put("onClick", postLandingUrl);
                                notification.URLs = Json.stringify(Json.toJson(jsonMap));
                                notification.message = msgSubjects + msgEnd;
                                notification.setUpdatedDate(new Date());
                                notification.save();
                            } else {
                                String msgSubjects = notification.addToList(User.findById(socialAction.actor));

                                jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                                jsonMap.put("onClick", commLandingUrl);
                                notification.URLs = Json.stringify(Json.toJson(jsonMap));
                                notification.status = 0;
                                notification.message = msgSubjects + msgEnd;
                                notification.merge();
                            }
                        }
                    }
                }
				break;

                case POSTED_QUESTION: {
                    Post post = Post.findById(socialAction.target);
                    User actor = User.findById(socialAction.actor);
                    Community community = post.community;
                    if (isBusinessCommunity(community)) {
                        break;
                    }

                    // send to All, or fan out to friends of same social community only
                    boolean sendToAll = isSendToAllMembers(community);
                    List<Long> frdIds = FriendCache.getFriendsIds(socialAction.actor);

                    if (sendToAll || frdIds.size() > 0) {
                        String commLandingUrl = resolveCommunityLandingUrl(community.id, community.communityType, community.targetingType);
                        String qnaLandingUrl = resolveQnALandingUrl(post.id, community.id, community.communityType);
                        String msgEnd = " 在「"+community.name+"」發佈了新話題";

                        List<User> members = sendToAll ? community.getMembers() : community.getMembersIn(frdIds);
                        for(User user : members){
                            if (user.getId() == actor.getId()) {
                                continue;   // skip the actor himself
                            }

                            Notification notification =
                                    Notification.getNotification(user.id, NotificationType.QUESTIONED, community.id, SocialObjectType.COMMUNITY);

                            if(notification == null){
                                String shortTitle = post.getShortenedTitle();
                                String msg = socialAction.actorname + msgEnd + ((shortTitle.length() == 0) ? "" : "\""+shortTitle+"\"");

                                notification = new Notification();
                                notification.addToList(actor);
                                notification.target = community.id;
                                notification.targetType = SocialObjectType.COMMUNITY;
                                notification.notificationType = NotificationType.QUESTIONED;
                                notification.recipient = user.id;
                                jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                                jsonMap.put("onClick", qnaLandingUrl);
                                notification.URLs = Json.stringify(Json.toJson(jsonMap));
                                notification.socialActionID = community.id;
                                notification.message = msg;
                                notification.setUpdatedDate(new Date());
                                notification.save();
                            } else {
                                String msgSubjects = notification.addToList(actor);

                                jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                                jsonMap.put("onClick", commLandingUrl);
                                notification.URLs = Json.stringify(Json.toJson(jsonMap));
                                notification.status = 0;
                                notification.message = msgSubjects + msgEnd;
                                notification.merge();
                            }
                        }
                    }
                }
				break;

                case WANT_ANS: {
                    if(socialAction.targetType == SocialObjectType.QUESTION){
                        Post post = Post.findById(socialAction.target);
                        long owner_id = post.owner.id;
                        if(User.findById(socialAction.actor).id == owner_id){
                            return;
                        }

                        String landingUrl = resolveQnALandingUrl(post.id, post.community.id, post.community.communityType);

                        String shortTitle = post.getShortenedTitle();
                        String msgEnd = " 把你的話題推上 - "+((shortTitle.length() == 0) ? "" : "\""+shortTitle+"\"");

                        Notification notification =
                                Notification.getNotification(owner_id, NotificationType.WANTED_ANS, socialAction.target, SocialObjectType.QUESTION);
                        if(notification == null){
                            String msg = socialAction.actorname + msgEnd;

                            notification = new Notification();
                            notification.target = socialAction.target;              // post id
                            notification.targetType = SocialObjectType.QUESTION;
                            notification.notificationType = NotificationType.WANTED_ANS;
                            notification.recipient = owner_id;
                            notification.socialActionID = socialAction.target;
                            notification.addToList(User.findById(socialAction.actor));
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.message = msg;
                            notification.status = 0;
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            String msgSubjects = notification.addToList(User.findById(socialAction.actor));

                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.message = msgSubjects + msgEnd;
                            notification.status = 0;
                            notification.merge();
                        }
                    }
                }
                break;

                case LIKED: {
                    // Liked QUESTION or POST
                    if (socialAction.targetType == SocialObjectType.QUESTION ||
                        socialAction.targetType == SocialObjectType.POST) {

                        Post post = Post.findById(socialAction.target);
                        long owner_id = post.owner.id;
                        if(User.findById(socialAction.actor).id == owner_id){
                            return;
                        }

                        String landingUrl = (socialAction.targetType == SocialObjectType.QUESTION) ?
                                resolveQnALandingUrl(post.id, post.community.id, post.community.communityType) :
                                resolvePostLandingUrl(post.id, post.community.id, post.community.communityType);
                                
                        String shortBody =StringUtil.truncateWithDots(post.title, 12);
                        String msgEnd = " 讚好您的話題"+((shortBody.length() >= 0) ? " - \""+shortBody+"\"" : "");

                        Notification notification =
                                Notification.getNotification(owner_id, NotificationType.LIKED, socialAction.target, SocialObjectType.POST);
                        if(notification == null){
                            String msg = socialAction.actorname + msgEnd;

                            notification = new Notification();
                            notification.target = socialAction.target;          // post id
                            notification.targetType = SocialObjectType.POST;
                            notification.notificationType = NotificationType.LIKED;
                            notification.recipient = owner_id;
                            notification.socialActionID = socialAction.target;
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.addToList(User.findById(socialAction.actor));
                            notification.message = msg;
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            String msgSubjects = notification.addToList(User.findById(socialAction.actor));

                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.message = msgSubjects + msgEnd;
                            notification.status = 0;
                            notification.merge();
                        }
                    }
                    // Liked COMMENT
                    else if(socialAction.targetType == SocialObjectType.COMMENT) {
                        Comment comment = Comment.findById(socialAction.target);
                        long owner_id = comment.owner.id;
                        if(User.findById(socialAction.actor).id == owner_id){
                            return;
                        }

                        Post post = comment.getPost();
                        String landingUrl = resolvePostLandingUrl(post.id, post.community.id, post.community.communityType);
                        
                        String shortBody = comment.getShortenedBody();
                        String msgEnd = " 讚好您的留言"+((shortBody.length() >= 0) ? " - \""+shortBody+"\"" : "");

                        Notification notification =
                                Notification.getNotification(owner_id, NotificationType.LIKED, socialAction.target, SocialObjectType.COMMENT);
                        if(notification == null) {
                            String msg = socialAction.actorname + msgEnd;

                            notification = new Notification();
                            notification.target = socialAction.target;
                            notification.targetType = SocialObjectType.COMMENT;
                            notification.notificationType = NotificationType.LIKED;
                            notification.recipient = owner_id;
                            notification.socialActionID = comment.getPost().id;
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.addToList(User.findById(socialAction.actor));
                            notification.message = msg;
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            String msgSubjects = notification.addToList(User.findById(socialAction.actor));

                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.message = msgSubjects + msgEnd;
                            notification.status = 0;
                            notification.merge();
                        }

                    }
                    // Liked ANSWER
                    else if(socialAction.targetType == SocialObjectType.ANSWER){
                        Comment comment = Comment.findById(socialAction.target);
                        long owner_id = comment.owner.id;
                        if(User.findById(socialAction.actor).id == owner_id){
                            return;
                        }

                        String landingUrl = MY_PREFIX +"/qna-landing/id/"+comment.getPost().id+"/communityId/"+comment.getPost().community.id;

                        String shortBody = comment.getShortenedBody();
                        String msgEnd = " 讚好您的回覆"+((shortBody.length() >= 0) ? " - \""+shortBody+"\"" : "");

                        Notification notification =
                                Notification.getNotification(owner_id, NotificationType.LIKED, socialAction.target, SocialObjectType.ANSWER);
                        if(notification == null){
                            String msg = socialAction.actorname + msgEnd;

                            notification = new Notification();
                            notification.target = socialAction.target;
                            notification.targetType = SocialObjectType.ANSWER;
                            notification.notificationType = NotificationType.LIKED;
                            notification.recipient = owner_id;
                            notification.socialActionID = comment.getPost().id;
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.addToList(User.findById(socialAction.actor));
                            notification.message = msg;
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            String msgSubjects = notification.addToList(User.findById(socialAction.actor));

                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.message = msgSubjects + msgEnd;
                            notification.status = 0;
                            notification.merge();
                        }
                    }
                }
				break;

                case COMMENTED: {
                    Comment comment= Comment.findById(socialAction.target);
                    Post post = comment.getPost();
                    long owner_id = post.owner.id;
                    if(User.findById(socialAction.actor).id == post.owner.id){
                        return;
                    }

                    String landingUrl = resolvePostLandingUrl(post.id, post.community.id, post.community.communityType);

                    String shortBody = comment.getShortenedBody();
                    String msgEnd = " 在您的分享留言";

                    Notification notification =
                            Notification.getNotification(owner_id, NotificationType.COMMENT, post.id, SocialObjectType.POST);
                    if(notification == null){
                        String msg = socialAction.actorname + msgEnd + ((shortBody.length() >= 0) ? " - \""+shortBody+"\"" : "");

                        notification = new Notification();
                        notification.target = post.id;
                        notification.targetType = SocialObjectType.POST;
                        notification.notificationType = NotificationType.COMMENT;
                        notification.recipient = owner_id;
                        notification.socialActionID = post.id;
                        jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                        jsonMap.put("onClick", landingUrl);
                        notification.URLs = Json.stringify(Json.toJson(jsonMap));
                        notification.addToList(User.findById(socialAction.actor));
                        notification.message = msg;
                        notification.setUpdatedDate(new Date());
                        notification.save();
                    } else {
                        String msgSubjects = notification.addToList(User.findById(socialAction.actor));

                        jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                        jsonMap.put("onClick", landingUrl);
                        notification.URLs = Json.stringify(Json.toJson(jsonMap));
                        notification.status = 0;
                        notification.message = msgSubjects + msgEnd;
                        notification.merge();
                    }
                }
				break;

                case ANSWERED: {
                    Comment comment= Comment.findById(socialAction.target);
                    Post post = comment.getPost();
                    Community community = post.getCommunity();

                    String landingUrl = resolveQnALandingUrl(post.id, post.community.id, post.community.communityType);

                    String shortTitle = post.getShortenedTitle();
                    String shortBody = comment.getShortenedBody();
                    String genMsgEnd = " 回應了話題: \""+shortTitle+"\"";
                    String yrMsgEnd = " 回應了您的話題: \""+shortTitle+"\"";

                    // add owner and comment users
                    Set<Long> recipientIds = post.getCommentUserIdsOfPost();
                    recipientIds.add(post.owner.id);
                    // fan-out if applicable
                    if (isSendToAllMembers(community)) {
                        recipientIds.addAll(community.getMemberIds());
                    }

                    for (Long recipientId : recipientIds) {
                        if (recipientId.equals(socialAction.actor)) {
                            continue;   // skip the actor himself
                        }

                        String msgEnd = recipientId.equals(post.owner.id) ? yrMsgEnd : genMsgEnd;

                        Notification notification =
                                Notification.getNotification(recipientId, NotificationType.ANSWERED, post.id, SocialObjectType.POST);
                        if(notification == null){
                            String msg = socialAction.actorname + msgEnd; // + ((shortBody.length() >= 0) ? " .. \""+shortBody+"\"" : "");

                            notification = new Notification();
                            notification.target = post.id;
                            notification.targetType = SocialObjectType.POST;
                            notification.notificationType = NotificationType.ANSWERED;
                            notification.recipient = recipientId;
                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.socialActionID = post.id;
                            notification.addToList(User.findById(socialAction.actor));
                            notification.message = msg;
                            notification.setUpdatedDate(new Date());
                            notification.save();
                        } else {
                            String msgSubjects = notification.addToList(User.findById(socialAction.actor));

                            jsonMap.put("photo", "/image/get-thumbnail-image-by-id/"+socialAction.actor);
                            jsonMap.put("onClick", landingUrl);
                            notification.URLs = Json.stringify(Json.toJson(jsonMap));
                            notification.message = msgSubjects + msgEnd;
                            notification.status = 0;
                            notification.merge();
                        }
                    }
                }
				break;	*/
			}
		} 
	}
}
