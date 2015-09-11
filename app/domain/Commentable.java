package domain;

import models.SocialObject;
import models.User;
import mybox.shopping.social.exception.SocialObjectNotCommentableException;

public interface Commentable {
	public abstract SocialObject onComment(User user, String body) throws SocialObjectNotCommentableException;
	public abstract void onDeleteComment(User user, String body) throws SocialObjectNotCommentableException;
}
