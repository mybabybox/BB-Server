package babybox.events.listener;

import models.Post;
import models.User;
import babybox.events.map.LikeMap;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class LikeEventListners {
	
	@Subscribe
    public void recordEventInDB(LikeMap map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
       	post.onLikedBy(user);
       	Long score = post.getCreatedDate().getTime();
		CalcServer.addToLikeQueue(post.id, user.id, score.doubleValue());
		CalcServer.buildBaseScore();
    }

}
