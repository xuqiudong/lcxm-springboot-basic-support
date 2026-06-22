package cn.xuqiudong.basic.framework.code2text.model;

import cn.xuqiudong.basic.framework.code2text.resolver.Code2TextResolver;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * Description:
 *   解析器的一些元数据
 * @author Vic.xu
 * @since 2026-06-21 15:46
 */
@Data
public class ResolverMeta {

    /**
     * 解析器的名称: 唯一标识
     */
    private String name;

    /**
     * 解析器class
     */
    private Class<? extends Code2TextResolver> resolverClass;

    /**
     *  标识当前解析器是否为占位符解析器: 是则 最终使用具体的实现类，除非没有配置解析器，则使用占位符解析器
     */
    private boolean placeholder;

   public ResolverMeta(String name, Class<? extends Code2TextResolver> resolverClass) {
       Assert.notNull(name, "name must not be null");
       Assert.notNull(resolverClass, "resolverClass must not be null");
       this.name = name;
       this.resolverClass = resolverClass;
   }

   public String region() {
       return resolverClass.getSimpleName();
   }
}
