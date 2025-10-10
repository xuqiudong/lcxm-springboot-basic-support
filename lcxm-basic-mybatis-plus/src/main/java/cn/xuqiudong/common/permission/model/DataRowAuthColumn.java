package cn.xuqiudong.common.permission.model;

import cn.xuqiudong.common.permission.enums.RowDataHandlerType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * 需要过滤的权限列字段; 当type一致 就认为是一个对象
 * 
 * @author VIC.xu
 *
 */
public class DataRowAuthColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 权限类型 */
	private RowDataHandlerType type;

	/** 过滤的列 如a.dept_id */
	private String column;

	/**
	 *  前置判断条件（如"a.xx_id is not null"）
	 *  最终形成的WHERE条件为：(前置条件 AND 权限条件)
	 *  形如:"( (a.xx_id is not null) and (a.dept_id in (1,2,3)) )"
	 */
	private String precondition;

	private boolean hasPrecondition = false;

	public DataRowAuthColumn(RowDataHandlerType type, String column) {
		super();
		this.type = type;
		this.column = column;
	}

	public DataRowAuthColumn(RowDataHandlerType type, String column, String precondition) {
		this(type, column);
		this.precondition = precondition;
		this.hasPrecondition = StringUtils.isNotBlank(precondition);
	}

	public String getPrecondition() {
		return precondition;
	}

	public boolean hasPrecondition() {
		return hasPrecondition;
	}



	public RowDataHandlerType getType() {
		return type;
	}

	public void setType(RowDataHandlerType type) {
		this.type = type;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(type).append(column).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;

		}
		if (!(obj instanceof DataRowAuthColumn)) {
			return false;
		}
		DataRowAuthColumn data = (DataRowAuthColumn) obj;
		return new EqualsBuilder().append(type, data.getType()).append(column, data.getColumn()).isEquals();
	}

}
