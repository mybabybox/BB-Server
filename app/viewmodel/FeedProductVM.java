package viewmodel;

import models.Product;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class FeedProductVM {
	@JsonProperty("id") public Long productId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String ownerName;
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public long postedOn;
	@JsonProperty("imgs") public Long images;
    @JsonProperty("ptyp") public String productType;
	@JsonProperty("pn") public String productName;
	@JsonProperty("pp") public Long productPrize;
	@JsonProperty("pd") public String productDescription;
	@JsonProperty("hasImage") public Boolean hasImage;
	@JsonProperty("isLike") public Boolean isLiked;
	@JsonProperty("nol") public Integer noOfLikes;
	
	public FeedProductVM(){
		
	}

	public FeedProductVM(Product product) {
		this.ownerId = product.owner.id;
		this.ownerName = product.owner.name;
		this.productId = product.id;
		this.productName = product.title;
		this.productDescription = product.description;
		this.productPrize = product.productPrize;
		this.noOfLikes = product.noOfLikes;
		if(product.folder != null && !CollectionUtils.isEmpty(product.folder.resources)) {
		    this.hasImage = true;
			this.images = product.folder.resources.get(0).getId();
		}
		
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
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
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Long getProductPrize() {
		return productPrize;
	}

	public void setProductPrize(Long productPrize) {
		this.productPrize = productPrize;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
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