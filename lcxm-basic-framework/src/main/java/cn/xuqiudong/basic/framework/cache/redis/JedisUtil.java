package cn.xuqiudong.basic.framework.cache.redis;

import cn.xuqiudong.basic.framework.util.ApplicationPropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.Tuple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jedis 工具类
 *
 * @author Vic.xu
 * @since 2021/08/26
 */

/**
 * spring.redis.host=127.0.0.1 <br />
 * spring.redis.password=123456!@#$%^ <br />
 * spring.redis.database=6 <br />
 * spring.redis.port=6379 <br />
 * spring.redis.timeout=5000 <br />
 * # springboot2 默认推荐使用lettuce连接池 <br />
 * spring.redis.lettuce.pool.max-active=20 <br />
 * spring.redis.lettuce.pool.max-idle=20 <br />
 * spring.redis.lettuce.pool.min-idle=10 <br />
 * spring.redis.lettuce.pool.max-wait=-1 <br />
 */
@Deprecated
@SuppressWarnings("PMD")
public class JedisUtil {

    protected static final Logger logger = LoggerFactory.getLogger(JedisUtil.class);

    private static JedisPool jedisPool = null;

    private static Charset charset = Charset.forName("UTF-8");

    static {
        init();
    }

    public static void init() {
        if (jedisPool != null) {
            jedisPool.close();
        }

        /*
         *  
            spring.redis.host=127.0.0.1
        	spring.redis.password=123456!@#$%^
        	spring.redis.database=6
        	spring.redis.port=6379
        	spring.redis.timeout=5000
        	#  springboot2 默认推荐使用lettuce连接池
        	spring.redis.lettuce.pool.max-active=20
        	spring.redis.lettuce.pool.max-idle=20
        	spring.redis.lettuce.pool.min-idle=10
        	spring.redis.lettuce.pool.max-wait=-1
         */
        int maxIdle = ApplicationPropertiesUtil.getInt("spring.redis.lettuce.pool.max-idle");
        long maxWaitMillis = ApplicationPropertiesUtil.getLong("spring.redis.lettuce.pool.max-wait");
        int maxTotal = ApplicationPropertiesUtil.getInt("spring.redis.lettuce.pool.max-idle");
        String redisIp = ApplicationPropertiesUtil.getString("spring.redis.host");
        int port = ApplicationPropertiesUtil.getInt("spring.redis.port");
        String password = ApplicationPropertiesUtil.getString("spring.redis.password");
        int database = ApplicationPropertiesUtil.getInt("spring.redis.database");
        logger.info(
                "jedisUtil   config:\n  maxIdle={}, maxWaitMillis={}, maxTotal={},redisIp={},port={},password={},database={}",
                new Object[]{maxIdle, maxWaitMillis, maxTotal, redisIp, port, password, database});

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMaxTotal(maxTotal);
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool(config, redisIp, port, 100000, password, database);
        // jedisPool = new JedisPool(config, redisIp, port, 10000);
        // jedisPool = new JedisPool(config, redisIp, port, 10000, KEY_PREFIX);
    }

    public static void main(String[] args) {
        JedisUtil.get("aa");
    }

    public static JedisPool getPool() {
        return jedisPool;
    }

