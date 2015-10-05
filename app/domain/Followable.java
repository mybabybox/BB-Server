package domain;

import babybox.shopping.social.exception.SocialObjectNotFollowableException;
import models.User;

public interface Followable {
	public abstract void onFollow(User user) throws SocialObjectNotFollowableException;
	public abstract void onUnFollow(User user) throws SocialObjectNotFollowableException;
}
