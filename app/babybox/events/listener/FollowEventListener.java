package babybox.events.listener;

import java.util.Date;

import models.Activity;
import models.User;
import models.Activity.ActivityType;
import babybox.events.map.FollowEvent;
import babybox.events.map.UnFollowEvent;

import com.google.common.eventbus.Subscribe;
import common.cache.CalcServer;

public class FollowEventListener {
	
	@Subscribe
    public void recordFollowEventInDB(FollowEvent map){
		User localUser = (User) map.get("localUser");
		User user = (User) map.get("user");
		
       	// why we require this, if we are renewing HOME_FOLLOWING feed after every 2 mins 
		if (localUser.onFollow(user)) {
			Long score = new Date().getTime();		// ideally use FollowSocialRelation.CREATED_DATE
			CalcServer.addToFollowQueue(user.id, localUser.id, score.doubleValue());
			
			Activity activity = new Activity();
			activity.recipient = localUser.id;
			activity.actor = user.id;
			activity.actvityType = ActivityType.FOLLOWED;
			activity.save();
		}
    }
	
	@Subscribe
    public void recordUnFollowEventInDB(UnFollowEvent map){
		User localUser = (User) map.get("localUser");
		User user = (User) map.get("user");
		if (localUser.onUnFollow(user)) {
			// do something
			CalcServer.removeFromFollowQueue(user.id, localUser.id);
		}
    }
}
