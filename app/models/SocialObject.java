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

import babybox.shopping.social.exception.SocialObjectNotCommentableException;
import babybox.shopping.social.exception.SocialObjectNotFollowableException;
import babybox.shopping.social.exception.SocialObjectNotLikableException;
import babybox.shopping.social.exception.SocialObjectNotPostableException;
import models.Post.PostType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.Constraints.Required;

import com.google.common.base.Objects;

import domain.AuditListener;
import domain.Commentable;
import domain.Creatable;
import domain.SocialObjectType;
import domain.Updatable;

//@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditListener.class)
@MappedSuperclass
public abstract class SocialObject extends domain.Entity implements Serializable, Creatable, Updatable, Commentable {

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
	
	public boolean isLikedBy(User user) {
		return LikeSocialRelation.isLiked(user.id, user.objectType, this.id, this.objectType);
    }
    
	protected final boolean recordLike(User user) {
		LikeSocialRelation action = new LikeSocialRelation(user, this);
		return action.ensureUniqueAndCreate();
		// Game Stats
		//GameAccountStatistics.recordLike(user.id);
	}
	
	protected final boolean recordFollow(User user) {
		FollowSocialRelation action = new FollowSocialRelation(this, user);
		return action.ensureUniqueAndCreate();
	}
	
	protected final void recordPostProduct(SocialObject user, SocialObject post) {
		PostSocialRelation action = new PostSocialRelation(user, post);
		action.actionType = PostSocialRelation.ActionType.PRODUCT;
		action.save();
		// Game Stats
		//GameAccountStatistics.recordPostProduct(user.id);
	}
	
	protected final void recordPostStory(SocialObject user, SocialObject post) {
		PostSocialRelation action = new PostSocialRelation(user, post);
		action.actionType = PostSocialRelation.ActionType.STORY;
		action.save();
		// Game Stats
		//GameAccountStatistics.recordPostStory(user.id);
	}

	protected void recordCommentProduct(SocialObject user, Comment comment) {
		CommentSocialRelation action = new CommentSocialRelation(user, comment);
		action.actionType = CommentSocialRelation.ActionType.PRODUCT;
		action.save();
	}
	
	protected void recordCommentStory(SocialObject user, Comment comment) {
		CommentSocialRelation action = new CommentSocialRelation(user, comment);
		action.actionType = CommentSocialRelation.ActionType.STORY;
		action.save();
	}
	
	protected final boolean recordView(User user) {
		ViewSocialRelation action = new ViewSocialRelation(user, this);
		return action.ensureUniqueAndCreate();
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

	public boolean onLikedBy(User user) throws SocialObjectNotLikableException {
		throw new SocialObjectNotLikableException(
				"Please make sure Social Object you are liking is Likable");
	}
	
	public boolean onUnlikedBy(User user) throws SocialObjectNotLikableException {
		throw new SocialObjectNotLikableException(
				"Please make sure Social Object you are unliking is Likable");
	}
	
	public boolean onFollowedBy(User user) throws SocialObjectNotFollowableException {
		throw new SocialObjectNotFollowableException(
				"Please make sure Social Object you are liking is Followable");
	}
	
	public boolean onUnFollowedBy(User user) throws SocialObjectNotFollowableException {
		throw new SocialObjectNotFollowableException(
				"Please make sure Social Object you are unliking is Followable");
	}
	
	public SocialObject onComment(User user, String body) throws SocialObjectNotCommentableException {
		throw new SocialObjectNotCommentableException("Please make sure Social Object you are commenting is Commentable");
	}

	public void onDeleteComment(User user, Comment comment) throws SocialObjectNotCommentableException {
        throw new SocialObjectNotCommentableException("Please make sure Social Object you are deleteing comment is Commentable");
    }
	
	public SocialObject onPost(User user, String title, String body, PostType type)
			throws SocialObjectNotPostableException {
		throw new SocialObjectNotPostableException(
				"Please make sure Social Object you are posting  is Postable");
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
