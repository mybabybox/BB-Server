package babybox.events.listener;

import models.Activity;
import models.Post;
import models.User;
import models.Activity.ActivityType;
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
		
		// who is recipient here
		Activity activity = new Activity();
        activity.actor = user.id;
        activity.actvityType = ActivityType.FOLLOWED;
        activity.save();
    }
	
	@Subscribe
    public void recordDeletePostEventOnCalcServer(DeletePostEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		CalcServer.removeFromCategoryQueues(post.id, post.category.id);
		CalcServer.removeFromPostQueue(post.id, post.owner.id);
    }
}
