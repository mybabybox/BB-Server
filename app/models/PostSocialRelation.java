package models;

import javax.persistence.Entity;

@Entity
public class PostSocialRelation extends SocialRelation {
    private static final play.api.Logger logger = play.api.Logger.apply(PostSocialRelation.class);
    
	public PostSocialRelation(){}
	
	public PostSocialRelation(Long id, SocialObject actor, Integer weight, SocialObject target) {
		super(id, actor, weight, target);
	}
	
	public PostSocialRelation(SocialObject actor, SocialObject target) {
		super(actor, target);
	}
	
	@Override
	public SocialRelation.Action getAction() {
		return Action.POST;
	}
}
