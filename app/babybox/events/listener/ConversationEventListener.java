package babybox.events.listener;

import models.Conversation;
import models.User;
import babybox.events.map.ConversationEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class ConversationEventListener {
	
	@Subscribe
    public void recordConversationEventInDB(ConversationEvent map){
	    Conversation conversation = (Conversation) map.get("conversation");
	    User user = (User) map.get("user");
       	CalcServer.recalcScoreAndAddToCategoryPopularQueue(conversation.post);
    }
}	
