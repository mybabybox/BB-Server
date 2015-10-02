package viewmodel;

import java.util.ArrayList;

import models.Post;
import models.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class FeedPostVM1 {
	@JsonProperty("id") public Long postId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String ownerName;
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public long postedOn;
	@JsonProperty("imgs") public Long images;
    @JsonProperty("ptyp") public String postType;
	@JsonProperty("pn") public String postName;
	@JsonProperty("pp") public Long postPrice;
	@JsonProperty("pd") public String postBody;
	@JsonProperty("hasImage") public Boolean hasImage;
	@JsonProperty("isLike") public Boolean isLiked;
	@JsonProperty("nol") public Integer noOfLikes;
	
	public ArrayList<Long> imageArray = new ArrayList<Long>(20);
	
	public FeedPostVM1(){
		
	}

	public FeedPostVM1(Post post) {
		this.ownerId = post.owner.id;
		this.ownerName = post.owner.name;
		this.postId = post.id;
		this.postName = post.title;
		this.postBody = post.body;
		this.postPrice = post.price.longValue();
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

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
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

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public Long getPostPrice() {
		return postPrice;
	}

	public void setPostPrice(Long postPrice) {
		this.postPrice = postPrice;
	}

	public String getPostBody() {
		return postBody;
	}

	public void setPostBody(String postBody) {
		this.postBody = postBody;
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