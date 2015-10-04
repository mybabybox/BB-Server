package models;

import java.io.File;
import java.io.IOException;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
	    if(this.conversation.user1.equals(sender)){
	      return conversation.user2;
	    } else {
	      return conversation.user1;
	    }
	}

	@Override
	public int compareTo(Message o) {
		 return this.getUpdatedDate().compareTo(o.getUpdatedDate());
	}

	public static Message findById(Long id) {
		 Query q = JPA.em().createQuery("SELECT m FROM Message m where id = ?1 and deleted = 0");
	     q.setParameter(1, id);
	     return (Message) q.getSingleResult();
	}
	
	 public Resource addPrivatePhoto(File source,User owner) throws IOException {
	        ensureAlbumExist(owner);
	        Resource cover_photo = this.folder.addFile(source,
	                SocialObjectType.PRIVATE_PHOTO);
	        cover_photo.save();
	        return cover_photo;
	    }
	    
    public void ensureAlbumExist(User owner) {
        if (this.folder == null) {
            this.folder = Folder.createAlbum(owner, "message-ps", "", true);
            this.merge();
        }
    }
}