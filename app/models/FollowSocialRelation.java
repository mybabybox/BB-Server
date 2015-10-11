package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
	
	public static List<Long> getFollowings(Long id) {
		Query q = JPA.em().createQuery("Select sa from FollowSocialRelation sa where actor = ?1");
		q.setParameter(1, id);
		List<FollowSocialRelation> sa = new ArrayList<>();
		List<Long> followings = new ArrayList<Long>();
		try {
			sa = q.getResultList();
			for (FollowSocialRelation ssr: sa) {
				followings.add(ssr.target);
			}
			return followings;
		} catch (NoResultException nre) {
		}
		return null;
	}
	
	public static List<Long> getFollowers(Long id) {
		Query q = JPA.em().createQuery("Select sa from FollowSocialRelation sa where target = ?1");
		q.setParameter(1, id);
		List<FollowSocialRelation> sa = new ArrayList<>();
		List<Long> followers = new ArrayList<Long>();
		try {
			sa = q.getResultList();
			for (FollowSocialRelation ssr: sa) {
				followers.add(ssr.actor);
			}
			return followers;
		} catch (NoResultException nre){
		}
		return null;
	}
}
