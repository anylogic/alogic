package com.logicbus.kvalue.common;

import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 分区
 * 
 * @author duanyy
 *
 */
public interface Partition extends XMLConfigurable,Reportable{
	/**
	 * 获取本分区的对应的数据源
	 * @return 数据源
	 */
	public String getSource();
	
	/**
	 * 获取本分区只读数据源
	 * @return 只读数据源
	 */
	public String[] getReplicates();
}
