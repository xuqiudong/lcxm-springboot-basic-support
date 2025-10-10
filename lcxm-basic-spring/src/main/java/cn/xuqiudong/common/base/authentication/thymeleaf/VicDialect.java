package cn.xuqiudong.common.base.authentication.thymeleaf;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.processor.IProcessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 描述:自定义的权限方言 , 只需把当前bean注入springBoot即可
 *    权限标签使用方式：<tagName vic:auth="xxCode" ></tageName>   如<div kjlink:auth="update">xxx</div>
 *   参见：StandardDialect
 * @author Vic.xu
 * @since 2022-02-11 9:26
 */
public class VicDialect extends AbstractProcessorDialect {

    public static final String NAME = "vic";
    public static final String PREFIX = "vic";
    public static final int PROCESSOR_PRECEDENCE = 1000;

    /**
     * 其他的自定义的解析器
     */
    private List<AbstractProcessor> processorsSet = new ArrayList<>();


    public void addProcessor(AbstractProcessor processor) {
        this.processorsSet.add(processor);
    }

    public VicDialect() {
        super(NAME, PREFIX, PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> set = new HashSet<>();
        set.add(new AuthorityElementTagProcessor(dialectPrefix));
        set.add(new AuthorityAttributeTagProcessor(dialectPrefix));
        set.addAll(processorsSet);
        return set;
    }
}
