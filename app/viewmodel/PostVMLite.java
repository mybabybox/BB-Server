package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

import common.cache.CalcServer;

import models.Folder;
import models.Post;
import models.User;

public class PostVMLite {
	@JsonProperty("id") public Long id;
	@JsonProperty("title") public String title;
	@JsonProperty("price") public double price;
	@JsonProperty("sold") public boolean sold;
	@JsonProperty("postType") public String postType;
	@JsonProperty("images") public Long[] images;
	@JsonProperty("hasImage") public Boolean hasImage;
	
	@JsonProperty("numLikes") public int numLikes;
	@JsonProperty("numChats") public int numChats;
	@JsonProperty("numBuys") public int numBuys;
	@JsonProperty("numComments") public int numComments;
	@JsonProperty("numViews") public int numViews;
	@JsonProperty("isLiked") public boolean isLiked = false;
    
	@JsonProperty("offset") public Long offset;
	
	// admin fields
	@JsonProperty("baseScore") public Long baseScore = 0L;
	@JsonProperty("timeScore") public Long timeScore = 0L;

    public PostVMLite(Post post, User user) {
        this.id = post.id;
        this.title = post.title;
        this.price = post.price;
        this.sold = post.sold;
        this.postType = post.postType.toString();
        
        this.numLikes = post.numLikes;
        this.numChats = post.numChats;
        this.numBuys = post.numBuys;
        this.numComments = post.numComments;
        this.numViews = post.numViews;
        
        this.isLiked = post.isLikedBy(user);
        
        Long[] images = Folder.getResources(post.folder);
        if (images != null && images.length > 0) {
        	this.hasImage = true;
        	this.images = images;
        }
        
        if (user.isSuperAdmin()) {
	        this.baseScore = post.baseScore;
	        this.timeScore = CalcServer.calculateTimeScore(post);
        }
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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