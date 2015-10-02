package models;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import common.utils.StringUtil;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.Creatable;
import domain.Likeable;
import domain.PostType;
import domain.SocialObjectType;

/**
 * A Comment by an User on a SocialObject (Post only)
 *
 */
@Entity
public class Comment extends SocialObject implements Comparable<Comment>, Likeable, Serializable, Creatable {
    private static final play.api.Logger logger = play.api.Logger.apply(Comment.class);

    @Required
    public Long socialObject;       // e.g. Post Id

    @Required
    @Column(length=255)
    public String body;

    @Enumerated(EnumType.STRING)
	public PostType commentType;
    
    public int noOfLikes = 0;

    @ManyToOne(cascade = CascadeType.REMOVE)
  	public Folder folder;

	public boolean mobile = false;
	public boolean android = false;
	public boolean ios = false;
	
    /**
     * Ctor
     */
    public Comment() {}
    
    public Comment(SocialObject socialObject, User user, String body) {
        this.owner = user;
        this.socialObject = socialObject.id;
        this.body = body;
    }

    public static Comment findById(Long id) {
        Query q = JPA.em().createQuery("SELECT c FROM Comment c where id = ?1 and deleted = false");
        q.setParameter(1, id);
        return (Comment) q.getSingleResult();
    }

    
    @Override
    public void onLikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][cmt="+id+"] Comment onLikedBy");
        }

        recordLike(user);
        this.noOfLikes++;
        user.numLikes++;
    }
    
    @Override
    public void onUnlikedBy(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.id+"][cmt="+id+"] Comment onUnlikedBy");
        }

        this.noOfLikes--;
        user.numLikes--;
    }

    @Override
    public int compareTo(Comment o) {
        return this.getCreatedDate().compareTo(o.getCreatedDate());
    }
    
    @Override
    public void save() {
        super.save();
    }
    
    public void delete(User deletedBy) {
        Post post = Post.findById(this.socialObject);
        post.noOfComments--;
        this.deleted = true;
        this.deletedBy = deletedBy;
        save();
    }

    public Resource addCommentPhoto(File source) throws IOException {
		ensureAlbumExist();
		Resource cover_photo = this.folder.addFile(source,
				SocialObjectType.COMMENT_PHOTO);
		
		return cover_photo;
    }
  
    public void ensureAlbumExist() {
		if (this.folder == null) {
			this.folder = Folder.createAlbum(this.owner, "comment-ps", "", true);
			this.merge();
		}
	}

    public Post getProduct() {
      	Query q = JPA.em().createNativeQuery("SELECT product_id FROM product_comment where comments_id = "+this.id);
      	BigInteger integer = (BigInteger) q.getSingleResult();
        Long id = integer.longValue();
        Post product = Post.findById(id);
        return product;
    }

    public String getShortenedBody() {
        if (body == null || body.startsWith("http")) {
            return "";
        } else {
            return StringUtil.truncateWithDots(body, 12);
        }
    }
}