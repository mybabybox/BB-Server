package service;

import models.Category;
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
import babybox.events.map.SoldEvent;
import babybox.events.map.UnFollowEvent;
import babybox.events.map.UnlikeEvent;
import babybox.events.map.ViewEvent;

public class SocialRelationHandler {
	
	public static void recordLikePost(Post post, User localUser){
		LikeEvent likeEvent = new LikeEvent();
		likeEvent.put("user", localUser);
		likeEvent.put("post", post);
		EventHandler.getInstance().getEventBus().post(likeEvent);
	}
	
	public static void recordUnLikePost(Post post, User localUser){
		UnlikeEvent likeEvent = new UnlikeEvent();
		likeEvent.put("user", localUser);
		likeEvent.put("post", post);
		EventHandler.getInstance().getEventBus().post(likeEvent);
	}

	public static void recordCreatePost(Post post, User localUser){
		PostEvent postEvent = new PostEvent();
		postEvent.put("user", localUser);
		postEvent.put("post", post);
		EventHandler.getInstance().getEventBus().post(postEvent);
	}
	
	public static void recordEditPost(Post post, Category category){
		DeletePostEvent postEvent = new DeletePostEvent();
		postEvent.put("post", post);
		postEvent.put("category", category);
		EventHandler.getInstance().getEventBus().post(postEvent);
	}
	
	public static void recordDeletePost(Post post, User localUser){
        DeletePostEvent postEvent = new DeletePostEvent();
        postEvent.put("user", localUser);
        postEvent.put("post", post);
        EventHandler.getInstance().getEventBus().post(postEvent);
    }
	
	public static void recordFollowUser(User localUser, User user){
		FollowEvent followEvent = new FollowEvent();
		followEvent.put("localUser", localUser);
		followEvent.put("user", user);
		EventHandler.getInstance().getEventBus().post(followEvent);
	}
	
	public static void recordUnFollowUser(User localUser, User user){
		UnFollowEvent followEvent = new UnFollowEvent();
		followEvent.put("localUser", localUser);
		followEvent.put("user", user);
		EventHandler.getInstance().getEventBus().post(followEvent);
	}
	
	public static void recordCreateComment(Comment comment, Post post, User localUser){
		CommentEvent postEvent = new CommentEvent();
		postEvent.put("comment", comment);
		postEvent.put("post", post);
		postEvent.put("user", localUser);
		EventHandler.getInstance().getEventBus().post(postEvent);
	}
	
	public static void recordDeleteComment(Comment comment, Post post) {
		DeleteCommentEvent postEvent = new DeleteCommentEvent();
		postEvent.put("comment", comment);
		postEvent.put("post", comment.getPost());
		EventHandler.getInstance().getEventBus().post(postEvent);
	}
	
	public static void recordSoldPost(Post post, User localUser){
		SoldEvent soldEvent = new SoldEvent();
		soldEvent.put("post", post);
		soldEvent.put("user", localUser);
		EventHandler.getInstance().getEventBus().post(soldEvent);
	}
	
	public static void recordViewPost(Post post, User localUser){
		ViewEvent viewEvent = new ViewEvent();
		viewEvent.put("post", post);
		viewEvent.put("user", localUser);
		EventHandler.getInstance().getEventBus().post(viewEvent);
	}
}
