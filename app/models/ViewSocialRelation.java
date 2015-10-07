package models;

import javax.persistence.Entity;

@Entity
public class ViewSocialRelation extends SocialRelation {
    private static final play.api.Logger logger = play.api.Logger.apply(ViewSocialRelation.class);
    
	public ViewSocialRelation(){}
	
	public ViewSocialRelation(Long id, SocialObject actor, Integer weight, SocialObject target) {
		super(id, actor, weight, target);
	}
	
	public ViewSocialRelation(SocialObject actor, SocialObject target) {
		super(actor, target);
	}
	
	@Override
	public SocialRelation.Action getAction() {
		return Action.VIEW;
	}
}
