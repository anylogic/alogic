package com.alogic.doer.core;

import com.anysoft.util.BaseException;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 任务分发器
 * 
 * @author duanyy
 * @since 1.6.3.4
 * 
 */
public interface TaskDispatcher extends XMLConfigurable, Reportable {
	/**
	 * 分发任务
	 * 
	 * @parameter task 任务实例
	 * 
	 */
	public void dispatch(Task task) throws BaseException;
}
