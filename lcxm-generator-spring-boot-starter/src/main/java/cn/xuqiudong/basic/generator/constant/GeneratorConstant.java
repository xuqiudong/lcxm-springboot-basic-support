package cn.xuqiudong.basic.generator.constant;

/**
 * 描述:
 * 代码生成中使用到的一些常量
 *
 * @author Vic.xu
 * @since 2025-09-12 17:05
 */
public interface GeneratorConstant {

    /**
     * 实体模板路径 (不带后缀)
     */
    String TEMPLATE_ENTITY_JAVA = "/templates/entity.java";

    /**
     * 查询对象模板路径 (不带后缀)
     */
    String TEMPLATE_QUERY_JAVA = "/templates/query.java";

    /**
     * 控制器模板路径 (不带后缀)
     */
    String TEMPLATE_CONTROLLER = "/templates/controller.java";

    /**
     * Mapper模板路径 (不带后缀)
     */
    String TEMPLATE_MAPPER = "/templates/mapper.java";

    /**
     * MapperXml模板路径 (不带后缀)
     */
    String TEMPLATE_XML = "/templates/mapper.xml";

    /**
     * Service模板路径 (不带后缀)
     */
    String TEMPLATE_SERVICE = "/templates/service.java";

    /**
     * entity 所在包
     */
    String PACKAGE_ENTITY = "entity";

    /**
     * query 所在包
     */
    String PACKAGE_QUERY = "query";
    /**
     * controller 所在包
     */
    String PACKAGE_CONTROLLER = "controller";
    /**
     * mapper 所在包
     */
    String PACKAGE_MAPPER = "mapper";
    /**
     * service 所在包
     */
    String PACKAGE_SERVICE = "service";
    /**
     * mapper 所在路径, 和 mapper 接口同包
     */
    String PATH_MAPPER = "mapper";

    /**
     * 点
     */
    String DOT = ".";

    /**
     * java 后缀
     */
    String JAVA_SUFFIX = ".java";
    /**
     * xml 后缀
     */
    String XML_SUFFIX = ".xml";

}
