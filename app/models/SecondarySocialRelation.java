package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.lang.exception.ExceptionUtils;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import domain.AuditListener;
import domain.Creatable;
import domain.SocialObjectType;
import domain.Updatable;

/**
 *  This class is analogous of Weighted Graph Data Structure. 
 *  In Weighted four entities are involved:
 * 
 *  Node A (subject / actor)
 *  Edge   (verb / action)
 *  Node B (object / target)
 *  Weight (adverb / degree) 
 * 
 *  Example 1: 
 *  Statement: Joe Lee Rated 4 Start to Post of John Voo.
 *  
 *  Graph will be:
 *  Actor  : Joe Lee,
 *  Target : John Voo's Post,
 *  Action : Ratted,
 *  Weight : 4
 *  
 *  Not all edge need to have weight. E.g Joe Lee Liked to Post of John Voo.
 *  In this example we don't have degree of likeness. 
 *  
 *  E.g Joe Lee Strongly(Adverb) Recommend John Voo's Novel.
 *  Actor  : Joe Lee,
 *  Target : John Voo's Novel,
 *  Action : Recommend,
 *  Weight : Strongly(may be 5)
 *  
 *  @author jagbirs
 *
 */

@Entity
@EntityListeners(AuditListener.class)
public class SecondarySocialRelation extends domain.Entity implements Serializable, Creatable, Updatable  {
    private static final play.api.Logger logger = play.api.Logger.apply(SecondarySocialRelation.class);
    
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	public Long actor;
	@Enumerated(EnumType.STRING)
	public SocialObjectType actorType;
	
	@Enumerated(EnumType.STRING)
	public Action action;
	
	public Integer relationWeight;
	
	@Enumerated(EnumType.STRING)
    public ActionType actionType;
    
    static public enum ActionType {
    		MESSAGE_SEND,
    }
	
	public Long target;
	@Enumerated(EnumType.STRING)
	public SocialObjectType targetType;

	@Transient
	public String targetname;
	@Transient
	public Long targetOwner;
	@Transient
	public String actorname;
	
	@Transient
	boolean isPostSave = true;
	
	static public enum Action {
		FOLLOWED
	}

	public SecondarySocialRelation(){}
	
	public SecondarySocialRelation(Long id, SocialObject actor, Action action,
			Integer weight, SocialObject target) {
		super();
		this.id = id;
		this.actor = actor.id;
		this.actorname = actor.name;
		this.action = action;
		this.relationWeight = weight;
		this.target = target.id;
		this.targetname = target.name;
		this.targetOwner = target.owner == null ? null :target.owner.id;
	}
	
	public SecondarySocialRelation(SocialObject actor, SocialObject target) {
		this.actor = actor.id;
		this.actorname = actor.name;
		this.target = target.id;
		this.targetname = target.name;
		this.targetOwner = target.owner == null ? null :target.owner.id;
		this.targetType = target.objectType;
		this.actorType = actor.objectType;
	}
	
	@Transactional
	public void validateUniquenessAndCreate() {
		Query q = JPA.em().createQuery("Select sa from SecondarySocialRelation sa where actor = ?1 and action = ?2 and target = ?3 and actorType = ?4 and targetType = ?5");
		q.setParameter(1, this.actor);
		q.setParameter(2, this.action);
		q.setParameter(3, this.target);
		q.setParameter(4, this.actorType);
		q.setParameter(5, this.targetType);
		if(q.getResultList().size() > 0 ) {
			// Already liked ; Any logic !
			return;
		} else {
			save();
		}
	}
	
	// NOTE: Caution, call this method when target and actor pair is one to one.
	@Transactional
	public void createOrUpdateForTargetAndActorPair() {
		Query q = JPA.em().createQuery("Select sa from SecondarySocialRelation sa where actor = ?1 and target = ?2 and actorType = ?3 and targetType = ?4");
		q.setParameter(1, this.actor);
		q.setParameter(2, this.target);
		q.setParameter(3, this.actorType);
		q.setParameter(4, this.targetType);
		SecondarySocialRelation sa = null;
		
		try{
			sa = (SecondarySocialRelation) q.getSingleResult();
		}
		catch (NoResultException nre){
		}
		
		if(sa == null ) {
			save();
		} else {
			sa.actionType = this.actionType;
			sa.merge();
		}
	}
	
	@Override
	public void postSave() {
		
	}
	
	public <T> T getTargetObject(Class<T> claszz){
		String query = "Select c from " + claszz.getName() + " c where id = ?1";
		Query q = JPA.em().createQuery(query);
		q.setParameter(1, this.target);
		try {
            return (T)q.getSingleResult();
        } catch (NoResultException e) {
            logger.underlyingLogger().error("getTargetObject() - TargetObject not found [Class|Id]:[" + claszz.getName() + "|" + this.target + "]");
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return null;
	}
	
	public SocialObject getTargetObject(){
		if(this.targetType == SocialObjectType.USER) {
		    return getTargetObject(User.class); 
		}
		return getTargetObject(User.class); 
	}
	
	public SocialObject getActorObject(){
		if(this.actorType == SocialObjectType.USER) {
		    return getActorObject(User.class); 
		}
		return getActorObject(User.class); 
	}
	
	public <T> T getActorObject(Class<T> claszz){
		String query = "Select c from " + claszz.getName() + " c where id = ?1";
		Query q = JPA.em().createQuery(query);
		q.setParameter(1, this.actor);
		try {
            return (T)q.getSingleResult();
        } catch (NoResultException e) {
            logger.underlyingLogger().error("getActorObject() - ActorObject not found [Class|Id]:[" + claszz.getName() + "|" + this.actor + "]");
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return null;
	}
	
	@Transactional
	public static List<Long> getFollowings(Long id) {
		Query q = JPA.em().createQuery("Select sa from SecondarySocialRelation sa where target = ?1");
		q.setParameter(1, id);
		List<SecondarySocialRelation> sa = new ArrayList<SecondarySocialRelation>();
		List<Long> follwers = new ArrayList<Long>();
		try{
			sa = q.getResultList();
			for(SecondarySocialRelation ssr: sa) {
				follwers.add(ssr.actor);
			}
			return follwers;
		}
		catch (NoResultException nre){
		}
		return null;
	}
	public static List<Long> getFollowers(Long id) {
		Query q = JPA.em().createQuery("Select sa from SecondarySocialRelation sa where actor = ?1");
		q.setParameter(1, id);
		List<SecondarySocialRelation> sa = new ArrayList<SecondarySocialRelation>();
		List<Long> follwers = new ArrayList<Long>();
		try{
			sa = q.getResultList();
			for(SecondarySocialRelation ssr: sa) {
				follwers.add(ssr.target);
			}
			return follwers;
		}
		catch (NoResultException nre){
		}
		return null;
	}
}
