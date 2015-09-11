package common.cache;

import play.Play;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.typesafe.plugin.RedisPlugin;
import common.serialize.JsonSerializer;

import java.util.Set;


public class JedisCache {
    private static final play.api.Logger logger = play.api.Logger.apply(JedisCache.class);
    
    private static final String SYS_PREFIX = Play.application().configuration().getString("keyprefix", "prod_");
    
    // All Redis Cache Key Prefix
    public static final String ARTICLE_SLIDER_PREFIX = SYS_PREFIX + "user_sc_";
    public static final String USER_FRIENDS_PREFIX = SYS_PREFIX + "user_frd_";
    public static final String SOCIAL_FEED_PREFIX = SYS_PREFIX + "user_";
    public static final String BIZ_FEED_PREFIX = SYS_PREFIX + "biz_";
    public static final String PN_FEED_PREFIX = SYS_PREFIX + "pn_";
    public static final String KG_FEED_PREFIX = SYS_PREFIX + "kg_";
    public static final String COMMUNITY_POST_PREFIX = SYS_PREFIX + "comm_";
    public final static String TODAY_WEATHER_KEY = "TODAY_WEATHER";
    
    private static JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
    
    private static JedisCache cache = new JedisCache();
    
    public enum Status {
        OK,
        ERROR
    }
    
    public static JedisCache cache() {
        return cache;
    }
    
    private JedisCache() {
    }
    
    public void putObj(String key, Object object) {
        putObj(key, object, -1);
    }
    
    public void putObj(String key, Object object, int expire) {
        String json = JsonSerializer.serialize(object);
        cache.put(key, json, expire);
    }
    
    public Object getObj(String key, Class<?> clazz) {
        Object object = null;
        String json = cache.get(key);
        if (json == null) {
            return null;
        } else {
            object = JsonSerializer.deserialize(json, clazz);
        }
        return object;
    }
    
    public Status put(String key, String value) {
        return put(key, value, -1);
    }
    
    public Status put(String key, String value, int expire) {
        Jedis j = null;
        try {
            j = getResource();
            String ret = j.set(key, value);
            if (!"OK".equalsIgnoreCase(ret)) {
                logger.underlyingLogger().error(ret);
                return Status.ERROR;
            }
            if (expire != -1) {
                cache.expire(key, expire);
            }
            return Status.OK;
        } finally {
            if (j != null)
                returnResource(j);
        }
    }

    public String get(String key) {
        Jedis j = null;
        try {
            j = getResource();
            if (!j.exists(key)) {
                return null;
            }
            String value = j.get(key);
            if ("".equals(value.trim())) {
                j.del(key);     // del key with invalid value
                return null;
            }
            return value;
        } finally {
            if (j != null)
                returnResource(j);
        }
    }

    ////////////////////////////////////////
    // Set operations
    public Status putToSet(String key, String value) {
        Jedis j = null;
        try {
            j = getResource();
            j.sadd(key, value);
            return Status.OK;
        } finally {
            if (j != null)
                returnResource(j);
        }
    }

    public Set<String> getSetMembers(String key) {
        Jedis j = null;
        try {
            j = getResource();
            return j.smembers(key);
        } finally {
            if (j != null)
                returnResource(j);
        }
    }

    public boolean isMemberOfSet(String key, String value) {
        Jedis j = null;
        try {
            j = getResource();
            return j.sismember(key, value);
        } finally {
            if (j != null)
                returnResource(j);
        }
    }

    public void removeMemberFromSet(String key, String value) {
        Jedis j = null;
        try {
            j = getResource();
            j.srem(key, value);
        } finally {
            if (j != null)
                returnResource(j);
        }
    }
    ////////////////////////////////////////


    public boolean exists(String key) {
        Jedis j = null;
        try {
            j = getResource();
            return j.exists(key);
        } finally {
            if (j != null)
                returnResource(j);
        }
    }
    
    public long remove(String key) {
        Jedis j = null;
        try {
            j = getResource();
            return j.del(key);
        } finally {
            if (j != null)
                returnResource(j);
        }
    }
    
    public long expire(String key, int secs) {
        Jedis j = null;
        try {
            j = getResource();
            return j.expire(key, secs);
        } finally {
            if (j != null)
                returnResource(j);
        }
    }
    
    private Jedis getResource() {
        return jedisPool.getResource();
    }
    
    private void returnResource(Jedis j) {
        jedisPool.returnResource(j);
    }
}
