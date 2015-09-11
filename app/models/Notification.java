package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.joda.time.DateTime;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import domain.AuditListener;
import domain.Creatable;
import domain.SocialObjectType;
import domain.Updatable;

@Entity
@EntityListeners(AuditListener.class)
public class Notification  extends domain.Entity implements Serializable, Creatable, Updatable  {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	/*To whom this notification is intended for*/
	@Required
	public Long recipient;
	 
	@Required
	public String message;
	
	public String URLs;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType targetType;
	
	public Long target;
	
	public long socialActionID;
	
	public String usersName;
	
	public Long count = 0L;

    // 0=unread, 1=read, 2=ignored, 3=accepted
	public int status = 0;
	
	@Enumerated(EnumType.STRING)
	public NotificationType notificationType;

    public static enum Status {
        Unread, Read, Ignored, Accepted
    }

	public static enum NotificationType {
        FRD_REQUEST,
        FRD_ACCEPTED,
		NEW_MESSAGE,
        COMM_JOIN_REQUEST,
        COMM_JOIN_APPROVED,
        COMM_INVITE_REQUEST,
		COMMENT,
		POSTED,
		LIKED,
		ANSWERED,
        QUESTIONED,
        WANTED_ANS,
        CAMPAIGN
	}


    public static List<Notification> getAllNotification(Long userId) {
        Query q = JPA.em().createQuery(
                "SELECT n from Notification n where recipient = ?1 and notificationType in (?2,?3,?4,?6,?7,?8,?9) and" +
                " CREATED_DATE > ?5 ORDER BY UPDATED_DATE desc");
        q.setParameter(1, userId);
        q.setParameter(2, NotificationType.COMMENT);
        q.setParameter(3, NotificationType.ANSWERED);
        q.setParameter(4, NotificationType.LIKED);
        q.setParameter(6, NotificationType.POSTED);
        q.setParameter(7, NotificationType.QUESTIONED);
        q.setParameter(8, NotificationType.WANTED_ANS);
        q.setParameter(9, NotificationType.CAMPAIGN);
        // subtract 1 month
        DateTime before = (new DateTime()).minusMonths(1);
        q.setParameter(5, before.toDate());

        return q.getResultList();
    }

    public static List<Notification> getAllRequestNotification(Long userId) {
        Query q = JPA.em().createQuery(
                "SELECT n from Notification n where recipient = ?1 and ( " +
                "( notificationType in (?2,?3,?5) and n.status in(?6,?8) ) or " +
                "( notificationType in (?4,?7) and CREATED_DATE > ?9)" +
                ") ORDER BY CREATED_DATE desc ");
        q.setParameter(1, userId);
        q.setParameter(2, NotificationType.COMM_JOIN_REQUEST);
        q.setParameter(3, NotificationType.COMM_INVITE_REQUEST);
        q.setParameter(4, NotificationType.COMM_JOIN_APPROVED);
        q.setParameter(5, NotificationType.FRD_REQUEST);
        q.setParameter(7, NotificationType.FRD_ACCEPTED);
        q.setParameter(6, Status.Unread.ordinal());
        q.setParameter(8, Status.Read.ordinal());
        // subtract 1 month
        DateTime before = (new DateTime()).minusMonths(1);
        q.setParameter(9, before.toDate());

        return q.getResultList();
    }

    /**
     * Find unique, unread Notification.
     * @param recipient
     * @param notificationType
     * @param target
     * @param targetType
     * @return
     */
    public static Notification getNotification(Long recipient, NotificationType notificationType,
                                               Long target, SocialObjectType targetType) {
		String sql = "SELECT n FROM Notification n WHERE recipient=?1 and notificationType=?2 and target=?3 and targetType=?4 and status=?5";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, recipient);
        query.setParameter(2, notificationType);
        query.setParameter(3, target);
        query.setParameter(4, targetType);
        query.setParameter(5, Status.Unread.ordinal());
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        	return null;
        }
	}

	public String addToList(User addUser) {
        String addUserName = addUser.displayName;
        if (addUserName == null) {
            addUserName = "User";
        }

		if (this.usersName == null) {
            this.usersName = addUserName;
            this.count++;
		} else {
			if(this.usersName.toLowerCase().contains(addUserName.toLowerCase())){
				return this.usersName;
			}

            this.count++;

            int lastDelimIdx = this.usersName != null ? this.usersName.lastIndexOf(",") : -1;
			if(count >= 3 && lastDelimIdx != -1){
                long othersCount = count - 2;
				this.usersName = addUserName+", "+this.usersName.substring(0,lastDelimIdx)+" 與另外"+othersCount+"人";
			} else {
				this.usersName = addUserName+", "+this.usersName;
            }
		}
        return this.usersName;
	}

	public void changeStatus(int status) {
		this.status = status;
	    save();
	}

	public String getUsersName() {
		return usersName;
	}

	public void setUsersName(String usersName) {
		this.usersName = usersName;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getRecipient() {
		return recipient;
	}

	public void setRecipient(Long recipient) {
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}
	
    public static Notification findById(Long id) {
        String sql = "SELECT n FROM Notification n WHERE id=?1";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, id);
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }
    
    public static Notification findBySocialAction(SocialRelation sr) {
        String sql = "SELECT n FROM Notification n WHERE SocialAction_id=?1";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, sr);
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }
    
    public static Notification findBySocialActionID(Long id) {
        String sql = "SELECT n FROM Notification n WHERE socialActionID=?1";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, id);
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }

	public static void markAsRead(String ids) {
		String[] idsLong = ids.split(",");
		List<Long> data = new ArrayList<>(); 
		for (int i = 0; i < idsLong.length; i++) {     
		    data.add(Long.parseLong(idsLong[i]));     
		}  
		 Query query = JPA.em().createQuery("update Notification n set n.status = ?1, n.count = ?4 where n.id in ?3 and n.status = ?2");
		 query.setParameter(1, Status.Read.ordinal());
		 query.setParameter(2, Status.Unread.ordinal());
		 query.setParameter(3, data);
		 query.setParameter(4, 0L);
		 query.executeUpdate();
	}

    /**
     * Purge the READ notifications more than 14 days old, and everything > 2 months old.
     */
	@Transactional
	public static void purgeNotification() {
        DateTime sevenDaysBefore = (new DateTime()).minusDays(14);
        Query query = JPA.em().createQuery("DELETE Notification n where n.status = ?1 and CREATED_DATE < ?2");
        query.setParameter(1, Status.Read.ordinal());
        query.setParameter(2, sevenDaysBefore.toDate());
        query.executeUpdate();

        DateTime twoMonthsBefore = (new DateTime()).minusMonths(2);
        query = JPA.em().createQuery("DELETE Notification n where CREATED_DATE < ?1");
        query.setParameter(1, twoMonthsBefore.toDate());
        query.executeUpdate();
	}
}
