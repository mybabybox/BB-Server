package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * 
 */
@Entity
public class Setting extends domain.Entity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Required
	public Long userId;
	
	@Required
    @ManyToOne
	public NotificationSetting notificationSetting;
	
	public Setting() {
	}
	
	public static Setting findById(Long id) {
        try {
            Query q = JPA.em().createQuery("SELECT s FROM Setting s where id = ?1");
            q.setParameter(1, id);
            return (Setting) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
	
	@Transactional
	public static Setting findByUserId(Long userId) {
        try {
            Query q = JPA.em().createQuery("SELECT s FROM Setting s where userId = ?1");
            q.setParameter(1, userId);
            return (Setting) q.getSingleResult();
        } catch (NoResultException nre) {
            NotificationSetting notificationSetting = new NotificationSetting();
            notificationSetting.save();

            Setting setting = new Setting();
            setting.userId = userId;
            setting.notificationSetting = notificationSetting;
            setting.save();
            return setting;
        }
    }
}
