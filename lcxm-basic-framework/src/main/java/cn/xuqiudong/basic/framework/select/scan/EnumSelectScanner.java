package cn.xuqiudong.basic.framework.select.scan;

import cn.xuqiudong.basic.framework.select.EnumSelectable;
import cn.xuqiudong.basic.framework.select.annotation.RegisterSelectEnum;
import cn.xuqiudong.basic.framework.select.registry.EnumSelectRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述:
 * Spring启动时扫描带@RegisterSelectEnum的枚举并注册到 EnumSelectRegistry
 * <p>
 * 扫描位置：
 * 1. 自行配置： lcxm.framework.enum.scan-base-packages=cn.xuqiudong,com.example
 * 2. 没有配置则扫描启动类所在包
 * </p>
 *
 * @author Vic.xu
 * @see RegisterSelectEnum
 * @since 2025-11-14 9:03
 */
public class EnumSelectScanner implements SmartInitializingSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumSelectScanner.class);


    private final ApplicationContext applicationContext;
    private final String scanPackage;

    public EnumSelectScanner(ApplicationContext applicationContext, String scanPackage) {
        this.applicationContext = applicationContext;
        this.scanPackage = scanPackage;
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] scanPackages = getScanPackages();
        if (ArrayUtils.isEmpty(scanPackages)) {
            LOGGER.warn("未配置扫描包，将跳过枚举扫描");
            return;
        }
        LOGGER.info("EnumScanInitializer 扫描包: {}", ArrayUtils.toString(scanPackages));

        Set<Class<? extends Enum<?>>> scannedClasses = new HashSet<>();
        for (String pkg : scanPackages) {
            scannedClasses.addAll(scanAnnotatedEnums(pkg));
        }
        LOGGER.info("扫描到被 @RegisterSelectEnum 标注的枚举数量为：{}", scannedClasses.size());

        // 4 注册到 EnumSelectRegistry

        for (Class<?> clazz : scannedClasses) {
            if (clazz.isEnum() && EnumSelectable.class.isAssignableFrom(clazz)) {
                RegisterSelectEnum annotation = clazz.getAnnotation(RegisterSelectEnum.class);
                String simpleName = clazz.getSimpleName();
                String value = annotation.value().isEmpty() ? simpleName : annotation.value();
                String desc = annotation.desc().isEmpty() ? simpleName : annotation.desc();
                //noinspection unchecked
                EnumSelectRegistry.register(value, (Class<? extends EnumSelectable>) clazz, desc);
            }
        }

        LOGGER.info("已注册的下拉框枚举数量为：{}", EnumSelectRegistry.getRegisteredEnums().size());
    }

    /**
     * 扫描指定包下的枚举
     */
    private Set<Class<? extends Enum<?>>> scanAnnotatedEnums(String basePackage) {
        Set<Class<? extends Enum<?>>> result = new HashSet<>();

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false) {
                    @Override
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        // 允许独立类（包括枚举、普通类）
                        return beanDefinition.getMetadata().isIndependent();
                    }
                };

        // 添加注解过滤器
        scanner.addIncludeFilter(new AnnotationTypeFilter(RegisterSelectEnum.class));

        for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
            String className = candidate.getBeanClassName();
            try {
                if (className == null) {
                    continue;
                }
                Class<?> clazz = Class.forName(className);
                if (clazz.isEnum()) {
                    //noinspection unchecked
                    result.add((Class<? extends Enum<?>>) clazz);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("无法加载类: {}", className, e);
            }
        }
        return result;

    }


    private String[] getScanPackages() {
        if (StringUtils.hasText(scanPackage)) {
            return scanPackage.split(",");
        }
        // 默认使用启动类所在包
        try {
            String[] beanNames = applicationContext.getBeanNamesForAnnotation(SpringBootApplication.class);
            if (beanNames.length > 0) {
                Class<?> mainClass = applicationContext.getType(beanNames[0]);
                if (mainClass != null) {
                    return new String[]{mainClass.getPackage().getName()};
                }
            }
        } catch (Exception e) {
            LOGGER.warn("无法自动识别主启动类", e);
        }
        return new String[0];
    }
}
