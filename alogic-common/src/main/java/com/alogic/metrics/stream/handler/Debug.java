package com.alogic.metrics.stream.handler;

import java.util.Collection;
import java.util.Map;
import org.w3c.dom.Element;

import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.Measures;
import com.alogic.metrics.stream.MetricsSummaryWriter;
import com.anysoft.util.Properties;

/**
 * 调试
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public class Debug extends MetricsSummaryWriter{
	
	@Override
	protected void write(Map<String, Fragment> data, long t) {		
		Collection<Fragment> values = data.values();
		
		for (Fragment f:values){
			LOG.info(f.id());
			Dimensions dims = f.getDimensions();
			if (dims != null){
				LOG.info(dims.toString());
			}
			
			Measures meas = f.getMeasures();
			if (meas != null){
				LOG.info(meas.toString());
			}
		}
	}

	@Override
	protected void onConfigure(Element e, Properties p) {

	}

}