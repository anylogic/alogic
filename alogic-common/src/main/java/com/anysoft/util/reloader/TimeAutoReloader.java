package com.anysoft.util.reloader;

import com.anysoft.util.Properties;

/**
 * 时间自动刷新器
 * 
 * 根据时间来进行刷新，时长参数从loader.time_out变量中提取。
 * @author duanyy
 *
 */
public class TimeAutoReloader implements AutoReloader {
	private long m_lastest_date = 0;
	
	/**
	 * 是否可以刷新
	 * @param props 变量集
	 */
	public boolean reload(Properties props) {
		
		long time_out = 86400 ;
		{
			String value;
			value = (props != null)?props.GetValue("loader.time_out","86400"):"86400";
			time_out  = Long.parseLong(value);
			time_out = time_out * 1000;
		}
		
		long now = System.currentTimeMillis();
		
		if (now - m_lastest_date > time_out)
		{
			m_lastest_date = now;
			return true;
		}		
		return false;
	}

}
