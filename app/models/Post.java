package models;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import play.db.jpa.JPA;
import babybox.shopping.social.exception.SocialObjectNotCommentableException;
import common.utils.StringUtil;
import domain.Commentable;
import domain.Likeable;
import domain.PostType;
import domain.SocialObjectType;

@Entity
public class Post extends SocialObject implements Likeable, Commentable {

	public String title;

	@Column(length=1000)
	public String body;

	@ManyToOne(cascade = CascadeType.REMOVE)
	public Folder folder;

	@ManyToOne(cascade=CascadeType.REMOVE)
	public Collection collection;

	@ManyToOne
	public Category category;

	@Enumerated(EnumType.STRING)
	public PostType postType;
	
	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	public Set<Comment> comments;

	public Double price = 0.0;

	public int noOfComments = 0;
	public int noOfLikes = 0;
	public int noOfBuys = 0;
	public int noOfViews = 0;
	public int noOfChats = 0;
	public Long baseScore = 0L;

	public boolean mobile = false;
	public boolean android = false;
	public boolean ios = false;

	/**
	 * Ctor
	 */
	public Post() {}

	public Post(User actor, String title, String body, Category category) {
		this.owner = actor;
		this.title = title;
		this.body = body;
		this.category = category;
		this.price = 0.0;
		this.postType = PostType.STORY;
		this.objectType = SocialObjectType.POST;
	}

	public Post(User actor, String title, String description, Category category, Double price) {
		this.owner = actor;
		this.title = title;
		this.body = body;
		this.category = category;
		this.price = price;
		this.postType = PostType.PRODUCT;
		this.objectType = SocialObjectType.POST;
	}

	@Override
	public void onLikedBy(User user) {
		if(!isLikedBy(user)){
			recordLike(user);
			this.noOfLikes++;
			user.numLikes++;
		}
	}

	@Override
	public void onUnlikedBy(User user) {
		if(isLikedBy(user)){
			this.noOfLikes--;
			user.numLikes--;
			Query q = JPA.em().createQuery("Delete from PrimarySocialRelation sa where actor = ?1 and action = ?2 and target = ?3 and actorType = ?4 and targetType = ?5");
			q.setParameter(1, user.id);
			q.setParameter(2, PrimarySocialRelation.Action.LIKED);
			q.setParameter(3, this.id);
			q.setParameter(4, SocialObjectType.USER);
			q.setParameter(5, SocialObjectType.POST);
			q.executeUpdate();
		}
	}
	
	@Override
	public boolean isLikedBy(User user){
		Query q = JPA.em().createQuery("Select sa from PrimarySocialRelation sa where actor = ?1 and action = ?2 and target = ?3 and actorType = ?4 and targetType = ?5");
		q.setParameter(1, user.id);
		q.setParameter(2, PrimarySocialRelation.Action.LIKED);
		q.setParameter(3, this.id);
		q.setParameter(4, SocialObjectType.USER);
		q.setParameter(5, SocialObjectType.POST);
		System.out.println("size :: "+q.getResultList().size());
		if(q.getResultList().size() > 0 ) {
			// Already liked ; Any logic !
			return true;
		}
		return false;
	}

	@Override
	public void save() {
		super.save();

		if (!this.deleted) {
            switch(this.postType) {
            	case PRODUCT: {
                    recordPostProduct(owner);
                    owner.numProducts++;
                    break;
                }
                case STORY: {
                    recordPostStory(owner);
                    owner.numStories++;
                    break;
                }
            }
        } else {
            switch(this.postType) {
                case PRODUCT: {
                    owner.numProducts--;
                    break;
                }
                case STORY: {
                    owner.numStories--;
                    break;
                }
            }
        }
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

	@Override
	public SocialObject onComment(User user, String body) {
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
		
        // record for notifications
        if (this.postType == PostType.PRODUCT) {
            recordCommentProduct(user, comment);
        } else if (this.postType == PostType.STORY) {
            recordCommentStory(user, comment);
        }
        
		return comment;
	}

	@Override
	public void onDeleteComment(User user, String body)
			throws SocialObjectNotCommentableException {
		// TODO Auto-generated method stub
		this.noOfComments--;
	}

	public void onView(User localUser) {
		ViewSocialRelation action = new ViewSocialRelation(localUser, this);
		action.ensureUniqueAndCreate();
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
