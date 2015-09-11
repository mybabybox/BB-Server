package common.cache;

import common.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 19/7/14
 * Time: 12:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleSliderCache {
    private static final play.api.Logger logger = play.api.Logger.apply(ArticleSliderCache.class);

    private static final int ttlSecs = 15 * 60;     // 15 min

    /**
     * @param userId
     * @param k
     * @return
     */
    public static List<Long> getTargetedArticles(Long userId, int k) {
        String key = JedisCache.ARTICLE_SLIDER_PREFIX+userId+"_"+k;
        String value = JedisCache.cache().get(key);

        List<Long> result = null;
        if (value != null) {
            result = new ArrayList<>();
            String[] tokens = value.split(",");
            for (String token : tokens) {
                try {
                    result.add(Long.parseLong(token));
                } catch (Exception e) {
                    logger.underlyingLogger().error("Error in parsing sc id: "+token, e);
                }
            }
        }

        return result;
    }

    /**
     * @param userId
     * @param k
     * @param scIds
     */
    public static void cacheTargetedArticles(Long userId, int k, List<Long> scIds) {
        String key = JedisCache.ARTICLE_SLIDER_PREFIX+userId+"_"+k;
        String value = StringUtil.collectionToString(scIds, ",");

        JedisCache.cache().put(key, value, ttlSecs);
    }

}
