package domain;

import babybox.shopping.social.exception.SocialObjectNotLikableException;
import models.User;

public interface Likeable {
	public abstract void onLikedBy(User user) throws SocialObjectNotLikableException;
	public abstract void onUnlikedBy(User user) throws SocialObjectNotLikableException;
}
