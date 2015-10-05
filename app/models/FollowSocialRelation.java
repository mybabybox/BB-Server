package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
	public Action getAction() {
		return Action.FOLLOW;
	}
	
	public static List<Long> getFollowings(Long id) {
		Query q = JPA.em().createQuery("Select sa from FollowSocialRelation sa where actor = ?1");
		q.setParameter(1, id);
		List<FollowSocialRelation> sa = new ArrayList<>();
		List<Long> follwers = new ArrayList<Long>();
		try{
			sa = q.getResultList();
			for(FollowSocialRelation ssr: sa) {
				follwers.add(ssr.actor);
			}
			return follwers;
		}
		catch (NoResultException nre){
		}
		return null;
	}
	
	public static List<Long> getFollowers(Long id) {
		Query q = JPA.em().createQuery("Select sa from FollowSocialRelation sa where target = ?1");
		q.setParameter(1, id);
		List<FollowSocialRelation> sa = new ArrayList<>();
		List<Long> follwers = new ArrayList<Long>();
		try{
			sa = q.getResultList();
			for(FollowSocialRelation ssr: sa) {
				follwers.add(ssr.target);
			}
			return follwers;
		}
		catch (NoResultException nre){
		}
		return null;
	}
}
