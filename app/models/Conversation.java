package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import common.utils.StringUtil;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.AuditListener;
import domain.Creatable;
import domain.DefaultValues;
import domain.Updatable;

/**
 * ALTER TABLE Conversation DROP COLUMN user1ReadDate;
 * ALTER TABLE Conversation CHANGE COLUMN user1_time user1ReadDate datetime;
 * 
 * ALTER TABLE Conversation DROP COLUMN user2ReadDate;
 * ALTER TABLE Conversation CHANGE COLUMN user2_time user2ReadDate datetime;
 * 
 * ALTER TABLE Conversation DROP COLUMN user1ArchiveDate;
 * ALTER TABLE Conversation CHANGE COLUMN user1_archive_time user1ArchiveDate datetime;
 * 
 * ALTER TABLE Conversation DROP COLUMN user2ArchiveDate;
 * ALTER TABLE Conversation CHANGE COLUMN user2_archive_time user2ArchiveDate datetime;
 * 
 * ALTER TABLE Conversation DROP COLUMN user1NumMessages;
 * ALTER TABLE Conversation CHANGE COLUMN user1_noOfMessages user1NumMessages int(11);
 * 
 * ALTER TABLE Conversation DROP COLUMN user2NumMessages;
 * ALTER TABLE Conversation CHANGE COLUMN user2_noOfMessages user2NumMessages int(11);
 * 
 * @author keithlei
 *
 */
@Entity
@EntityListeners(AuditListener.class)
public class Conversation extends domain.Entity implements Serializable, Creatable, Updatable {
    private static final play.api.Logger logger = play.api.Logger.apply(Conversation.class);
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Required
	@ManyToOne
	public User user1;

	@Required
	@ManyToOne
	public User user2;

	@Required
	@ManyToOne
	public Post post;

	public String lastMesage;
	
	public Date user1ReadDate;
	
	public Date user2ReadDate;
	
	public Date user1ArchiveDate;
	
	public Date user2ArchiveDate;
	
	public int user1NumMessages = 0;
	
	public int user2NumMessages = 0;

	public Boolean deleted = false; 
	
