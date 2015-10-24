package babybox.events.listener;

import mobile.GcmSender;
import models.Activity;
import models.Activity.ActivityType;
import models.Post;
import models.User;
import babybox.events.map.LikeEvent;
import babybox.events.map.UnlikeEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;
import common.utils.StringUtil;

public class LikeEventListener {
	
	@Subscribe
    public void recordLikeEventInDB(LikeEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
       	if (post.onLikedBy(user)) {
	       	CalcServer.addToCategoryPopularQueue(post);
	       	CalcServer.addToLikeQueue(post, user);
	       	
	       	if (user.id != post.owner.id) {
    	       	Activity activity = new Activity(
    					ActivityType.LIKED, 
    					post.owner.id,
    					user.id,
    					user.displayName,
    					post.id,
    					StringUtil.shortMessage(post.title));
    	        activity.ensureUniqueAndCreate();
    	        
    	        //GCM Notification sender
    	        System.out.println("LIKE");
    	        GcmSender.sendNotification(post.owner.id, user.name+" Liked on your post "+post.title);
	       	}
       	}
    }
	
	@Subscribe
    public void recordUnlikeEventInDB(UnlikeEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
       	if (post.onUnlikedBy(user)) {
       	    CalcServer.addToCategoryPopularQueue(post);
       		CalcServer.removeFromLikeQueue(post, user);
       	}
    }
}
