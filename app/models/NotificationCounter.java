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
	 
	public Long activities = 0L;
	
	public Long conversations = 0L;
		
	public Boolean deleted = false;
	
	public NotificationCounter() {
	}
	
	public static void readActivityCounter(Long userId) {
		NotificationCounter counter = getNotificationCounter(userId);
		if (counter != null) {
			counter.activities = 0L;
		}
	}
	
	public static void readConversationCounter(Long userId) {
		NotificationCounter counter = getNotificationCounter(userId);
		if (counter != null) {
			counter.conversations = 0L;
		}
	}
	
	public static NotificationCounter getNotificationCounter(Long userId) {
		Query q = JPA.em().createQuery("SELECT c from NotificationCounter c where userId = ?1 and deleted = 0");
		q.setParameter(1, userId);
		
		try {
			return (NotificationCounter)q.getResultList();
		} catch (NoResultException e) {
			return new NotificationCounter();
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
