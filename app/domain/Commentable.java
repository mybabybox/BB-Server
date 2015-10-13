package domain;

import babybox.shopping.social.exception.SocialObjectNotCommentableException;
import models.Comment;
import models.SocialObject;
import models.User;

public interface Commentable {
	public abstract SocialObject onComment(User user, String body) throws SocialObjectNotCommentableException;
	public abstract void onDeleteComment(User user, Comment comment) throws SocialObjectNotCommentableException;
}
