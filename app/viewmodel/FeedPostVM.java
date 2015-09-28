package viewmodel;

import java.util.ArrayList;

import models.Post;
import models.Resource;

import org.apache.commons.collections.CollectionUtils;

public class FeedPostVM {
	public Long id;
	public Long ownerId;
	public String ownerName;
	public String postedBy;
	public long postedOn;
    public String type;
    public Long image;
	public String title;
	public Long price;
	public Boolean hasImage;
	public Boolean isLiked = false;
	public Integer noOfLikes;
	
    public Boolean sold = false;
    public int numLikes;
    public int numChats;
    public int numBuys;
    public int numComments;
    public int numViews;
    public boolean isOwner = false;
	
	public ArrayList<Long> images = new ArrayList<Long>(20);
	
	public FeedPostVM(){
		
	}

	public FeedPostVM(Post post) {
		this.ownerId = post.owner.id;
		this.ownerName = post.owner.name;
		this.id = post.id;
		this.postedBy = post.getCreatedBy();
		this.postedOn = post.getCreatedDate().getTime();
		this.type = post.category.name;
		this.title = post.title;
		this.price = post.price;
		this.noOfLikes = post.noOfLikes;
		this.isLiked = post.isLikedBy(post.owner);
		
		this.sold = false; //TODO need to change 
		
		this.numLikes = post.noOfLikes;
		this.numChats = post.noOfChats;
		this.numBuys = post.noOfBuys;
		this.numComments = post.noOfComments;
		this.numViews = post.noOfViews;
		
		this.isOwner = false; //TODO need to change
		
		if(post.folder != null && !CollectionUtils.isEmpty(post.folder.resources)) {
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

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	public long getPostedOn() {
		return postedOn;
	}

	public void setPostedOn(long postedOn) {
		this.postedOn = postedOn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getImage() {
		return image;
	}

	public void setImage(Long image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Boolean getHasImage() {
		return hasImage;
	}

	public void setHasImage(Boolean hasImage) {
		this.hasImage = hasImage;
	}

	public Boolean getIsLiked() {
		return isLiked;
	}

	public void setIsLiked(Boolean isLiked) {
		this.isLiked = isLiked;
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

	public Boolean getSold() {
		return sold;
	}
	
	public void setSold(Boolean sold) {
		this.sold = sold;
	}

	public int getNumLikes() {
		return numLikes;
	}

	public void setNumLikes(int numLikes) {
		this.numLikes = numLikes;
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

	public int getNumViews() {
		return numViews;
	}

	public void setNumViews(int numViews) {
		this.numViews = numViews;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public ArrayList<Long> getImages() {
		return images;
	}

	public void setImages(ArrayList<Long> images) {
		this.images = images;
	}
	
}