package babybox.events.handler;
import babybox.events.listener.FollowEventListners;
import babybox.events.listener.LikeEventListners;

import com.google.common.eventbus.EventBus;


public class EventHandler {

	Class [] listners = {LikeEventListners.class, FollowEventListners.class};
	private static EventHandler eventHandler = new EventHandler();
	private EventBus eventBus = new EventBus();
	private EventHandler() {
		registerSubscribers();
	}
	
	public static EventHandler getInstance () {
		return eventHandler;
	}

	public EventBus getEventBus(){
		return eventBus;
	}

	void registerSubscribers() {
		for(Class listner : listners) {
			try {
				eventBus.register(listner.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void unRegisterSubscribers() {
		for(Class listner : listners) {
			try {
				eventBus.unregister(listner.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
