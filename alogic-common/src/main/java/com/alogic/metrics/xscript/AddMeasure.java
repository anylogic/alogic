package com.alogic.metrics.xscript;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.Measures;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 增加量度
 * @author yyduan
 *
 */
public class AddMeasure extends MetricsBuilder {
	protected String id = "";
	protected String value = "";
	protected Fragment.DataType type = Fragment.DataType.S;
	protected Fragment.Method method = Fragment.Method.sum;
	
	public AddMeasure(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id",id,true);
		value = PropertiesConstants.getRaw(p, "value", value);
		
		type = Fragment.DataType.valueOf(PropertiesConstants.getString(p,"type",type.name(),true));
		method = Fragment.Method.valueOf(PropertiesConstants.getString(p,"method",method.name(),true));
	}

	@Override
	protected void onExecute(Fragment f, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			String valueValue = ctx.transform(value);
			
			if (StringUtils.isNotEmpty(valueValue)){
				Measures meas = f.getMeasures();
				
				switch (type){
				case D:
					try {
						long longValue = Long.parseLong(valueValue);
						meas.set(id, longValue,method);
					}catch (NumberFormatException ex){
						
					}					
				case L:
					try {
						long longValue = Long.parseLong(valueValue);
						meas.set(id, longValue,method);
					}catch (NumberFormatException ex){
						
					}
				default:
					meas.set(id, valueValue);
				}
			}
		}
	}

}