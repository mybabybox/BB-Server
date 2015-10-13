package babybox.events.listener;

import models.User;
import babybox.events.map.FollowEvent;
import babybox.events.map.UnFollowEvent;

import com.google.common.eventbus.Subscribe;

public class FollowEventListener {
	
	@Subscribe
    public void recordFollowEventInDB(FollowEvent map){
		User localUser = (User) map.get("localUser");
		User user = (User) map.get("user");
		localUser.onFollow(user);
    }
	
	@Subscribe
    public void recordUnFollowEventInDB(UnFollowEvent map){
		User localUser = (User) map.get("localUser");
		User user = (User) map.get("user");
		localUser.onUnFollow(user);
    }
}