	@OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "conversation")
	public Set<Message> messages = new TreeSet<Message>();

	public Conversation() {}
	
	public Conversation(Post post, User user) {
		this.post = post;
		this.user1 = user;
		this.user2 = post.owner;
	}

	public User otherUser(User user) {
	    if (this.user1.equals(user)) {
	    	return user2;
	    } else {
	    	return user1;
	    }
	}
	
	public static Message newMessage(Long conversationId, User sender, String body) {
		Conversation conversation = Conversation.findById(conversationId);
		return conversation.addMessage(sender, body);
	}
	
	public Message addMessage(User sender, String body) {
		Date now = new Date();
		Message message = new Message();
		message.body = body;
		message.sender = sender;
		message.conversation = this;
		message.conversation.lastMesage = trimLastMessage(body);
		message.setCreatedDate(now);
		message.save();
		this.messages.add(message);
		
		this.setUpdatedDate(now);
		if (this.user1 == sender) {
			setReadDate(this.user1);
			this.user2NumMessages++;
			
			// first message, increment conversationsCount for receiver
			if (this.user2NumMessages == 1) {
				NotificationCounter.incrementConversationsCount(this.user2.id);
			}
		} else {
			setReadDate(this.user2);
			this.user1NumMessages++;
			
			// first message, increment conversationsCount for receiver
			if (this.user2NumMessages == 1) {
			    NotificationCounter.incrementConversationsCount(this.user1.id);
			}
		}
		this.save();
		
		NotificationCounter.incrementConversationsCount(message.receiver().id);
		
		return message;
	}
	
	public static Conversation findByUsers(User u1, User u2) {
		Query q = JPA.em().createQuery(
		        "SELECT c from Conversation c where ( ((user1 = ?1 and user2 = ?2) or (user1 = ?2 and user2 = ?1)) ) and c.deleted = 0");
		q.setParameter(1, u1);
		q.setParameter(2, u2);
		
		try {
			return (Conversation) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public static Conversation getConversation(Post post, User user) {
		Query q = JPA.em().createQuery(
		        "SELECT c from Conversation c where user1 = ?1 and user2 = ?2 and post = ?3 and deleted = 0");
		q.setParameter(1, user);
		q.setParameter(2, post.owner);
		q.setParameter(3, post);
		
		try {
			return (Conversation) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Message> getMessages(User user, Long offset) {
		Query q = JPA.em().createQuery(
				"SELECT m from Message m where conversation_id = ?1 and CREATED_DATE > ?2 and m.deleted = 0 order by CREATED_DATE desc ");
		q.setParameter(1, this);

		if (this.user1 == user) {
			setReadDate(user1);
			if(this.user1ArchiveDate == null){
				q.setParameter(2, new Date(0));
			} else {
				q.setParameter(2, this.user1ArchiveDate);
			}
		} else { 
			setReadDate(user2);
			if(this.user2ArchiveDate == null){
				q.setParameter(2, new Date(0));
			} else {
				q.setParameter(2, this.user2ArchiveDate);
			}
		}
		
		try {
			q.setFirstResult((int) (offset * DefaultValues.CONVERSATION_MESSAGE_COUNT));
			q.setMaxResults(DefaultValues.CONVERSATION_MESSAGE_COUNT);
			return (List<Message>) q.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public static List<Conversation> findUserConversations(User user, int latest) {
		Query q = JPA.em().createQuery(
		        "SELECT c from Conversation c where deleted = 0 and (" + 
		        "(user1 = ?1 and (user1ArchiveDate < UPDATED_DATE or user1ArchiveDate is null)) or " + 
		        "(user2 = ?1 and (user2ArchiveDate < UPDATED_DATE or user2ArchiveDate is null)) ) order by UPDATED_DATE desc");
		q.setParameter(1, user);
		
		try {
			return q.setMaxResults(latest).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public static List<Conversation> findPostConversations(Post post, User user, int latest) {
		Query q = JPA.em().createQuery(
				"SELECT c from Conversation c where deleted = 0 and post = ?1 and (" + 
				        "(user1 = ?2 and (user1ArchiveDate < UPDATED_DATE or user1ArchiveDate is null)) or " + 
				        "(user2 = ?2 and (user2ArchiveDate < UPDATED_DATE or user2ArchiveDate is null)) ) order by UPDATED_DATE desc");
		q.setParameter(1, post);
		q.setParameter(2, user);
		
		try {
			return q.setMaxResults(latest).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static Conversation openConversation(Post post, User user) {
		if (post == null || user == null) {
			return null;
		}
		
		Conversation conversation = getConversation(post, user);
		if (conversation != null) {
			return conversation;
		}
		
		Date now = new Date();
		conversation = new Conversation(post, user);
		conversation.setUpdatedDate(now);
		conversation.setReadDate(user);		// New conversation always opened by buyer
		conversation.save();
		return conversation;
	}

	// obsolete, should NEVER use
	public String getLastMessage(User user) {
		Query q = JPA.em().createQuery(
		        "SELECT m FROM Message m WHERE m.CREATED_DATE = (SELECT MAX(CREATED_DATE) FROM Message WHERE conversation_id = ?1 and deleted = 0) and m.CREATED_DATE > ?2 and m.deleted = 0");
        q.setParameter(1, this.id);
        
        if(this.user1 == user){
        	if(this.user1ArchiveDate == null){
        		q.setParameter(2, new Date(0));
        	} else {
        		q.setParameter(2, this.user1ArchiveDate);
        	}
        } else {
        	if(this.user2ArchiveDate == null){
        		q.setParameter(2, new Date(0));
        	} else {
        		q.setParameter(2, this.user2ArchiveDate);
        	}
        }
        
		try{
			Message message = (Message) q.getSingleResult();
			return message.body;
		} catch (NoResultException e) {
		    return "";
		} catch (NonUniqueResultException e) {
			Message message = (Message) q.getResultList().get(0);
			logger.underlyingLogger().error("Duplicate message found for id="+message.id+" in getLastMessage", e);
			return message.body;
		} catch (Exception e){
		    logger.underlyingLogger().error("Error in getLastMessage", e);
			return null;
		}
	}

	public static Conversation findById(Long id) {
		Query q = JPA.em().createQuery("SELECT c FROM Conversation c where id = ?1 and deleted = 0");
        q.setParameter(1, id);
        try {
            return (Conversation) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
	}

	public boolean isReadBy(User user) {
		if (this.user1 == user) {
			return this.user1ReadDate == null || (this.user1ReadDate.getTime() >= this.getUpdatedDate().getTime());
		} else { 
			return this.user2ReadDate == null || (this.user2ReadDate.getTime() >= this.getUpdatedDate().getTime());
		}
	}
	
	public static void archiveConversation(Long id, User user) {
		Conversation conversation = Conversation.findById(id);
		conversation.setArchiveDate(user);
	}
	
	public static Long getUnreadConversationCount(Long userId) {
        Query q = JPA.em().createQuery(
                "Select count(c) from Conversation c where c.deleted = 0 and (" + 
                        "(c.user1.id = ?1 and c.user1NumMessages > 0 and (c.user1ReadDate < UPDATED_DATE or c.user1ReadDate is null)) or " + 
                        "(c.user2.id = ?1 and c.user2NumMessages > 0 and (c.user2ReadDate < UPDATED_DATE or c.user2ReadDate is null)) )");
        q.setParameter(1, userId);
        Long ret = (Long) q.getSingleResult();
        return ret;
    }

	public Long getUnreadCount(User user) {
		if (this.user1 == user) {
			return ((Integer)user1NumMessages).longValue();
		}
		return ((Integer)user2NumMessages).longValue();
    }
	
	public void markDelete() {
		logger.underlyingLogger().debug("[conv="+this.id+"] markDelete");
		
		try {
			Query q = JPA.em().createQuery("update Message set deleted = 1 where conversation_id = ?1");
			q.setParameter(1, this.id);
			q.executeUpdate();
			
			q = JPA.em().createQuery("update Conversation set deleted = 1 where id = ?1");
	        q.setParameter(1, id);
	        q.executeUpdate();
		} catch (Exception e) {
			logger.underlyingLogger().error("Failed to mark delete conversation conv="+this.id, e);
		}
	}
	
	private void setReadDate(User user) {
		logger.underlyingLogger().debug("[conv="+this.id+"][u="+user.id+"] setReadTime");
		
		if (this.user1 == user) {
		    // unread messages, decrement conversationsCount
		    if (this.user1NumMessages > 0) {
		        NotificationCounter.decrementConversationsCount(user1.id);
		    }
		    
            this.user1ReadDate = new Date();
            this.user1NumMessages = 0;
	    } else {
	        // unread messages, decrement conversationsCount
            if (this.user2NumMessages > 0) {
                NotificationCounter.decrementConversationsCount(user2.id);
            }
            
            this.user2ReadDate = new Date();
            this.user2NumMessages = 0;
	    }
	}
	
	private void setArchiveDate(User user){
		logger.underlyingLogger().debug("[conv="+this.id+"][u="+user.id+"] setArchiveTime");
		
		if (this.user1 == user) {
		    // unread messages, decrement conversationsCount
            if (this.user1NumMessages > 0) {
                NotificationCounter.decrementConversationsCount(user1.id);
            }
            
            this.user1ArchiveDate = new Date();
            this.user1NumMessages = 0;
	    } else {
	        // unread messages, decrement conversationsCount
            if (this.user2NumMessages > 0) {
                NotificationCounter.decrementConversationsCount(user2.id);
            }
            
            this.user2ArchiveDate = new Date();
            this.user2NumMessages = 0;
	    }
	    
	    if (isArchivedByBoth()) {
	    	markDelete();
	    }
	}
	
	public boolean isArchivedBy(User user) {
		if (this.messages.isEmpty()) {
			return true;
		}
		
		if (this.user1 == user) {
			return user1ArchiveDate != null && user1ArchiveDate.getTime() >= getUpdatedDate().getTime();	
		} else {
			return user2ArchiveDate != null && user2ArchiveDate.getTime() >= getUpdatedDate().getTime();
		}
	}
	
	public boolean isArchivedByBoth() {
		if (this.messages.isEmpty()) {
			return true;
		}
		
		return isArchivedBy(this.user1) && isArchivedBy(this.user2);
	}
	
	private String trimLastMessage(String message) {
		return StringUtil.shortMessage(message);
	}
	
	private String removeEmoticons(String message) {
		return message.replaceAll("<img([^>]*)>", " ");
	}
}