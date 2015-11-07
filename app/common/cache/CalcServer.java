package common.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import models.Category;
import models.FollowSocialRelation;
import models.LikeSocialRelation;
import models.Post;
import models.SocialRelation;
import models.User;
import play.Play;
import play.db.jpa.JPA;
import common.model.FeedFilter.FeedType;
import common.schedule.JobScheduler;
import common.thread.ThreadLocalOverride;
import common.utils.NanoSecondStopWatch;
import domain.DefaultValues;

public class CalcServer {
	
	private static play.api.Logger logger = play.api.Logger.apply(CalcServer.class);
	
	public static final Long FEED_SCORE_COMPUTE_SCHEDULE = Play.application().configuration().getLong("feed.score.compute.schedule");
	public static final Long FEED_SCORE_HIGH_BASE = Play.application().configuration().getLong("feed.score.high.base");
	public static final Long FEED_HOME_COUNT_MAX = Play.application().configuration().getLong("feed.home.count.max");
	public static final Long FEED_CATEGORY_EXPOSURE_MIN = Play.application().configuration().getLong("feed.category.exposure.min");
	public static final int FEED_SCORE_RANDOMIZE_PERCENT = Play.application().configuration().getInt("feed.score.randomize.percent");
	public static final int FEED_SNAPSHOT_EXPIRY = Play.application().configuration().getInt("feed.snapshot.expiry");
	public static final int FEED_SOLD_CLEANUP_DAYS = Play.application().configuration().getInt("feed.sold.cleanup.days");
	public static final int FEED_RETRIEVAL_COUNT = DefaultValues.FEED_INFINITE_SCROLL_COUNT;
	
	private static CalcFormula formula = new CalcFormula();
	private static Random random = new Random();
	
	public static void warmUpActivity(final JedisCache jedisCache) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("warmUpActivity starts");
		buildCategoryQueues(jedisCache);
		buildUserQueues(jedisCache);
		buildPostQueues(jedisCache);
		
		JobScheduler.getInstance().schedule(
		        "buildCategoryPopularQueue", 
		        FEED_SCORE_COMPUTE_SCHEDULE,  // initial delay 
		        FEED_SCORE_COMPUTE_SCHEDULE,  // interval
		        TimeUnit.HOURS,
				new Runnable() {
					public void run() {
						JPA.withTransaction(new play.libs.F.Callback0() {
							@Override
							public void invoke() throws Throwable {
								buildCategoryPopularQueues(jedisCache);
							}
						});
					}
				});
        
