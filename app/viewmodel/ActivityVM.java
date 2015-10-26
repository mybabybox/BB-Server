package viewmodel;

import models.Activity;

public class ActivityVM {
	public Long id;
	public String activityType;
	public Long actor;
	public String actorName;
    public String actorType;
	public Long target;
    public String targetName;
    public String targetType;
	public Boolean viewed;
	
	public ActivityVM(){}
	
	public ActivityVM(Activity activity) {
		this.id = activity.id;
		this.activityType = activity.activityType.name();
        this.actor = activity.actor;
        this.actorName = activity.actorName;
        this.actorType = activity.actorType.name();
        this.target = activity.target;
        this.targetName = activity.targetName;
        this.targetType = activity.targetType.name();
        this.viewed = activity.viewed;
	}
}
