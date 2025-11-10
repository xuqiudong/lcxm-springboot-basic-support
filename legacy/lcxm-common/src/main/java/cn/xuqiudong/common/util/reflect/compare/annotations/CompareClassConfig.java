/**
 * 
 */
package cn.xuqiudong.common.util.reflect.compare.annotations;

import cn.xuqiudong.common.util.reflect.compare.CompareClassUtil;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  说明 :  比较两个类的字段的注解， 只比较加了这个注解的字段
 *  @see CompareClassUtil#compare(Class, Object, Object)
 *  @author Vic.xu
 * @since  2020年4月21日上午10:48:46
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface CompareClassConfig {
	/**
	 * 字段描述 必填
	 */
	String value();
	
	/**
	 * 参见  InnerType枚举的描述
	 * @see InnerType
	 * 用以区分属性的类型，非常重要
	 */
	InnerType compareInner() default InnerType.NONE;
	
	/**
	 * 两个对象比较时， 只比较对象的某个指定的属性值 如id，以判断是否是相同的对象， InnerType为非NONE得时候
	 */
	String innerIdentifyField() default "id";
	
	/**
	 *@see InnerType.CASCADE_SINGLE 和InnerType.CASCADE_LIST
	 * 展开比较的时候, 选择对象的某个属性值  记录为变化说明 InnerType为非NONE得时候
	 */
	String innerRecordShowField() default "name";
	
	
	/**
	 * 字段为Date时 format 方式
	 * @return
	 */
	String datePattern() default "yyyy-MM-dd";

	/**
	 * 展开比较的时候的属性类型:单个对象或者list对象
	 */
	public enum InnerType {
		/**
		 * 不展开比较， 一般是基本数据类型
		 */
		NONE,
		/**单个对象, 需要展开对象内部进行比较*/
		SINGLE,
		/**单个对象,  只比较'id', 记录'name'变化**/
		CASCADE_SINGLE,
		/**list对象, 需要展看内部进行比较*/
		LIST,
		/**list对象, 只遍历比较'id', 记录'name'变化*/
		CASCADE_LIST,
		;
	}
}
