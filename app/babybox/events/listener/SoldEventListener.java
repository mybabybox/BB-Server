package babybox.events.listener;

import models.Post;
import models.User;
import babybox.events.map.SoldEvent;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import common.cache.CalcServer;

public class SoldEventListener {
	
	CalcServer calcServer = play.Play.application().injector().instanceOf(CalcServer.class);
	
	@Subscribe
    public void recordSoldEventInDB(SoldEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		if (post.onSold(user)) {
		    // NOTE: sold posts purged by daily scheduler at 5am HKT !!
			//CalcServer.removeFromCategoryQueues(post);
			
			/*
			// Need to query chat users as recipients
			Activity activity = new Activity(
					ActivityType.SOLD, 
					user.id,
					true, 
					user.id,
					user.id,
					user.displayName,
					post.id,
					post.getImage(), 
					StringUtil.shortMessage(post.title));
	        activity.save();
	        */
		}
    }
}
