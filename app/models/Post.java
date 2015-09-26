package models;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import play.db.jpa.JPA;
import babybox.shopping.social.exception.SocialObjectNotCommentableException;
import common.cache.CalServer;
import common.thread.ThreadLocalOverride;
import common.utils.StringUtil;
import domain.Commentable;
import domain.Likeable;
import domain.PostType;
import domain.SocialObjectType;

@Entity
public class Post extends SocialObject implements Likeable, Commentable {

	public String title;

	@Column(length=2000)
	public String description;

	@ManyToOne(cascade = CascadeType.REMOVE)
	public Folder folder;

	@ManyToOne(cascade=CascadeType.REMOVE)
	public Collection collection;

	@ManyToOne
	public Category category;

	@ManyToOne
	public User socialUpdatedBy;
	public Date socialUpdatedDate = new Date();

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	public Set<Comment> comments;

	public String tagWords;     // comma separated list

	public Long postPrize = 0L;

	public int noOfComments = 0;
	public int noOfLikes = 0;
	public int noOfBuys = 0;
	public int noOfViews = 0;
	public int noOfChats = 0;
	public Long baseScore = 0L;

	public boolean mobile = false;
	public boolean android = false;
	public boolean ios = false;

	public PostType postType;



	/**
	 * Ctor
	 */
	public Post() {}

	/**
	 * Ctor
	 * @param actor
	 * @param title
	 * @param description
	 * @param community
	 */
	public Post(User actor, String title, String description) {
		this.owner = actor;
		this.title = title;
		this.description = description;
		this.objectType = SocialObjectType.POST;
	}

	public Post(User actor, String post) {
		this(actor, null, post);
	}

	public Post(User actor, String title, String description,
			Category category) {
		this.owner = actor;
		this.title = title;
		this.description = description;
		this.category = category;
		this.objectType = SocialObjectType.POST;
	}

	public Post(User actor, String title, String description,
			Category category, Long postPrize) {
		this.owner = actor;
		this.title = title;
		this.description = description;
		this.category = category;
		this.postPrize = postPrize;
//		this.productType = productType;
	}

	@Override
	public void onLikedBy(User user) {
		recordLike(user);
		this.noOfLikes++;
		user.likesCount++;
	}

	@Override
	public void onUnlikedBy(User user) {
		this.noOfLikes--;
		user.likesCount--;
		Query q = JPA.em().createQuery("Delete from PrimarySocialRelation sa where actor = ?1 and action = ?2 and target = ?3 and actorType = ?4 and targetType = ?5");
		q.setParameter(1, user.id);
		q.setParameter(2, PrimarySocialRelation.Action.LIKED);
		q.setParameter(3, this.id);
		q.setParameter(4, SocialObjectType.USER);
		q.setParameter(5, SocialObjectType.USER);
		q.executeUpdate();
	}
	
	@Override
	public boolean isLikedBy(User user){
		return CalServer.isLiked(user.id, this.id);
	}

	@Override
	public void save() {
		super.save();

		if (this.socialUpdatedBy == null) {
			this.socialUpdatedBy = this.owner;
		}
		Date override = ThreadLocalOverride.getSocialUpdatedDate();
		this.socialUpdatedDate = (override == null) ? new Date() : override;

		// push to / remove from community
		/* if (!this.deleted) {
            switch(this.postType) {
                case SIMPLE: {
                    recordPost(owner);
                    owner.postsCount++;
                    break;
                }
                case QUESTION: {
                    recordQnA(owner);
                    owner.questionsCount++;
                    break;
                }
            }
        } else {
            switch(this.postType) {
                case SIMPLE: {
                    owner.postsCount--;
                    break;
                }
                case QUESTION: {
                    owner.questionsCount--;
                    break;
                }
            }
        }*/
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public void delete(User deletedBy) {
		this.deleted = true;
		this.deletedBy = deletedBy;
		save();
	}


	public Resource addPostPhoto(File source) throws IOException {
		ensureAlbumExist();
		Resource post_photo = this.folder.addFile(source,
				SocialObjectType.POST_PHOTO);
		System.out.println("cover photo :: "+post_photo.resourceName);
		post_photo.save();
		return post_photo;
	}

	public void ensureAlbumExist() {
		if (this.folder == null) {
			this.folder = Folder.createAlbum(this.owner, "post-ps", "", true);
			this.merge();
		}
	}

	///////////////////// Query APIs /////////////////////
	public static Post findById(Long id) {
		try {
			Query q = JPA.em().createQuery("SELECT p FROM Post p where id = ?1 and deleted = false");
			q.setParameter(1, id);
			return (Post) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public static List<Post> getAllPosts() {
		try {
			Query q = JPA.em().createQuery("SELECT p FROM Post p where deleted = false");
			return (List<Post>) q.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public static List<Post> getUserPosts(Long id) {
		try {
			Query q = JPA.em().createQuery("SELECT p FROM Post p where owner = ? and deleted = false");
			q.setParameter(1, User.findById(id));
			return (List<Post>) q.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

	///////////////////// Getters /////////////////////
	public String getBody() {
		return description;
	}

	public Folder getFolder() {
		return folder;
	}

	public Date getSocialUpdatedDate() {
		return socialUpdatedDate;
	}

	@Override
	public SocialObject onComment(User user, String body)
	{
		// update last socialUpdatedDate in Post
		this.socialUpdatedBy = user;
		Date override = ThreadLocalOverride.getSocialUpdatedDate();
		this.socialUpdatedDate = (override == null) ? new Date() : override;

		// create Comment object
		Comment comment = new Comment(this, user, body);
		comment.objectType = SocialObjectType.COMMENT;
		comment.save();

		// merge into Post
		if (comments == null) {
			comments = new HashSet<>();
		}
		this.comments.add(comment);
		this.noOfComments++;
		JPA.em().merge(this);
		recordCommentOnPost(user, comment);
		return comment;
	}

	@Override
	public void onDeleteComment(User user, String body)
			throws SocialObjectNotCommentableException {
		// TODO Auto-generated method stub
		this.noOfComments--;
	}

	public void onView(User localUser) {
		ViewMapping mapping = new ViewMapping();
		mapping.postId = this.id;
		mapping.userId = localUser.id;
		mapping.viewedDate = new Date();
		mapping.save();
	}

	public static List<Post> getPostsByCategory(Category category) {
		try {
			Query q = JPA.em().createQuery("SELECT p FROM Post p where category = ? and deleted = false");
			q.setParameter(1,category);
			return (List<Post>) q.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public static List<Post> getPosts(List<Long> postIds) {
		try {
			 Query query = JPA.em().createQuery(
			            "select p from Post p where "+
			            "p.id in ("+StringUtil.collectionToString(postIds, ",")+") and "+
			            "p.deleted = false ORDER BY FIELD(p.id,"+StringUtil.collectionToString(postIds, ",")+")");
			 return (List<Post>) query.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

}
