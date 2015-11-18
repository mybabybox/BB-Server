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
	
	public static void warmUpActivity() {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("warmUpActivity starts");
		
		buildCategoryQueues();
		buildUserQueues();
		buildPostQueues();
		
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
								buildCategoryPopularQueues();
							}
						});
					}
				});
        
		sw.stop();
		logger.underlyingLogger().debug("warmUpActivity completed. Took "+sw.getElapsedSecs()+"s");
	}

	public static void clearCategoryQueues() {
		for(Category category : Category.getAllCategories()){
			JedisCache.cache().remove(getKey(FeedType.CATEGORY_PRICE_HIGH_LOW,category.id));
			JedisCache.cache().remove(getKey(FeedType.CATEGORY_NEWEST,category.id));
			JedisCache.cache().remove(getKey(FeedType.CATEGORY_POPULAR,category.id));
			JedisCache.cache().remove(getKey(FeedType.CATEGORY_PRICE_LOW_HIGH,category.id));
		}
	}
	
	public static void clearUserQueues(User user) {
		JedisCache.cache().remove(getKey(FeedType.USER_POSTED,user.id));
		JedisCache.cache().remove(getKey(FeedType.USER_LIKED,user.id));
		JedisCache.cache().remove(getKey(FeedType.USER_FOLLOWING,user.id));
	}
	
	public static void clearPostQueues(Post post) {
		JedisCache.cache().remove(getKey(FeedType.PRODUCT_LIKES,post.id));
	}


	public static Long calculateBaseScore(Post post) {
		// skip already calculated posts during server startup
		if (ThreadLocalOverride.isServerStartingUp() && post.baseScore > 0L) {
			return post.baseScore;
		}
		return formula.computeBaseScore(post);
	}
	
	private static void buildUserQueues() {
		for(User user : User.getEligibleUserForFeed()){
			clearUserQueues(user);
			buildUserPostedQueue(user);
			buildUserLikedPostQueue(user);
			buildUserFollowingUserQueue(user);
		}
	}

	private static void buildUserPostedQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserPostedQueue starts");
		
		for(Post post : user.getUserPosts()){
			JedisCache.cache().putToSortedSet(getKey(FeedType.USER_POSTED,user.id), post.getCreatedDate().getTime() , post.id.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserPostedQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void buildUserLikedPostQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserLikedPostQueue starts");
		
		for (SocialRelation socialRelation : LikeSocialRelation.getUserLikedPosts(user.id)) {
			JedisCache.cache().putToSortedSet(getKey(FeedType.USER_LIKED,user.id), socialRelation.getCreatedDate().getTime(), socialRelation.target.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserLikedPostQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void buildUserFollowingUserQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserLikedPostQueue starts");
		
		for (SocialRelation socialRelation : FollowSocialRelation.getUserFollowings(user.id)) {
			JedisCache.cache().putToSortedSet(getKey(FeedType.USER_FOLLOWING,user.id), socialRelation.getCreatedDate().getTime() , socialRelation.target.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserLikedPostQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void buildCategoryQueues() {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
        logger.underlyingLogger().debug("buildCategoryQueues starts");
        
		clearCategoryQueues();
		for (Post post : Post.getEligiblePostsForFeeds()) {
		    if (post.soldMarked) {
                continue;
            }
		    addToCategoryPriceLowHighQueue(post);
		    addToCategoryNewestQueue(post);
		    addToCategoryPopularQueue(post);
		}
		
		sw.stop();
        logger.underlyingLogger().debug("buildCategoryQueues completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	private static void buildCategoryPopularQueues() {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
        logger.underlyingLogger().debug("buildCategoryPopularQueue starts");
        
		for (Post post : Post.getEligiblePostsForFeeds()) {
		    if (post.soldMarked) {
                continue;
            }
			addToCategoryPopularQueue(post);
		}
		
		sw.stop();
        logger.underlyingLogger().debug("buildCategoryPopularQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	private static void buildPostQueues() {
		for (Post post : Post.getEligiblePostsForFeeds()) {
			clearPostQueues(post);
			if (post.sold) {
                continue;
            }
		    buildProductLikedUserQueue(post);
		}
	}
	
	private static void buildProductLikedUserQueue(Post post) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildProductLikedUserQueue starts");
		
		for (SocialRelation socialRelation : LikeSocialRelation.getPostLikedUsers(post.id)) {
			JedisCache.cache().putToSortedSet(getKey(FeedType.PRODUCT_LIKES,post.id), socialRelation.getCreatedDate().getTime(), socialRelation.actor.toString());
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

	public static void recalcScoreAndAddToCategoryPopularQueue(Post post) {
	    addToCategoryPopularQueue(post);
	}
	
	private static void addToCategoryPopularQueue(Post post) {
	    if (post.soldMarked) {
            return;
        }
        Double timeScore = calculateTimeScore(post, true);
        JedisCache.cache().putToSortedSet(getKey(FeedType.CATEGORY_POPULAR,post.category.id),  timeScore, post.id.toString());
    }
	
	private static void addToCategoryNewestQueue(Post post) {
	    if (post.soldMarked) {
            return;
        }
		JedisCache.cache().putToSortedSet(getKey(FeedType.CATEGORY_NEWEST,post.category.id), post.getCreatedDate().getTime() , post.id.toString());
	}

	private static void addToCategoryPriceLowHighQueue(Post post) {
	    if (post.soldMarked) {
            return;
        }
		JedisCache.cache().putToSortedSet(getKey(FeedType.CATEGORY_PRICE_LOW_HIGH,post.category.id), post.price * FEED_SCORE_HIGH_BASE + post.id , post.id.toString());
	}
	
	private static void buildUserExploreFeedQueue(Long userId) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserExploreQueue starts");
		
		User user = User.findById(userId);
		if (user == null) {
			logger.underlyingLogger().error("buildUserExploreQueue failed!! User[id="+userId+"] not exists");
			return;
		}
		Map<String, Long> map = user.getUserCategoriesForFeed();
		for (Category category : Category.getAllCategories()){
			Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.CATEGORY_POPULAR,category.id), 0L);
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
				JedisCache.cache().putToSortedSet(getKey(FeedType.HOME_EXPLORE,user.id), Math.random() * FEED_SCORE_HIGH_BASE, postId.toString());
			}
		}
		JedisCache.cache().expire(getKey(FeedType.HOME_EXPLORE,user.id), FEED_SNAPSHOT_EXPIRY);
		
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
	
	private static void buildUserFollowingFeedQueue(Long userId) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserFollowingQueue starts");
		
		List<Long> followings = getUserFollowingFeeds(userId);
		for (Long followingUser : followings){
			Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.USER_POSTED,followingUser), 0L);
			for (String value : values) {
				try {
					Long postId = Long.parseLong(value);
					JedisCache.cache().putToSortedSet(getKey(FeedType.HOME_FOLLOWING,userId), getScore(getKey(FeedType.USER_POSTED, followingUser), postId), postId.toString());
				} catch (Exception e) {
				}
			}
		}
		JedisCache.cache().expire(getKey(FeedType.HOME_FOLLOWING,userId), FEED_SNAPSHOT_EXPIRY);
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserFollowingQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	private static void buildSuggestedProductQueue(Long productId) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildSuggestedProductQueue starts");
		
		List<Long> users = getProductLikeUserQueue(productId);
		List<Long> postIds = new ArrayList<>();
		for (Long userId : users){
			Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.USER_LIKED, userId), 0L);
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
			JedisCache.cache().putToSortedSet(getKey(FeedType.PRODUCT_SUGGEST, productId), Math.random() * FEED_SCORE_HIGH_BASE, postId.toString());
		}
		
		JedisCache.cache().expire(getKey(FeedType.PRODUCT_SUGGEST, productId), FEED_SNAPSHOT_EXPIRY);
		
		sw.stop();
		logger.underlyingLogger().debug("buildSuggestedProductQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	public static boolean isLiked(Long userId, Long postId) {
		 String key = getKey(FeedType.USER_LIKED,userId);
	     return JedisCache.cache().isMemberOfSortedSet(key, postId.toString());
	}
	
	public static boolean isFollowed(Long userId, Long followingUserId) {
		 String key = getKey(FeedType.USER_FOLLOWING,userId);
	     return JedisCache.cache().isMemberOfSortedSet(key, followingUserId.toString());
	}

	public static List<Long> getCategoryPopularFeed(Long id, Double offset) {
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.CATEGORY_POPULAR,id), offset);
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
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.CATEGORY_NEWEST,id), offset);
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
		Set<String> values = JedisCache.cache().getSortedSetAsc(getKey(FeedType.CATEGORY_PRICE_LOW_HIGH,id), offset);
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
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.CATEGORY_PRICE_HIGH_LOW,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;
	}
	
	public static List<Long> getHomeExploreFeed(Long id, Double offset) {
		if(!JedisCache.cache().exists(getKey(FeedType.HOME_EXPLORE,id))){
			buildUserExploreFeedQueue(id);
		}
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.HOME_EXPLORE,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        JedisCache.cache().expire(getKey(FeedType.HOME_EXPLORE,id), FEED_SNAPSHOT_EXPIRY);
        return postIds;

	}
	
	public static List<Long> getHomeFollowingFeed(Long id, Double offset) {
		if(!JedisCache.cache().exists(getKey(FeedType.HOME_FOLLOWING,id))){
			buildUserFollowingFeedQueue(id);
		}
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.HOME_FOLLOWING,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        JedisCache.cache().expire(getKey(FeedType.HOME_FOLLOWING,id), FEED_SNAPSHOT_EXPIRY);
        return postIds;

	}
	
	public static List<Long> getUserPostedFeeds(Long id, Double offset) {
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.USER_POSTED,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;

	}
	
	public static List<Long> getUserLikedFeeds(Long id, Double offset) {
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.USER_LIKED,id), offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;

	}
	
	public static List<Long> getUserFollowingFeeds(Long id) {
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.USER_FOLLOWING,id), 0L);
        final List<Long> userIds = new ArrayList<>();
        for (String value : values) {
            try {
                userIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return userIds;

	}
	
	public static List<Long> getSuggestedProducts(Long id) {
		if(!JedisCache.cache().exists(getKey(FeedType.PRODUCT_SUGGEST, id))){
			buildSuggestedProductQueue(id);
		}
		
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.PRODUCT_SUGGEST, id) , 0L);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        JedisCache.cache().expire(getKey(FeedType.PRODUCT_SUGGEST, id), FEED_SNAPSHOT_EXPIRY);
        return postIds;

	}
	
	public static List<Long> getProductLikeUserQueue(Long productId) {
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.PRODUCT_LIKES,productId), 0L);
        final List<Long> userIds = new ArrayList<>();
        for (String value : values) {
            try {
                userIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return userIds;

	}
	
	public static void addToCategoryQueues(Post post) {
		addToCategoryPriceLowHighQueue(post);
		addToCategoryNewestQueue(post);
		addToCategoryPopularQueue(post);
	}
	
	public static void removeFromCategoryQueues(Post post){
		removeFromCategoryQueues(post, post.category);
	}
	
	public static void removeFromCategoryQueues(Post post, Category category){
	    removeMemberFromPriceLowHighPostQueue(post.id, category.id);
        removeMemberFromNewestPostQueue(post.id, category.id);
        removeMemberFromPopularPostQueue(post.id, category.id);
	}
	
	public static void removeMemberFromPriceLowHighPostQueue(Long postId, Long categoryId){
		JedisCache.cache().removeMemberFromSortedSet(getKey(FeedType.CATEGORY_PRICE_LOW_HIGH,categoryId), postId.toString());
	}
	
	public static void removeMemberFromNewestPostQueue(Long postId, Long categoryId){
		JedisCache.cache().removeMemberFromSortedSet(getKey(FeedType.CATEGORY_NEWEST,categoryId), postId.toString());
	}

	public static void removeMemberFromPopularPostQueue(Long postId, Long categoryId){
		JedisCache.cache().removeMemberFromSortedSet(getKey(FeedType.CATEGORY_POPULAR,categoryId), postId.toString());
	}
	
	public static void addToLikeQueue(Post post, User user){
		JedisCache.cache().putToSortedSet(getKey(FeedType.USER_LIKED,user.id), new Date().getTime(), post.id.toString());
	}
	
	public static void removeFromLikeQueue(Post post, User user){
		JedisCache.cache().removeMemberFromSortedSet(getKey(FeedType.USER_LIKED,user.id), post.id.toString());
	}

	public static void addToFollowQueue(Long userId, Long followingUserId, Double score){
		JedisCache.cache().remove(getKey(FeedType.HOME_FOLLOWING,userId));
		JedisCache.cache().putToSortedSet(getKey(FeedType.USER_FOLLOWING,userId), score, followingUserId.toString());
	}
	
	public static void removeFromFollowQueue(Long userId, Long followingUserId){
		JedisCache.cache().remove(getKey(FeedType.HOME_FOLLOWING,userId));
		JedisCache.cache().removeMemberFromSortedSet(getKey(FeedType.USER_FOLLOWING,userId), followingUserId.toString());
	}

	public static void addToUserPostedQueue(Post post, User user){
		JedisCache.cache().putToSortedSet(getKey(FeedType.USER_POSTED,user.id), post.getCreatedDate().getTime(), post.id.toString());
	}
	
	public static void removeFromUserPostedQueue(Post post, User user){
		JedisCache.cache().removeMemberFromSortedSet(getKey(FeedType.USER_POSTED,user.id), post.id.toString());
	}

	public static void removeFromAllUsersLikedQueues(Post post) {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
        logger.underlyingLogger().debug("removeFromAllUsersLikedQueues starts");
        
        for (SocialRelation socialRelation : LikeSocialRelation.getPostLikedUsers(post.id)) {
            JedisCache.cache().removeMemberFromSortedSet(getKey(FeedType.USER_LIKED,socialRelation.actor), post.id.toString());
        }
        
        sw.stop();
        logger.underlyingLogger().debug("removeFromAllUsersLikedQueues completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	public static Double getScore(String key, Long postId){
		return JedisCache.cache().getScore(key, postId.toString());
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
            logger.underlyingLogger().info("cleanupSoldPosts - There are "+soldPosts.size()+" sold posts before "+daysBefore.toString());
            for (Post soldPost : soldPosts) {
                CalcServer.removeFromCategoryQueues(soldPost);
                soldPost.soldMarked = true;
                logger.underlyingLogger().info("[id="+soldPost.id+"] removeFromCategoryQueues: "+soldPost.title);
            }
        } else {
            logger.underlyingLogger().info("cleanupSoldPosts - There is no sold post before "+daysBefore.toString());
        }
    }
}
