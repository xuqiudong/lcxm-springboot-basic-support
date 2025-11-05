package cn.xuqiudong.generator.controller;

import cn.xuqiudong.common.base.model.BaseResponse;
import cn.xuqiudong.common.base.model.PageInfo;
import cn.xuqiudong.generator.model.TableConfigVO;
import cn.xuqiudong.generator.model.TableEntity;
import cn.xuqiudong.generator.service.GeneratorService;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自动生成代码
 *
 * @author VIC
 */
@Controller
@RequestMapping("/generator")
public class GeneratorController {


    @Resource
    private GeneratorService generatorService;

    public GeneratorController() {
        super();
    }

    /**
     * @param generatorService
     */
    public GeneratorController(GeneratorService generatorService) {
        super();
        this.generatorService = generatorService;
    }

    @GetMapping(value = {"", "/"})
    public void home(HttpServletResponse response, HttpServletRequest request) throws Exception {
        String url = "generator.html";
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * 数据库表列表
     * @param lookup
     */
    @ResponseBody
    @GetMapping(value = "/list")
    public BaseResponse<?> list(TableEntity lookup) {
        PageInfo<TableEntity> data = generatorService.list(lookup);
        return BaseResponse.success(data);
    }


    /**
     * 查询表的列的情况
     * @param tableName
     */
    @ResponseBody
    @GetMapping(value = "/table/detail")
    public BaseResponse<?> table(@RequestParam(value = "id") String tableName) {
        TableEntity data = generatorService.buildTableDetail(tableName);
        return BaseResponse.success(data);
    }

    /**
     * 根据条件导出
     */
    @ResponseBody
    @RequestMapping(value = "/exportByConfig", method = {RequestMethod.POST, RequestMethod.GET})
    public void exportByConfig(TableConfigVO tableConfigVO, HttpServletResponse response) throws IOException {
        byte[] data = generatorService.exportByConfig(tableConfigVO);
        download(data, response);
    }


    /**
     * 同时导出多个表数据
     * @param tableName  tableName
     * @param packageName packageName
     * @param moduleName moduleName
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/exportTables", method = {RequestMethod.POST, RequestMethod.GET})
    public void exportTables(String[] tableName, @RequestParam(defaultValue = "") String packageName,
                             @RequestParam(defaultValue = "") String moduleName, HttpServletResponse response) throws IOException {
        byte[] data = generatorService.exportTables(tableName, packageName, moduleName);
        download(data, response);
    }

    private void download(byte[] data, HttpServletResponse response) throws IOException {
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"generator-vic.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }

}
