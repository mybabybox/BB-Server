package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;

/**
 * 
 */
@Entity
public class NotificationSetting extends domain.Entity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	public Boolean emailNewPost = true;
	
	public Boolean emailNewConversation = true;
	
	public Boolean emailNewComment = false;
	
	public Boolean emailNewPromotions = true;
	
	public Boolean pushNewConversation = true;
	
	public Boolean pushNewComment = true;
	
	public Boolean pushNewFollow = true;
	
	public Boolean pushNewFeedback = true;
	
	public Boolean pushNewPromotions = true;
	
	public NotificationSetting() {
	}
	
	public static NotificationSetting findById(Long id) {
        try {
            Query q = JPA.em().createQuery("SELECT s FROM NotificationSetting s where id = ?1");
            q.setParameter(1, id);
            return (NotificationSetting) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
