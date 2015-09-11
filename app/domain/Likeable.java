package domain;

import models.User;
import mybox.shopping.social.exception.SocialObjectNotLikableException;

public interface Likeable {
	public abstract void onLikedBy(User user) throws SocialObjectNotLikableException;
	public abstract void onUnlikedBy(User user) throws SocialObjectNotLikableException;
}
