package cn.xuqiudong.common.base.controller;

import cn.xuqiudong.common.base.lookup.Lookup;
import cn.xuqiudong.common.base.tool.Tools;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpSession;

/**
 * @author Vic.xu
 */
public abstract class BaseConsoleController {
    /**
     * 每个controller的查询条件的session中的后缀
     */
    public static final String SESSION_KEY_SUFFIX_LOOKUP = "lookup";


    /**
     * 获得本controller的查询条件
     */
    protected Lookup getLookup(Lookup defaultLookup) throws IllegalStateException {
        String lookupKey = getClass().getName() + "." + SESSION_KEY_SUFFIX_LOOKUP;
        Lookup lookup = getSessionAttribute(lookupKey);
        if (lookup == null) {
            lookup = defaultLookup;
            setSessionAttribute(lookupKey, lookup);
        }
        return lookup;
    }

    /**
     * 获得本controller的查询条件:存在不同的情况下通过前缀区分
     */
    protected Lookup getLookup(String prefix, Lookup defaultLookup) throws IllegalStateException {
        String lookupKey = getClass().getName() + "." + SESSION_KEY_SUFFIX_LOOKUP + prefix;
        Lookup lookup = getSessionAttribute(lookupKey);
        if (lookup == null) {
            lookup = defaultLookup;
            setSessionAttribute(lookupKey, lookup);
        }
        return lookup;
    }

    /**
     * 保存提交的查询条件
     */
    protected void setLookup(Lookup lookup) {
        setSessionAttribute(getClass().getName() + "." + SESSION_KEY_SUFFIX_LOOKUP, lookup);
    }

    /**
     * 保存提交的查询条件: 存在多个不同的查询条件的时候根据前缀区分
     */
    protected void setLookup(Lookup lookup, String prefix) {
        setSessionAttribute(getClass().getName() + "." + SESSION_KEY_SUFFIX_LOOKUP + prefix, lookup);
    }

    /**
     * 当前线程中的session
     */
    protected HttpSession currentSession() {
        return Tools.currentSession();
    }

    /**
     * 设置session
     */
    protected void setSessionAttribute(String key, Object obj) throws IllegalStateException {
        WebUtils.setSessionAttribute(Tools.currentRequest(), key, obj);
    }

    /**
     * 获取session中的对象  若当前无session则创建一个
     */
    @SuppressWarnings("unchecked")
    protected <T> T getSessionAttribute(String key) throws IllegalStateException {
        return (T) WebUtils.getSessionAttribute(Tools.currentRequest(), key);
    }

    /**
     * 获得session中的对象
     */
    @SuppressWarnings("unchecked")
    protected <T> T getOrCreateSessionAttribute(String key, Class<T> clazz) throws IllegalStateException {
        HttpSession session = Tools.currentSession();
        if (session == null) {
            return null;
        }
        return (T) getOrCreateSessionAttribute(session, key, clazz);
    }


    /**
     * from spring4.x WebUtils.getOrCreateSessionAttribute
     * @param session session
     * @param name attribute name
     * @param clazz attribute class
     * @return object
     * @throws IllegalArgumentException ex
     */
    private static Object getOrCreateSessionAttribute(HttpSession session, String name, Class<?> clazz)
            throws IllegalArgumentException {

        Object sessionObject = session.getAttribute(name);
        if (sessionObject == null) {
            try {
                sessionObject = clazz.newInstance();
            } catch (InstantiationException ex) {
                throw new IllegalArgumentException(
                        "Could not instantiate class [" + clazz.getName() +
                                "] for session attribute '" + name + "': " + ex.getMessage());
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException(
                        "Could not access default constructor of class [" + clazz.getName() +
                                "] for session attribute '" + name + "': " + ex.getMessage());
            }
            session.setAttribute(name, sessionObject);
        }
        return sessionObject;
    }


}