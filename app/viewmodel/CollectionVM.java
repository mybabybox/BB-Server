package viewmodel;

import java.util.ArrayList;
import java.util.List;

import models.Collection;
import models.Product;
import models.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class CollectionVM {
	@JsonProperty("id") public Long collectionId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String ownerName;
	@JsonProperty("cn") public String collectionName;
	@JsonProperty("products") public List<Long> productImages = new ArrayList<>();
	
	public CollectionVM(){
		
	}

	public CollectionVM(Collection collection) {
		this.ownerId = collection.owner.id;
		this.ownerName = collection.owner.name;
		this.collectionId = collection.id;
		this.collectionName = collection.name;
		for(Product product : collection.products){
			if(product.folder != null && !CollectionUtils.isEmpty(product.folder.resources)) {
				this.productImages.add(product.folder.resources.get(0).getId());
			}
		}
		
	}

	public Long getProductId() {
		return collectionId;
	}

	public void setProductId(Long productId) {
		this.collectionId = productId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Long collectionId) {
		this.collectionId = collectionId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public List<Long> getProductImages() {
		return productImages;
	}

	public void setProductImages(List<Long> productImages) {
		this.productImages = productImages;
	}

}