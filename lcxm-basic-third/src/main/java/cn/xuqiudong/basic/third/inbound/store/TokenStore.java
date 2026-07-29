package cn.xuqiudong.basic.third.inbound.store;

import java.time.Duration;

import cn.xuqiudong.basic.third.inbound.model.TokenValue;

/**
 * token 存储接口。
 *
 * <p>公共模块只依赖该接口；业务项目可以选择 Redis、Caffeine 或自定义存储。</p>
 *
 * @author Vic.xu
 */
public interface TokenStore {

    /**
     * 保存 token。
     *
     * @param token token 字符串
     * @param value token 对应的业务信息
     * @param ttl token 有效期
     */
    void put(String token, TokenValue value, Duration ttl);

    /**
     * 根据 token 查询存储值。
     *
     * @param token token 字符串
     * @return token 存储值；不存在或已过期时返回 null
     */
    TokenValue get(String token);

    /**
     * 删除 token。
     *
     * @param token token 字符串
     */
    void remove(String token);
}
