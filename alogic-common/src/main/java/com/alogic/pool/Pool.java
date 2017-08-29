package com.alogic.pool;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 缓冲池接口
 * 
 * @author duanyy
 *
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - Pool的returnObject接口增加是否出错的参数 <br>
 * 
 */
public interface Pool extends Reportable,Configurable,XMLConfigurable,AutoCloseable{
		
	/**
	 * 从缓冲池中借出缓冲对象
	 * @param priority 优先级
	 * @return pooled
	 * @throws BaseException
	 */
	public <pooled> pooled borrowObject(int priority,int timeout);
	
	/**
	 * 归还缓冲对象
	 * @param obj 缓冲对象
	 * @param hasError 是否发生过错误
	 */
	public <pooled> void returnObject(pooled obj,boolean hasError);
}
