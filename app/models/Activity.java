package models;

import java.io.Serializable;
import java.util.Date;
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

import play.Play;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import domain.AuditListener;
import domain.Creatable;
import domain.DefaultValues;
import domain.SocialObjectType;
import domain.Updatable;

@Entity
@EntityListeners(AuditListener.class)
public class Activity  extends domain.Entity implements Serializable, Creatable, Updatable {

	private static final play.api.Logger logger = play.api.Logger.apply(Activity.class);

	private static final int ACTIVITY_VIEWED_CLEANUP_DAYS = Play.application().configuration().getInt("activity.viewed.cleanup.days");
	private static final int ACTIVITY_ALL_CLEANUP_DAYS = Play.application().configuration().getInt("activity.all.cleanup.days");

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

	public Activity(){}

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

	@Override
	public void postSave() {
		super.postSave();

		// increment notification counter for the recipient
		NotificationCounter.incrementActivitiesCount(userId);
	}

	public static void purgeActivity() {
		DateTime ninetyDaysBefore = (new DateTime()).minusDays(ACTIVITY_VIEWED_CLEANUP_DAYS);
		Query query = JPA.em().createQuery("DELETE Activity a where a.viewed = ?1 and CREATED_DATE < ?2");
		query.setParameter(1, true);
		query.setParameter(2, ninetyDaysBefore.toDate());
		query.executeUpdate();

		DateTime oneEightyDaysBefore = (new DateTime()).minusDays(ACTIVITY_ALL_CLEANUP_DAYS);
		query = JPA.em().createQuery("DELETE Activity a where CREATED_DATE < ?1");
		query.setParameter(1, oneEightyDaysBefore);
		query.executeUpdate();
	}

	@Transactional
	public boolean ensureUniqueAndCreate() {
		Activity activity = getActivity();
		System.out.println(activity);
		if (activity == null) {
			System.out.println("save");
			this.save();
			return true;
		} else {
			System.out.println("merge");
			activity.setCreatedDate(new Date());
			activity.merge();
			return false;
		}
	}

	public Activity getActivity() {
		Query q = JPA.em().createQuery(
				"Select a from Activity a where actor = ?1 and actorType = ?2 and target = ?3 and targetType = ?4 and userId = ?5 ");
		q.setParameter(1, this.actor);
		q.setParameter(2, this.actorType);
		q.setParameter(3, this.target);
		q.setParameter(4, this.targetType);
		q.setParameter(5, this.userId);
		System.out.println("this.actor :: "+this.actor);
		System.out.println("this.actorType :: "+this.actorType);
		System.out.println("this.target :: "+this.target);
		System.out.println("this.targetType :: "+this.targetType);
		System.out.println("this.userId :: "+this.userId);
		try {
			Activity activity = (Activity) q.getSingleResult();
			return activity;
		} catch (NoResultException nre){
		}
		return null;
	}

	public static Activity findById(Long id) {
		try { 
			Query q = JPA.em().createQuery("SELECT a FROM Activity a where id = ?1 and deleted = false");
			q.setParameter(1, id);
			return (Activity) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			logger.underlyingLogger().error("Error in findById", e);
			return null;
		}
	}

	public static List<Activity> getAllActivities(Long userId, Long offset) {
		// increment notification counter for the recipient
		if (offset == 0) {
			NotificationCounter.readActivitiesCount(userId);
		}

		Query q = JPA.em().createQuery("SELECT a FROM Activity a where userId = ? and deleted = false");
		q.setFirstResult((int) (offset * DefaultValues.ACTIVITY_COUNT));
		q.setMaxResults(DefaultValues.ACTIVITY_COUNT);
		q.setParameter(1, userId);
		try {
			return (List<Activity>) q.getResultList();
		} catch (NoResultException nre) {
			return null;
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
