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
public class Activity  extends domain.Entity implements Serializable, Creatable, Updatable  {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	/*To whom this Activity is intended for*/
	@Required
	public Long recipient;
	 
	//@Required
	public String message;
	
	public String URLs;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType targetType;
	
	public Long target;
	
	public long actor;
	
	public String usersName;

	@Enumerated(EnumType.STRING)
	public ActivityType actvityType;

    public static enum Status {
        Unread, Read, Ignored, Accepted
    }

	public static enum ActivityType {
		NEW_MESSAGE,
		COMMENT,
		POSTED,
		LIKED,
		FOLLOWED,
		SOLD,
		VIEWED,
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getURLs() {
		return URLs;
	}

	public void setURLs(String uRLs) {
		URLs = uRLs;
	}

	public SocialObjectType getTargetType() {
		return targetType;
	}

	public void setTargetType(SocialObjectType targetType) {
		this.targetType = targetType;
	}

	public Long getTarget() {
		return target;
	}

	public void setTarget(Long target) {
		this.target = target;
	}

	public long getActor() {
		return actor;
	}

	public void setActor(long actor) {
		this.actor = actor;
	}

	public String getUsersName() {
		return usersName;
	}

	public void setUsersName(String usersName) {
		this.usersName = usersName;
	}

	public ActivityType getActvityType() {
		return actvityType;
	}

	public void setActvityType(ActivityType actvityType) {
		this.actvityType = actvityType;
	}

}
