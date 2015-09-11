package common.cache;

import akka.actor.ActorSystem;
import common.utils.NanoSecondStopWatch;
import models.SocialRelation;
import play.db.jpa.JPA;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * Date: 31/7/14
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class FriendCache {
    private static final play.api.Logger logger = play.api.Logger.apply(FriendCache.class);

    /**
     * @param userId
     * @return
     */
    public static List<Long> getFriendsIds(Long userId) {
        String key = JedisCache.USER_FRIENDS_PREFIX+userId;
        Set<String> values = JedisCache.cache().getSetMembers(key);

        final List<Long> friendIds = new ArrayList<>();
        for (String value : values) {
            try {
                friendIds.add(Long.parseLong(value));
            } catch (Exception e) {
                logger.underlyingLogger().error("Error in parsing friend id: "+value, e);
            }
        }
        return friendIds;
    }

    /**
     * @param userIdA
     * @param userIdB
     * @return
     */
    public static boolean areFriends(Long userIdA, Long userIdB) {
        String keyA = JedisCache.USER_FRIENDS_PREFIX+userIdA;
        return JedisCache.cache().isMemberOfSet(keyA, userIdB.toString());
    }

    /**
     * @param userIdA
     * @param userIdB
     */
    public static void onBecomeFriend(Long userIdA, Long userIdB) {
        String keyA = JedisCache.USER_FRIENDS_PREFIX+userIdA;
        JedisCache.cache().putToSet(keyA, userIdB.toString());

        String keyB = JedisCache.USER_FRIENDS_PREFIX+userIdB;
        JedisCache.cache().putToSet(keyB, userIdA.toString());
    }

    /**
     * @param userIdA
     * @param userIdB
     */
    public static void onUnFriend(Long userIdA, Long userIdB) {
        String keyA = JedisCache.USER_FRIENDS_PREFIX+userIdA;
        JedisCache.cache().removeMemberFromSet(keyA, userIdB.toString());

        String keyB = JedisCache.USER_FRIENDS_PREFIX+userIdB;
        JedisCache.cache().removeMemberFromSet(keyB, userIdA.toString());
    }

    /**
     * On system startup. Bootstrap friends sets.
     */
	public static void bootstrapFriendsSets() {
		ActorSystem actorSystem = Akka.system();
		 actorSystem.scheduler().scheduleOnce(
			Duration.create(0, TimeUnit.MILLISECONDS),
			new Runnable() {
				public void run() {
					JPA.withTransaction(new play.libs.F.Callback0() {
                        @Override
                        public void invoke() throws Throwable {
                            NanoSecondStopWatch sw = new NanoSecondStopWatch();

                            final List<BigInteger> userIds = JPA.em().createNativeQuery("SELECT id from User").getResultList();
                            logger.underlyingLogger().info("bootstrapFriendsSets - start. User count: " + userIds.size());

                            // remove friends set for each user
                            for (BigInteger userId : userIds) {
                                String key = JedisCache.USER_FRIENDS_PREFIX+userId;
                                JedisCache.cache().remove(key);

                            }

                            // Populate friends links
                            Query q = JPA.em().createQuery(
                                "SELECT actor, target from SocialRelation where action = ?1 and actionType =?2"
                            );
                            q.setParameter(1, SocialRelation.Action.FRIEND);
                            q.setParameter(2, SocialRelation.ActionType.GRANT);
                            final List<Object[]> friendLinks = q.getResultList();
                            logger.underlyingLogger().info("bootstrapFriendsSets. Friend links: " + friendLinks.size());

                            for (Object[] friendLink : friendLinks) {
                                Long userIdA = (Long) friendLink[0];
                                Long userIdB = (Long) friendLink[1];
                                onBecomeFriend(userIdA, userIdB);
                            }

                            sw.stop();
                            logger.underlyingLogger().info("bootstrapFriendsSets - end. Took "+sw.getElapsedMS()+"ms");
                        }
                    });
			    }
            }, actorSystem.dispatcher()
        );
	}
}
