package viewmodel;

import java.util.ArrayList;
import java.util.List;

import models.Product;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import controllers.Application;

public class ProductInfoVM extends FeedProductVM{

	@JsonProperty("ifu")  public boolean isFollowdByUser = false;
	@JsonProperty("relatedProducts")  public List<ReleatedProductVM> relatedProducts = new ArrayList<>();
	
	public ProductInfoVM(){
		
	}

	public ProductInfoVM(Product product, User localUser) {
		super(product);
		this.isLiked = product.isLikedBy(localUser);
		this.isFollowdByUser = product.owner.isFollowedBy(localUser);
		int i = 0;
		for(Product pro : Product.getUserProducts(product.owner.id)) {
			relatedProducts.add(new ReleatedProductVM(pro));
			if(i == 3)
				break;
			i++;
		}
	}
	
	 
}

class ReleatedProductVM{
	
	@JsonProperty("id") public Long id;
	@JsonProperty("img") public Long imageId;
	
	public ReleatedProductVM(Product pro){
		this.id = pro.id;
		if(pro.folder != null && !CollectionUtils.isEmpty(pro.folder.resources)) {
		    this.imageId = pro.folder.resources.get(0).getId();
		}
		
		
	}
}