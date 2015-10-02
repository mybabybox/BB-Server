package viewmodel;

import models.Comment;

public class CommentVM {
    public Long id;
    public Long ownerId;
    public String postedBy;
    public Long createdDate;
    public boolean hasImage = false;
    public Long[] images;
    public String body;

    public boolean isOwner = false;
    public boolean isLike = false;     // filled outside

    public boolean mobile = false;
    public boolean android = false;
    public boolean ios = false;

    // helper state
    public boolean imageLoaded = false;

    public CommentVM(Comment comment) {
        this.id = comment.id;
        this.ownerId = comment.owner.id;
        this.postedBy = comment.getCreatedBy();
        this.createdDate = comment.getCreatedDate().getTime();
        this.body = comment.body;
        
        this.mobile = comment.mobile;
        this.android = comment.android;
        this.ios = comment.ios;
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

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Long[] getImages() {
        return images;
    }

    public void setImages(Long[] images) {
        this.images = images;
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

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }
}