/**
 * 
 */
package cn.xuqiudong.basic.core.util.reflect.compare.model;

import cn.xuqiudong.basic.core.util.reflect.compare.CompareResultEnum;

/**
 *  说明 :  对象比较结果
 *  @author Vic.xu
 * @since  2020年4月21日下午1:54:57
 */
public class CompareClassResultModel {
	
	/**
	 * 比较的字段
	 */
	private String fieldName;
	
	/**
	 * 字段说明
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
	 * 比较结果
	 */
	private CompareResultEnum resultEnum;
	/**
	 * 比较前的内容
	 */
	private String before;
	/**
	 * 比较后的内容
	 */
	private String after;
	
	/**
	 * @param fieldName
	 * @param fieldDesc
	 */
	public CompareClassResultModel(String fieldName, String fieldDesc) {
		super();
		this.fieldName = fieldName;
		this.fieldDesc = fieldDesc;
	}
	
	/**
	 * @param field
	 * @param resultEnum 只处理 新增和删除的
	 */
	public CompareClassResultModel(SameAnnotionField field, CompareResultEnum resultEnum) {
		this.fieldName = field.getFieldName();
		this.fieldDesc = field.getFieldDesc();
		this.parentFieldNames = field.getParentFieldNames();
		this.parentFieldDescs = field.getParentFieldDescs();
		this.resultEnum = resultEnum;
		//新增 表示只有after有值
		if(resultEnum == CompareResultEnum.ADD) {
			this.after = field.getValue();
		}
		//删除 表示只有before有值
		if(resultEnum == CompareResultEnum.DELETE) {
			this.before = field.getValue();
		}
	}
	
	
	
	/**
	 * @param fieldName
	 * @param fieldDesc
	 * @param resultEnum
	 * @param before
	 * @param after
	 */
	public CompareClassResultModel(String fieldName, String fieldDesc, CompareResultEnum resultEnum, Object before,
			Object after) {
		super();
		this.fieldName = fieldName;
		this.fieldDesc = fieldDesc;
		this.resultEnum = resultEnum;
		this.before = before + "";
		this.after = after + "";
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
	public CompareClassResultModel setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
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
	public CompareClassResultModel setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
		return this;
	}
	/**
	 * @return the resultEnum
	 */
	public CompareResultEnum getResultEnum() {
		return resultEnum;
	}
	/**
	 * @param resultEnum the resultEnum to set
	 */
	public CompareClassResultModel setResultEnum(CompareResultEnum resultEnum) {
		this.resultEnum = resultEnum;
		return this;
	}
	/**
	 * @return the before
	 */
	public String getBefore() {
		return before;
	}
	/**
	 * @param before the before to set
	 */
	public CompareClassResultModel setBefore(String before) {
		this.before = before;
		return this;
	}
	/**
	 * @return the after
	 */
	public String getAfter() {
		return after;
	}
	/**
	 * @param after the after to set
	 */
	public CompareClassResultModel setAfter(String after) {
		this.after = after;
		return this;
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

	@Override
	public String toString() {
		return " [属性=" + fieldName + ", 属性描述=" + fieldDesc + ", 含父类的属性="
				+ parentFieldNames + ", 含父类的属性描述=" + parentFieldDescs + ", 操作=" + resultEnum
				+ ", 原值=" + before + ", 现值=" + after + "]";
	}

	
	
	

}
