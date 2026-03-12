package cn.xuqiudong.basic.framework.code2text.cache;

import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 描述:
 * 某个具体的 Code2TextResolver 是否提供缓存预热功能
 * 只有支持预热的 resolver 才实现该接口
 * - Code2TextResolver —— 基础能力
 *  -Code2TextPreloadable —— 可预热能力（可选）
 * @author Vic.xu
 * @since 2026-01-16 11:26
 */
public interface Code2TextPreloadable<A extends Annotation> extends Code2TextResolver<A> {

    /**
     * 预加载
     * 可以按需 实现，对于非常大的数据，可以只加载热点数据
     * @return 缓存数据
     */
    Map<String, String> preload();
}
