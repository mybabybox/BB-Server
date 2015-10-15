package babybox.events.listener;

import models.Post;
import models.User;
import babybox.events.map.SoldEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class SoldEventListener {
	
	@Subscribe
    public void recordSoldEventInDB(SoldEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		if (post.onSold(user)) {
			CalcServer.removeFromCategoryFeeds(post.id);
		}
    }
}
