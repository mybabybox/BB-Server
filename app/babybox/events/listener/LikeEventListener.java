package babybox.events.listener;

import java.util.Date;

import models.Activity;
import models.Activity.ActivityType;
import models.Post;
import models.User;
import babybox.events.map.LikeEvent;
import babybox.events.map.UnlikeEvent;

import com.google.common.eventbus.Subscribe;
import common.cache.CalcServer;

public class LikeEventListener {
	
	@Subscribe
    public void recordLikeEventInDB(LikeEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
       	if (post.onLikedBy(user)) {
	       	Long score = new Date().getTime();		// ideally use LikeSocialRelation.CREATED_DATE
	       	CalcServer.calculateBaseScore(post);
	       	CalcServer.addToLikeQueue(post.id, user.id, score.doubleValue());
	       	
	    	Activity activity = new Activity();
	        activity.recipient = post.owner.id;
	        activity.actor = user.id;
	        activity.actvityType = ActivityType.LIKED;
	        activity.save();
       	}
    }
	
	@Subscribe
    public void recordUnlikeEventInDB(UnlikeEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
       	if (post.onUnlikedBy(user)) {
       		CalcServer.calculateBaseScore(post);
       		CalcServer.removeFromLikeQueue(post.id, user.id);
       	}
    }
}
