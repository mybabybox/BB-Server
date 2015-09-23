package viewmodel;

import java.util.ArrayList;
import java.util.List;

import models.Collection;
import models.Post;
import models.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class CollectionVM {
	public Long id;
	public Long ownerId;
	public String ownerName;
	public String collectionName;
	public List<Long> productImages = new ArrayList<>();
	
	
	//new added from android babybox
	public String name;
	public String desc;
	public Boolean system;
	public String type;
	public Long seq;
	
	
	public CollectionVM(){
		
	}

	public CollectionVM(Collection collection) {
		this.ownerId = collection.owner.id;
		this.ownerName = collection.owner.name;
		this.id = collection.id;
		this.collectionName = collection.name;
		for(Post product : collection.products){
			if(product.folder != null && !CollectionUtils.isEmpty(product.folder.resources)) {
				this.productImages.add(product.folder.resources.get(0).getId());
			}
		}
		
	}

	public Long getProductId() {
		return id;
	}

	public void setProductId(Long productId) {
		this.id = productId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getCollectionId() {
		return id;
	}

	public void setCollectionId(Long collectionId) {
		this.id = collectionId;
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