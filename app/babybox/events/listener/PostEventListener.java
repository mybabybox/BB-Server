package babybox.events.listener;

import models.Activity;
import models.Post;
import models.User;
import models.Activity.ActivityType;
import babybox.events.map.DeletePostEvent;
import babybox.events.map.PostEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;
import common.utils.StringUtil;

public class PostEventListener {
	
	@Subscribe
    public void recordPostEventOnCalcServer(PostEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		CalcServer.addToCategoryQueues(post);
		CalcServer.addToPostQueue(post, user);
		
		/*
		// Need to query followers as recipients
		Activity activity = new Activity(
				ActivityType.NEW_POST, 
				user.id,
				user.id,
				user.displayName,
				post.id,
				StringUtil.shortMessage(post.title));
        activity.ensureUniqueAndCreate();
        */
    }
	
	@Subscribe
    public void recordDeletePostEventOnCalcServer(DeletePostEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		CalcServer.removeFromCategoryQueues(post);
		CalcServer.removeFromUserQueues(post, post.owner);
    }
}
