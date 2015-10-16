package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
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
	 
	public Long actor;
	
	public String actorName;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType actorType;
	
	public Long target;
	
	public String targetName;

	@Enumerated(EnumType.STRING)
	public SocialObjectType targetType;

	public Boolean read = false;
	
	public Boolean deleted = false;
	
	@Enumerated(EnumType.STRING)
	public ActivityType actvityType;

	public static enum ActivityType {
		NEW_POST,
		NEW_COMMENT,
		LIKED,
		FOLLOWED,
		SOLD
	}

	public Activity(ActivityType activityType, Long recipient, 
			Long actor, String actorName, Long target, String targetName) {
		this.actvityType = activityType;
		this.recipient = recipient;
		this.actor = actor;
		this.actorName = actorName;
		this.target = target;
		this.targetName = targetName;
		setActorTargetType();
	}
	
	private void setActorTargetType() {
		switch (this.actvityType) {
		case NEW_POST:
			this.actorType = SocialObjectType.USER;
			this.targetType = SocialObjectType.POST;
			break;
		case NEW_COMMENT:
			this.actorType = SocialObjectType.USER;
			this.targetType = SocialObjectType.COMMENT;
			break;
		case LIKED:
			this.actorType = SocialObjectType.USER;
			this.targetType = SocialObjectType.POST;
			break;
		case FOLLOWED:
			this.actorType = SocialObjectType.USER;
			this.targetType = SocialObjectType.USER;
			break;
		case SOLD:
			this.actorType = SocialObjectType.USER;
			this.targetType = SocialObjectType.POST;
			break;
		}
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

	public String getActorName() {
		return actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public SocialObjectType getActorType() {
		return actorType;
	}

	public void setActorType(SocialObjectType actorType) {
		this.actorType = actorType;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Boolean isRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public ActivityType getActvityType() {
		return actvityType;
	}

	public void setActvityType(ActivityType actvityType) {
		this.actvityType = actvityType;
	}
}