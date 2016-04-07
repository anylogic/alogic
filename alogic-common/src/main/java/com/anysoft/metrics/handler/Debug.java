package com.anysoft.metrics.handler;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.anysoft.metrics.core.Dimensions;
import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.core.Measures;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

/**
 * 调试，直接输出到log4j
 * 
 * @author duanyy
 *
 */
public class Debug extends SummaryWriter{

	/**
	 * 主机
	 */
	protected String host;
	
	/**
	 * 应用
	 */
	protected String app;
	
	@Override
	protected void write(Map<String, Fragment> data, long t) {
		Settings settings = Settings.get();
		if (StringUtils.isEmpty(host)){
			host = PropertiesConstants.getString(settings,"host", "${server.ip}:${server.port}");
		}
		if (StringUtils.isEmpty(app)){
			app = PropertiesConstants.getString(settings,"app","${server.app}");
		}
		
		Collection<Fragment> values = data.values();
		
		for (Fragment f:values){
			LOG.info(f.getId());
			Dimensions dims = f.getDimensions();
			if (dims != null){
				dims.lpush(host,app);
				LOG.info(dims.toString());
			}
			
			Measures meas = f.getMeasures();
			if (meas != null){
				LOG.info(meas);
			}
		}
	}

	@Override
	protected void onConfigure(Element e, Properties p) {

	}

}