		sw.stop();
		logger.underlyingLogger().debug("warmUpActivity completed. Took "+sw.getElapsedSecs()+"s");
	}

	public static void clearCategoryQueues(JedisCache jedisCache) {
		for(Category category : Category.getAllCategories()){
			jedisCache.remove(getKey(FeedType.CATEGORY_PRICE_HIGH_LOW,category.id));
			jedisCache.remove(getKey(FeedType.CATEGORY_NEWEST,category.id));
			jedisCache.remove(getKey(FeedType.CATEGORY_POPULAR,category.id));
			jedisCache.remove(getKey(FeedType.CATEGORY_PRICE_LOW_HIGH,category.id));
		}
	}
	
	public static void clearUserQueues(User user, JedisCache jedisCache) {
		jedisCache.remove(getKey(FeedType.USER_POSTED,user.id));
		jedisCache.remove(getKey(FeedType.USER_LIKED,user.id));
		jedisCache.remove(getKey(FeedType.USER_FOLLOWING,user.id));
	}
	
	public static void clearPostQueues(Post post, JedisCache jedisCache) {
		jedisCache.remove(getKey(FeedType.PRODUCT_LIKES,post.id));
	}


	public static Long calculateBaseScore(Post post) {
		// skip already calculated posts during server startup
		if (ThreadLocalOverride.isServerStartingUp() && post.baseScore > 0L) {
			return post.baseScore;
		}
		return formula.computeBaseScore(post);
	}
	
	private static void buildUserQueues(JedisCache jedisCache) {
		for(User user : User.getEligibleUserForFeed()){
			clearUserQueues(user, jedisCache);
			buildUserPostedQueue(user, jedisCache);
			buildUserLikedPostQueue(user, jedisCache);
			buildUserFollowingUserQueue(user, jedisCache);
		}
	}

	private static void buildUserPostedQueue(User user, JedisCache jedisCache) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserPostedQueue starts");
		
		for(Post post : user.getUserPosts()){
			jedisCache.putToSortedSet(getKey(FeedType.USER_POSTED,user.id), post.getCreatedDate().getTime() , post.id.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserPostedQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void buildUserLikedPostQueue(User user, JedisCache jedisCache) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserLikedPostQueue starts");
		
		for (SocialRelation socialRelation : LikeSocialRelation.getUserLikedPosts(user.id)) {
			jedisCache.putToSortedSet(getKey(FeedType.USER_LIKED,user.id), socialRelation.getCreatedDate().getTime(), socialRelation.target.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserLikedPostQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void buildUserFollowingUserQueue(User user, JedisCache jedisCache) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserLikedPostQueue starts");
		
		for (SocialRelation socialRelation : FollowSocialRelation.getUserFollowings(user.id)) {
			jedisCache.putToSortedSet(getKey(FeedType.USER_FOLLOWING,user.id), socialRelation.getCreatedDate().getTime() , socialRelation.target.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserLikedPostQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void buildCategoryQueues(JedisCache jedisCache) {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
        logger.underlyingLogger().debug("buildCategoryQueues starts");
        
		clearCategoryQueues(jedisCache);
		for (Post post : Post.getEligiblePostsForFeeds()) {
		    if (post.soldMarked) {
                continue;
            }
		    addToCategoryPriceLowHighQueue(post, jedisCache);
		    addToCategoryNewestQueue(post, jedisCache);
		    addToCategoryPopularQueue(post, jedisCache);
		}
		
		sw.stop();
        logger.underlyingLogger().debug("buildCategoryQueues completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	private static void buildCategoryPopularQueues(JedisCache jedisCache) {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
        logger.underlyingLogger().debug("buildCategoryPopularQueue starts");
        
		for (Post post : Post.getEligiblePostsForFeeds()) {
		    if (post.soldMarked) {
                continue;
            }
			addToCategoryPopularQueue(post, jedisCache);
		}
		
		sw.stop();
        logger.underlyingLogger().debug("buildCategoryPopularQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	private static void buildPostQueues(JedisCache jedisCache) {
		for (Post post : Post.getEligiblePostsForFeeds()) {
			clearPostQueues(post, jedisCache);
			if (post.sold) {
                continue;
            }
		    buildProductLikedUserQueue(post, jedisCache);
		}
	}
	
	private static void buildProductLikedUserQueue(Post post, JedisCache jedisCache) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildProductLikedUserQueue starts");
		
		for (SocialRelation socialRelation : LikeSocialRelation.getPostLikedUsers(post.id)) {
			jedisCache.putToSortedSet(getKey(FeedType.PRODUCT_LIKES,post.id), socialRelation.getCreatedDate().getTime(), socialRelation.actor.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildProductLikedUserQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	public static Double calculateTimeScore(Post post) {
	    return calculateTimeScore(post, false);
	}
	
	private static Double calculateTimeScore(Post post, boolean recalcBaseScore) {
	    if (recalcBaseScore) {
	        calculateBaseScore(post);
	    }
		return formula.computeTimeScore(post);
	}

	public static void recalcScoreAndAddToCategoryPopularQueue(Post post, JedisCache jedisCache) {
	    addToCategoryPopularQueue(post, jedisCache);
	}
	
	private static void addToCategoryPopularQueue(Post post, JedisCache jedisCache) {
	    if (post.soldMarked) {
            return;
        }
        Double timeScore = calculateTimeScore(post, true);
        jedisCache.putToSortedSet(getKey(FeedType.CATEGORY_POPULAR,post.category.id),  timeScore, post.id.toString());
    }
	
	private static void addToCategoryNewestQueue(Post post, JedisCache jedisCache) {
	    if (post.soldMarked) {
            return;
        }
		jedisCache.putToSortedSet(getKey(FeedType.CATEGORY_NEWEST,post.category.id), post.getCreatedDate().getTime() , post.id.toString());
	}

	private static void addToCategoryPriceLowHighQueue(Post post, JedisCache jedisCache) {
	    if (post.soldMarked) {
            return;
        }
		jedisCache.putToSortedSet(getKey(FeedType.CATEGORY_PRICE_LOW_HIGH,post.category.id), post.price * FEED_SCORE_HIGH_BASE + post.id , post.id.toString());
	}
	
	private static void buildUserExploreFeedQueue(Long userId, JedisCache jedisCache) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserExploreQueue starts");
		
		User user = User.findById(userId);
		if (user == null) {
			logger.underlyingLogger().error("buildUserExploreQueue failed!! User[id="+userId+"] not exists");
			return;
		}
		Map<String, Long> map = user.getUserCategoriesForFeed();
		for (Category category : Category.getAllCategories()){
			Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.CATEGORY_POPULAR,category.id), 0L);
			final List<Long> postIds = new ArrayList<>();
			for (String value : values) {
				try {
					postIds.add(Long.parseLong(value));
				} catch (Exception e) {
				}
			}
			Long percentage  = FEED_CATEGORY_EXPOSURE_MIN;
			if(map.get(category.getId()) != null){
				percentage = map.get(category.getId());
			}
			Long postsSize = postIds.size() > FEED_HOME_COUNT_MAX ? FEED_HOME_COUNT_MAX : postIds.size(); // if post.size() is less than FEED_HOME_COUNT_MAX (limit of post) 
			Integer length =  (int) ((postsSize * percentage) / 100);
			postIds.subList(0, length);
			for(Long postId : postIds){
				jedisCache.putToSortedSet(getKey(FeedType.HOME_EXPLORE,user.id), Math.random() * FEED_SCORE_HIGH_BASE, postId.toString());
			}
		}
		jedisCache.expire(getKey(FeedType.HOME_EXPLORE,user.id), FEED_SNAPSHOT_EXPIRY);
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserExploreQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	private static Double randomizeScore(Post post) {
	    Double timeScore = calculateTimeScore(post, false);
	    int min = 100 - FEED_SCORE_RANDOMIZE_PERCENT;
	    int max = 100 + FEED_SCORE_RANDOMIZE_PERCENT;
	    int percent = (random.nextInt(max - min) + min) / 100;
	    return timeScore * percent;
	}
	
	private static void buildUserFollowingFeedQueue(Long userId, JedisCache jedisCache) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserFollowingQueue starts");
		
		List<Long> followings = getUserFollowingFeeds(userId, jedisCache);
		for (Long followingUser : followings){
			Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.USER_POSTED,followingUser), 0L);
			for (String value : values) {
				try {
					Long postId = Long.parseLong(value);
					jedisCache.putToSortedSet(getKey(FeedType.HOME_FOLLOWING,userId), getScore(getKey(FeedType.USER_POSTED, followingUser), postId, jedisCache), postId.toString());
				} catch (Exception e) {
				}
			}
		}
		jedisCache.expire(getKey(FeedType.HOME_FOLLOWING,userId), FEED_SNAPSHOT_EXPIRY);
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserFollowingQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	private static void buildSuggestedProductQueue(Long productId, JedisCache jedisCache) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildSuggestedProductQueue starts");
		
		List<Long> users = getProductLikeUserQueue(productId, jedisCache);
		List<Long> postIds = new ArrayList<>();
		for (Long userId : users){
			Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.USER_LIKED, userId), 0L);
			for (String value : values) {
				try {
					Long postId = Long.parseLong(value);
					postIds.add(postId);
				} catch (Exception e) {
				}
			}
		}
		Collections.shuffle(postIds);
		postIds = postIds.subList(0, postIds.size() <= 20 ? postIds.size() : 20 );
		
		
		for(Long postId : postIds){
			jedisCache.putToSortedSet(getKey(FeedType.PRODUCT_SUGGEST, productId), Math.random() * FEED_SCORE_HIGH_BASE, postId.toString());
		}
		
		jedisCache.expire(getKey(FeedType.PRODUCT_SUGGEST, productId), FEED_SNAPSHOT_EXPIRY);
		
		sw.stop();
		logger.underlyingLogger().debug("buildSuggestedProductQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	public static boolean isLiked(Long userId, Long postId, JedisCache jedisCache) {
		 String key = getKey(FeedType.USER_LIKED,userId);
	     return jedisCache.isMemberOfSortedSet(key, postId.toString());
	}
	
	public static boolean isFollowed(Long userId, Long followingUserId, JedisCache jedisCache) {
		 String key = getKey(FeedType.USER_FOLLOWING,userId);
	     return jedisCache.isMemberOfSortedSet(key, followingUserId.toString());
	}

	public static List<Long> getCategoryPopularFeed(Long id, Double offset, JedisCache jedisCache) {
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.CATEGORY_POPULAR,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;
	}
	
	public static List<Long> getCategoryNewestFeed(Long id, Double offset, JedisCache jedisCache) {
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.CATEGORY_NEWEST,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;

	}
    
	public static List<Long> getCategoryPriceLowHighFeed(Long id, Double offset, JedisCache jedisCache) {
		Set<String> values = jedisCache.getSortedSetAsc(getKey(FeedType.CATEGORY_PRICE_LOW_HIGH,id), offset);
        final List<Long> postIds = new ArrayList<>();

        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;
	}
	
	public static List<Long> getCategoryPriceHighLowFeed(Long id, Double offset, JedisCache jedisCache) {
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.CATEGORY_PRICE_HIGH_LOW,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;
	}
	
	public static List<Long> getHomeExploreFeed(Long id, Double offset, JedisCache jedisCache) {
		if(!jedisCache.exists(getKey(FeedType.HOME_EXPLORE,id))){
			buildUserExploreFeedQueue(id, jedisCache);
		}
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.HOME_EXPLORE,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        jedisCache.expire(getKey(FeedType.HOME_EXPLORE,id), FEED_SNAPSHOT_EXPIRY);
        return postIds;

	}
	
	public static List<Long> getHomeFollowingFeed(Long id, Double offset, JedisCache jedisCache) {
		if(!jedisCache.exists(getKey(FeedType.HOME_FOLLOWING,id))){
			buildUserFollowingFeedQueue(id, jedisCache);
		}
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.HOME_FOLLOWING,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        jedisCache.expire(getKey(FeedType.HOME_FOLLOWING,id), FEED_SNAPSHOT_EXPIRY);
        return postIds;

	}
	
	public static List<Long> getUserPostedFeeds(Long id, Double offset, JedisCache jedisCache) {
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.USER_POSTED,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;

	}
	
	public static List<Long> getUserLikedFeeds(Long id, Double offset, JedisCache jedisCache) {
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.USER_LIKED,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;

	}
	
	public static List<Long> getUserFollowingFeeds(Long id, JedisCache jedisCache) {
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.USER_FOLLOWING,id), 0L);
        final List<Long> userIds = new ArrayList<>();
        for (String value : values) {
            try {
                userIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return userIds;

	}
	
	public static List<Long> getSuggestedProducts(Long id, JedisCache jedisCache) {
		if(!jedisCache.exists(getKey(FeedType.PRODUCT_SUGGEST, id))){
			buildSuggestedProductQueue(id, jedisCache);
		}
		
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.PRODUCT_SUGGEST, id) , 0L);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        jedisCache.expire(getKey(FeedType.PRODUCT_SUGGEST, id), FEED_SNAPSHOT_EXPIRY);
        return postIds;

	}
	
	public static List<Long> getProductLikeUserQueue(Long productId, JedisCache jedisCache) {
		Set<String> values = jedisCache.getSortedSetDsc(getKey(FeedType.PRODUCT_LIKES,productId), 0L);
        final List<Long> userIds = new ArrayList<>();
        for (String value : values) {
            try {
                userIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return userIds;

	}
	
	public static void addToCategoryQueues(Post post, JedisCache jedisCache) {
		addToCategoryPriceLowHighQueue(post, jedisCache);
		addToCategoryNewestQueue(post, jedisCache);
		addToCategoryPopularQueue(post, jedisCache);
	}
	
	public static void removeFromCategoryQueues(Post post, JedisCache jedisCache){
		removeFromCategoryQueues(post, post.category, jedisCache);
	}
	
	public static void removeFromCategoryQueues(Post post, Category category, JedisCache jedisCache){
	    removeMemberFromPriceLowHighPostQueue(post.id, category.id, jedisCache);
        removeMemberFromNewestPostQueue(post.id, category.id, jedisCache);
        removeMemberFromPopularPostQueue(post.id, category.id, jedisCache);
	}
	
	public static void removeMemberFromPriceLowHighPostQueue(Long postId, Long categoryId, JedisCache jedisCache){
		jedisCache.removeMemberFromSortedSet(getKey(FeedType.CATEGORY_PRICE_LOW_HIGH,categoryId), postId.toString());
	}
	
	public static void removeMemberFromNewestPostQueue(Long postId, Long categoryId, JedisCache jedisCache){
		jedisCache.removeMemberFromSortedSet(getKey(FeedType.CATEGORY_NEWEST,categoryId), postId.toString());
	}

	public static void removeMemberFromPopularPostQueue(Long postId, Long categoryId, JedisCache jedisCache){
		jedisCache.removeMemberFromSortedSet(getKey(FeedType.CATEGORY_POPULAR,categoryId), postId.toString());
	}
	
	public static void addToLikeQueue(Post post, User user, JedisCache jedisCache){
		jedisCache.putToSortedSet(getKey(FeedType.USER_LIKED,user.id), new Date().getTime(), post.id.toString());
	}
	
	public static void removeFromLikeQueue(Post post, User user, JedisCache jedisCache){
		jedisCache.removeMemberFromSortedSet(getKey(FeedType.USER_LIKED,user.id), post.id.toString());
	}

	public static void addToFollowQueue(Long userId, Long followingUserId, Double score, JedisCache jedisCache){
		jedisCache.remove(getKey(FeedType.HOME_FOLLOWING,userId));
		jedisCache.putToSortedSet(getKey(FeedType.USER_FOLLOWING,userId), score, followingUserId.toString());
	}
	
	public static void removeFromFollowQueue(Long userId, Long followingUserId, JedisCache jedisCache){
		jedisCache.remove(getKey(FeedType.HOME_FOLLOWING,userId));
		jedisCache.removeMemberFromSortedSet(getKey(FeedType.USER_FOLLOWING,userId), followingUserId.toString());
	}

	public static void addToUserPostedQueue(Post post, User user, JedisCache jedisCache){
		jedisCache.putToSortedSet(getKey(FeedType.USER_POSTED,user.id), post.getCreatedDate().getTime(), post.id.toString());
	}
	
	public static void removeFromUserPostedQueue(Post post, User user, JedisCache jedisCache){
		jedisCache.removeMemberFromSortedSet(getKey(FeedType.USER_POSTED,user.id), post.id.toString());
	}

	public static void removeFromAllUsersLikedQueues(Post post, JedisCache jedisCache) {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
        logger.underlyingLogger().debug("removeFromAllUsersLikedQueues starts");
        
        for (SocialRelation socialRelation : LikeSocialRelation.getPostLikedUsers(post.id)) {
            jedisCache.removeMemberFromSortedSet(getKey(FeedType.USER_LIKED,socialRelation.actor), post.id.toString());
        }
        
        sw.stop();
        logger.underlyingLogger().debug("removeFromAllUsersLikedQueues completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	public static Double getScore(String key, Long postId, JedisCache jedisCache){
		return jedisCache.getScore(key, postId.toString());
	}
	
	public static String getKey(FeedType feedType, Long keyId) {
		// Only 1 queue CATEGORY_PRICE_LOW_HIGH
		if (FeedType.CATEGORY_PRICE_HIGH_LOW.equals(feedType)) {
			feedType = FeedType.CATEGORY_PRICE_LOW_HIGH;
		}
		return feedType+":"+keyId;
	}
	
	public static void cleanupSoldPosts() {
        DateTime daysBefore = (new DateTime()).minusDays(FEED_SOLD_CLEANUP_DAYS);
        List<Post> soldPosts = Post.getUnmarkedSoldPostsAfter(daysBefore.toDate());
        if (soldPosts != null) {
            for (Post soldPost : soldPosts) {
                CalcServer.removeFromCategoryQueues(soldPost, null);
                soldPost.soldMarked = true;
            }
        }
    }
}
