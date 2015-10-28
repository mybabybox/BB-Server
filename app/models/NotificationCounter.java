package models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.AuditListener;
import domain.Creatable;
import domain.Updatable;

@Entity
@EntityListeners(AuditListener.class)
public class NotificationCounter extends domain.Entity implements Serializable, Creatable, Updatable {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@Column(unique=true)
	@Required
	public Long userId;
	 
	public Long activitiesCount = 0L;
	
	public Long conversationsCount = 0L;
		
	public Boolean deleted = false;
	
	public NotificationCounter() {
    }
	
	public NotificationCounter(Long userId) {
	    this.userId = userId;
	}
	
	public static NotificationCounter getNotificationCounter(Long userId) {
		Query q = JPA.em().createQuery("SELECT c from NotificationCounter c where userId = ?1 and deleted = 0");
		q.setParameter(1, userId);
		
		try {
            return (NotificationCounter) q.getSingleResult();
        } catch (NoResultException e) {
            return new NotificationCounter(userId);
        }
	}
	
	public static void resetActivitiesCount(Long userId) {
		NotificationCounter counter = getNotificationCounter(userId);
		if (counter != null && counter.activitiesCount != 0L) {
			counter.activitiesCount = 0L;
			counter.save();
		}
	}
	
	public static void resetConversationsCount(Long userId) {
        NotificationCounter counter = getNotificationCounter(userId);
        if (counter != null && counter.conversationsCount != 0L) {
            counter.conversationsCount = 0L;
            counter.save();
        }
    }
	
	public static void setActivitiesCount(Long userId, Long count) {
        NotificationCounter counter = getNotificationCounter(userId);
        if (counter != null && counter.activitiesCount != count) {
            counter.activitiesCount = count;
            counter.save();
        }
    }
    
	public static void setConversationsCount(Long userId, Long count) {
        NotificationCounter counter = getNotificationCounter(userId);
        if (counter != null && counter.conversationsCount != count) {
            counter.conversationsCount = count;
            counter.save();
        }
    }
	
	public static void incrementActivitiesCount(Long userId) {
		NotificationCounter counter = NotificationCounter.getNotificationCounter(userId);
		if (counter != null) {
			counter.activitiesCount++;
			counter.save();
		}
	}
	
	public static void decrementActivitiesCount(Long userId) {
		NotificationCounter counter = NotificationCounter.getNotificationCounter(userId);
		if (counter != null && counter.activitiesCount > 0) {
			counter.activitiesCount--;
			counter.save();
		}
	}
	
	public static void incrementConversationsCount(Long userId) {
		NotificationCounter counter = NotificationCounter.getNotificationCounter(userId);
		if (counter != null) {
			counter.conversationsCount++;
			counter.save();
		}
	}
	
	public static void decrementConversationsCount(Long userId) {
		NotificationCounter counter = NotificationCounter.getNotificationCounter(userId);
		if (counter != null && counter.conversationsCount > 0) {
			counter.conversationsCount--;
			counter.save();
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
