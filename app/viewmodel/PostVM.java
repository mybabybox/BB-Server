package viewmodel;

import models.Post;
import models.User;

public class PostVM extends PostVMLite {
    public Long ownerId;
    public String postedBy;
    public long createdDate;
    public long updatedDate;
    public String body;
    public String categoryType;
    public String categoryName;
    public String categoryIcon;
    public Long categoryId;
    
    public boolean isOwner = false;
    public boolean isFollowingOwner = false;
    
    public boolean mobile = false;
    public boolean android = false;
    public boolean ios = false;

    public PostVM(Post post, User user) {
    	super(post);
        this.ownerId = post.owner.id;
        this.postedBy = post.getCreatedBy();
        this.createdDate = post.getCreatedDate().getTime();
        this.updatedDate = post.getUpdatedDate().getTime();
        this.body = post.body;
        this.categoryType = post.category.categoryType.toString();
        this.categoryName = post.category.name;
        this.categoryIcon = post.category.icon;
        this.categoryId = post.category.id;

        this.isOwner = (post.owner.id == user.id);
        this.isFollowingOwner = user.isFollowing(post.owner);
        
        this.mobile = post.mobile;
        this.android = post.android;
        this.ios = post.ios;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
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