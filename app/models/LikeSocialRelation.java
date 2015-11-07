package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Query;

import play.db.jpa.JPA;
import domain.SocialObjectType;

@Entity
public class LikeSocialRelation extends SocialRelation {
    private static final play.api.Logger logger = play.api.Logger.apply(LikeSocialRelation.class);
    
	public LikeSocialRelation(){}
	
	public LikeSocialRelation(Long id, SocialObject actor, Integer weight, SocialObject target) {
		super(id, actor, weight, target);
	}
	
	public LikeSocialRelation(SocialObject actor, SocialObject target) {
		super(actor, target);
	}
	
	@Override
	public SocialRelation.Action getAction() {
		return Action.LIKE;
	}
	
	public static boolean isLiked(Long actor, SocialObjectType actorType, Long target, SocialObjectType targetType) {
        Query q = JPA.em().createQuery(
        		"Select sr from LikeSocialRelation sr where actor = ?1 and actorType = ?2 and target = ?3 and targetType = ?4");
        q.setParameter(1, actor);
        q.setParameter(2, actorType);
        q.setParameter(3, target);
        q.setParameter(4, targetType);
        return q.getResultList().size() > 0;
    }
	
	public static boolean unlike(Long actor, SocialObjectType actorType, Long target, SocialObjectType targetType) {
    	Query q = JPA.em().createQuery(
    			"Delete from LikeSocialRelation sr where actor=?1 and actorType=?2 and target=?3 and targetType=?4");
    	q.setParameter(1, actor);
        q.setParameter(2, actorType);
        q.setParameter(3, target);
        q.setParameter(4, targetType);
		return q.executeUpdate() > 0;
    }
	
    public static List<LikeSocialRelation> getUserLikedPosts(Long id){
    	Query q = JPA.em().createQuery(
    			"Select sr from LikeSocialRelation sr where actor = ?1 and actorType = ?2 and targetType = ?3");
		q.setParameter(1, id);
		q.setParameter(2, SocialObjectType.USER);
		q.setParameter(3, SocialObjectType.POST);
		if (q.getResultList().size() > 0){
			return q.getResultList(); 
		}
    	return new ArrayList<>();
    }
    
    public static List<LikeSocialRelation> getPostLikedUsers(Long id){
    	Query q = JPA.em().createQuery(
    			"Select sr from LikeSocialRelation sr where target = ?1 and targetType = ?2 and actorType = ?3");
		q.setParameter(1, id);
		q.setParameter(2, SocialObjectType.POST);
		q.setParameter(3, SocialObjectType.USER);
		if (q.getResultList().size() > 0){
			return q.getResultList(); 
		}
    	return new ArrayList<>();
    }
}