package cn.xuqiudong.common.base.aspect;

import cn.xuqiudong.common.base.aspect.annotation.RepeatCommitVersion;
import cn.xuqiudong.common.base.enums.CommonMsgEnum;
import cn.xuqiudong.common.base.exception.CommonException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 描述:form重复提交版本检测，    依赖于数据库字段 ;
 * 用于：{@link org.springframework.web.bind.annotation.RequestMapping } &&
 *     {@link  RepeatCommitVersion }
 * @author Vic.xu
 * @since 2022-03-16 10:27
 */
@Aspect
@Component
public class RepeatCommitVersionAspect {

    @Resource
    private HttpServletRequest request;

    public Logger logger = LoggerFactory.getLogger(RepeatCommitVersionAspect.class);

    @Resource
    private JdbcTemplate jdbcTemplate;

    public RepeatCommitVersionAspect() {
    }

    public RepeatCommitVersionAspect(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 版本检测切面
     */
    @Pointcut("@annotation(cn.xuqiudong.common.base.aspect.annotation.RepeatCommitVersion)")
    public void version() {

    }

    /**
     * 进入请求的时候拼装相关信息
     *
     * @param joinPoint
     */
    @Before("version()")
    public void before(JoinPoint joinPoint) {
        RepeatCommitVersion version = getVersion(joinPoint);
        String versionAttributeValue = request.getParameter(version.versionAttribute());
        String id = request.getParameter(version.idAttribute());
        if (id == null || id.trim().length() == 0) {
            logger.debug("提交的表单无id的值，无需进行version 重复提交检验");
            return;
        }
        try {
            int formVersion = Integer.parseInt(versionAttributeValue);
            int oldVersion = findOldVersion(version, id);
            if (oldVersion > formVersion) {
                logger.info("页面的version {}大于数据库的version {}，不可提交", formVersion, oldVersion);
                throw new CommonException(CommonMsgEnum.REPEAT_COMMIT);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CommonException(CommonMsgEnum.REPEAT_COMMIT);
        }

    }

    /**
     * 查询数据库原来的version值
     * @param version 注解
     * @param id id值
     * @return old version value
     */
    private int findOldVersion(RepeatCommitVersion version, Object id) {
        String sql = "select " + version.column() + " from " + version.table() + " where id = " + id;
        try {
            Integer oldValue = jdbcTemplate.queryForObject(sql, Integer.class);
            return oldValue == null ? 0 : oldValue.intValue();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new CommonException(CommonMsgEnum.REPEAT_COMMIT);
        }

    }

    /**
     * 获取方法注解
     */
    private RepeatCommitVersion getVersion(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RepeatCommitVersion annotation = signature.getMethod().getAnnotation(RepeatCommitVersion.class);
        return annotation;

    }


}
