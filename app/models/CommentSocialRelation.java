package models;

import javax.persistence.Entity;

@Entity
public class CommentSocialRelation extends SocialRelation {
    private static final play.api.Logger logger = play.api.Logger.apply(CommentSocialRelation.class);
    
	public CommentSocialRelation(){}
	
	public CommentSocialRelation(Long id, SocialObject actor, Integer weight, SocialObject target) {
		super(id, actor, weight, target);
	}
	
	public CommentSocialRelation(SocialObject actor, SocialObject target) {
		super(actor, target);
	}
	
	@Override
	public SocialRelation.Action getAction() {
		return Action.COMMENT;
	}
}
