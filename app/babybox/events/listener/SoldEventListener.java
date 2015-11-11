package babybox.events.listener;

import models.Activity;
import models.Post;
import models.User;
import models.Activity.ActivityType;
import babybox.events.map.SoldEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;
import common.utils.StringUtil;

public class SoldEventListener {
    private static final play.api.Logger logger = play.api.Logger.apply(SoldEventListener.class);
    
	@Subscribe
    public void recordSoldEventInDB(SoldEvent map){
	    try {
    		Post post = (Post) map.get("post");
    		User user = (User) map.get("user");
    		if (post.onSold(user)) {
    		    // NOTE: sold posts purged by daily scheduler at 5am HKT !!
    			//CalcServer.removeFromCategoryQueues(post);
    			
    			/*
    			// Need to query chat users as recipients
    			Activity activity = new Activity(
    					ActivityType.SOLD, 
    					user.id,
    					true, 
    					user.id,
    					user.id,
    					user.displayName,
    					post.id,
    					post.getImage(), 
    					StringUtil.shortMessage(post.title));
    	        activity.save();
    	        */
    		}
	    } catch(Exception e) {
            logger.underlyingLogger().error(e.getMessage(), e);
        }
    }
}
