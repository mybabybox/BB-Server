package viewmodel;

import models.Post;

public class PostVM extends PostVMLite {
    public Long ownerId;
    public String postedBy;
    public long createdDate;
    public long updatedDate;
    public String desc;
    public String categoryType;
    public String categoryName;
    public String categoryIcon;
    public Long categoryId;
    public boolean isFollowingOwner;
    
    public boolean mobile = false;
    public boolean android = false;
    public boolean ios = false;

    public PostVM(Post post) {
    	super(post);
        this.ownerId = post.owner.id;
        this.postedBy = post.getCreatedBy();
        this.createdDate = post.getCreatedDate().getTime();
        this.updatedDate = post.getUpdatedDate().getTime();
        this.desc = post.description;
        this.categoryType = post.category.name; //TODO
        this.categoryName = post.category.name;
        this.categoryIcon = post.category.name;//TODO
        this.categoryId = post.category.id;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}