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

public class CommentEventListener {

	@Subscribe
	public void recordCommentEventInDB(CommentEvent map){
		Comment comment = (Comment) map.get("comment");
		Post post = (Post) map.get("post");
		User user = (User) map.get("localUser");
		CalcServer.calculateBaseScore(post);
		
		
		Activity activity = new Activity();
        activity.recipient = post.owner.id;
        activity.actor = user.id;
        activity.actvityType = ActivityType.COMMENT;
        activity.save();
	}
	
	@Subscribe
	public void recordDeleteCommentEventInDB(DeleteCommentEvent map) {
		Comment comment = (Comment) map.get("comment");
		Post post = (Post) map.get("post");
		CalcServer.calculateBaseScore(post);
	}
}
