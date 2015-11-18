package handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import models.Post;
import models.User;
import viewmodel.PostVMLite;

import com.google.inject.Singleton;
import common.cache.CalcServer;
import common.model.FeedFilter.FeedType;

@Singleton
public class FeedHandler {
	
	@Inject
	CalcServer calcServer;

    private static boolean filterSold = false;
    
	public List<PostVMLite> getPostVM(Long id, Long offset,
			User localUser, FeedType feedType) {
	    
	    filterSold = false;
		List<Long> postIds = new ArrayList<>();
		switch (feedType) {
			case HOME_EXPLORE:
				postIds = calcServer.getHomeExploreFeed(id, offset.doubleValue());
				break;
				
			case HOME_FOLLOWING:
				postIds = calcServer.getHomeFollowingFeed(id, offset.doubleValue());
				filterSold = true;
				break;
		
			case CATEGORY_POPULAR:
				postIds = calcServer.getCategoryPopularFeed(id, offset.doubleValue());
				break;
				
			case CATEGORY_NEWEST:
				postIds = calcServer.getCategoryNewestFeed(id, offset.doubleValue());
				break;
				
			case CATEGORY_PRICE_HIGH_LOW:
				postIds = calcServer.getCategoryPriceHighLowFeed(id, offset.doubleValue());
				break;
				
			case CATEGORY_PRICE_LOW_HIGH:
				postIds = calcServer.getCategoryPriceLowHighFeed(id, offset.doubleValue());
				break;
			
			case USER_LIKED:
				postIds = calcServer.getUserLikedFeeds(id, offset.doubleValue());
				break;
			
			case USER_POSTED:
				postIds = calcServer.getUserPostedFeeds(id, offset.doubleValue());
				break;
				
			case PRODUCT_SUGGEST:
				postIds = calcServer.getSuggestedProducts(id);
				break;
			
			
			default:
				break;
			
		}
		
		List<PostVMLite> vms = new ArrayList<>();
		if(postIds.size() == 0){
			return vms;
		}
		
		List<Post> posts =  Post.getPosts(postIds);
		for (Post post : posts) {
		    if (filterSold && post.sold) {
		        continue;
		    }
		    
			PostVMLite vm = new PostVMLite(post, localUser);
			//TODO: offset is bad name , need to change it to proper name.
			vm.offset = calcServer.getScore(calcServer.getKey(feedType, id), post.id).longValue();
			vms.add(vm);
		}
		return vms;
	}
}
