package cn.xuqiudong.basic.generator.plugin;

import cn.xuqiudong.basic.generator.plugin.impl.LombokPlugin;
import cn.xuqiudong.basic.generator.plugin.impl.PlusPlugin;
import cn.xuqiudong.basic.generator.plugin.impl.SpringdocPlugin;

import java.util.HashSet;
import java.util.Set;


/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-09-16 14:08
 */
public abstract class BaseGeneratorPlugin implements IGeneratorPlugin {

    private static final Set<IGeneratorPlugin> DEFAULT_PLUGINS = new HashSet<>();

    /**
     * 注册默认插件
     */
    static {
        DEFAULT_PLUGINS.add(new LombokPlugin());
        DEFAULT_PLUGINS.add(new PlusPlugin());
        DEFAULT_PLUGINS.add(new SpringdocPlugin());
    }

    public static Set<IGeneratorPlugin> getDefaultPlugins(){
        return DEFAULT_PLUGINS;
    }

}
