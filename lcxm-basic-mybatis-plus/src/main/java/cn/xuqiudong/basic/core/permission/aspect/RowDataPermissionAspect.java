package cn.xuqiudong.basic.core.permission.aspect;
import cn.xuqiudong.basic.core.permission.RowDataHelper;
import cn.xuqiudong.basic.core.permission.annotation.RowDataPermission;
import cn.xuqiudong.basic.core.permission.enums.RowDataHandlerType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 描述:
 * 通过切面的方式 启动数据权限, 保证  MPDataPermissionHandler 的职责单一;
 * 与手动开启方式逻辑统一, 都是放在当前线程中
 *
 * @author Vic.xu
 * @since 2025-09-23 16:03
 */
@Aspect
public class RowDataPermissionAspect {


    /**
     * 环绕通知：在方法执行前后处理权限上下文 拦截所有标注了@RowDataPermission的方法，执行前解析注解
     */
    @Around("@annotation(rowDataPermission)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint, RowDataPermission rowDataPermission) throws Throwable {
        try {
            // 1.  此处并不 判断 RowDataHelper.getLocalAuthorityData() 是否存在,  两者是合并处理
            // 2. 解析注解中的权限规则
            for (RowDataPermission.Item item : rowDataPermission.value()) {
                Class<? extends Enum<? extends RowDataHandlerType>> typeClass = item.type();
                String typeValue = item.value();
                // 3. 获取枚举实例
                RowDataHandlerType type = resolvePermissionType(typeClass, typeValue);
                // 4. 填充到上下文（复用原有ThreadLocal逻辑）
                RowDataHelper.start(type, item.column(), item.jointLogic(), item.precondition());
            }
            // 2. 执行目标方法 分页应该在此处 执行两条sql
            return joinPoint.proceed();
        } finally {
            // 3. 方法执行后：强制清理权限上下文
            RowDataHelper.clear();
        }
    }


    /**
     * 将注解中配置的权限类型解析为实际的枚举实例
     *
     * @param typeClass 注解中配置的枚举类（必须实现RowDataHandlerType接口）
     * @param typeValue 注解中配置的枚举值名称（如"USER"）
     * @return 对应的RowDataHandlerType枚举实例
     */
    private RowDataHandlerType resolvePermissionType(
            Class<? extends Enum<? extends RowDataHandlerType>> typeClass,
            String typeValue) {
        try {
            // 1. 校验：枚举类必须实现RowDataHandlerType接口
            if (!RowDataHandlerType.class.isAssignableFrom(typeClass)) {
                throw new IllegalArgumentException(
                        "枚举类" + typeClass.getName() + "必须实现RowDataHandlerType接口"
                );
            }

            // 2. 从枚举类中查找指定名称的枚举实例
            Enum<?>[] enumConstants = typeClass.getEnumConstants();
            for (Enum<?> enumConstant : enumConstants) {
                // 匹配枚举值名称（如"USER"对应AuthDataRowSqlHandlerType.USER）
                if (enumConstant.name().equals(typeValue)) {
                    // 强转为RowDataHandlerType接口类型（因为已校验）
                    return (RowDataHandlerType) enumConstant;
                }
            }

            // 3. 未找到对应枚举值时抛出异常
            throw new IllegalArgumentException(
                    "枚举类" + typeClass.getName() + "中不存在值：" + typeValue
            );
        } catch (Exception e) {
            throw new RuntimeException("解析数据权限类型失败", e);
        }
    }
}
