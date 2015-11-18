package babybox.events.listener;

import models.Activity;
import models.Category;
import models.Post;
import models.User;
import models.Activity.ActivityType;
import babybox.events.map.DeletePostEvent;
import babybox.events.map.EditPostEvent;
import babybox.events.map.PostEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;
import common.utils.StringUtil;

public class PostEventListener {
	
	CalcServer calcServer = play.Play.application().injector().instanceOf(CalcServer.class);
	
	@Subscribe
    public void recordPostEventOnCalcServer(PostEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		calcServer.addToCategoryQueues(post);
		calcServer.addToUserPostedQueue(post, user);
		
		/*
		// Need to query followers as recipients
		Activity activity = new Activity(
				ActivityType.NEW_POST, 
				user.id,
				true, 
				user.id,
				user.id,
				user.displayName,
				post.id,
				post.getImage(), 
				StringUtil.shortMessage(post.title));
        activity.ensureUniqueAndCreate();
        */
    }
	
	@Subscribe
    public void recordEditPostEventOnCalcServer(EditPostEvent map){
        Post post = (Post) map.get("post");
        Category category = (Category) map.get("category");
        calcServer.removeFromCategoryQueues(post, category);
        calcServer.addToCategoryQueues(post);
    }
	
	@Subscribe
    public void recordDeletePostEventOnCalcServer(DeletePostEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		calcServer.removeFromCategoryQueues(post);
		calcServer.removeFromUserPostedQueue(post, post.owner);
		calcServer.removeFromAllUsersLikedQueues(post);
    }
}
