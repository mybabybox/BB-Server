package babybox.events.listener;

import models.Activity;
import models.Post;
import models.User;
import models.Activity.ActivityType;
import babybox.events.map.ViewEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class ViewEventListener {
	
	@Subscribe
    public void recordViewEventInDB(ViewEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		if (post.onView(user)) {
			CalcServer.calculateBaseScore(post);
			
			Activity activity = new Activity();
	        activity.recipient = post.owner.id;
	        activity.actor = user.id;
	        activity.actvityType = ActivityType.VIEWED;
	        activity.save();
		}
    }
}
