package cn.xuqiudong.generator.service;

import cn.xuqiudong.common.base.model.PageInfo;
import cn.xuqiudong.generator.autoconfigure.GeneratorProperties;
import cn.xuqiudong.generator.dao.BaseGeneratorDao;
import cn.xuqiudong.generator.model.*;
import cn.xuqiudong.generator.tool.DataBaseLikeJointTool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 描述:
 * @author Vic.xu
 * @since 2020年3月11日 下午3:07:20
 */
public class GeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorService.class);

    BaseGeneratorDao generatorDao;

    private GeneratorProperties generatorProperties;

    /**
     * 没有配置的sql和java类型转换时候，默认的java类型。<br/>
     * 此处并没有默认String为默认数据类型,所以没有配置的类型在java实体中会报错
     */
    private static final String DEFAULT_JAVA_TYPE = "unknownType";

    public GeneratorService(BaseGeneratorDao generatorDao, GeneratorProperties generatorProperties) {
        super();
        this.generatorDao = generatorDao;
        this.generatorProperties = generatorProperties;
    }

    public GeneratorService() {
        super();
    }

    /**
     * 说明 :  分页列表
     * @param lookup TableEntity
     * @return PageInfo
     */
    public PageInfo<TableEntity> list(TableEntity lookup) {
        int total = generatorDao.countList(lookup);
        List<TableEntity> list = generatorDao.queryList(lookup);
        return new PageInfo<>(total, list, lookup);
    }

    /**
     * 构建table的详情 包含的列的信息等
     * @param tableName tableName
     * @return TableEntity
     */
    public TableEntity buildTableDetail(String tableName) {
        // 查询表信息
        TableEntity table = generatorDao.queryTable(tableName);
        if (table == null) {
            throw new RuntimeException("不存在的表信息");
        }
        // 查询列信息
        List<ColumnEntity> columns = generatorDao.queryColumns(tableName);
        // 配置信息
        boolean hasBigDecimal = false;
        String className = tableToJava(table.getTableName(), generatorProperties.getTablePrefix());

        table.setClassName(className);
        // 第一个字母小写
        table.setClassname(StringUtils.uncapitalize(className));

        // 被实体忽略的字段集合
        Set<String> ignoreColumns = generatorProperties.getIgnores();
        // 没有配置的java类型列
        List<ColumnEntity> unknownJavaTypeList = new ArrayList<>();


        // 列信息
        for (ColumnEntity column : columns) {
            String columnName = column.getColumnName().toLowerCase();
            // 列名转换成Java属性名
            String attrName = columnToJava(columnName);
            column.setAttrName(attrName);
            column.setAttrname(StringUtils.uncapitalize(attrName));

            // 列的数据类型，转换成Java类型
            // 此处并没有默认String为默认数据类型,所以没有配置的类型在java实体中会报错
            String attrType = generatorProperties.getDataTypeConvert().getOrDefault(column.getDataType().toLowerCase(),
                    DEFAULT_JAVA_TYPE);
            column.setAttrType(attrType);
            if (DEFAULT_JAVA_TYPE.equals(attrType)) {
                unknownJavaTypeList.add(column);
            }
            // 此字段是否在实体中忽略(基类中已定义)
            boolean ignored = ignoreColumns.contains(columnName);
            if (ignored) {
                column.setEntityIgnore(true);
            }else {
                // 只有非忽略字段的时候，这个时候才会引入到Entity中， 才需要判断是否引入对应数据类型
                if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                    hasBigDecimal = true;
                }
                if ("Date".equals(attrType)) {
                    table.setHasDate(true);
                }
            }
            // 是否主键
            boolean isPk = column.isPrimaryKey();
            if (isPk && table.getPk() == null) {
                table.setPk(column);
            }
            // 根据列是否是图片和日期 判断表中是否含有图片和日期

        }
        printUnknownJavaType(unknownJavaTypeList);
        // 没主键，推断主键:字段为id 或者 取第一个列
        table.setColumns(columns);
        if (table.getPk() == null) {
            table.setPk(inferPk(columns));
        }
        return table;

    }

    /**
     *推断主键：当忘记设置主键的时候，自动推断主键：根据字段名 是否是 id，或者取第一个列
     */
    private ColumnEntity inferPk(List<ColumnEntity> columns) {
        ColumnEntity column =
                columns.stream().filter(c -> StringUtils.equalsIgnoreCase(c.getColumnName(), "id")).
                        findAny().orElse(null);
        return column == null ? columns.get(0) : column;
    }

    /**
     * 打印没有定义映射的数据列
     */
    private static void printUnknownJavaType(List<ColumnEntity> UnknownJavaTypeList) {
        if (CollectionUtils.isEmpty(UnknownJavaTypeList)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        UnknownJavaTypeList.forEach(c ->
                sb.append("\t").append(c.getColumnName()).append("  -->  ").append(c.getDataType())
        );
        logger.warn("没有为数据库类型配置对应的java类型有:\n{}", sb);

    }

    /**
     * 表名转换成Java类名
     */
    private String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            // 忽略大小写
            tableName = ignoreCaseStartCut(tableName, tablePrefix);
            // tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 把开头部分给截取掉，忽略大小写
     *
     * @param source source
     * @param start start
     * @return case and  cut
     */
    private static String ignoreCaseStartCut(String source, String start) {
        if (StringUtils.startsWithIgnoreCase(source, start)) {
            return source.substring(start.length());
        }
        return source;
    }

    /**
     * 列名转换成Java属性名 is_delete -->delete
     */
    public static String columnToJava(String columnName) {
        // return WordUtils.capitalizeFully(columnName, new char[] { '_' }).replace("_", "");//aaBBcc-->aabbcc 不是我想要的
        return underlineToCamel(columnName);
    }

    /** 下划线 */
    static String UNDERLINE = "_";
    /** 字段属性转java属性忽略的前缀 */
    static String IGNORE_COLUMN_PREFIX = "is_";

    /**
     * 下划线转驼峰 但是不小写
     *
     * @param str 列
     * @return 驼峰
     */
    public static String underlineToCamel(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        int prefixLen = IGNORE_COLUMN_PREFIX.length();
        if (str.length() > prefixLen && str.substring(0, prefixLen).equalsIgnoreCase(IGNORE_COLUMN_PREFIX)) {
            str = str.substring(prefixLen);
        }
        str = str.toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (String s : str.split(UNDERLINE)) {
            sb.append(StringUtils.capitalize(s));
        }
        return sb.toString();
    }

    /**
     * 同时导出多张表
     */
    public byte[] exportTables(String[] tableNames, String packageName, String moduleName) {
        if (tableNames == null || tableNames.length == 0) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames) {
            generatorCode(new TableConfigVO(tableName, packageName, moduleName), zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 根据条件导出
     * @param tableConfigVO TableConfigVO
     */
    public byte[] exportByConfig(TableConfigVO tableConfigVO) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(tableConfigVO, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    private void generatorCode(TableConfigVO tableConfigVO, ZipOutputStream zip) {
        TableEntity table = buildTableDetail(tableConfigVO.getTableName());
//        Map<String, Object> map =
        TemplateContext templateData = getTemplateData(table, tableConfigVO);
        VelocityContext context = new VelocityContext(templateData.getMapContext());
        // 获取模板列表
        List<String> templates = generatorProperties.getTemplates();
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try {
                // 添加到zip
                String fileName = getFileName(template, table.getClassName(), tableConfigVO.getPackageName(),
                        tableConfigVO.getModuleName());
                zip.putNextEntry(new ZipEntry(fileName));
                IOUtils.write(sw.toString(), zip, "UTF-8");
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("渲染模板失败，表名：" + table.getTableName(), e);
            }

        }
    }

    /**
     * 通过前端配置构建模板数据
     */
    private TemplateContext getTemplateData(TableEntity table, TableConfigVO tableConfigVO) {

        String packageName = tableConfigVO.getPackageName();
        String moduleName = tableConfigVO.getModuleName();
        // 设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        //加入自定义函数(全路径)
        prop.setProperty("userdirective", DataBaseLikeJointTool.class.getName());
        Velocity.init(prop);
        // 根据页面配置列的信息:是否列表 查询条件
        configColumn(table, tableConfigVO);
        // 封装模板数据
//        Map<String, Object> map = new HashMap<>(32);
//        map.put("table", table);
//        map.put("tableName", table.getTableName());
//        map.put("comments", table.getComments());
//        map.put("pk", table.getPk());
//        map.put("className", table.getClassName());
//        map.put("classname", table.getClassname());
//        map.put("pathName", table.getClassname().toLowerCase());
//        map.put("columns", table.getColumns());
//        //列表页展示的列，即columns中extend.show = true
//        map.put("listColumns", table.getListColumns());
//        map.put("hasBigDecimal", table.getHasBigDecimal());
//        map.put("package", packageName);
//        map.put("moduleName", moduleName);
//        map.put("author", generatorProperties.getAuthor());
//        map.put("dialect", generatorProperties.getTaDatabaseType().getDialect());
//        map.put("datetime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        TemplateContext templateContext = new TemplateContext(table, generatorProperties);
        templateContext.setPackageName(packageName);
        templateContext.setModuleName(moduleName);
        return templateContext;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName) {
        // String packagePath = "generator" + File.separator + className;
        String packagePath = (packageName + File.separator + moduleName).replace(".", File.separator);
        String entityName = "Entity.java";
        // 把完整模板路径转化为文件名 generator/templates/Entity.java.vm --> Entity.java
        String filename = template.substring(0, template.length() - 3).substring(template.lastIndexOf("/") + 1);
        // 实体不加后缀
        String type = filename.substring(0, filename.indexOf("."));
        if (entityName.equals(filename)) {
            filename = ".java";
            type = "model";
        }
        // 形如:packagePath/Entity/xxx.java
        filename =
                packagePath.toLowerCase() + File.separator + type.toLowerCase() + File.separator + className + filename;
        return filename;
    }

    /**
     * 配置表的列信息
     */
    private void configColumn(TableEntity table, TableConfigVO tableConfigVO) {
        if (table == null || tableConfigVO == null || table.getColumns() == null) {
            return;
        }
        // 表的全部列
        List<ColumnEntity> columns = table.getColumns();

        // 被忽略的字段
        Set<String> ignoreColumns = generatorProperties.getIgnores();
        // 前端配置的需要展示的列表
        List<ColumnConfigVO> columnsConfig = tableConfigVO.getColumns();
        // 列配置转为 列名->配置
        Map<String, ColumnConfigVO> columnsConfigMap = new HashMap<>(32);

        // 当由前端配置表展示的时候
        if (columnsConfig != null) {
            for (ColumnConfigVO co : columnsConfig) {
                columnsConfigMap.put(co.getColumnName(), co);
            }
            for (ColumnEntity c : columns) {
                ColumnConfigVO config = columnsConfigMap.get(c.getColumnName());
                if (config != null) {
                    c.getExtend().setShow(config.getShow()).setWhere(config.getShow())
                            .setCondition(config.getCondition());
                }
                if (ignoreColumns.contains(c.getColumnName().toLowerCase())) {
                    // 忽略的字段默认展示
                    c.getExtend().setShow(true);
                }
            }
            // 当前端没有配置的时候:默认只过滤掉大文本类型的字段
        } else {
            // 大文本类型
            Set<String> textTypes = generatorProperties.getTextTypes();

            columns.forEach(c -> {
                String dateType = c.getDataType();
                // 非大文本 就展示
                boolean show = !textTypes.contains(dateType);
                // 比较方式0:=; 1:like;
                int condition = "String".equalsIgnoreCase(c.getAttrType()) ? 1 : 0;
                c.getExtend().setShow(show).setWhere(show).setCondition(condition);
            });
        }

    }

}
