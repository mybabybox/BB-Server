package viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.Comment;
import models.User;

public class CommentVM {
	@JsonProperty("id") public Long id;
	@JsonProperty("createdDate") public Long createdDate;
	@JsonProperty("ownerId") public Long ownerId;
	@JsonProperty("ownerName") public String ownerName;
	@JsonProperty("body") public String body;

	@JsonProperty("isOwner") public boolean isOwner = false;

	@JsonProperty("deviceType") public String deviceType;
	
    public CommentVM(Comment comment, User user) {
        this.id = comment.id;
        this.ownerId = comment.owner.id;
        this.ownerName = comment.owner.displayName;
        this.createdDate = comment.getCreatedDate().getTime();
        this.body = comment.body;
        this.isOwner = (comment.owner.id == user.id);
        this.deviceType = comment.deviceType == null? "" : comment.deviceType.name();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }
}