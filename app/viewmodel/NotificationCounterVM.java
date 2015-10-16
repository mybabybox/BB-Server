package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import models.NotificationCounter;

public class NotificationCounterVM {
	@JsonProperty("id") public Long id;
	@JsonProperty("userId") public Long userId;
	@JsonProperty("activities") public Long activities;
	@JsonProperty("conversations") public Long conversations;

    public NotificationCounterVM(NotificationCounter counter) {
        this.id = counter.id;
        this.userId = counter.userId;
        this.activities = counter.activities;
        this.conversations = counter.conversations;
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

	public Long getActivities() {
		return activities;
	}

	public void setActivities(Long activities) {
		this.activities = activities;
	}

	public Long getConversations() {
		return conversations;
	}

	public void setConversations(Long conversations) {
		this.conversations = conversations;
	}
}