package cn.xuqiudong.common.base.cache;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 描述:这是一个简单的缓存key生成器,满足key为简单参数的缓存处理器: 一般只作用在方法级别的注解上;
 * 之所以在生成的key上没有加上方法名，是因为对于一个资源既可能是缓存，也可能是驱逐，二者的需要能一致；
 * <p>
 *     1.使用方法，把此方法注入spring， bean的name设置为 CommonKeyGenerator.GENERATOR_NAME
 *     2. 缓存方法的Cacheable/CacheEvict/CachePut注解的keyGenerator属性设置为CommonKeyGenerator.GENERATOR_NAME
 * </p>
 * @author Vic.xu
 * @since 2021-12-07 9:24
 */
public class CommonKeyGenerator implements KeyGenerator {

    /**
     * 当前bean注册到容器的时候,使用的名字 和本参数保持一致,方便在缓存注解中使用此参数
     */
    public static final String GENERATOR_NAME = "commonKeyGenerator";

    /**
     * 这个分隔符非常重要,请自行查看REDIS命名空间相关
     */
    private static final String DELIMITER = ":";

    /**
     * 若多个参数 组成key的时候的连接符
     */
    private static final String PARAMS_DELIMITER = "-";

    /**
     * 万一参数是空呢
     */
    private static final String EMPTY = "0";

    /**
     * 缓存key的默认前缀
     */
    private static final String DEFAULT_PREFIX = "cache";

    /**
     * 缓存key的前缀
     */
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 缓存方法名:方法名:参数-参数-参数
     */
    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(getCacheName(method)).append(DELIMITER);
        if (params == null || params.length == 0) {
            sb.append(EMPTY);
        } else {
            for (Object param : params) {
                if (param == null) {
                    continue;
                }

                //简单数据类型取自身 非简单数据类型取hashcode
                if (!isSimpleType(param.getClass())) {
                    param = param.hashCode();
                }
                sb.append(param).append(PARAMS_DELIMITER);
            }
        }
        //删除掉最后一个分隔符号
        if (sb.toString().endsWith(PARAMS_DELIMITER)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 是否是简单的数据类型:此处只做部分简单的判断8中基本数据类型及包装类和string /Number /date
     * @return
     */
    public static boolean isSimpleType(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz) || CharSequence.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz);
    }

    /**
     * 获取方法去上的缓存方法, 并新增前缀
     * @param method
     * @return
     */
    private String getCacheName(Method method) {
        String cacheName = "";
        if (method.isAnnotationPresent(Cacheable.class)) {
            Cacheable cacheable = method.getAnnotation(Cacheable.class);
            cacheName = getFirstCacheName(cacheable.value());
        } else if (method.isAnnotationPresent(CacheEvict.class)) {
            CacheEvict cacheable = method.getAnnotation(CacheEvict.class);
            cacheName = getFirstCacheName(cacheable.value());
        } else if (method.isAnnotationPresent(CachePut.class)) {
            CachePut cacheable = method.getAnnotation(CachePut.class);
            cacheName = getFirstCacheName(cacheable.value());
        }

        return addPrefix(cacheName);

    }

    /**
     * 增加前缀
     * @param cacheName
     * @return
     */
    private String addPrefix(String cacheName) {
        String prefix = StringUtils.hasText(getPrefix()) ? DEFAULT_PREFIX : getPrefix();
        return prefix + DELIMITER + cacheName;
    }


    private static String getFirstCacheName(String[] names) {
        if (names != null && names.length > 0) {
            return names[0];
        }
        return "";
    }

}