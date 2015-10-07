package common.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import models.Post;
import models.User;
import common.utils.NanoSecondStopWatch;

public class CalcServer {
	private static play.api.Logger logger = play.api.Logger.apply(CalcServer.class);
	
	public static void warmUpActivity() {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("warmUpActivity starts");
		
		buildBaseScore();
		buildCategoryQueue();
		buildUserQueue();
		
		/*
		ActorSystem actorSystem = Akka.system();
		actorSystem.scheduler().scheduleOnce(
				Duration.create(0, TimeUnit.MILLISECONDS),
				new Runnable() {
					public void run() {
						JPA.withTransaction(new play.libs.F.Callback0() {
							@Override
							public void invoke() throws Throwable {
								buildBaseScore();
								buildCategoryQueue();
								buildUserQueue();
							}
						});
					}
				}, actorSystem.dispatcher());
		*/
		
		sw.stop();
		logger.underlyingLogger().debug("warmUpActivity completed. Took "+sw.getElapsedSecs()+"s");
	}

	public static void buildBaseScore() {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildBaseScore starts");
		
		for(Post product : Post.getAllPosts()){
			calculateBaseScore(product);
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildBaseScore completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void calculateBaseScore(Post product) {
		if (product.baseScore == -1L) {
			product.baseScore = (long) (product.noOfViews
					+ 2 * product.noOfLikes
					+ 3 * product.noOfChats
					+ 4 *product.noOfBuys
					+ 5 * product.noOfComments);
			product.save();
		}
	}
	
	private static void buildUserQueue() {
		for(User user : User.getEligibleUserForFeed()){
			buildUserPostedQueue(user);
			buildUserLikedPostQueue(user);
		}
	}

	private static void buildUserPostedQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserPostedQueue starts");
		
		for(Post post : user.getUserPosts()){
			JedisCache.cache().putToSet("USER_POSTS:"+user.id, post.id.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserPostedQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void buildUserLikedPostQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserLikedPostQueue starts");
		
		for(Post post : user.getUserLikedPosts()){
			JedisCache.cache().putToSet("USER_LIKES:"+user.id, post.id.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserLikedPostQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void buildCategoryQueue() {
				for(Post post : Post.getAllPosts()){
			buildPriceHighLowPostQueue(post);
			buildNewestPostQueue(post);
			buildPopularPostQueue(post);
		}
	}

	private static void buildPopularPostQueue(Post post) {
		Long timeScore = calculateTimeScore(post);
		JedisCache.cache().putToSortedSet("CATEGORY_POPULAR:"+post.category.id,  timeScore, post.id.toString());
	}

	private static Long calculateTimeScore(Post post) {
		Long timeScore = Math.max(post.baseScore, 1);
		int timeDiff = Weeks.weeksBetween(new DateTime(new Date()), new DateTime(post.getCreatedDate())).getWeeks();
		if (timeDiff > 0) 
		timeScore = (long) (timeScore * Math.exp(-8 * timeDiff * timeDiff));
		timeScore = timeScore * 1000000 + post.id;
		return timeScore;
	}

	private static void buildNewestPostQueue(Post post) {
		JedisCache.cache().putToSortedSet("CATEGORY_NEWEST:"+post.category.id, post.getCreatedDate().getTime() , post.id.toString());
	}

	private static void buildPriceHighLowPostQueue(Post post) {
		JedisCache.cache().putToSortedSet("CATEGORY_PRICE_LOW_HIGH:"+post.category.id, post.price*1000000 + post.id , post.id.toString());
	}
	
	private static void buildUserExplorerFeedQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserPostedQueue starts");
		
		Map<String, Long> map = user.getUserCategoriesForFeed();
		for (Entry<String, Long> entry : map.entrySet()){
			Set<String> values = JedisCache.cache().getSortedSetDsc("CATEGORY_POPULAR:"+entry.getKey(), 0L);
			final List<Long> postIds = new ArrayList<>();
			for (String value : values) {
				try {
					postIds.add(Long.parseLong(value));
				} catch (Exception e) {
				}
			}
			int length =  (int) ((postIds.size()*entry.getValue()) / 100);
			postIds.subList(0,length);
			for(Long postId : postIds){
				JedisCache.cache().putToSortedSet("HOME_EXPLORE:"+user.id, JedisCache.cache().getScore("CATEGORY_POPULAR:"+entry.getKey(), postId.toString()), postId.toString());
			}
			JedisCache.cache().expire("HOME_EXPLORE:"+user.id, 60 * 2);
		}

		sw.stop();
		logger.underlyingLogger().debug("buildUserPostedQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	

	public static boolean isLiked(Long userId, Long postId) {
		 String key = "USER_LIKES:"+userId;
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
		Set<String> values = JedisCache.cache().getSortedSetDsc("CATEGORY_NEWEST:"+id, offset);
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
		Set<String> values = JedisCache.cache().getSortedSetAsc("CATEGORY_PRICE_LOW_HIGH:"+id, offset);
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
		Set<String> values = JedisCache.cache().getSortedSetDsc("CATEGORY_PRICE_LOW_HIGH:"+id, offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;
	}
	
	public static List<Long> getHomeExploreFeed(Long id, Long offset) {
		if(!JedisCache.cache().exists("HOME_EXPLORE:"+id)){
			buildUserExplorerFeedQueue(User.findById(id));
		}
		Set<String> values = JedisCache.cache().getSortedSetDsc("HOME_EXPLORE:"+id, offset);
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

	public static void addToQueues(Post post) {
		buildBaseScore();
		buildPriceHighLowPostQueue(post);
		buildNewestPostQueue(post);
		buildPopularPostQueue(post);
	}
	
	public static void addToLikeQueue(Long postId, Long userId){
		JedisCache.cache().putToSet("USER_LIKES:"+userId, postId.toString());
	}
	
	public static void removeFromLikeQueue(Long postId, Long userId){
		JedisCache.cache().removeMemberFromSet("USER_LIKES:"+userId, postId.toString());
	}

	public static void addToPostQueue(Long postId, Long userId){
		JedisCache.cache().putToSet("USER_POSTS:"+userId, postId.toString());
	}
	
	public static void removeFromPostQueue(Long postId, Long userId){
		JedisCache.cache().removeMemberFromSet("USER_POSTS:"+userId, postId.toString());
	}
	
	public static Double getScore(String key, Long postId){
		return JedisCache.cache().getScore(key, postId.toString());
	}

}
