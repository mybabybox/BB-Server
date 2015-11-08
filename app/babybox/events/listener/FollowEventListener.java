package babybox.events.listener;

import java.util.Date;

import models.Activity;
import models.User;
import models.Activity.ActivityType;
import babybox.events.map.FollowEvent;
import babybox.events.map.UnFollowEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class FollowEventListener {
    private static final play.api.Logger logger = play.api.Logger.apply(FollowEventListener.class);
    
	@Subscribe
    public void recordFollowEventInDB(FollowEvent map){
	    try {
    		User localUser = (User) map.get("localUser");
    		User user = (User) map.get("user");
    		
           	// why we require this, if we are renewing HOME_FOLLOWING feed after every 2 mins 
    		if (localUser.onFollow(user)) {
    			Long score = new Date().getTime();		// ideally use FollowSocialRelation.CREATED_DATE
    			CalcServer.addToFollowQueue(localUser.id, user.id, score.doubleValue());
    			
    			if (user.id != localUser.id) {
        			Activity activity = new Activity(
        					ActivityType.FOLLOWED, 
        					user.id,
        					false, 
        					localUser.id,
        					localUser.id,
        					localUser.displayName,
        					user.id,
        					user.id,
        					user.displayName);
        			activity.ensureUniqueAndCreate();
    			}
    		}
    	} catch(Exception e) {
            logger.underlyingLogger().error(e.getMessage(), e);
        }
    }
	
	@Subscribe
    public void recordUnFollowEventInDB(UnFollowEvent map){
	    try {
    		User localUser = (User) map.get("localUser");
    		User user = (User) map.get("user");
    		if (localUser.onUnFollow(user)) {
    			CalcServer.removeFromFollowQueue(localUser.id, user.id);
    		}
    	} catch(Exception e) {
            logger.underlyingLogger().error(e.getMessage(), e);
        }
    }
}
