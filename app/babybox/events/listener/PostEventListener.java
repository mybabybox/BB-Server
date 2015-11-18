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
	
	@Subscribe
    public void recordPostEventOnCalcServer(PostEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		CalcServer.getInstanceForDI().addToCategoryQueues(post);
		CalcServer.getInstanceForDI().addToUserPostedQueue(post, user);
		
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
        CalcServer.getInstanceForDI().removeFromCategoryQueues(post, category);
        CalcServer.getInstanceForDI().addToCategoryQueues(post);
    }
	
	@Subscribe
    public void recordDeletePostEventOnCalcServer(DeletePostEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		CalcServer.getInstanceForDI().removeFromCategoryQueues(post);
		CalcServer.getInstanceForDI().removeFromUserPostedQueue(post, post.owner);
		CalcServer.getInstanceForDI().removeFromAllUsersLikedQueues(post);
    }
}
