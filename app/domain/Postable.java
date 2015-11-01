package domain;

import babybox.shopping.social.exception.SocialObjectNotPostableException;
import models.Post;
import models.Post.PostType;
import models.SocialObject;
import models.User;

public interface Postable {
	public abstract SocialObject onPost(User user, String title, String body, PostType type) throws SocialObjectNotPostableException;
}
