package babybox.events.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import play.libs.Json;
import mobile.GcmSender;
import models.Activity;
import models.Comment;
import models.Post;
import models.Activity.ActivityType;
import babybox.events.map.CommentEvent;
import babybox.events.map.DeleteCommentEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;
import common.utils.StringUtil;
import domain.DefaultValues;

public class CommentEventListener {
    private static final play.api.Logger logger = play.api.Logger.apply(CommentEventListener.class);
    
	@Subscribe
	public void recordCommentEventInDB(CommentEvent map){
	    try {
    		Comment comment = (Comment) map.get("comment");
    		Post post = (Post) map.get("post");
    		CalcServer.recalcScoreAndAddToCategoryPopularQueue(post);
    		
    		// first of all, send to post owner
            if (comment.owner.id != post.owner.id) {
                Activity activity = new Activity(
                        ActivityType.NEW_COMMENT, 
                        post.owner.id,
                        true,
                        comment.owner.id, 
                        comment.owner.id,
                        comment.owner.name,
                        post.id,
                        post.getImage(), 
                        StringUtil.shortMessage(comment.body));
                activity.ensureUniqueAndCreate();
                
                //GCM
                Map<String, String> data = new HashMap<>();
                data.put("message", comment.owner.name+" commented on your product: "+post.title);
                data.put("messageType", GcmSender.NotificationType.COMMENT.name());
                data.put("postId", post.id.toString());
        		String msg = Json.stringify(Json.toJson(data));
        		
                GcmSender.sendNotification(post.owner.id, msg);
            }
            
    		// fan out to all commenters
    		Set<Long> commenterIds = new HashSet<>();
    		for (Comment c : post.comments) {
    		    // 1. skip post owner here, sent already
    		    // 2. skip comment owner
    		    // 3. remove duplicates
    		    if (c.owner.id == post.owner.id || 
    		            c.owner.id == comment.owner.id || 
    		            commenterIds.contains(c.owner.id)) {
    		        continue;
    		    }
    		    
    		    // safety measure, fan out to max N commenters
    		    if (commenterIds.size() > DefaultValues.ACTIVITY_NEW_COMMENT_MAX_FAN_OUT) {
    		        break;
    		    }
    		    
                Activity activity = new Activity(
                        ActivityType.NEW_COMMENT, 
                        c.owner.id,
                        false, 
                        comment.owner.id, 
                        comment.owner.id, 
                        comment.owner.name,
                        post.id,
                        post.getImage(), 
                        StringUtil.shortMessage(comment.body));
                activity.ensureUniqueAndCreate();
                
                commenterIds.add(c.owner.id);
            }
	    } catch(Exception e) {
            logger.underlyingLogger().error(e.getMessage(), e);
        }
	}
	
	@Subscribe
	public void recordDeleteCommentEventInDB(DeleteCommentEvent map) {
	    try {
    		Comment comment = (Comment) map.get("comment");
    		Post post = (Post) map.get("post");
    		CalcServer.recalcScoreAndAddToCategoryPopularQueue(post);
	    } catch(Exception e) {
            logger.underlyingLogger().error(e.getMessage(), e);
        }
	}
}
