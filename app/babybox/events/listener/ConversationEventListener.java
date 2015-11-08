package babybox.events.listener;

import models.Conversation;
import models.Post;
import babybox.events.map.ConversationEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class ConversationEventListener {
    private static final play.api.Logger logger = play.api.Logger.apply(ConversationEventListener.class);
    
	@Subscribe
    public void recordConversationEventInDB(ConversationEvent map){
	    try {
    	    Conversation conversation = (Conversation) map.get("conversation");
    	    Post post = (Post) map.get("post");
           	CalcServer.recalcScoreAndAddToCategoryPopularQueue(post);
    	} catch(Exception e) {
            logger.underlyingLogger().error(e.getMessage(), e);
        }
    }
}	
