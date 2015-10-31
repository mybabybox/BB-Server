package babybox.events.listener;

import models.Conversation;
import models.Post;
import babybox.events.map.ConversationEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class ConversationEventListener {
	
	@Subscribe
    public void recordConversationEventInDB(ConversationEvent map){
	    Conversation conversation = (Conversation) map.get("conversation");
	    Post post = (Post) map.get("post");
       	CalcServer.recalcScoreAndAddToCategoryPopularQueue(post);
    }
}	
