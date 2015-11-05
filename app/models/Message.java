package models;

import java.io.File;
import java.io.IOException;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import domain.SocialObjectType;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@Entity
public class Message extends SocialObject implements Comparable<Message> {
	/*
	@Id
	public Long id;*/
	
	@Required
	@ManyToOne
	public Conversation conversation;

	@Required
	@ManyToOne
	public User sender;
	  
	@Column(length=1000)
	public String body;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
    public Folder folder;
	
	public User receiver() {
		return conversation.otherUser(sender);
	}

	public Long getImage() {
        Long[] images = getImages();
        if (images != null && images.length > 0) {
            return images[0];
        }
        return null;
    }
    
    public Long[] getImages() {
        return Folder.getResources(folder);
    }
    
	@Override
	public int compareTo(Message o) {
		 return this.getUpdatedDate().compareTo(o.getUpdatedDate());
	}

	public static Message findById(Long id) {
	    try {
            Query q = JPA.em().createQuery("SELECT m FROM Message m where id = ?1 and deleted = 0");
            q.setParameter(1, id);
            return (Message) q.getSingleResult();
	    } catch (NoResultException e) {
            return null;
        }
	}
	
	public Resource addMessagePhoto(File source, User owner) throws IOException {
		ensureAlbumExist(owner);
		Resource photo = this.folder.addFile(source, SocialObjectType.MESSAGE_PHOTO);
		photo.save();
		
		conversation.lastMessageHasImage = true;
		conversation.save();
		
		return photo;
	}
	    
    public void ensureAlbumExist(User owner) {
        if (this.folder == null) {
            this.folder = Folder.createFolder(owner, "message-ps", "", true);
            this.merge();
        }
    }
}