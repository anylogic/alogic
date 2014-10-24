package com.logicbus.kvalue.core;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * KeyValue DB的模式
 * @author duanyy
 *
 */
public interface Schema extends AutoCloseable,XMLConfigurable,Reportable{
	public String getId();
	public void create(Properties props)throws BaseException;
	public Table getTable(String name);
}
