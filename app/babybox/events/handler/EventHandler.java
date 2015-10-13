package babybox.events.handler;
import babybox.events.listener.FollowEventListener;
import babybox.events.listener.LikeEventListener;

import com.google.common.eventbus.EventBus;

public class EventHandler {

	Class [] listeners = { LikeEventListener.class, FollowEventListener.class };
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
		for (Class listener : listeners) {
			try {
				eventBus.register(listener.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void unRegisterSubscribers() {
		for (Class listener : listeners) {
			try {
				eventBus.unregister(listener.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
}
