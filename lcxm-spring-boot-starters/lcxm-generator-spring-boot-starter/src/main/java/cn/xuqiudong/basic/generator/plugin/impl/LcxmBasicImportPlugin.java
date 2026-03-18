package cn.xuqiudong.basic.generator.plugin.impl;

import cn.xuqiudong.basic.core.model.BaseResponse;
import cn.xuqiudong.basic.core.model.PageInfo;
import cn.xuqiudong.basic.core.request.CheckNotRepeatRequest;
import cn.xuqiudong.basic.generator.config.ConfigBundle;
import cn.xuqiudong.basic.generator.model.context.ControllerContext;
import cn.xuqiudong.basic.generator.model.context.ServiceContext;
import cn.xuqiudong.basic.generator.model.context.TemplateContext;
import cn.xuqiudong.basic.generator.plugin.BaseGeneratorPlugin;
import cn.xuqiudong.basic.generator.util.ImportPackageUtils;
import cn.xuqiudong.basic.mybatisplus.convert.PageConvert;

/**
 * 描述:
 * 一些lcxm  basic 通用的 导入. 方便在代码重构的时候， 不需要关注模板的改造
 *
 * @author Vic.xu
 * @since 2026-03-16 8:50
 */
public class LcxmBasicImportPlugin extends BaseGeneratorPlugin {

    @Override
    public boolean enable(ConfigBundle config) {
        return true;
    }

    @Override
    public void beforeGenerate(TemplateContext templateContext) {

        serviceDefaultImport(templateContext.getService());
        controllerDefaultImport(templateContext.getController());

    }


    /**
     * service上的一些默认注入
     */
    private void serviceDefaultImport(ServiceContext serviceContext) {
        serviceContext.addImport(ImportPackageUtils.getImport(PageInfo.class));
        serviceContext.addImport(ImportPackageUtils.getImport(PageConvert.class));
//        serviceContext.addImport(ImportPackageUtils.getImport(PageQuery.class));

    }

    /**
     * controller上一些默认的导入
     */
    public void controllerDefaultImport(ControllerContext controllerContext) {
        controllerContext.addImport(ImportPackageUtils.getImport(BaseResponse.class));
        controllerContext.addImport(ImportPackageUtils.getImport(PageInfo.class));
        controllerContext.addImport(ImportPackageUtils.getImport(CheckNotRepeatRequest.class));
    }


    @Override
    public void afterGenerate(String content) {

    }
}
