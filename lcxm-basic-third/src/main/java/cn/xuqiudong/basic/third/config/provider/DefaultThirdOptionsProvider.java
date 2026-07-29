package cn.xuqiudong.basic.third.config.provider;

import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;

/**
 * 默认出站参数 provider。
 *
 * <p>所有第三方共用同一份默认参数。</p>
 *
 * @author Vic.xu
 */
public class DefaultThirdOptionsProvider implements ThirdOptionsProvider {

    private final ThirdClientOptions defaultOptions;

    public DefaultThirdOptionsProvider() {
        this(new ThirdClientOptions());
    }

    /**
     * 使用指定默认参数创建 provider。
     */
    public DefaultThirdOptionsProvider(ThirdClientOptions defaultOptions) {
        this.defaultOptions = defaultOptions == null ? new ThirdClientOptions() : defaultOptions;
    }

    @Override
    public ThirdClientOptions getOptions(String thirdCode) {
        return defaultOptions;
    }
}
