package common.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.Category;
import models.Post;
import models.User;

public class CalcServer {

	public static void warmUpActivity() {
		buildBaseScore();
		buildCategoryQueue();
		buildUserQueue();
		
	}

	private static void buildUserQueue() {
		for(User user : User.getEligibleUserForFeed()){
			bulidUserPostedQueue(user);
			bulidUserLikedPostQueue(user);
		}
	}

	private static void bulidUserPostedQueue(User user) {
		for(Post post : user.getUserPosts()){
			JedisCache.cache().putToSet("USER_POSTS:"+user.id, post.id.toString());
		}
	}

	private static void bulidUserLikedPostQueue(User user) {
		for(Post post : user.getUserLikedPosts()){
			JedisCache.cache().putToSet("USER_LIKES:"+user.id, post.id.toString());
		}
	}

	private static void buildBaseScore() {
		for(Post product : Post.getAllPosts()){
			calculateBaseScore(product);
		}
	}

	private static void calculateBaseScore(Post product) {
		product.baseScore = (long) (product.noOfViews
				+ 2 * product.noOfLikes
				+ 3 * product.noOfChats
				+ 4 *product.noOfBuys
				+ 5 * product.noOfComments);
		product.save();
	}

	private static void buildCategoryQueue() {
		for(Category category : Category.getAllCategory()){
			for(Post post : Post.getPostsByCategory(category)){
				buildPrizeHighLowPostQueue(post);
				buildNewestPostQueue(post);
				buildPopularPostQueue(post);
			}
		}
	}

	private static void buildPopularPostQueue(Post post) {
		JedisCache.cache().putToSortedSet("CATEGORY_POPULAR:"+post.category.id, post.baseScore , post.id.toString());
	}

	private static void buildNewestPostQueue(Post post) {
		JedisCache.cache().putToSortedSet("CATEGORY_NEWEST:"+post.category.id, post.getCreatedDate().getTime() , post.id.toString());
	}

	private static void buildPrizeHighLowPostQueue(Post post) {
		JedisCache.cache().putToSortedSet("CATEGORY_PRICE_LOW_HIGH:"+post.category.id, post.price*1000000 + post.id , post.id.toString());
	}

	public static boolean isLiked(Long userId, Long postId) {
		 String key = "USER_LIKES"+userId;
	     return JedisCache.cache().isMemberOfSet(key, postId.toString());
	}

	public static List<Long> getCategoryPopularFeed(Long id, Double offset) {
		Set<String> values = JedisCache.cache().getSortedSetDsc("CATEGORY_POPULAR:"+id, offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;
	}
	
	public static List<Long> getCategoryNewestFeed(Long id, Double offset) {
		Set<String> values = JedisCache.cache().getSortedSetAsc("CATEGORY_NEWEST:"+id, offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;

	}
    
	public static List<Long> getCategoryPriceLowHighFeed(Long id, Double offset) {
		Set<String> values = JedisCache.cache().getSortedSetAsc("CATEGORY_PRICE_LOW_HIGH:"+id, offset*1000000);
        final List<Long> postIds = new ArrayList<>();

        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;
	}
	
	public static List<Long> getCategoryPriceHighLowFeed(Long id, Double offset) {
		Set<String> values = JedisCache.cache().getSortedSetDsc("CATEGORY_PRICE_LOW_HIGH:"+id, offset*1000000);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;

	}
	
	public static List<Long> getUserPostFeeds(Long id) {
		Set<String> values = JedisCache.cache().getSetMembers("USER_POSTS:"+id);
        final List<Long> postIds = new ArrayList<>();

        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;
	}
	
	public static List<Long> getUserLikeFeeds(Long id) {
		Set<String> values = JedisCache.cache().getSetMembers("USER_LIKES:"+id);
        final List<Long> postIds = new ArrayList<>();

        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;
	}

}
