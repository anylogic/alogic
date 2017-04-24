package com.alogic.remote;

import com.anysoft.util.Configurable;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * Client
 *
 * @author yyduan
 * @since 1.6.8.12
 */
public interface Client extends Reportable,Configurable,XMLConfigurable{
	
	/**
	 * 创建新的Client
	 * @param method 请求方法
	 * @return Request
	 */
	public Request build(String method);
}
