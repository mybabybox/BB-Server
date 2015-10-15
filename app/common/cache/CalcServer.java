package common.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

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

public class CalcServer {
	private static play.api.Logger logger = play.api.Logger.apply(CalcServer.class);
	
	private static final Long FEED_HOME_COUNT_MAX = Play.application().configuration().getLong("feed.home.count.max");
	private static final Long FEED_CATEGORY_EXPOSURE_MIN = Play.application().configuration().getLong("feed.category.exposure.min");
	
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
		
		for(Post post : Post.getAllPosts()){
			calculateBaseScore(post);
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildBaseScore completed. Took "+sw.getElapsedSecs()+"s");
	}

	public static void calculateBaseScore(Post post) {
		// skip already calculated posts during server startup
		if (ThreadLocalOverride.isServerStartingUp() && post.baseScore > 0L) {
			return;
		}
		
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("calculateBaseScore for p="+post.id);
		
		post.baseScore = (long) (
				post.numComments
				+ 2 * post.numViews
				+ 3 * post.numLikes
				+ 4 * post.numChats
				+ 5 * post.numBuys
				+ 1);	// min score of 1
		post.save();
		
		sw.stop();
		logger.underlyingLogger().debug("calculateBaseScore completed with baseScore="+post.baseScore+". Took "+sw.getElapsedSecs()+"s");
	}
	
	private static void buildUserQueue() {
		for(User user : User.getEligibleUserForFeed()){
			JedisCache.cache().remove(FeedType.USER_POSTED+":"+user.id);
			JedisCache.cache().remove(FeedType.USER_LIKED+":"+user.id);
			buildUserPostedQueue(user);
			buildUserLikedPostQueue(user);
		}
	}

	private static void buildUserPostedQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserPostedQueue starts");
		
		for(Post post : user.getUserPosts()){
			JedisCache.cache().putToSortedSet(FeedType.USER_POSTED+":"+user.id, post.getCreatedDate().getTime() , post.id.toString());
		}
		
