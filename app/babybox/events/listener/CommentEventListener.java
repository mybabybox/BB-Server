package babybox.events.listener;

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

public class CommentEventListener {

	@Subscribe
	public void recordCommentEventInDB(CommentEvent map){
		Comment comment = (Comment) map.get("comment");
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		CalcServer.addToPopularPostQueue(post);
		
		if (user.id != post.owner.id) {
    		Activity activity = new Activity(
    				ActivityType.NEW_COMMENT, 
    				post.owner.id,
    				user.id, 
    				user.name,
    				comment.id,
    				StringUtil.shortMessage(comment.body));
            activity.ensureUniqueAndCreate();
		}
	}
	
	@Subscribe
	public void recordDeleteCommentEventInDB(DeleteCommentEvent map) {
		Comment comment = (Comment) map.get("comment");
		Post post = (Post) map.get("post");
		CalcServer.addToPopularPostQueue(post);
	}
}
