package service;

import domain.Followable;
import models.Comment;
import models.Post;
import models.User;
import babybox.events.handler.EventHandler;
import babybox.events.map.CommentEvent;
import babybox.events.map.DeleteCommentEvent;
import babybox.events.map.DeletePostEvent;
import babybox.events.map.FollowEvent;
import babybox.events.map.LikeEvent;
import babybox.events.map.PostEvent;
import babybox.events.map.UnFollowEvent;
import babybox.events.map.UnlikeEvent;
import babybox.events.map.ViewEvent;

public class SocialRelationHandler {
	
	public static void recordLikeOnPost(Post post, User localUser){
		LikeEvent likeEvent = new LikeEvent();
		likeEvent.put("user", localUser);
		likeEvent.put("post", post);
		EventHandler.getInstance().getEventBus().post(likeEvent);; 
	}
	
	public static void recordUnLikeOnPost(Post post, User localUser){
		UnlikeEvent likeEvent = new UnlikeEvent();
		likeEvent.put("user", localUser);
		likeEvent.put("post", post);
		EventHandler.getInstance().getEventBus().post(likeEvent);; 
	}
	
	public static void recordDeletePost(Post post, User localUser){
		DeletePostEvent postEvent = new DeletePostEvent();
		postEvent.put("user", localUser);
		postEvent.put("post", post);
		EventHandler.getInstance().getEventBus().post(postEvent);
	}
	
	public static void recordCreatePost(Post post, User localUser){
		PostEvent postEvent = new PostEvent();
		postEvent.put("user", localUser);
		postEvent.put("post", post);
		EventHandler.getInstance().getEventBus().post(postEvent);
	}
	
	public static void recordOnFollowUser(User localUser, User user){
		FollowEvent followEvent = new FollowEvent();
		followEvent.put("localUser", localUser);
		followEvent.put("user", user);
		EventHandler.getInstance().getEventBus().post(followEvent);
	}
	
	public static void recordOnUnFollowUser(User localUser, User user){
		UnFollowEvent followEvent = new UnFollowEvent();
		followEvent.put("localUser", localUser);
		followEvent.put("user", user);
		EventHandler.getInstance().getEventBus().post(followEvent);
	}
	
	public static void recordCommentOnPost(Comment comment, Post post){
		CommentEvent postEvent = new CommentEvent();
		postEvent.put("post", post);
		postEvent.put("comment", comment);
		EventHandler.getInstance().getEventBus().post(postEvent);
	}
	
	public static void recordViewOnPost(Post post, User localUser){
		ViewEvent viewEvent = new ViewEvent();
		viewEvent.put("post", post);
		viewEvent.put("user", localUser);
		EventHandler.getInstance().getEventBus().post(viewEvent);
	}

	public static void recordOnDeleteComment(Comment comment, User localUser) {
		DeleteCommentEvent postEvent = new DeleteCommentEvent();
		postEvent.put("user", localUser);
		postEvent.put("comment", comment);
		postEvent.put("post", comment.getPost());
		EventHandler.getInstance().getEventBus().post(postEvent);
		
	}

}
