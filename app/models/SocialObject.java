package models;

import java.io.Serializable;

import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import common.cache.FriendCache;
import models.SocialRelation.Action;
import mybox.shopping.social.exception.SocialObjectNotCommentableException;
import mybox.shopping.social.exception.SocialObjectNotFollowableException;
import mybox.shopping.social.exception.SocialObjectNotJoinableException;
import mybox.shopping.social.exception.SocialObjectNotLikableException;
import mybox.shopping.social.exception.SocialObjectNotPostableException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

import com.google.common.base.Objects;

import domain.AuditListener;
import domain.CommentType;
import domain.Commentable;
import domain.Creatable;
import domain.Followable;
import domain.Joinable;
import domain.Likeable;
import domain.ProductType;
import domain.Postable;
import domain.SocialObjectType;
import domain.Updatable;

//@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditListener.class)
@MappedSuperclass
public abstract class SocialObject extends domain.Entity implements
		Serializable, Creatable, Updatable{

	@Id
	//MySQL5Dialect does not support sequence
	//@GeneratedValue(generator = "social-sequence")
	//@GenericGenerator(name = "social-sequence",strategy = "com.mnt.persist.generator.SocialSequenceGenerator")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Enumerated(EnumType.STRING)
	public SocialObjectType objectType;

	public String name;

	@JsonIgnore
	@ManyToOne
	public User owner;

	/*
	 * Folder
	 *     System albums will not generate socialAction onCreate and should be always public 
	 *     (the privacy is set on the single inner elements)
	 *     
	 * Community
	 *     System communities will have special treatment e.g. targeting, privacy
     */
	@Required
    public Boolean system = false;
    
	@Required
    public Boolean deleted = false;     // social objects should always be soft deleted
	
	@JsonIgnore
    @ManyToOne
    public User deletedBy;
	
	public boolean isLikedBy(User user){
        Query q = JPA.em().createQuery("Select sr from PrimarySocialRelation sr where sr.action=?1 and sr.actor=?2 " +
                "and sr.target=?3 and sr.targetType=?4");
        q.setParameter(1, PrimarySocialRelation.Action.LIKED);
        q.setParameter(2, user.id);
        q.setParameter(3, this.id);
        q.setParameter(4, this.objectType);
        PrimarySocialRelation sr = null;
        try {
            sr = (PrimarySocialRelation)q.getSingleResult();
        }
        catch(NoResultException nre) {
            return false;
        }
        return true;
    }
    
    public boolean isWantAnswerBy(User user) {
        Query q = JPA.em().createQuery("Select sr from PrimarySocialRelation sr where sr.action=?1 and sr.actor=?2 " +
                "and sr.target=?3 and sr.targetType=?4");
        q.setParameter(1, PrimarySocialRelation.Action.WANT_ANS);
        q.setParameter(2, user.id);
        q.setParameter(3, this.id);
        q.setParameter(4, this.objectType);
        PrimarySocialRelation sr = null;
        try {
            sr = (PrimarySocialRelation)q.getSingleResult();
        }
        catch(NoResultException nre) {
            return false;
        }
        return true;
    }
    
    public boolean isBookmarkedBy(User user) {
        Query q = JPA.em().createQuery("Select sr from SecondarySocialRelation sr where sr.action=?1 and sr.actor=?2 " +
                "and sr.target=?3 and sr.targetType=?4");
        q.setParameter(1, SecondarySocialRelation.Action.BOOKMARKED);
        q.setParameter(2, user.id);
        q.setParameter(3, this.id);
        q.setParameter(4, this.objectType);
        try {
            SecondarySocialRelation sr = (SecondarySocialRelation)q.getSingleResult();
        } catch(NoResultException nre) {
            return false;
        }
        return true;
    }

    public Boolean getYesNoVote(User user) {
        Query q = JPA.em().createQuery("Select sr from PrimarySocialRelation sr where sr.action in (?1,?2) and sr.actor=?3 " +
                "and sr.target=?4 and sr.targetType=?5");
        q.setParameter(1, PrimarySocialRelation.Action.YES_VOTED);
        q.setParameter(2, PrimarySocialRelation.Action.NO_VOTED);
        q.setParameter(3, user.id);
        q.setParameter(4, this.id);
        q.setParameter(5, this.objectType);
        PrimarySocialRelation sr;
        try {
            sr = (PrimarySocialRelation)q.getSingleResult();
        } catch(NoResultException nre) {
            return null;
        } catch(NonUniqueResultException nure) {
            sr = (PrimarySocialRelation)(q.getResultList().get(0));
        }
        
        return (sr.action == PrimarySocialRelation.Action.YES_VOTED);
    }
    
	protected final void recordLike(User user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.LIKED;
		action.validateUniquenessAndCreate();
        // Game Stats
        //GameAccountStatistics.recordLike(user.id);
	}
	
	protected final void recordFollow(User user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.FOLLOWED;
		action.validateUniquenessAndCreate();
	}
	
	protected void recordCommentOnProduct(SocialObject user, Comment comment) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, comment);
		action.action = PrimarySocialRelation.Action.COMMENTED;
        action.save();
	}

    protected final void recordWantAnswer(User user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.WANT_ANS;
		action.validateUniquenessAndCreate();
	}

    protected final void recordYesVote(User user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.YES_VOTED;
		action.validateUniquenessAndCreate();
    }

    protected final void recordNoVote(User user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.NO_VOTED;
		action.validateUniquenessAndCreate();
    }
	
	protected final void recordBookmark(User user) {
		SecondarySocialRelation action = new SecondarySocialRelation(user, this);
		action.action = SecondarySocialRelation.Action.BOOKMARKED;
		action.validateUniquenessAndCreate();
	}
	
	protected final void recordJoinRequest(User user) {
		SocialRelation action = new SocialRelation(user, this);
		action.actionType = SocialRelation.ActionType.JOIN_REQUESTED;
		action.createOrUpdateForTargetAndActorPair();
	}
	
	protected final void ownerMemberOfCommunity(User user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.MEMBER;
		action.actionType = SocialRelation.ActionType.GRANT;
		action.isPostSave = false;
		action.ensureUniqueAndCreate();
	}

    /**
     * Be member of Open Community without join request.
     */
	protected final void beMemberOfOpenCommunity(User user, boolean sendNotification) {
	    // Join community
	    SocialRelation action = new SocialRelation(user, this);
        action.action = SocialRelation.Action.MEMBER;
        action.actionType = SocialRelation.ActionType.GRANT;
        action.memberJoinedOpenCommunity = true;
        action.isPostSave = sendNotification;

        // save SocialRelation
        if (action.ensureUniqueAndCreate()) {
            // save community affinity
            //UserCommunityAffinity.onJoinedCommunity(user.id, this.id);
        }
        
        // Clear up invite request if any
        SocialRelation request = getInviteRequest(user);
        if (request != null) {
            request.delete();
        }
	}

	protected final void recordJoinRequestAccepted(User user) {
		// must have a join request to proceed
        SocialRelation request = getJoinRequest(user);
        if (request == null) {
            return;
        }
        
        // use existing join request to capture MEMBER relationship
        request.action = SocialRelation.Action.MEMBER;
        request.actionType = SocialRelation.ActionType.GRANT;
        request.ensureUniqueAndCreate();
        
        // save community affinity
        //UserCommunityAffinity.onJoinedCommunity(user.id, this.id);
	}
	
	protected final void recordInviteRequestAccepted(User user) {
	    // must have a join request to proceed
        SocialRelation request = getInviteRequest(user);
        if (request == null) {
            return;
        }
        
        // use existing join request to capture MEMBER relationship
        request.action = SocialRelation.Action.MEMBER;
        request.actionType = SocialRelation.ActionType.GRANT;
        request.save();

        // save community affinity
        //UserCommunityAffinity.onJoinedCommunity(user.id, this.id);
	}

	protected final SocialRelation getJoinRequest(User user) {
        return getRequest(user, SocialRelation.ActionType.JOIN_REQUESTED);
    }
    
    protected final SocialRelation getInviteRequest(User user) {
        return getRequest(user, SocialRelation.ActionType.INVITE_REQUESTED);
    }
    
    protected final SocialRelation getRequest(User user, SocialRelation.ActionType actionType) {
        Query q = JPA.em().createQuery(
                "SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
        q.setParameter(1, user.id);
        q.setParameter(2, this.id);
        q.setParameter(3, actionType);

        try {
            SocialRelation request = (SocialRelation) q.getSingleResult();
            return request;
        } catch (NoResultException nre){
        }
        return null;
    }
    
	protected final void recordFriendRequest(User invitee) {
		SocialRelation action = new SocialRelation(this, invitee);
		action.actionType = SocialRelation.ActionType.FRIEND_REQUESTED;
		action.ensureUniqueAndCreate();
	}

	protected final void recordFriendRequestAccepted(User user) {
		Query q = JPA.em().createQuery(
		        "SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user.id);
		q.setParameter(2, this.id);
		q.setParameter(3, SocialRelation.ActionType.FRIEND_REQUESTED);

		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = SocialRelation.Action.FRIEND;
        // update SocialRelation to GRANT
		action.save();

        // update Friends cache
        FriendCache.onBecomeFriend(user.id, this.id);
	}

	protected final void recordRelationshipRequest(User user, Action relation) {
		SocialRelation action = new SocialRelation(this,user);
		action.actionType = SocialRelation.ActionType.RELATIONSHIP_REQUESTED;
		action.action = relation;
		action.ensureUniqueAndCreate();
	}

	protected final void recordRelationshipRequestAccepted(User user,
			Action relation) {
		Query q = JPA.em().createQuery(
		        "SELECT sa from SocialRelation sa where actor = ?1 and target = ?2 and actionType =?3");
		q.setParameter(1, user.id);
		q.setParameter(2, this.id);
		q.setParameter(3, SocialRelation.ActionType.RELATIONSHIP_REQUESTED);
		SocialRelation action = (SocialRelation) q.getSingleResult();
		action.actionType = SocialRelation.ActionType.GRANT;
		action.action = relation;
		action.save();
	}

	protected final void recordPost(SocialObject user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.POSTED;
		action.save();
        // Game Stats
        //GameAccountStatistics.recordPost(user.id);
	}

	protected void recordQnA(SocialObject user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.POSTED_QUESTION;
		action.save();
        // Game Stats
        //GameAccountStatistics.recordPost(user.id);
	}

    @Deprecated
    protected void recordCommentOnArticle(SocialObject user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.COMMENTED;
		action.save();
	}


	protected void recordAddedPhoto(SocialObject user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.ADDED;
		action.save();
	}

	protected final void recordInviteRequestByCommunity(User invitee) {
		SocialRelation action = new SocialRelation(invitee, this);
		action.actionType = SocialRelation.ActionType.INVITE_REQUESTED;
		action.ensureUniqueAndCreate();
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(name, objectType, id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof SocialObject) {
			final SocialObject other = (SocialObject) obj;
			return new EqualsBuilder().append(name, other.name)
					.append(id, other.id).append(objectType, other.objectType)
					.isEquals();
		} else {
			return false;
		}
	}

	public void onLikedBy(User user) throws SocialObjectNotLikableException {
		throw new SocialObjectNotLikableException(
				"Please make sure Social Object you are liking is Likable");
	}
	
	public void onUnlikedBy(User user) throws SocialObjectNotLikableException {
		throw new SocialObjectNotLikableException(
				"Please make sure Social Object you are unliking is Likable");
	}
	
	public void onFollowedBy(User user) throws SocialObjectNotFollowableException {
		throw new SocialObjectNotFollowableException(
				"Please make sure Social Object you are liking is Followable");
	}
	
	public void onUnFollowedBy(User user) throws SocialObjectNotFollowableException {
		throw new SocialObjectNotFollowableException(
				"Please make sure Social Object you are unliking is Followable");
	}
	
	public SocialObject onComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException {
		throw new SocialObjectNotCommentableException("Please make sure Social Object you are commenting is Commentable");
	}

	public void onDeleteComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException {
        throw new SocialObjectNotCommentableException("Please make sure Social Object you are deleteing comment is Commentable");
    }
	
	public SocialObject onPost(User user, String title, String body, ProductType type)
			throws SocialObjectNotPostableException {
		throw new SocialObjectNotPostableException(
				"Please make sure Social Object you are posting  is Postable");
	}

	public void onJoinRequest(User user)
			throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException(
				"Please make sure Social Object you are joining  is Joinable");
	}

	public void onJoinRequestAccepted(User toBeMember)
			throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException(
				"Please make sure Social Object you are joining  is Joinable");
	}
	
	public void onInviteRequestAccepted(User toBeMember)
			throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException(
				"Please make sure Social Object you are joining  is Joinable");
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SocialObjectType getObjectType() {
		return objectType;
	}

	public void setObjectType(SocialObjectType objectType) {
		this.objectType = objectType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
