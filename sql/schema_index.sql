-- User
CREATE INDEX user_idx_created_system ON user (created_date, system);


-- Notification
CREATE INDEX notification_idx_recipient_created ON Notification (recipient, CREATED_DATE);


-- SocialRelation (Friends, Community Members)
CREATE INDEX socialrel_idx_actor_action ON socialrelation (actor, action);
CREATE INDEX socialrel_idx_target_action ON socialrelation (target, action);
CREATE INDEX socialrel_idx_actor_target_action ON socialrelation (actor, target, action);
CREATE INDEX socialrel_idx_action_actType ON socialrelation (action(32), actionType(32));


-- SecondarySocialRelation (Bookmarks)
CREATE INDEX secsocialrel_idx_actor_action ON secondarysocialrelation (actor, action);
CREATE INDEX secsocialrel_idx_target_action ON secondarysocialrelation (target, action);
CREATE INDEX secsocialrel_idx_actor_target_action ON secondarysocialrelation (actor, target, action);


-- PrimarySocialRelation (Likes, Posts)
CREATE INDEX primsocialrel_idx_actor_action ON primarysocialrelation (actor, action);
CREATE INDEX primsocialrel_idx_target_action ON primarysocialrelation (target, action);
CREATE INDEX primsocialrel_idx_actor_target_action ON primarysocialrelation (actor, target, action);


-- Community
CREATE INDEX community_idx_tgtType_system ON community (targetingType, system);


-- CommunityStatistics
CREATE INDEX communitystats_idx_actdate ON communitystatistics (activityDate);


-- Post
CREATE INDEX post_idx_comm_ptyp_upddate ON post (community_id, postType, UPDATED_DATE);


-- Comment
CREATE INDEX comment_idx_sobj_date ON comment (socialObject, date);


-- User Community Affinity
CREATE INDEX usercommunityaffinity_idx_usr_comm ON usercommunityaffinity (userId, communityId);


-- GameAccount
CREATE INDEX gameaccount_idx_userid ON GameAccount (User_id);


-- GameAccountTransaction
CREATE INDEX gameaccounttransaction_idx_userid ON GameAccountTransaction (userId);


-- GameAccountStatistics
CREATE INDEX gameaccountstatistics_idx_userid ON GameAccountStatistics (User_id);


-- Conversation
CREATE INDEX conversation_idx_user1_updateddate ON Conversation (user1_id, updated_date);
CREATE INDEX conversation_idx_user2_updateddate ON Conversation (user2_id, updated_date);