    /**
     * 获取缓存
     *
     * @param key
     *            键
     * @return 值
     */
    public static String get(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("get {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key
     *            键
     * @return 值
     */
    public static Object getObject(String key) {
        Object value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = toObject(jedis.get(getBytesKey(key)));
                logger.debug("getObject {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObject {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置缓存
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param cacheSeconds
     *            超时时间，0为不超时
     * @return
     */
    public static String set(String key, String value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("set {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 重新设置过期时间
     *
     * @param key
     * @param cacheSeconds
     * @return
     */
    public static String expire(String key, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("expire {} = {}", key, cacheSeconds);
        } catch (Exception e) {
            logger.warn("expire {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static Long zadd(String key, long score, String member) {
        Long result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.zadd(key, score, member);
        } catch (Exception e) {
            logger.warn("setObject {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置缓存
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param cacheSeconds
     *            超时时间，0为不超时
     * @return
     */
    public static String setObject(String key, Object value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(getBytesKey(key), toBytes(value));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObject {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setObject {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * key筛选
     *
     * @param pattern
     * @return
     */
    public static Set<String> keys(String pattern) {
        Set<String> result = new HashSet<String>();
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.keys(pattern);
        } catch (Exception e) {
            logger.warn("pattern {}", pattern, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取List缓存
     *
     * @param key
     *            键
     * @return 值
     */
    public static List<String> getList(String key) {
        List<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.lrange(key, 0, -1);
            }
        } catch (Exception e) {
            logger.warn("getList {}", key, e);

        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public static List<Tuple> zrangeWithScores(String key, int start, int end) {
        List<Tuple> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.zrangeWithScores(key, start, end);
            }
        } catch (Exception e) {
            logger.warn("getList {}", key, e);

        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取List缓存
     *
     * @param key
     *            键
     * @return 值
     */
    public static List<Object> getObjectList(String key) {
        List<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            byte[] keybyte = getBytesKey(key);
            if (jedis.exists(keybyte)) {
                List<byte[]> list = jedis.lrange(keybyte, 0, -1);
                value = new ArrayList<>();
                for (byte[] bs : list) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectList key={}, value= ", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectList {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 描述:根据key前缀模糊查询缓存对象
     * @param keyPrefix
     * @return List<Object>
     * @since 2016年9月5日
     * @author zhuliyun
     */
    public static List<Object> getObjectListByKeyPrefix(String keyPrefix) {
        List<Object> value = new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = getResource();
            Set<String> keys = getKeysByPrefix(keyPrefix);
            if (keys != null && keys.size() > 0) {
                for (String key : keys) {
                    Object object = getObject(key);
                    value.add(object);

                    logger.debug("getObjectList {} = {}", key, value);
                }

            }
        } catch (Exception e) {
            logger.warn("getObjectList {} ", keyPrefix, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置List缓存
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param cacheSeconds
     *            超时时间，0为不超时
     * @return
     */
    public static long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.rpush(key, value.toArray(new String[0]));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setList {}={} ", key, value);
        } catch (Exception e) {
            logger.warn("setList {}={} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置List缓存
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param cacheSeconds
     *            超时时间，0为不超时
     * @return
     */
    public static long setObjectList(String key, List<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            List<byte[]> list = new ArrayList<>();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (Exception e) {
            logger.warn("setObjectList {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key
     *            键
     * @param value
     *            值
     * @return
     */
    public static long listAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.rpush(key, value);
        } catch (Exception e) {
            logger.warn("listAdd {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key
     *            键
     * @param value
     *            值
     * @return
     */
    public static long listObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            List<byte[]> list = new ArrayList<>();
            for (Object o : value) {
                list.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) list.toArray());
            logger.debug("listObjectAdd {}={} ", key, value);
        } catch (Exception e) {
            logger.warn("listObjectAdd {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取缓存
     *
     * @param key
     *            键
     * @return 值
     */
    public static Set<String> getSet(String key) {
        Set<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.smembers(key);
                logger.debug("getSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getSet {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 描述:获取以指定开头前缀的所有Key
     * @param keyPrefix
     * @return Set<String>
     * @since 2016年9月5日
     * @author zhuliyun
     */
    public static Set<String> getKeysByPrefix(String keyPrefix) {
        Set<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            value = jedis.keys(keyPrefix + "*");
        } catch (Exception e) {
            logger.warn("getSet {} ", keyPrefix, e);
        } finally {
            returnResource(jedis);
        }
        return value;

    }

    /**
     * 获取缓存
     *
     * @param key
     *            键
     * @return 值
     */
    public static Set<Object> getObjectSet(String key) {
        Set<Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = new HashSet<Object>();
                Set<byte[]> set = jedis.smembers(getBytesKey(key));
                for (byte[] bs : set) {
                    value.add(toObject(bs));
                }
                logger.debug("getObjectSet {}={} ", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectSet {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置Set缓存
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param cacheSeconds
     *            超时时间，0为不超时
     * @return
     */
    public static long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.sadd(key, value.toArray(new String[0]));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setSet {}={} ", key, value);
        } catch (Exception e) {
            logger.warn("setSet {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Set缓存
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param cacheSeconds
     *            超时时间，0为不超时
     * @return
     */
    public static long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Set<byte[]> set = new HashSet<byte[]>();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (Exception e) {
            logger.warn("setObjectSet {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key
     *            键
     * @param value
     *            值
     * @return
     */
    public static long setSetAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.sadd(key, value);
            logger.debug("setSetAdd {}={} ", key, value);
        } catch (Exception e) {
            logger.warn("setSetAdd {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key
     *            键
     * @param value
     *            值
     * @return
     */
    public static long setSetObjectAdd(String key, Object... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            Set<byte[]> set = new HashSet<byte[]>();
            for (Object o : value) {
                set.add(toBytes(o));
            }
            result = jedis.rpush(getBytesKey(key), (byte[][]) set.toArray());
            logger.debug("setSetObjectAdd {}={} ", key, value);
        } catch (Exception e) {
            logger.warn("setSetObjectAdd {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取Map缓存
     *
     * @param key
     *            键
     * @return 值
     */
    public static Map<String, String> getMap(String key) {
        Map<String, String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.hgetAll(key);
                logger.debug("getMap {}={} ", key, value);
            }
        } catch (Exception e) {
            logger.warn("getMap {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取Map缓存
     *
     * @param key
     *            键
     * @return 值
     */
    public static Map<String, Object> getObjectMap(String key) {
        Map<String, Object> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                value = new HashMap<String, Object>();
                Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
                for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
                    value.put(StringUtils.toEncodedString(e.getKey(), charset), toObject(e.getValue()));
                }
                logger.debug("getObjectMap {} {} ", key, value);
            }
        } catch (Exception e) {
            logger.warn("getObjectMap {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置Map缓存
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param cacheSeconds
     *            超时时间，0为不超时
     * @return
     */
    public static String setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.hmset(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setMap {}={} ", key, value);
        } catch (Exception e) {
            logger.warn("setMap {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Map缓存
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param cacheSeconds
     *            超时时间，0为不超时
     * @return
     */
    public static String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                jedis.del(key);
            }
            Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setObjectMap {}={} ", key, value);
        } catch (Exception e) {
            logger.warn("setObjectMap {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key
     *            键
     * @param value
     *            值
     * @return
     */
    public static String mapPut(String key, Map<String, String> value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hmset(key, value);
            logger.debug("mapPut {}={} ", key, value);
        } catch (Exception e) {
            logger.warn("mapPut {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key
     *            键
     * @param value
     *            值
     * @return
     */
    public static String mapObjectPut(String key, Map<String, Object> value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
            for (Map.Entry<String, Object> e : value.entrySet()) {
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedis.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
            logger.debug("mapObjectPut {}={} ", key, value);
        } catch (Exception e) {
            logger.warn("mapObjectPut {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key
     *            键
     * @param mapKey
     *            值
     * @return
     */
    public static long mapRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(key, mapKey);
            logger.debug("mapRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapRemove {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key
     *            键
     * @param mapKey
     *            值
     * @return
     */
    public static long mapObjectRemove(String key, String mapKey) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapObjectRemove {}  ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key
     *            键
     * @param mapKey
     *            值
     * @return
     */
    public static boolean mapExists(String key, String mapKey) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(key, mapKey);
            logger.debug("mapExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapExists {}  ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key
     *            键
     * @param mapKey
     *            值
     * @return
     */
    public static boolean mapObjectExists(String key, String mapKey) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
            logger.debug("mapObjectExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapObjectExists {} ", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     *
     * @param key
     *            键
     * @return
     */
    public static long del(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                result = jedis.del(key);
                logger.debug("del {}", key);
            } else {
                logger.debug("del {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("del {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     *
     * @param key
     *            键
     * @return
     */
    public static long delObject(String key) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(getBytesKey(key))) {
                result = jedis.del(getBytesKey(key));
                logger.debug("delObject {}", key);
            } else {
                logger.debug("delObject {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("delObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     *
     * @param key
     *            键
     * @return
     */
    public static boolean exists(String key) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(key);
            logger.debug("exists {}", key);
        } catch (Exception e) {
            logger.warn("exists {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     *
     * @param key
     *            键
     * @return
     */
    public static boolean existsObject(String key) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(getBytesKey(key));
            logger.debug("existsObject {}", key);
        } catch (Exception e) {
            logger.warn("existsObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取资源
     *
     * @return
     * @throws JedisException
     */
    public static Jedis getResource() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // logger.debug("getResource.", jedis);
        } catch (JedisException e) {
            logger.warn("getResource.", e);
            returnBrokenResource(jedis);
            throw e;
        }
        return jedis;
    }

    /**
     * 归还资源
     *
     * @param jedis
     */
    public static void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {

            // jedisPool.returnBrokenResource(jedis);
            jedis.close();
        }
    }

    /**
     * 释放资源
     *
     * @param jedis
     */
    public static void returnResource(Jedis jedis) {
        if (jedis != null) {

            // jedisPool.returnResource(jedis);
            jedis.close();
        }
    }

    /**
     * 获取byte[]类型Key
     *
     * @param object
     * @return
     */
    public static byte[] getBytesKey(Object object) {
        if (object instanceof String) {
            return ((String) object).getBytes(charset);
        } else {
            return serialize(object);
        }
    }

    /**
     * 获取byte[]类型Key
     *
     * @param key
     * @return
     */
    public static Object getObjectKey(byte[] key) {
        try {
            return StringUtils.toEncodedString(key, charset);
        } catch (UnsupportedOperationException uoe) {
            try {
                return JedisUtil.toObject(key);
            } catch (UnsupportedOperationException uoe2) {
                uoe2.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * Object转换byte[]类型
     *
     * @param object
     * @return
     */
    public static byte[] toBytes(Object object) {
        return serialize(object);
    }

    /**
     * byte[]型转换Object
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        return unserialize(bytes);
    }

    /**
     * 序列化对象
     *
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            if (object != null) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化对象
     *
     * @param bytes
     * @return
     */
    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            if (bytes != null && bytes.length > 0) {
                bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
