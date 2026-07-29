package cn.xuqiudong.basic.third.config.provider;

import cn.xuqiudong.basic.third.config.model.ThirdClientOptions;

/**
 * 按第三方编码提供出站运行参数。
 *
 * @author Vic.xu
 */
public interface ThirdOptionsProvider {

    /**
     * 获取指定第三方的运行参数。
     */
    ThirdClientOptions getOptions(String thirdCode);
}
