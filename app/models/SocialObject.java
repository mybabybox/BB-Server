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
import javax.persistence.Query;

import babybox.shopping.social.exception.SocialObjectNotCommentableException;
import babybox.shopping.social.exception.SocialObjectNotFollowableException;
import babybox.shopping.social.exception.SocialObjectNotJoinableException;
import babybox.shopping.social.exception.SocialObjectNotLikableException;
import babybox.shopping.social.exception.SocialObjectNotPostableException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

import com.google.common.base.Objects;

import domain.AuditListener;
import domain.Creatable;
import domain.PostType;
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
    
	protected final void recordLike(User user) {
		PrimarySocialRelation action = new PrimarySocialRelation(user, this);
		action.action = PrimarySocialRelation.Action.LIKED;
		action.validateUniquenessAndCreate();
        // Game Stats
        //GameAccountStatistics.recordLike(user.id);
	}
	
	protected final void recordFollow(User user) {
		SecondarySocialRelation action = new SecondarySocialRelation(user, this);
		action.action = SecondarySocialRelation.Action.FOLLOWED;
		action.validateUniquenessAndCreate();
	}
	
	protected final void recordPostProduct(SocialObject user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.POST_PRODUCT;
		action.save();
        // Game Stats
        //GameAccountStatistics.recordPostProduct(user.id);
	}
	
	protected final void recordPostStory(SocialObject user) {
		SocialRelation action = new SocialRelation(user, this);
		action.action = SocialRelation.Action.POST_STORY;
		action.save();
        // Game Stats
        //GameAccountStatistics.recordPostStory(user.id);
	}

	protected void recordCommentProduct(SocialObject user, Comment comment) {
		SocialRelation action = new SocialRelation(user, comment);
		action.action = SocialRelation.Action.COMMENT_PRODUCT;
        action.save();
	}
	
	protected void recordCommentStory(SocialObject user, Comment comment) {
		SocialRelation action = new SocialRelation(user, comment);
		action.action = SocialRelation.Action.COMMENT_STORY;
        action.save();
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
	
	public SocialObject onComment(User user, String body) throws SocialObjectNotCommentableException {
		throw new SocialObjectNotCommentableException("Please make sure Social Object you are commenting is Commentable");
	}

	public void onDeleteComment(User user, String body) throws SocialObjectNotCommentableException {
        throw new SocialObjectNotCommentableException("Please make sure Social Object you are deleteing comment is Commentable");
    }
	
	public SocialObject onPost(User user, String title, String body, PostType type)
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
