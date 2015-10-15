package domain;

import babybox.shopping.social.exception.SocialObjectNotFollowableException;
import models.User;

public interface Followable {
	public abstract boolean onFollow(User user) throws SocialObjectNotFollowableException;
	public abstract boolean onUnFollow(User user) throws SocialObjectNotFollowableException;
}