		sw.stop();
		logger.underlyingLogger().debug("buildUserPostedQueue completed. Took "+sw.getElapsedSecs()+"s");
	}

	private static void buildUserLikedPostQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserLikedPostQueue starts");
		
		for (SocialRelation socialRelation : LikeSocialRelation.getUserLikedPosts(user.id)) {
			JedisCache.cache().putToSortedSet(FeedType.USER_LIKED+":"+user.id, socialRelation.getCreatedDate().getTime() , socialRelation.target.toString());
		}
		
		/*
		for (Post post : user.getUserLikedPosts()) {
			JedisCache.cache().putToSortedSet("USER_LIKES:"+user.id, post.getCreatedDate().getTime() , post.id.toString());
		}
		*/
		
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
		JedisCache.cache().putToSortedSet(FeedType.CATEGORY_POPULAR+":"+post.category.id,  timeScore, post.id.toString());
	}

	public static Long calculateTimeScore(Post post) {
		Long timeScore = Math.max(post.baseScore, 1);
		int timeDiff = Weeks.weeksBetween(new DateTime(new Date()), new DateTime(post.getCreatedDate())).getWeeks();
		if (timeDiff > 0)
			timeScore = (long) (timeScore * Math.exp(-8 * timeDiff * timeDiff));
		timeScore = timeScore * 1000000 + post.id;
		return timeScore;
	}

	private static void buildNewestPostQueue(Post post) {
		JedisCache.cache().putToSortedSet(FeedType.CATEGORY_NEWEST+":"+post.category.id, post.getCreatedDate().getTime() , post.id.toString());
	}

	private static void buildPriceHighLowPostQueue(Post post) {
		JedisCache.cache().putToSortedSet(FeedType.CATEGORY_PRICE_LOW_HIGH+":"+post.category.id, post.price*1000000 + post.id , post.id.toString());
	}
	
	private static void buildUserExplorerFeedQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserExplorerQueue starts");
		
		Map<String, Long> map = user.getUserCategoriesForFeed();
		for (Category category : Category.getAllCategories()){
			Set<String> values = JedisCache.cache().getSortedSetDsc(FeedType.CATEGORY_POPULAR+":"+category.getId(), 0L);
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
				JedisCache.cache().putToSortedSet(FeedType.HOME_EXPLORE+":"+user.id, Math.random() , postId.toString());
			}
			JedisCache.cache().expire(FeedType.HOME_EXPLORE+":"+user.id, 60 * 2); // expiration time 120 secs
		}

		sw.stop();
		logger.underlyingLogger().debug("buildUserExplorerQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	private static void buildUserFollowingFeedQueue(User user) {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();
		logger.underlyingLogger().debug("buildUserFollowingQueue starts");
		
		List<FollowSocialRelation> followings = FollowSocialRelation.getUserFollowings(user.id);
		for (SocialRelation socaiRelation : followings){
			Set<String> values = JedisCache.cache().getSortedSetDsc(FeedType.USER_POSTED+":"+socaiRelation.target, 0L);
			for (String value : values) {
				try {
					Long postId = Long.parseLong(value);
					JedisCache.cache().putToSortedSet(FeedType.HOME_FOLLOWING+":"+user.id, getScore(FeedType.USER_POSTED+":"+socaiRelation.target, postId) , postId.toString());
				} catch (Exception e) {
				}
			}
			JedisCache.cache().expire(FeedType.HOME_FOLLOWING+":"+user.id, 60 * 2); // expiration time 120 secs
		}

		sw.stop();
		logger.underlyingLogger().debug("buildUserFollowingQueue completed. Took "+sw.getElapsedSecs()+"s");
	}
	
	public static boolean isLiked(Long userId, Long postId) {
		 String key = FeedType.USER_LIKED+":"+userId;
	     return JedisCache.cache().isMemberOfSortedSet(key, postId.toString());
	}

	public static List<Long> getCategoryPopularFeed(Long id, Double offset) {
		Set<String> values = JedisCache.cache().getSortedSetDsc(FeedType.CATEGORY_POPULAR+":"+id, offset);
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
		Set<String> values = JedisCache.cache().getSortedSetDsc(FeedType.CATEGORY_NEWEST+":"+id, offset);
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
		Set<String> values = JedisCache.cache().getSortedSetAsc(FeedType.CATEGORY_PRICE_LOW_HIGH+":"+id, offset);
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
		Set<String> values = JedisCache.cache().getSortedSetDsc(FeedType.CATEGORY_PRICE_LOW_HIGH+":"+id, offset);
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
		if(!JedisCache.cache().exists(FeedType.HOME_EXPLORE+":"+id)){
			buildUserExplorerFeedQueue(User.findById(id));
		}
		Set<String> values = JedisCache.cache().getSortedSetDsc(FeedType.HOME_EXPLORE+":"+id, offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;

	}
	
	public static List<Long> getHomeFollowingFeed(Long id, Long offset) {
		if(!JedisCache.cache().exists(FeedType.HOME_FOLLOWING+":"+id)){
			buildUserFollowingFeedQueue(User.findById(id));
		}
		Set<String> values = JedisCache.cache().getSortedSetDsc(FeedType.HOME_FOLLOWING+":"+id, offset);
        final List<Long> postIds = new ArrayList<>();
        for (String value : values) {
            try {
                postIds.add(Long.parseLong(value));
            } catch (Exception e) {
            }
        }
        return postIds;

	}
	
	public static List<Long> getUserPostFeeds(Long id, Double offset) {
		Set<String> values = JedisCache.cache().getSortedSetDsc(FeedType.USER_POSTED+":"+id, offset);
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
		Set<String> values = JedisCache.cache().getSortedSetDsc(FeedType.USER_LIKED+":"+id, offset);
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
		calculateBaseScore(post);
		buildPriceHighLowPostQueue(post);
		buildNewestPostQueue(post);
		buildPopularPostQueue(post);
	}
	
	public static void removeFromCategoryQueues(Long postId, Long categoryId){
		removeMemberFromPriceHighLowPostQueue(postId, categoryId);
		removeMemberFromNewestPostQueue(postId, categoryId);
		removeMemberFromPopularPostQueue(postId, categoryId);
	}
	
	public static void removeMemberFromPriceHighLowPostQueue(Long postId, Long categoryId){
		JedisCache.cache().removeMemberFromSortedSet(FeedType.CATEGORY_POPULAR+":"+categoryId, postId.toString());
	}
	
	public static void removeMemberFromNewestPostQueue(Long postId, Long categoryId){
		JedisCache.cache().removeMemberFromSortedSet(FeedType.CATEGORY_NEWEST+":"+categoryId, postId.toString());
	}

	public static void removeMemberFromPopularPostQueue(Long postId, Long categoryId){
		JedisCache.cache().removeMemberFromSortedSet(FeedType.CATEGORY_PRICE_LOW_HIGH+":"+categoryId, postId.toString());
	}
	
	public static void addToLikeQueue(Long postId, Long userId, Double score){
		JedisCache.cache().putToSortedSet(FeedType.USER_LIKED+":"+userId, score, postId.toString());
	}
	
	public static void removeFromLikeQueue(Long postId, Long userId){
		JedisCache.cache().removeMemberFromSortedSet(FeedType.USER_LIKED+":"+userId, postId.toString());
	}

	public static void addToPostQueue(Long postId, Long userId, Double score){
		JedisCache.cache().putToSortedSet(FeedType.USER_POSTED+":"+userId, score, postId.toString());
	}
	
	public static void removeFromPostQueue(Long postId, Long userId){
		JedisCache.cache().removeMemberFromSortedSet(FeedType.USER_POSTED+":"+userId, postId.toString());
	}
	
	public static Double getScore(String key, Long postId){
		return JedisCache.cache().getScore(key, postId.toString());
	}

	/**
	 * Remove deleted / sold products from all category feeds
	 * @param postId
	 */
	public static void removeFromCategoryFeeds(Long postId) {
		
	}
	
	/**
	 * Remove deleted products from owner posted and liked feeds
	 * Remove deleted products from other users liked feeds (query from db)
	 * @param postId
	 */
	public static void removeFromUserFeeds(Long postId, Long userId) {
		
	}
}
