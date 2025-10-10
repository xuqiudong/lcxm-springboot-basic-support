package cn.xuqiudong.basic.generator.config;

/**
 * 描述:
 * 配置构建接口: 参考自 mybatisPlus 的 代码生成, 我觉得这个方式还不错, 借鉴一下
 *
 * @author Vic.xu
 * @since 2025-09-12 9:04
 */
public interface IConfigBuilder<T> {

    T build();
}
