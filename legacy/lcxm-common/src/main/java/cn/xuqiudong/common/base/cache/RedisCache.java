package cn.xuqiudong.common.base.cache;

import cn.xuqiudong.common.base.tool.ApplicationContextHolder;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 将Redis作为二级缓存
 * Mybatis的二级缓存可以自动地对数据库的查询做缓存，并且可以在更新数据时同时自动地更新缓存。
 * 实现Mybatis的二级缓存很简单，只需要新建一个类实现org.apache.ibatis.cache.Cache接口即可。
 * 该接口共有以下 7个方法：
 *    1.String getId()：mybatis缓存操作对象的标识符。一个mapper对应一个mybatis的缓存操作对象。
 *    2.void putObject(Object key, Object value)：将查询结果塞入缓存。
 *    3.Object getObject(Object key)：从缓存中获取被缓存的查询结果。
 *    4.Object removeObject(Object key)：从缓存中删除对应的key、value。只有在回滚时触发。一般我们也可以不用实现，具体使用方式请参考：org.apache.ibatis.cache.decorators.TransactionalCache。
 *    5.void clear()：发生更新时，清除缓存。
 *    6.int getSize()：可选实现。返回缓存的数量。
 *    7.ReadWriteLock getReadWriteLock()：可选实现。用于实现原子性的缓存操作。
 * @author Vic.xu
 */
public class RedisCache implements Cache {

    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);
    /*
    https://blog.csdn.net/mingshen3/article/details/88996514?utm_medium=distribute.pc_relevant.none-task-blog-baidulandingword-2&spm=1001.2101.3001.4242
     */

    /*
     * https://blog.csdn.net/xushiyu1996818/article/details/89215428  :   hash cache
     */

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /**
     * cache instance id  always    the  namespace of  mapper
     */
    private final String id;

    /**
     * redisTemplate name     redisTemplateCustomize
     */
    private static String REDIS_TEMPLATE_NAME = "redisTemplateCustomize";
    private RedisTemplate<String, Object> redisTemplate;
    /**
     *  redis过期时间
     */
    private static final long EXPIRE_TIME_IN_MINUTES = 120;


    public static void setRedisTemplateName(String name) {
        RedisCache.REDIS_TEMPLATE_NAME = name;
    }

    public RedisCache(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Put query result to redis
     *
     * @param key key
     * @param value v
     */
    @Override
    public void putObject(Object key, Object value) {
        logger.debug("putObject id=[{}],key=[{}]", id, key);
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        HashOperations<String, String, Object> opsForHash = redisTemplate.opsForHash();
        opsForHash.put(id, String.valueOf(key), value);
        setExpire();
    }

    /**
     * Get cached query result from redis
     *
     * @param key key
     * @return cached value
     */
    @Override
    public Object getObject(Object key) {
        logger.debug("getObject id=[{}],key=[{}]", id, key);
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        HashOperations<String, String, Object> opsForHash = redisTemplate.opsForHash();
        logger.debug("redis cache " + id + " get key: " + key.toString());
        Object object = opsForHash.get(id, key);
        setExpire();
        return object;
    }

    /**
     * Remove cached query result from redis
     *
     * @param key k
     * @return Removed Object
     */
    @Override
    public Object removeObject(Object key) {
        logger.debug("removeObject id=[{}],key=[{}]", id, key);
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        HashOperations<String, String, Object> opsForHash = redisTemplate.opsForHash();
        opsForHash.delete(id, key);
        setExpire();
        return null;
    }

    /**
     * Clears this cache instance
     */
    @Override
    public void clear() {
        logger.debug("clear id=[{}]", id);
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        redisTemplate.execute((RedisCallback<String>) connection -> {
            redisTemplate.delete(id);
            setExpire();
            return null;
        });
    }

    @Override
    public int getSize() {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        HashOperations<String, String, Object> opsForHash = redisTemplate.opsForHash();
        Long size = opsForHash.size(id);
        return size.intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    private RedisTemplate<String, Object> getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = ApplicationContextHolder.getBean(REDIS_TEMPLATE_NAME);
        }
        return redisTemplate;
    }


    /**
     * 每次操作后 设置过期时间
     * 其实这么做来，还不如不使用hash，直接键值对操作算了
     */
    public void setExpire() {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        redisTemplate.expire(id, EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
    }
}
