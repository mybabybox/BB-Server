package viewmodel;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import domain.DefaultValues;
import models.Comment;
import models.Post;
import models.User;

public class PostVM extends PostVMLite {
	@JsonProperty("createdDate") public Long createdDate;
	@JsonProperty("updatedDate") public Long updatedDate;
	@JsonProperty("ownerNumProducts") public Long ownerNumProducts;
	@JsonProperty("ownerNumFollowers") public Long ownerNumFollowers;
	@JsonProperty("body") public String body;
	@JsonProperty("categoryId") public Long categoryId;
	@JsonProperty("categoryName") public String categoryName;
	@JsonProperty("categoryIcon") public String categoryIcon;
	@JsonProperty("categoryType") public String categoryType;	
    @JsonProperty("latestComments") public List<CommentVM> latestComments;
    
	@JsonProperty("isOwner") public boolean isOwner = false;
	@JsonProperty("isFollowingOwner") public boolean isFollowingOwner = false;
    
	@JsonProperty("deviceType") public String deviceType;
	
    public PostVM(Post post, User user) {
    	super(post, user);
    	
        this.ownerNumProducts = post.owner.numProducts;
        this.ownerNumFollowers = post.owner.numFollowers;
        this.createdDate = post.getCreatedDate().getTime();
        this.updatedDate = post.getUpdatedDate().getTime();
        this.body = post.body;
        this.categoryType = post.category.categoryType.toString();
        this.categoryName = post.category.name;
        this.categoryIcon = post.category.icon;
        this.categoryId = post.category.id;
        
        latestComments = new ArrayList<>();
        for (Comment comment : post.getLatestComments(DefaultValues.LATEST_COMMENTS_COUNT)) {
        	this.latestComments.add(new CommentVM(comment, user));
        }
        
        this.isOwner = (post.owner.id == user.id);
        this.isFollowingOwner = user.isFollowing(post.owner);
        
        this.deviceType = post.deviceType == null? "" : post.deviceType.name();
    }

    public Long getOwnerNumProducts() {
        return ownerNumProducts;
    }

    public void setOwnerNumProducts(Long ownerNumProducts) {
        this.ownerNumProducts = ownerNumProducts;
    }
    
    public Long getOwnerNumFollowers() {
        return ownerNumFollowers;
    }

    public void setOwnerNumFollowers(Long ownerNumFollowers) {
        this.ownerNumFollowers = ownerNumFollowers;
    }
    
    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPostType() {
        return postType;
    }

    public void setType(String postType) {
        this.postType = postType;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    
    public List<CommentVM> getLatestComments() {
		return latestComments;
	}

	public void setLatestComments(List<CommentVM> latestComments) {
		this.latestComments = latestComments;
	}

	public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }
    
    public boolean isFollowingOwner() {
        return isFollowingOwner;
    }

    public void setIsFollowingOwner(boolean isFollowingOwner) {
        this.isFollowingOwner = isFollowingOwner;
    }
}