package com.anysoft.util.reloader;

import com.anysoft.util.Properties;

/**
 * 计数自动刷新器
 * 
 * 根据计数器来进行刷新，计数值从loader.count_out变量中提取。
 * @author duanyy
 *
 */
public class CounterAutoReloader implements AutoReloader {
	private long m_count = Long.MAX_VALUE;
	public boolean reload(Properties _props) {
		long count_out = 1000 ;
		{
			String value;
			value = (_props != null)?_props.GetValue("loader.count_out","1000"):"1000";
			try{
			count_out  = Long.parseLong(value);
			}catch (Exception ex){
				count_out = 1000;
			}
		}
				
		if (m_count > count_out)
		{
			m_count = 0;	
			return true;
		}		
		m_count ++;
		return false;
	}

}
