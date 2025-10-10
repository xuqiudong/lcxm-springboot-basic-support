package cn.xuqiudong.basic.generator.model.context;

import cn.xuqiudong.basic.generator.config.GlobalConfig;
import cn.xuqiudong.basic.generator.model.TableInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.ReflectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述:
 * 模板的数据格式
 *
 * @author Vic.xu
 * @since 2025-09-12 17:27
 */
@Getter
@Setter
public class TemplateContext {
    /**
     * 作者
     */
    private String author;

    /**
     * 是否支持lombok
     */
    private boolean lombok;

    /**
     * 是否 使用springdoc  注解
     */
    private boolean springdoc;

    /**
     * 时间
     */
    private String datetime;

    /**
     * 表数据 对应 mapper.xml  模板
     */
    private TableInfo table;

    /**
     * 实体信息: 用于 entity.java 模板
     */
    private EntityContext entity;

    /**
     * mapper信息: 用于 mapper.java 和mapper.xmk 模板
     */
    private MapperContext mapper;

    /**
     * service信息: 用于 service.java 模板
     */
    private ServiceContext service;

    /**
     * controller信息: 用于 controller.java 模板
     */
    private ControllerContext controller;



    public TemplateContext(GlobalConfig globalConfig) {
        this.author = globalConfig.getAuthor();
        this.lombok = globalConfig.isLombok();
        this.springdoc = globalConfig.isSpringdoc();
        this.datetime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm");
    }

    /**
     * 模板上下文数据模型 转为map 类型
     */
    public Map<String, Object> toMapContext() {
        Map<String, Object> mapContext = new HashMap<>();
        // 允许访问私有字段
        ReflectionUtils.doWithFields(this.getClass(), field -> {
            field.setAccessible(true);
            // 值为原始对象
            mapContext.put(field.getName(), field.get(this));
        });
        return mapContext;
    }


    public TemplateContext() {
        this.datetime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm");
    }


}
