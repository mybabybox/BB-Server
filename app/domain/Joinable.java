package domain;

import models.User;
import mybox.shopping.social.exception.SocialObjectNotJoinableException;

public interface Joinable {
	void onJoinRequest(User user) throws SocialObjectNotJoinableException;
	void onJoinRequestAccepted(User user) throws SocialObjectNotJoinableException;
}
