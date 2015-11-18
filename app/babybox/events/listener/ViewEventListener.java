package babybox.events.listener;

import models.Post;
import models.User;
import babybox.events.map.ViewEvent;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import common.cache.CalcServer;

public class ViewEventListener {
	
	CalcServer calcServer = play.Play.application().injector().instanceOf(CalcServer.class);
	
	@Subscribe
    public void recordViewEventInDB(ViewEvent map){
		Post post = (Post) map.get("post");
		User user = (User) map.get("user");
		if (post.onView(user)) {
		    //CalcServer.recalcScoreAndAddToCategoryPopularQueue(post);
		}
    }
}
