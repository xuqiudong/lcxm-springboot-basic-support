package cn.xuqiudong.common.base.select.config;

import cn.xuqiudong.common.base.select.EnumSelectable;
import cn.xuqiudong.common.base.select.annotation.RegisterSelectEnum;
import cn.xuqiudong.common.base.select.registry.EnumSelectRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 描述:
 * Spring启动时扫描带@RegisterSelectEnum的枚举并注册
 * @see RegisterSelectEnum
 * @author Vic.xu
 * @since 2025-11-13 17:31
 */
public class EnumScanInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 扫描所有带@RegisterEnum注解的类
        Set<Class<?>> scannedClasses = scanWithAnnotation(RegisterSelectEnum.class);

        // 筛选枚举并注册
        for (Class<?> clazz : scannedClasses) {
            if (clazz.isEnum() && EnumSelectable.class.isAssignableFrom(clazz)) {
                RegisterSelectEnum annotation = clazz.getAnnotation(RegisterSelectEnum.class);
                String simpleName = clazz.getSimpleName();
                String value = annotation.value();
                if (StringUtils.isBlank(value)) {
                    value = simpleName;
                }
                String desc = annotation.desc();
                if (StringUtils.isBlank(desc)) {
                    desc = simpleName;
                }
                EnumSelectRegistry.register(value, (Class<? extends EnumSelectable>) clazz, desc);
            }
        }
    }

    // 扫描指定注解的类（基于Spring的类路径扫描）
    private Set<Class<?>> scanWithAnnotation(Class<? extends Annotation> annotation) {
        // 实际实现可使用Spring的ClassPathScanningCandidateComponentProvider
        // 这里简化处理，实际开发中需完善扫描逻辑（支持自定义扫描包）
        return SpringFactoriesLoader.loadFactoryNames(annotation, ClassUtils.getDefaultClassLoader())
                .stream()
                .map(className -> {
                    try {
                        return ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("扫描枚举失败：" + className, e);
                    }
                })
                .collect(java.util.stream.Collectors.toSet());
    }
}
