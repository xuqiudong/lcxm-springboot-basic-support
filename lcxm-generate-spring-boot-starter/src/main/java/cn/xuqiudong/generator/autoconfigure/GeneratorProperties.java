package cn.xuqiudong.generator.autoconfigure;

import cn.xuqiudong.generator.contant.DatabaseType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * 说明 :  代码自动生成配置项
 * @author  Vic.xu
 * @since  2019年12月11日 上午9:33:21
 */
@Configuration
@ConfigurationProperties(prefix = GeneratorProperties.GENERATOR_PREFIX)
@PropertySource("classpath:config/generator-config.properties")
public class GeneratorProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String GENERATOR_PREFIX = "generator";

    /**
     * 数据库类型，默认mysql
     */
    @Value("${generator.database:mysql}")
    private String database;

    /**
     * 作者
     */
    private String author;

    /**
     * 表前缀 若存在则生成的实体名会去掉前缀
     */
    private String tablePrefix;

    /**
     * 实体中忽略的表字段
     */
    private Set<String> ignores;
    /**
     * 数据库中哪些数据类型表示大文本，(这样是数据不应出现在list页面)
     */
    private Set<String> textTypes;

    /**
     * 需要填充的数据模板
     */
    private List<String> templates;

    /**
     * 数据类型转换 数据库类型--> java 类型
     */
    private Map<String, String> dataTypeConvert;

    /**
     * 数据库类型枚举
     */
    private static DatabaseType databaseType = DatabaseType.mysql;

    @PostConstruct
    private void post() {
        mergerDataType();
        ignorestoLowerCase();

    }

    /**
     * 忽略的字符串 转小写
     */
    private void ignorestoLowerCase() {
        if (ignores == null) {
            ignores = new HashSet<>();
            return;
        }
        Set<String> set = new HashSet<>();
        ignores.forEach(i -> set.add(i.toLowerCase()));
        ignores = set;
    }

    /**
     * 把默认的数据转换和配置的数据转换合并起来 并转小写
     */
    private void mergerDataType() {
        if (dataTypeConvert == null || dataTypeConvert.isEmpty()) {
            dataTypeConvert = new HashMap<>();
            dataTypeConvert.putAll(DEFAULT_DATA_TYPE_CONVERT);
            return;
        }

        dataTypeConvert.forEach((k, v) -> dataTypeConvert.put(k.toLowerCase(), v));

        // 把没有配置的项放进来
        DEFAULT_DATA_TYPE_CONVERT.forEach((k, v) -> {
            if (!dataTypeConvert.containsKey(k)) {
                dataTypeConvert.put(k, v);
            }
        });
    }

    @Override
    public String toString() {
        List<String> dataTypeConverts = new ArrayList<>();
        dataTypeConvert.forEach((k, v) -> dataTypeConverts.add(k + "=" + v));
        String sb = "\t【database=" + database + ", author=" + author + ", tablePrefix=" + tablePrefix +
                ",\n\t ignores=[" + ignores + "],\n\t textTypes=[" + String.join(";", textTypes) +
                "],\n\t templates=[\n\t\t" + String.join("\n\t\t", templates) +
                "],\n\t  dataTypeConvert=[" + String.join(";", dataTypeConverts) + "]\n\t】";
        return sb;
    }

    public String getAuthor() {
        return author;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public Set<String> getIgnores() {
        return ignores;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public Map<String, String> getDataTypeConvert() {
        return dataTypeConvert;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public void setIgnores(Set<String> ignores) {
        this.ignores = ignores;
    }

    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }

    public void setDataTypeConvert(Map<String, String> dataTypeConvert) {
        this.dataTypeConvert = dataTypeConvert;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    public DatabaseType getTaDatabaseType() {
        return DatabaseType.getByName(database);
    }

    /**
     * @param database
     *            the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
        DatabaseType databaseType = DatabaseType.getByName(database);
        if (databaseType == null) {
            throw new IllegalArgumentException("Illegal database parameters: " + database);
        }
        GeneratorProperties.databaseType = databaseType;
    }


    public static DatabaseType getDatabaseType() {
        return databaseType;
    }

    /**
     * @return the textTypes
     */
    public Set<String> getTextTypes() {
        return textTypes;
    }

    /**
     * @param textTypes
     *            the textTypes to set
     */
    public void setTextTypes(Set<String> textTypes) {
        this.textTypes = textTypes;
    }

    /**
     * 默认的数据库类型和java类型的转换关系
     */
    @SuppressFBWarnings(value = "MS_SHOULD_BE_FINAL",
            justification = "是可变的")
    public static Map<String, String> DEFAULT_DATA_TYPE_CONVERT = new HashMap<>();

    static {
        DEFAULT_DATA_TYPE_CONVERT.put("tinyint", "Integer");
        DEFAULT_DATA_TYPE_CONVERT.put("smallint", "Integer");
        DEFAULT_DATA_TYPE_CONVERT.put("mediumint", "Integer");
        DEFAULT_DATA_TYPE_CONVERT.put("int", "Integer");
        DEFAULT_DATA_TYPE_CONVERT.put("integer", "Integer");
        DEFAULT_DATA_TYPE_CONVERT.put("number", "Integer");
        // GaussDB
        DEFAULT_DATA_TYPE_CONVERT.put("numeric", "Integer");

        DEFAULT_DATA_TYPE_CONVERT.put("bigint", "Long");
        DEFAULT_DATA_TYPE_CONVERT.put("float", "Float");
        DEFAULT_DATA_TYPE_CONVERT.put("double", "Double");
        DEFAULT_DATA_TYPE_CONVERT.put("decimal", "BigDecimal");
        DEFAULT_DATA_TYPE_CONVERT.put("bit", "BigDecimal");
        DEFAULT_DATA_TYPE_CONVERT.put("enum", "String");

        DEFAULT_DATA_TYPE_CONVERT.put("char", "String");
        DEFAULT_DATA_TYPE_CONVERT.put("varchar", "String");
        DEFAULT_DATA_TYPE_CONVERT.put("nvarchar", "String");
        DEFAULT_DATA_TYPE_CONVERT.put("varchar2", "String");
        DEFAULT_DATA_TYPE_CONVERT.put("tinytext", "String");
        DEFAULT_DATA_TYPE_CONVERT.put("mediumtext", "String");
        DEFAULT_DATA_TYPE_CONVERT.put("longtext", "String");
        DEFAULT_DATA_TYPE_CONVERT.put("blob", "String");
        DEFAULT_DATA_TYPE_CONVERT.put("clob", "String");
        DEFAULT_DATA_TYPE_CONVERT.put("text", "String");

        DEFAULT_DATA_TYPE_CONVERT.put("date", "Date");
        DEFAULT_DATA_TYPE_CONVERT.put("time", "Date");
        DEFAULT_DATA_TYPE_CONVERT.put("datetime", "Date");
        DEFAULT_DATA_TYPE_CONVERT.put("timestamp", "Date");
        DEFAULT_DATA_TYPE_CONVERT.put("timestamp(6)", "Date");
    }

}
