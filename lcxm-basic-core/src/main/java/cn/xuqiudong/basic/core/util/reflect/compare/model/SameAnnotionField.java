/**
 * 
 */
package cn.xuqiudong.basic.core.util.reflect.compare.model;

/**
 * 说明 :  根据注解获得属性对象
 * @author Vic.xu
 * @since  2020年4月22日上午9:35:17
 */
public class SameAnnotionField {

	/**
	 * 解析的深度
	 */
	private int deep;

	/**
	 * 属性名
	 */
	private String fieldName;

	/**
	 * 属性描述
	 */
	private String fieldDesc;

	/**
	 * 包含父类的属性名，多级逗号分隔
	 */
	private String parentFieldNames;
	/**
	 * 包含父类的属性描述， 多级逗号分隔
	 */
	private String parentFieldDescs;
	/**
	 * 字段值
	 */
	private String value;
	
	/**
	 * @param deep
	 * @param fieldName
	 * @param fieldDesc
	 * @param parentFieldNames
	 * @param parentFieldDescs
	 * @param value
	 */
	public SameAnnotionField(int deep, String fieldName, String fieldDesc, String parentFieldNames,
			String parentFieldDescs, Object value) {
		super();
		this.deep = deep;
		this.fieldName = fieldName;
		this.fieldDesc = fieldDesc;
		this.parentFieldNames = parentFieldNames + "," + fieldName;
		this.parentFieldDescs = parentFieldDescs + "," + fieldDesc;
		this.value = String.valueOf(value);
	}

	/**
	 * @return the deep
	 */
	public int getDeep() {
		return deep;
	}

	/**
	 * @param deep the deep to set
	 */
	public void setDeep(int deep) {
		this.deep = deep;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the fieldDesc
	 */
	public String getFieldDesc() {
		return fieldDesc;
	}

	/**
	 * @param fieldDesc the fieldDesc to set
	 */
	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}

	/**
	 * @return the parentFieldNames
	 */
	public String getParentFieldNames() {
		return parentFieldNames;
	}

	/**
	 * @param parentFieldNames the parentFieldNames to set
	 */
	public void setParentFieldNames(String parentFieldNames) {
		this.parentFieldNames = parentFieldNames;
	}

	/**
	 * @return the parentFieldDescs
	 */
	public String getParentFieldDescs() {
		return parentFieldDescs;
	}

	/**
	 * @param parentFieldDescs the parentFieldDescs to set
	 */
	public void setParentFieldDescs(String parentFieldDescs) {
		this.parentFieldDescs = parentFieldDescs;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
