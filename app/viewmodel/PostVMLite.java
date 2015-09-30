package viewmodel;

import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;

import models.Post;
import models.Resource;

public class PostVMLite {
    public Long id;
    public String title;
    public double price;
    public boolean sold;
    //public Long[] images;
	public ArrayList<Long> images = new ArrayList<Long>(20);
    public String type;
    public int numLikes;
    public int numChats;
    public int numBuys;
    public int numComments;
    public int numViews;
    public boolean isOwner = false;
    public boolean isLiked = false;
    
    public Long offset;

	public Boolean hasImage;
    public Long image;
    
    public PostVMLite(Post post) {
        this.id = post.id;
        this.title = post.title;
        this.price = post.price;
        this.sold = false; //TODO
        this.numComments = post.noOfComments;
        this.type = post.category.name;
        this.numViews = post.noOfViews;
        this.numLikes = post.noOfLikes;
        this.numBuys = post.noOfBuys;
        this.numComments = post.noOfComments;
        this.numChats = post.noOfChats;
        this.isOwner = false; //TODO
        this.isLiked = post.isLikedBy(post.owner);
        
		if (post.folder != null && !CollectionUtils.isEmpty(post.folder.resources)) {
		    this.hasImage = true;
			this.image = post.folder.resources.get(0).getId();
			for (Resource resource : post.folder.resources) {
				this.images.add(resource.getId());
			}
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

/*    public ArrayList<Long> getImages() {
        return images;
    }

    public void setImages(Long[] imgs) {
        this.images = imgs;
    }*/

    public String getType() {
        return type;
    }

    public ArrayList<Long> getImages() {
		return images;
	}
	public void setImages(ArrayList<Long> images) {
		this.images = images;
	}
	public Boolean getHasImage() {
		return hasImage;
	}
	public void setHasImage(Boolean hasImage) {
		this.hasImage = hasImage;
	}
	public Long getImage() {
		return image;
	}
	public void setImage(Long image) {
		this.image = image;
	}
	public void setType(String type) {
        this.type = type;
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

    public boolean isOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
}