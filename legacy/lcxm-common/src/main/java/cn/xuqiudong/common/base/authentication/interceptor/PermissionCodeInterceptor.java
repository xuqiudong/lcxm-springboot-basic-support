package cn.xuqiudong.common.base.authentication.interceptor;

import cn.xuqiudong.common.base.authentication.annotation.Logical;
import cn.xuqiudong.common.base.authentication.annotation.Permission;
import cn.xuqiudong.common.base.authentication.model.PermissionModel;
import cn.xuqiudong.common.base.exception.CommonException;
import cn.xuqiudong.common.base.model.BaseResponse;
import cn.xuqiudong.common.base.tool.Tools;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 授权code拦截器
 * 如果没有配置权限注解Permission，依然判断当前用户是否具有当前url, 
 * 如果配置了权限注解Permission， 在配置了权限标识的时候，只判断权限标示，
 * @see Permission
 * @author VIC.xu
 *
 */
public class PermissionCodeInterceptor implements HandlerInterceptor {

    private static final String ROOT_PATH = "/";

    private static final String TIPS = "没有操作权限，请联系管理员!";

    /**
     * 是否默认拦截url，当controller 中的请求没有配置注解的时候
     */
    private final boolean willInterceptUrlDefaults;

    /**
     * 获取当前session中的权限model的Supplier
     */
    private final Supplier<PermissionModel> permissionModelSupplier;

    /**
     * 没有权限时重定向到500页面的处理
     */
    private final Consumer<HttpServletResponse> redirectTo500Handler;

    public PermissionCodeInterceptor(Supplier<PermissionModel> permissionModelSupplier, Consumer<HttpServletResponse> redirectTo500Handler, boolean willInterceptUrlDefaults) {
        this.permissionModelSupplier = permissionModelSupplier;
        this.redirectTo500Handler = redirectTo500Handler;
        this.willInterceptUrlDefaults = willInterceptUrlDefaults;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //由于已经存在判断是否登陆的Filter   故此处 不再判断是否登陆
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        PermissionModel permissionModel = getPermissionModel();
        HandlerMethod method = (HandlerMethod) handler;
        Permission permission = method.getMethodAnnotation(Permission.class);
        String url = Tools.getRequestUrl(request);

        boolean allow = allow(url, permissionModel, permission);
        if (allow) {
            return true;
        }
        // 根据请求类型：ajax则返回json， 非ajax 则应返回401 页面
        if (Tools.isAjax(request)) {
            Tools.writeJson(BaseResponse.error(TIPS), response);
        } else {
            if (redirectTo500Handler != null) {
                redirectTo500Handler.accept(response);
            } else {
                throw new CommonException(TIPS);
            }
        }
        return false;
    }


    private boolean allow(String curUrl, PermissionModel permissionModel, Permission permission) {
        //1. 跟目录不拦截
        if (ROOT_PATH.equals(curUrl)) {
            return true;
        }

        //2. 没有配置注解，且默认拦截 则只拦截URL,否则放行
        if (permission == null) {
            if (willInterceptUrlDefaults) {
                return hasResource(curUrl, permissionModel);
            }
            return true;
        }
        //3. 配置了注解 且配置了权限标示　　则只判断权限标示
        String[] permissions = permission.permissions();
        if (permissions != null && permissions.length > 0) {
            return hasPermissions(permissions, permission.logical(), permissionModel);
        }
        //4. 没有配置了权限标示，但配置了拦截菜单且为true
        if (permission.url()) {
            return hasResource(curUrl, permissionModel);
        }
        //其他情况: 配置的url为false 不拦截
        return true;
    }

    /**
     * 是否具有此权限标示
     * @param permissions
     * @param logical
     * @param permissionModel PermissionModel
     * @return boolean
     */
    private boolean hasPermissions(String[] permissions, Logical logical, PermissionModel permissionModel) {
        //当前用户的全部资源
        Set<String> codeSet = permissionModel.getCodes();
        if (CollectionUtils.isEmpty(codeSet)) {
            return false;
        }
        switch (logical) {
            case AND:
                //全部匹配
                return Stream.of(permissions).allMatch(codeSet::
                        contains);
            case OR:
                //匹配一个即可
                return Stream.of(permissions).anyMatch(codeSet::contains);
            default:
                break;
        }
        return true;
    }

    /**
     * 是否具有此菜单,
     * @param url
     * @param permissionModel PermissionModel
     * @return boolean
     */
    private boolean hasResource(String url, PermissionModel permissionModel) {
        return permissionModel.getUrls().contains(url);

    }

    public PermissionModel getPermissionModel() {
        PermissionModel permissionModel = null;
        if (permissionModelSupplier != null) {
            permissionModel = permissionModelSupplier.get();
        }
        if (permissionModel == null) {
            permissionModel = new PermissionModel();
        }
        return permissionModel;
    }

}
