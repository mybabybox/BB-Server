package babybox.events.listener;

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
       	post.onLikedBy(user);
    }
	
	@Subscribe
    public void recordUnlikeEventInDB(UnlikeEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
       	post.onUnlikedBy(user);
    }

}
