package viewmodel;

import models.Activity;

public class ActivityVM {
	public Long id;
	public Long createdDate;
	public String activityType;
	public Long actor;
	public Long actorImage;
	public String actorName;
    public String actorType;
	public Long target;
	public Long targetImage;
    public String targetName;
    public String targetType;
	public Boolean viewed;
	
	public ActivityVM(){}
	
	public ActivityVM(Activity activity) {
		this.id = activity.id;
		this.createdDate = activity.getCreatedDate().getTime();
		this.activityType = activity.activityType.name();
        this.actor = activity.actor;
        this.actorImage = activity.actorImage;
        this.actorName = activity.actorName;
        this.actorType = activity.actorType.name();
        this.target = activity.target;
        this.targetImage = activity.targetImage;
        this.targetName = activity.targetName;
        this.targetType = activity.targetType.name();
        this.viewed = activity.viewed;
	}
}
