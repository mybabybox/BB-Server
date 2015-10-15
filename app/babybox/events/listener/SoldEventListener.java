package babybox.events.listener;

import models.Activity;
import models.Post;
import models.User;
import models.Activity.ActivityType;
import babybox.events.map.SoldEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class SoldEventListener {
	
	@Subscribe
    public void recordSoldEventInDB(SoldEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		if (post.onSold(user)) {
			CalcServer.removeFromCategoryQueues(post.id, post.category.id);
			
			Activity activity = new Activity();
	        activity.recipient = post.owner.id;
	        activity.actor = user.id;
	        activity.actvityType = ActivityType.SOLD;
	        activity.save();
		}
		
    }
}
