package cn.xuqiudong.basic.framework.runtime;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Random;

/**
 * Description:
 *
 * @author Vic.xu
 * @since 2026-07-27 16:21
 */
@Aspect
public class RuntimeGuard {


    @Resource
    private HttpServletResponse response;

    static {
        LcRuntimeHelper.instance();
    }

    /**
     * 切requestmapping
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void check() {
    }

    @Before("check()")
    public void before() {
        if (LcRuntimeHelper.instance().expired()) {
            if (unlucky()) {
                try {
                    response.sendError(500, TextBundle.getTip(2));
                } catch (Exception e) {

                }
            }
        }
    }

    static Random random = new Random();

    private static boolean unlucky() {
        int num = random.nextInt(10);
        return num > 7;
    }

}
