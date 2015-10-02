package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.NoResultException;
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
	public Action getAction() {
		return Action.LIKE;
	}
	
	public static boolean isLikedBy(Long actor, SocialObjectType actorType, Long target, SocialObjectType targetType) {
        Query q = JPA.em().createQuery(
        		"Select sr from LikeSocialRelation sr where sr.actor=?1 and sr.actorType=?2 and sr.target=?3 and sr.targetType=?4");
        q.setParameter(1, actor);
        q.setParameter(4, actorType);
        q.setParameter(3, target);
        q.setParameter(4, targetType);
        LikeSocialRelation sr = null;
        try {
            sr = (LikeSocialRelation)q.getSingleResult();
        } catch(NoResultException nre) {
            return false;
        }
        return true;
    }
	
    public static List<LikeSocialRelation> getUserLikedPosts(Long id){
    	Query q = JPA.em().createQuery("Select sa from LikeSocialRelation sa where actor = ?1 and pr.actionType = ?2 and pr.targetType = ?3");
		q.setParameter(1, id);
		q.setParameter(2, SocialObjectType.USER);
		q.setParameter(3, SocialObjectType.POST);
		List<LikeSocialRelation> l = new ArrayList<>();
		if(q.getResultList().size() != 0){
			return q.getResultList(); 
		}
    	return l;
    }
}