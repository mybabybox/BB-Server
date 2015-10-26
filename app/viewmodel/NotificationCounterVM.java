package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import models.NotificationCounter;

public class NotificationCounterVM {
	@JsonProperty("id") public Long id;
	@JsonProperty("userId") public Long userId;
	@JsonProperty("activitiesCount") public Long activitiesCount;
	@JsonProperty("conversationsCount") public Long conversationsCount;

    public NotificationCounterVM(NotificationCounter counter) {
        this.id = counter.id;
        this.userId = counter.userId;
        this.activitiesCount = counter.activitiesCount;
        this.conversationsCount = counter.conversationsCount;
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

	public Long getActivitiesCount() {
		return activitiesCount;
	}

	public void setActivitiesCount(Long activitiesCount) {
		this.activitiesCount = activitiesCount;
	}

	public Long getConversationsCount() {
		return conversationsCount;
	}

	public void setConversationsCount(Long conversationsCount) {
		this.conversationsCount = conversationsCount;
	}
}