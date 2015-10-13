package babybox.events.listener;

import models.Comment;
import models.Post;
import babybox.events.map.CommentEvent;
import babybox.events.map.DeleteCommentEvent;

import com.google.common.eventbus.Subscribe;

import common.cache.CalcServer;

public class CommentEventListener {

	@Subscribe
	public void recordCommentEventInDB(CommentEvent map){
		Comment comment = (Comment) map.get("comment");
		Post post = (Post) map.get("post");
		CalcServer.calculateBaseScore(post);
	}
	
	@Subscribe
	public void recordDeleteCommentEventInDB(DeleteCommentEvent map) {
		Comment comment = (Comment) map.get("comment");
		Post post = (Post) map.get("post");
		CalcServer.calculateBaseScore(post);
	}
}
