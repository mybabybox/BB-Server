package viewmodel;

import java.util.ArrayList;

import models.Post;
import models.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class FeedPostVM {
	@JsonProperty("id") public Long postId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String ownerName;
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public long postedOn;
	@JsonProperty("imgs") public Long images;
    @JsonProperty("ptyp") public String postType;
	@JsonProperty("pn") public String postName;
	@JsonProperty("pp") public Long postPrize;
	@JsonProperty("pd") public String postDescription;
	@JsonProperty("hasImage") public Boolean hasImage;
	@JsonProperty("isLike") public Boolean isLiked;
	@JsonProperty("nol") public Integer noOfLikes;
	
	public ArrayList<Long> imageArray = new ArrayList<Long>(20);
	
	public FeedPostVM(){
		
	}

	public FeedPostVM(Post post) {
		this.ownerId = post.owner.id;
		this.ownerName = post.owner.name;
		this.postId = post.id;
		this.postName = post.title;
		this.postDescription = post.description;
		this.postPrize = post.postPrize;
		this.noOfLikes = post.noOfLikes;
		
		if(post.folder != null && !CollectionUtils.isEmpty(post.folder.resources)) {
		    this.hasImage = true;
			this.images = post.folder.resources.get(0).getId();
			for (Resource resource : post.folder.resources) {
				this.imageArray.add(resource.getId());
			}
				
		}
		
	}

	public ArrayList<Long> getImageArray() {
		return imageArray;
	}


	public Long getProductId() {
		return postId;
	}

	public void setProductId(Long postId) {
		this.postId = postId;
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

	public long getPostedOn() {
		return postedOn;
	}

	public void setPostedOn(long postedOn) {
		this.postedOn = postedOn;
	}

	public Long getImages() {
		return images;
	}

	public void setImages(Long images) {
		this.images = images;
	}

	public String getProductType() {
		return postType;
	}

	public void setProductType(String postType) {
		this.postType = postType;
	}

	public String getProductName() {
		return postName;
	}

	public void setProductName(String postName) {
		this.postName = postName;
	}

	public Long getProductPrice() {
		return postPrize;
	}

	public void setProductPrice(Long postPrice) {
		this.postPrize = postPrice;
	}

	public String getProductDescription() {
		return postDescription;
	}

	public void setProductDescription(String postDescription) {
		this.postDescription = postDescription;
	}

	public boolean getIsHasImage() {
		return hasImage;
	}

	public void setIsHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
}