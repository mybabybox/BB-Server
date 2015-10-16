package models;

import java.io.Serializable;
import java.util.List;

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
public class Activity extends domain.Entity implements Serializable, Creatable, Updatable {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	/*To whom this Activity is intended for*/
	@Required
	public Long userId;
	 
	public Long actor;
	
	public String actorName;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType actorType;
	
	public Long target;
	
	public String targetName;

	@Enumerated(EnumType.STRING)
	public SocialObjectType targetType;

	public Boolean viewed = false;
	
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

	public Activity(ActivityType activityType, Long userId, 
			Long actor, String actorName, Long target, String targetName) {
		this.actvityType = activityType;
		this.userId = userId;
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
	
	public static List<Activity> getAllActivities(Long userId, Long offset) {
		// fill in retrieve logic here...
		
		
		// increment notification counter for the recipient
		if (offset == 0) {
			NotificationCounter.readActivitiesCount(userId);
		}
		
		return null;
	}
	
	@Override
	public void postSave() {
		super.postSave();
		
		// increment notification counter for the recipient
		NotificationCounter.incrementActivitiesCount(userId);
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

	public Boolean isViewed() {
		return viewed;
	}

	public void setViewed(Boolean viewed) {
		this.viewed = viewed;
	}

	public ActivityType getActvityType() {
		return actvityType;
	}

	public void setActvityType(ActivityType actvityType) {
		this.actvityType = actvityType;
	}
}
