package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import domain.DefaultValues;
import domain.SocialObjectType;
import play.db.jpa.JPA;

@Entity
public class FollowSocialRelation extends SocialRelation {
    private static final play.api.Logger logger = play.api.Logger.apply(FollowSocialRelation.class);
    
	public FollowSocialRelation(){}
	
	public FollowSocialRelation(Long id, SocialObject actor, Integer weight, SocialObject target) {
		super(id, actor, weight, target);
	}
	
	public FollowSocialRelation(SocialObject actor, SocialObject target) {
		super(actor, target);
	}
	
	@Override
	public SocialRelation.Action getAction() {
		return Action.FOLLOW;
	}
	
	public static boolean isFollowing(Long actor, SocialObjectType actorType, Long target, SocialObjectType targetType) {
        Query q = JPA.em().createQuery(
        		"Select sr from FollowSocialRelation sr where actor = ?1 and actorType = ?2 and target = ?3 and targetType = ?4");
        q.setParameter(1, actor);
        q.setParameter(2, actorType);
        q.setParameter(3, target);
        q.setParameter(4, targetType);
        return q.getResultList().size() > 0;
    }
	
	public static boolean unfollow(Long actor, SocialObjectType actorType, Long target, SocialObjectType targetType) {
    	Query q = JPA.em().createQuery(
    			"Delete from FollowSocialRelation sr where actor = ?1 and actorType = ?2 and target = ?3 and targetType = ?4");
    	q.setParameter(1, actor);
        q.setParameter(2, actorType);
        q.setParameter(3, target);
        q.setParameter(4, targetType);
		return q.executeUpdate() > 0;
    }
	
	public static List<FollowSocialRelation> getUserFollowings(Long id) {
		Query q = JPA.em().createQuery(
				"Select sr from FollowSocialRelation sr where actor = ?1 and actorType = ?2 and targetType = ?3");
		q.setParameter(1, id);
		q.setParameter(2, SocialObjectType.USER);
		q.setParameter(3, SocialObjectType.USER);
		if (q.getResultList().size() > 0){
			return q.getResultList(); 
		}
    	return new ArrayList<>();
	}
	
	public static List<FollowSocialRelation> getUserFollowings(Long id, Long offset) {
		Query q = JPA.em().createQuery(
				"Select sr from FollowSocialRelation sr where actor = ?1 and actorType = ?2 and targetType = ?3");
		q.setParameter(1, id);
		q.setParameter(2, SocialObjectType.USER);
		q.setParameter(3, SocialObjectType.USER);
		try {
			q.setFirstResult((int) (offset * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT));
			q.setMaxResults(DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
			return (List<FollowSocialRelation>) q.getResultList();
		} catch (NoResultException e) {
			return new ArrayList<>();
		}
	}
	
	public static List<FollowSocialRelation> getUserFollowers(Long id) {
		Query q = JPA.em().createQuery(
				"Select sr from FollowSocialRelation sr where target = ?1 and actorType = ?2 and targetType = ?3");
		q.setParameter(1, id);
		q.setParameter(2, SocialObjectType.USER);
		q.setParameter(3, SocialObjectType.USER);
		if (q.getResultList().size() > 0){
			return q.getResultList(); 
		}
    	return new ArrayList<>();
	}
	
	public static List<FollowSocialRelation> getUserFollowers(Long id, Long offset) {
		Query q = JPA.em().createQuery(
				"Select sr from FollowSocialRelation sr where target = ?1 and actorType = ?2 and targetType = ?3");
		q.setParameter(1, id);
		q.setParameter(2, SocialObjectType.USER);
		q.setParameter(3, SocialObjectType.USER);
		try {
			q.setFirstResult((int) (offset * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT));
			q.setMaxResults(DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
			return (List<FollowSocialRelation>) q.getResultList();
		} catch (NoResultException e) {
			return new ArrayList<>();
		}
	}
}
