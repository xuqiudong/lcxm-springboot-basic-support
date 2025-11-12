package cn.xuqiudong.basic.generator.enums;

/**
 * 描述:
 * 生成的模板类型
 *
 * @author Vic.xu
 * @since 2025-09-11 10:43
 */
public enum TemplateType {
    /**
     * 实体类
     */
    ENTITY,
    /**
     * QUERY
     */
    QUERY,
    /**
     * mapper接口
     */
    MAPPER,
    /**
     * mapper.xml
     */
    XML,
    /**
     * service接口
     */
    SERVICE,
    /**
     * rest controller
     */
    CONTROLLER,

    /**
     * 自定义模板：  需要自己写模板文件(.ftl)，
     * 上下文依然参见：
     * @see cn.xuqiudong.basic.generator.model.context.TemplateContext
     *
     */
    CUSTOMIZE;




}
