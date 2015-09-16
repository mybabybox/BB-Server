package domain;

import babybox.shopping.social.exception.SocialObjectNotFollowableException;
import models.User;

public interface Followable {
	public abstract void onFollowedBy(User user) throws SocialObjectNotFollowableException;
	public abstract void onUnFollowedBy(User user) throws SocialObjectNotFollowableException;
}
