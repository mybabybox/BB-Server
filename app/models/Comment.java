package models;

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
import controllers.Application.DeviceType;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.Creatable;
import domain.Likeable;
import domain.PostType;

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
    
    @ManyToOne(cascade = CascadeType.REMOVE)
  	public Folder folder;

    public DeviceType deviceType;
	
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
    public int compareTo(Comment o) {
        return this.getCreatedDate().compareTo(o.getCreatedDate());
    }
    
    @Override
    public void save() {
        super.save();
    }
    
    public void delete(User deletedBy) {
        Post post = Post.findById(this.socialObject);
        post.comments.remove(this);
        post.numComments--;
        this.deleted = true;
        this.deletedBy = deletedBy;
        save();
    }

    public void ensureAlbumExist() {
		if (this.folder == null) {
			this.folder = Folder.createFolder(this.owner, "comment-ps", "", true);
			this.merge();
		}
	}

    public Post getPost() {
      	Query q = JPA.em().createNativeQuery("SELECT post_id FROM post_comment where comments_id = "+this.id);
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