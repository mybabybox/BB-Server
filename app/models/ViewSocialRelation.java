package models;

import java.io.Serializable;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import babybox.shopping.social.SocialActivity;

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
public class ViewSocialRelation extends domain.Entity implements Serializable, Creatable, Updatable  {
    private static final play.api.Logger logger = play.api.Logger.apply(ViewSocialRelation.class);
    
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	public Long actor;
	@Enumerated(EnumType.STRING)
	public SocialObjectType actorType;
	
	public Integer relationWeight;
	
	@Enumerated(EnumType.STRING)
    public ActionType actionType;
    
    static public enum ActionType {
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

	public ViewSocialRelation(){}
	
	public ViewSocialRelation(Long id, SocialObject actor, Integer weight, SocialObject target) {
		super();
		this.id = id;
		this.actor = actor.id;
		this.actorname = actor.name;
		this.relationWeight = weight;
		this.target = target.id;
		this.targetname = target.name;
		this.targetOwner = target.owner == null ? null :target.owner.id;
	}
	
	public ViewSocialRelation(SocialObject actor, SocialObject target) {
		this.actor = actor.id;
		this.actorname = actor.name;
		this.target = target.id;
		this.targetname = target.name;
		this.targetOwner = target.owner == null ? null :target.owner.id;
		this.targetType = target.objectType;
		this.actorType = actor.objectType;
	}
	
	@Transactional
    public List<ViewSocialRelation> getSocialRelations() {
        Query q = JPA.em().createQuery(
                "Select sa from ViewSocialRelation sa where actor = ?1 and target = ?2 and actorType = ?3 and targetType = ?4");
        q.setParameter(1, this.actor);
        q.setParameter(2, this.target);
        q.setParameter(3, this.actorType);
        q.setParameter(4, this.targetType);
        try {
            List<ViewSocialRelation> socialRelations = q.getResultList();
            return socialRelations;
        } catch (NoResultException nre){
        }
        return null;
    }
	
	@Transactional
	public boolean ensureUniqueAndCreate() {
	    List<ViewSocialRelation> socialRelations = getSocialRelations();
	    if (CollectionUtils.isEmpty(socialRelations)) {
	        save();
	        return true;
	    }
	    
	    boolean keepSocialRelation = true;
	    for (ViewSocialRelation socialRelation : socialRelations) {
	        if (keepSocialRelation) {
	            keepSocialRelation = false;
	            continue;
	        }

	        socialRelation.delete();
	    }
	    return false;
	}
	
	// NOTE: Caution, call this method when target and actor pair is one to one.
	@Transactional
	public void createOrUpdateForTargetAndActorPair() {
		Query q = JPA.em().createQuery(
		        "Select sa from ViewSocialRelation sa where actor = ?1 and target = ?2 and actorType = ?3 and targetType = ?4");
		q.setParameter(1, this.actor);
		q.setParameter(2, this.target);
		q.setParameter(3, this.actorType);
		q.setParameter(4, this.targetType);
		ViewSocialRelation sa = null;
		
		try{
			sa = (ViewSocialRelation) q.getSingleResult();
		} catch (NoResultException nre){
		}
		
		if (sa == null) {
			save();
		} else {
			sa.actionType = this.actionType;
			sa.merge();
		}
	}
	
	@Override
	public void delete() {
	    Notification notification = Notification.findBySocialActionID(this.id);
        if (notification != null)
            notification.delete();
        super.delete();
	}
	
	@Override
	public void postSave() {
		if (isPostSave) {
            SocialActivity.handle(this);
        }
	}
	
	public <T> T getTargetObject(Class<T> claszz){
		String query = "Select c from " + claszz.getName() + " c where id = ?1 and deleted = false";
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
		String query = "Select c from " + claszz.getName() + " c where id = ?1 and deleted = false";
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
}
