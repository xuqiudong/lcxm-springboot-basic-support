package cn.xuqiudong.basic.framework.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Cookie的工具类
 *
 * @author Vic.xu
 * @since 2021/09/08
 */
public class CookieUtil {

    /**
     * 默认缓存时间,单位/秒, 2H
     */
    private static int COOKIE_MAX_AGE = 60 * 60 * 24 * 7;
    /**
     * 保存路径,根路径
      */
    private static String COOKIE_PATH = "/";

    /**
     * 设置cookie的缓存时间和 路径
     *
     * @param cookieAge
     *            缓存时间,单位/秒 默认7天
     * @param cookiePath
     *            保存路径 默认 /
     */
    public static void init(int cookieAge, String cookiePath) {
        COOKIE_MAX_AGE = cookieAge;
        COOKIE_PATH = cookiePath;
    }

    /**
     * 保存到cookie
     *
     * @param response
     * @param key  key
     * @param value value
     * @param ifRemember 是否记住：是则记住COOKIE_MAX_AGE秒，否则 -1  即一次回话
     */
    public static void set(HttpServletResponse response, String key, String value, boolean ifRemember) {
        int age = ifRemember ? COOKIE_MAX_AGE : -1;
        set(response, key, value, null, COOKIE_PATH, age, true);
    }

    /**
     * 保存到cookie
     *
     * @param response
     * @param key
     * @param value
     * @param domain 域
     * @param maxAge 生命周期
     */
    public static void set(HttpServletResponse response, String key, String value, String domain, String path,
                           int maxAge, boolean isHttpOnly) {
        Cookie cookie = new Cookie(key, value);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        response.addCookie(cookie);
    }

    /**
     * 查询value
     *
     * @param request
     * @param key
     * @return
     */
    public static String getValue(HttpServletRequest request, String key) {
        Cookie cookie = get(request, key);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    /**
     * 查询Cookie
     *
     * @param request
     * @param key
     */
    private static Cookie get(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 删除Cookie
     *
     * @param request
     * @param response
     * @param key
     */
    public static void remove(HttpServletRequest request, HttpServletResponse response, String key) {
        Cookie cookie = get(request, key);
        if (cookie != null) {
            set(response, key, "", null, COOKIE_PATH, 0, true);
        }
    }

}
