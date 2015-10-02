package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.lang.exception.ExceptionUtils;

import common.thread.ThreadLocalOverride;
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
@EntityListeners(AuditListener.class)
@MappedSuperclass
public abstract class SocialRelation extends domain.Entity implements Serializable, Creatable, Updatable {
    private static final play.api.Logger logger = play.api.Logger.apply(SocialRelation.class);
    
    public static enum Action {
    	LIKE,
    	FOLLOW,
		POST,
		COMMENT,
		VIEW
	}
    
    public static enum ActionType {
    	USER,
		PRODUCT,
		STORY
    }
    
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	public Long actor;
	@Enumerated(EnumType.STRING)
	public SocialObjectType actorType;
	
	public Integer relationWeight;
	
	@Enumerated(EnumType.STRING)
    public ActionType actionType;
    
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
	protected boolean isPostSave = true;

	abstract public Action getAction();

	public String getTableName() {
		return this.getClass().getName();
	}
	
	public SocialRelation(){}
	
	public SocialRelation(Long id, SocialObject actor, Integer weight, SocialObject target) {
		super();
		this.id = id;
		this.actor = actor.id;
		this.actorname = actor.name;
		this.relationWeight = weight;
		this.target = target.id;
		this.targetname = target.name;
		this.targetOwner = target.owner == null ? null :target.owner.id;
	}
	
	public SocialRelation(SocialObject actor, SocialObject target) {
		this.actor = actor.id;
		this.actorname = actor.name;
		this.target = target.id;
		this.targetname = target.name;
		this.targetOwner = target.owner == null ? null :target.owner.id;
		this.targetType = target.objectType;
		this.actorType = actor.objectType;
	}
	
    public List<SocialRelation> getSocialRelations() {
        Query q = JPA.em().createQuery(
                "Select sa from " + getTableName() + " sa where actor = ?1 and target = ?2 and actorType = ?3 and targetType = ?4");
        q.setParameter(1, this.actor);
        q.setParameter(2, this.target);
        q.setParameter(3, this.actorType);
        q.setParameter(4, this.targetType);
        try {
            List<SocialRelation> socialRelations = q.getResultList();
            return socialRelations;
        } catch (NoResultException nre){
        }
        return null;
    }
	
	@Transactional
	public boolean ensureUniqueAndCreate() {
	    Query q = JPA.em().createQuery(
                "Select count(sa) from " + getTableName() + " sa where actor = ?1 and target = ?2 and actorType = ?3 and targetType = ?4");
        q.setParameter(1, this.actor);
        q.setParameter(2, this.target);
        q.setParameter(3, this.actorType);
        q.setParameter(4, this.targetType);
        try {
        	Long count = (Long)q.getSingleResult();
    		if (count == 0) {
    			save();
    			return true;
    		}
        } catch (NoResultException nre){
        }
        
        return false;
	}
	
	// NOTE: Caution, call this method when target and actor pair is one to one.
	@Transactional
	public void createOrUpdateForTargetAndActorPair() {
		Query q = JPA.em().createQuery(
		        "Select sa from " + getTableName() + " sa where actor = ?1 and target = ?2 and actorType = ?3 and targetType = ?4");
		q.setParameter(1, this.actor);
		q.setParameter(2, this.target);
		q.setParameter(3, this.actorType);
		q.setParameter(4, this.targetType);
		SocialRelation sa = null;
		
		try{
			sa = (SocialRelation) q.getSingleResult();
		} catch (NoResultException nre){
		}
		
		if(sa == null ) {
			save();
		} else {
			sa.actionType = this.actionType;
			sa.merge();
		}
	}
	
	@Override
	public void delete() {
	    Notification notification = Notification.findBySocialActionId(this.id);
        if (notification != null) {
            notification.delete();
        }
        super.delete();
	}
	
	@Override
	public void postSave() {
		if (isPostSave) {
            if (ThreadLocalOverride.isDisableNotification()) {
                logger.underlyingLogger().info("Notification is disabled for: "+this.id);
            } else {
	            SocialActivity.handle(this);
            }
        }
	}
	
	public SocialObject getTargetObject(){
		if (this.targetType == SocialObjectType.USER) { 
		    return getTargetObject(User.class); 
		} else if (this.targetType == SocialObjectType.CATEGORY) { 
		    return getTargetObject(Category.class);
		} else if (this.targetType == SocialObjectType.POST) { 
		    return getTargetObject(Post.class);
		} else if (this.targetType == SocialObjectType.COMMENT) { 
		    return getTargetObject(Comment.class);
		}
		return null; 
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
	
	public SocialObject getActorObject(){
		if (this.targetType == SocialObjectType.USER) { 
		    return getActorObject(User.class); 
		} else if (this.targetType == SocialObjectType.CATEGORY) { 
		    return getActorObject(Category.class);
		} else if (this.targetType == SocialObjectType.POST) { 
		    return getActorObject(Post.class);
		} else if (this.targetType == SocialObjectType.COMMENT) { 
		    return getActorObject(Comment.class);
		}
		return null;
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
