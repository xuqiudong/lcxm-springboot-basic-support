package cn.xuqiudong.basic.mybatisplus.permission.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 存储在当前线程中用于权限处理拦截器的基本数据
 * @author VIC.xu
 *
 */
public class DataRowAuthModel implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 需要过滤的权限字段
	 */
	private Set<DataRowAuthColumn> filterColumns = new HashSet<DataRowAuthColumn>();
	
	/**
	 * 新增要过滤的权限字段
	 */
	public DataRowAuthModel addFilterColumn(DataRowAuthColumn column) {
		this.filterColumns.add(column);
		return this;
	}
	public Set<DataRowAuthColumn> getFilterColumns() {
		return filterColumns;
		
	}
}
