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
       	if (post.onLikedBy(user)) {
	       	Long score = post.getCreatedDate().getTime();
	       	CalcServer.calculateBaseScore(post);
	       	CalcServer.addToLikeQueue(post.id, user.id, score.doubleValue());
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
