package babybox.events.listener;

import java.util.HashSet;
import java.util.Set;

import models.Activity;
import models.Comment;
import models.Post;
import models.Activity.ActivityType;
import models.User;
import babybox.events.map.CommentEvent;
import babybox.events.map.DeleteCommentEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;
import common.utils.StringUtil;
import domain.DefaultValues;

public class CommentEventListener {

	@Subscribe
	public void recordCommentEventInDB(CommentEvent map){
		Comment comment = (Comment) map.get("comment");
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		CalcServer.recalcScoreAndAddToCategoryPopularQueue(post);
		
		// first of all, send to post owner
        if (user.id != post.owner.id) {
            Activity activity = new Activity(
                    ActivityType.NEW_COMMENT, 
                    post.owner.id,
                    true,
                    user.id, 
                    user.id,
                    user.name,
                    post.id,
                    post.getImage(), 
                    StringUtil.shortMessage(comment.body));
            activity.ensureUniqueAndCreate();
        }
        
		// fan out to all commenters
		Set<Long> commenterIds = new HashSet<>();
		for (Comment c : post.comments) {
		    // 1. skip post owner here, sent already
		    // 2. skip comment owner
		    // 3. remove duplicates
		    if (c.owner.id == post.owner.id || 
		            c.owner.id == user.id || 
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
                    user.id, 
                    user.id, 
                    user.name,
                    post.id,
                    post.getImage(), 
                    StringUtil.shortMessage(comment.body));
            activity.ensureUniqueAndCreate();
            
            commenterIds.add(c.owner.id);
        }
	}
	
	@Subscribe
	public void recordDeleteCommentEventInDB(DeleteCommentEvent map) {
		Comment comment = (Comment) map.get("comment");
		Post post = (Post) map.get("post");
		CalcServer.recalcScoreAndAddToCategoryPopularQueue(post);
	}
}
