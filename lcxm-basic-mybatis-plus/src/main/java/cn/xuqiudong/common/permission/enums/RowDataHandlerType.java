package cn.xuqiudong.common.permission.enums;

/**
 *  数据权限类型枚举的接口
 * @author VIC.xu
 *
 */
public interface RowDataHandlerType {
	
	/**
	 * 某个类型对SQL的处理
	 * @param column 需要关联的SQL 字段
	 * @return 权限sql片段 : 必须满足 CCJSqlParserUtil.parseCondExpression ,
	 * 是一个符合基本 SQL 语法（尤其是引号、括号、关键字使用）
	 */
	String handlerSql(String column);

}
