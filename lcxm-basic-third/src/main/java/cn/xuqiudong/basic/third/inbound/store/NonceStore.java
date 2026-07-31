package cn.xuqiudong.basic.third.inbound.store;

/**
 * nonce 防重存储接口。
 *
 * <p>仅在 {@code InboundAppConfig.nonceRequired=true} 时需要。</p>
 *
 * @author Vic.xu
 */
public interface NonceStore {

    /**
     * 保存 nonce，已存在时返回 false。
     *
     * @param appId 第三方 appId
     * @param nonce 第三方请求随机串
     * @param ttlSeconds nonce 防重有效期，单位秒，通常与签名时间窗一致
     * @return true 表示首次保存成功；false 表示重复 nonce
     */
    boolean saveIfAbsent(String appId, String nonce, long ttlSeconds);
}
