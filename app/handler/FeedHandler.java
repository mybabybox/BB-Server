package handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import models.Post;
import models.User;
import viewmodel.PostVMLite;
import common.cache.CalcServer;
import common.cache.JedisCache;
import common.model.FeedFilter.FeedType;

public class FeedHandler {
	
    private static boolean filterSold = false;
    
	public static List<PostVMLite> getPostVM(Long id, Long offset,
			User localUser, FeedType feedType, JedisCache jedisCache) {
	    
	    filterSold = false;
	    
		List<Long> postIds = new ArrayList<>();
		switch (feedType) {
			case HOME_EXPLORE:
				postIds = CalcServer.getHomeExploreFeed(id, offset.doubleValue(), jedisCache);
				break;
				
			case HOME_FOLLOWING:
				postIds = CalcServer.getHomeFollowingFeed(id, offset.doubleValue(), jedisCache);
				filterSold = true;
				break;
		
			case CATEGORY_POPULAR:
				postIds = CalcServer.getCategoryPopularFeed(id, offset.doubleValue(), jedisCache);
				break;
				
			case CATEGORY_NEWEST:
				postIds = CalcServer.getCategoryNewestFeed(id, offset.doubleValue(), jedisCache);
				break;
				
			case CATEGORY_PRICE_HIGH_LOW:
				postIds = CalcServer.getCategoryPriceHighLowFeed(id, offset.doubleValue(), jedisCache);
				break;
				
			case CATEGORY_PRICE_LOW_HIGH:
				postIds = CalcServer.getCategoryPriceLowHighFeed(id, offset.doubleValue(), jedisCache);
				break;
			
			case USER_LIKED:
				postIds = CalcServer.getUserLikedFeeds(id, offset.doubleValue(), jedisCache);
				break;
			
			case USER_POSTED:
				postIds = CalcServer.getUserPostedFeeds(id, offset.doubleValue(), jedisCache);
				break;
				
			case PRODUCT_SUGGEST:
				postIds = CalcServer.getSuggestedProducts(id, jedisCache);
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
		    
			PostVMLite vm = new PostVMLite(post, localUser, jedisCache);
			//TODO: offset is bad name , need to change it to proper name.
			vm.offset = CalcServer.getScore(CalcServer.getKey(feedType, id), post.id, jedisCache).longValue();
			vms.add(vm);
		}
		return vms;
	}
}
