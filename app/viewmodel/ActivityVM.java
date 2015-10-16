package viewmodel;

import models.Activity;

public class ActivityVM {
	public Long id;
	public Long target;
	public String targetName;
	public String targetType;
	public String actvityType;
	public Long actor;
	public String actorName;
	public Boolean viewed;
	
	public ActivityVM(){}
	
	public ActivityVM(Activity activity) {
		this.id = activity.id;
		this.target = activity.target;
		this.targetName = activity.targetName;
		this.targetType = activity.targetType.name();
		this.actvityType = activity.actvityType.name();
        this.actor = activity.actor;
        this.actorName = activity.actorName;
        this.viewed = activity.viewed;
	}
}
