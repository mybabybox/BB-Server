package viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.Post;
import models.User;

public class PostVMLite {
	@JsonProperty("id") public Long id;
	@JsonProperty("ownerId") public Long ownerId;
    @JsonProperty("ownerName") public String ownerName;
    
	@JsonProperty("title") public String title;
	@JsonProperty("price") public double price;
	@JsonProperty("sold") public boolean sold;
	@JsonProperty("postType") public String postType;
	@JsonProperty("conditionType") public String conditionType;
	@JsonProperty("images") public Long[] images;
	@JsonProperty("hasImage") public Boolean hasImage = false;
	
	@JsonProperty("numLikes") public int numLikes;
	@JsonProperty("numChats") public int numChats;
	@JsonProperty("numBuys") public int numBuys;
	@JsonProperty("numComments") public int numComments;
	@JsonProperty("numViews") public int numViews;
	@JsonProperty("isLiked") public boolean isLiked = false;
    
	@JsonProperty("offset") public Long offset;
	
	// admin fields
	@JsonProperty("baseScore") public Long baseScore = 0L;
	@JsonProperty("timeScore") public Double timeScore = 0D;

    public PostVMLite(Post post, User user) {
        this.id = post.id;
        this.ownerId = post.owner.id;
        this.ownerName = post.owner.displayName;
        this.title = post.title;
        this.price = post.price;
        this.sold = post.sold;
        this.postType = post.postType.toString();
        if (post.conditionType != null) {
            this.conditionType = post.conditionType.toString();
        }
        
        this.numLikes = post.numLikes;
        this.numChats = post.numChats;
        this.numBuys = post.numBuys;
        this.numComments = post.numComments;
        this.numViews = post.numViews;
        
        this.isLiked = post.isLikedBy(user);
        
        Long[] images = post.getImages();
        if (images != null && images.length > 0) {
        	this.hasImage = true;
        	this.images = images;
        }
        
        if (user.isSuperAdmin()) {
	        this.baseScore = post.baseScore;
	        this.timeScore = post.timeScore;
        }
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
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean getSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public int getNumChats() {
        return numChats;
    }

    public void setNumChats(int numChats) {
        this.numChats = numChats;
    }

    public int getNumBuys() {
        return numBuys;
    }

    public void setNumBuys(int numBuys) {
        this.numBuys = numBuys;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public String getPostType() {
        return postType;
    }
    
    public void setPostType(String postType) {
        this.postType = postType;
    }
    
    public String getConditionType() {
        return conditionType;
    }
    
    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public Long[] getImages() {
		return images;
	}
	
    public void setImages(Long[] images) {
		this.images = images;
	}
	
	public Boolean getHasImage() {
		return hasImage;
	}
	
	public void setHasImage(Boolean hasImage) {
		this.hasImage = hasImage;
	}
	
    public int getNumViews() {
        return numViews;
    }

    public void setNumViews(int numViews) {
        this.numViews = numViews;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
}