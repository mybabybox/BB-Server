package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.Creatable;
import domain.DefaultValues;
import domain.Updatable;

@Entity
public class Conversation extends domain.Entity implements Serializable, Creatable, Updatable {
    private static final play.api.Logger logger = play.api.Logger.apply(Conversation.class);
    
	public Conversation(){}
	
	public Conversation(User user1, User user2) {
		this.user1 = user1;
		this.user2 = user2;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Required
	@ManyToOne
	public User user1;

	@Required
	@ManyToOne
	public User user2;

	public Date conv_time;
	
	public Date user1_time;
	
	public Date user2_time;
	
	public Date user1_archive_time;
	
	public Date user2_archive_time;
	
	public int user1_noOfMessages = 0;
	
	public int user2_noOfMessages = 0;

	public Boolean deleted = false; 
	
	@OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "conversation")
	public Set<Message> messages = new TreeSet<Message>();

	public Message addMessage(User sender, String body) {
		Date now = new Date();
		Message message = new Message();
		message.body = body;
		message.userFrom = sender;
		message.conversation = this;
		this.messages.add(message);
		message.setCreatedDate(now);
		message.save();
		
		this.setUpdatedDate(now);
		this.conv_time = now;
		if(this.user1 == sender){
			setReadTime(this.user1);
			this.user2_noOfMessages++;
		} else {
			setReadTime(this.user2);
			this.user1_noOfMessages++;
		}
		this.save();
		
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

	public List<Message> getMessages(User user, Long offset) {
		Query q = JPA.em().createQuery(
				"SELECT m from Message m where conversation_id = ?1 and m.date > ?2 and m.deleted = 0 order by m.date desc ");
		q.setParameter(1, this);

		if(this.user1 == user){
			setReadTime(user1);
			if(this.user1_archive_time == null){
				q.setParameter(2, new Date(0));
			} else {
				q.setParameter(2, this.user1_archive_time);
			}
		} else { 
			setReadTime(user2);
			if(this.user2_archive_time == null){
				q.setParameter(2, new Date(0));
			} else {
				q.setParameter(2, this.user2_archive_time);
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
	
	public static List<Conversation> findAllConversations(User user, int latest) {
		Query q = JPA.em().createQuery(
		        "SELECT c from Conversation c where deleted = 0 and (" + 
		        "(user1 = ?1 and (user1_archive_time < conv_time or user1_archive_time is null)) or " + 
		        "(user2 = ?1 and (user2_archive_time < conv_time or user2_archive_time is null)) ) order by updated_date desc");
		q.setParameter(1, user);
		
		try {
			return q.setMaxResults(latest).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static Conversation startConversation(User sender, User receiver) {
		if (sender == null || receiver == null) {
			return null;
		}
		
		Conversation conversation = findByUsers(sender, receiver);
		if (conversation != null) {
			return conversation;
		}
		
		Date now = new Date();
		conversation = new Conversation(sender, receiver);
		conversation.conv_time = now;
		conversation.setUpdatedDate(now);
		conversation.setReadTime(sender);
		conversation.save();
		return conversation;
	}

	public String getLastMessage(User localUser) {
		Query q = JPA.em().createQuery(
		        "SELECT m FROM Message m WHERE m.date = (SELECT MAX(date) FROM Message WHERE conversation_id = ?1 and deleted = 0) and m.date > ?2 and m.deleted = 0");
        q.setParameter(1, this.id);
        
        if(this.user1 == localUser){
        	if(this.user1_archive_time == null){
        		q.setParameter(2, new Date(0));
        	} else {
        		q.setParameter(2, this.user1_archive_time);
        	}
        } else {
        	if(this.user2_archive_time == null){
        		q.setParameter(2, new Date(0));
        	} else {
        		q.setParameter(2, this.user2_archive_time);
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
        return (Conversation) q.getSingleResult();
	}

	public static Message sendMessage(User sender, User receiver, String msgText) {
		Conversation conversation = Conversation.startConversation(sender, receiver);
		return conversation.addMessage(sender, msgText);
	}

	public boolean isReadBy(User user) {
		if (this.user1 == user) {
			return this.user1_time == null || (this.user1_time.getTime() >= this.conv_time.getTime());
		} else { 
			return this.user2_time == null || (this.user2_time.getTime() >= this.conv_time.getTime());
		}
	}
	
	public static void archiveConversation(Long id, User user) {
		Conversation conversation = Conversation.findById(id);
		conversation.setArchiveTime(user);
	}
	
	public static Long getUnreadConversationCount(Long userId) {
        Query q = JPA.em().createQuery(
                "Select count(c) from Conversation c where c.deleted = 0 and (" + 
                        "(c.user1.id = ?1 and c.user1_noOfMessages > 0 and (c.user1_time < c.conv_time or c.user1_time is null)) or " + 
                        "(c.user2.id = ?1 and c.user2_noOfMessages > 0 and (c.user2_time < c.conv_time or c.user2_time is null)) )");
        q.setParameter(1, userId);
        Long ret = (Long) q.getSingleResult();
        return ret;
    }

	public Long getUnreadCount(User user) {
		if (this.user1 == user) {
			return ((Integer)user1_noOfMessages).longValue();
		}
		return ((Integer)user2_noOfMessages).longValue();
		
		/*
        Query q = JPA.em().createQuery("Select count(m) from Message m where m.conversation = ?2 and m.date > ?1 and m.deleted = 0");
        q.setParameter(2, this);
        if(this.user1 == user){
            if(this.user2_archive_time == null){
                q.setParameter(1, new Date(0));
            } else {
                q.setParameter(1, this.user2_archive_time);
            }
        } else {
            if(this.user1_archive_time == null){
                q.setParameter(1, new Date(0));
            } else {
                q.setParameter(1, this.user1_archive_time);
            }
        }
        
        Long ret = (Long) q.getSingleResult();
        return ret;
        */
    }
	
	public void markDelete() {
		logger.underlyingLogger().debug("[conv="+this.id+"] markDelete");
		
		Query q = JPA.em().createQuery("update Message set deleted = 1 where conversation_id = ?1");
		q.setParameter(1, this.id);
		q.executeUpdate();
		q = JPA.em().createQuery("update Conversation set deleted = 1 where id = ?1");
        q.setParameter(1, id);
        q.executeUpdate();
	}
	
	private void setReadTime(User user) {
		logger.underlyingLogger().debug("[conv="+this.id+"][u="+user.id+"] setReadTime");
		
		if(this.user1 == user){
            this.user1_time = new Date();
            this.user1_noOfMessages = 0;
	    } else {
            this.user2_time = new Date();
            this.user2_noOfMessages = 0;
	    }
	}
	
	private void setArchiveTime(User user){
		logger.underlyingLogger().debug("[conv="+this.id+"][u="+user.id+"] setArchiveTime");
		
	    if(this.user1 == user){
            this.user1_archive_time = new Date();
            this.user1_noOfMessages = 0;
	    } else {
            this.user2_archive_time = new Date();
            this.user2_noOfMessages = 0;
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
			return user1_archive_time != null && user1_archive_time.getTime() >= conv_time.getTime();	
		} else {
			return user2_archive_time != null && user2_archive_time.getTime() >= conv_time.getTime();
		}
	}
	
	public boolean isArchivedByBoth() {
		if (this.messages.isEmpty()) {
			return true;
		}
		
		return isArchivedBy(this.user1) && isArchivedBy(this.user2);
	}
}
