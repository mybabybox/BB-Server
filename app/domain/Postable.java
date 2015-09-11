package domain;

import models.SocialObject;
import models.User;
import mybox.shopping.social.exception.SocialObjectNotPostableException;

public interface Postable {
	public abstract SocialObject onPost(User user, String title, String body, ProductType type) throws SocialObjectNotPostableException;
}
