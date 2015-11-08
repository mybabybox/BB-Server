package babybox.events.listener;

import models.Post;
import models.User;
import babybox.events.map.TouchEvent;
import babybox.events.map.ViewEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class ViewEventListener {
    private static final play.api.Logger logger = play.api.Logger.apply(ViewEventListener.class);
    
	@Subscribe
    public void recordViewEventInDB(ViewEvent map){
	    try {
    		Post post = (Post) map.get("post");
    		User user = (User) map.get("user");
    		if (post.onView(user)) {
    		    //CalcServer.recalcScoreAndAddToCategoryPopularQueue(post);
    		}
    	} catch(Exception e) {
            logger.underlyingLogger().error(e.getMessage(), e);
        }
    }
	
	@Subscribe
    public void recordTouchEventOnCalcServer(TouchEvent map){
	    try {
            Post post = (Post) map.get("post");
            User user = (User) map.get("user");
            CalcServer.recalcScoreAndAddToCategoryPopularQueue(post);
    	} catch(Exception e) {
            logger.underlyingLogger().error(e.getMessage(), e);
        }
    }
}
