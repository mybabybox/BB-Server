package common.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;

import play.Play;
import models.Category;
import models.FollowSocialRelation;
import models.LikeSocialRelation;
import models.Post;
import models.SocialRelation;
import models.User;
import common.model.FeedFilter.FeedType;
import common.thread.ThreadLocalOverride;
import common.utils.NanoSecondStopWatch;
import domain.DefaultValues;

public class CalcServer {
	private static play.api.Logger logger = play.api.Logger.apply(CalcServer.class);
	
	public static final Long FEED_SCORE_HIGH_BASE = Play.application().configuration().getLong("feed.score.high.base");
	public static final Long FEED_HOME_COUNT_MAX = Play.application().configuration().getLong("feed.home.count.max");
	public static final Long FEED_CATEGORY_EXPOSURE_MIN = Play.application().configuration().getLong("feed.category.exposure.min");
	public static final int FEED_SCORE_RANDOMIZE_PERCENT = Play.application().configuration().getInt("feed.score.randomize.percent");
	public static final int FEED_EXPIRY = Play.application().configuration().getInt("feed.expiry");
	public static final int FEED_RETRIEVAL_COUNT = DefaultValues.FEED_INFINITE_SCROLL_COUNT;
	
	private static CalcFormula formula = new CalcFormula();
	private static Random random = new Random();
	
	public static void warmUpActivity() {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("warmUpActivity starts");
		
		buildCategoryQueues();
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
								buildCategoryQueues();
								buildUserQueue();
							}
						});
					}
				}, actorSystem.dispatcher());
		*/
		
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

	public static Long calculateBaseScore(Post post) {
		// skip already calculated posts during server startup
		if (ThreadLocalOverride.isServerStartingUp() && post.baseScore > 0L) {
			return post.baseScore;
		}
		return formula.computeBaseScore(post);
	}
	
	private static void buildUserQueue() {
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
		clearCategoryQueues();
		for (Post post : Post.getAllPosts()) {
		    if (post.sold) {
		        continue;
		    }
		    addToCategoryPriceLowHighQueue(post);
		    addToCategoryNewestQueue(post);
			addToCategoryPopularQueue(post);
		}
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

	public static void addToCategoryPopularQueue(Post post) {
        Double timeScore = calculateTimeScore(post, true);
        JedisCache.cache().putToSortedSet(getKey(FeedType.CATEGORY_POPULAR,post.category.id),  timeScore, post.id.toString());
    }
	
	private static void addToCategoryNewestQueue(Post post) {
		JedisCache.cache().putToSortedSet(getKey(FeedType.CATEGORY_NEWEST,post.category.id), post.getCreatedDate().getTime() , post.id.toString());
	}

	private static void addToCategoryPriceLowHighQueue(Post post) {
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
		JedisCache.cache().expire(getKey(FeedType.HOME_EXPLORE,user.id), FEED_EXPIRY);
		
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
		
		List<Long> followings = getUserFollowingFeeds(userId, 0L);
		for (Long followingUser : followings){
			Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.USER_POSTED,followingUser), 0L);
			for (String value : values) {
				try {
					Long postId = Long.parseLong(value);
					JedisCache.cache().putToSortedSet(getKey(FeedType.HOME_FOLLOWING,userId), getScore(getKey(FeedType.USER_POSTED, followingUser), postId) , postId.toString());
				} catch (Exception e) {
				}
			}
		}
		JedisCache.cache().expire(getKey(FeedType.HOME_FOLLOWING,userId), FEED_EXPIRY);
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserFollowingQueue completed. Took "+sw.getElapsedSecs()+"s");
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
        JedisCache.cache().expire(getKey(FeedType.HOME_EXPLORE,id), FEED_EXPIRY);
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
        JedisCache.cache().expire(getKey(FeedType.HOME_FOLLOWING,id), FEED_EXPIRY);
        return postIds;

	}
	
	public static List<Long> getUserPostFeeds(Long id, Double offset) {
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
	
	public static List<Long> getUserLikeFeeds(Long id, Double offset) {
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
	
	public static List<Long> getUserFollowingFeeds(Long id, Long offset) {
		Set<String> values = JedisCache.cache().getSortedSetDsc(getKey(FeedType.USER_FOLLOWING,id), offset);
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
		removeMemberFromPriceLowHighPostQueue(post.id, post.category.id);
		removeMemberFromNewestPostQueue(post.id, post.category.id);
		removeMemberFromPopularPostQueue(post.id, post.category.id);
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

	public static void addToPostQueue(Post post, User user){
		JedisCache.cache().putToSortedSet(getKey(FeedType.USER_POSTED,user.id), post.getCreatedDate().getTime(), post.id.toString());
	}
	
	public static void removeFromPostQueue(Post post, User user){
		JedisCache.cache().removeMemberFromSortedSet(getKey(FeedType.USER_POSTED,user.id), post.id.toString());
	}

	/**
	 * Remove deleted products from owner posted and liked feeds
	 * Remove deleted products from other users liked feeds (query from db)
	 * @param postId
	 */
	public static void removeFromUserQueues(Post post, User user) {
	    removeFromLikeQueue(post, user);
	    removeFromPostQueue(post, user);
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
}
