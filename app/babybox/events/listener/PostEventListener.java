package babybox.events.listener;

import models.Post;
import models.User;
import babybox.events.map.DeletePostEvent;
import babybox.events.map.PostEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class PostEventListener {
	
	@Subscribe
    public void recordPostEventOnCalcServer(PostEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		CalcServer.addToQueues(post);
		Long score = post.getCreatedDate().getTime();
		CalcServer.addToPostQueue(post.id, user.id, score.doubleValue());
    }
	
	@Subscribe
    public void recordDeletePostEventOnCalcServer(DeletePostEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		CalcServer.removeFromCategoryQueues(post.id, post.category.id);
		CalcServer.removeFromPostQueue(post.id, post.owner.id);
    }
}
