package com.alogic.metrics.xscript;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 增加维度
 * 
 * @author yyduan
 *
 */
public class AddDimension extends MetricsBuilder {
	protected String id = "";
	protected String value = "";
	protected boolean overwrite = true;
	
	public AddDimension(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id",id,true);
		value = PropertiesConstants.getRaw(p, "value", value);
		overwrite = PropertiesConstants.getBoolean(p, "overwrite", overwrite,true);
	}

	@Override
	protected void onExecute(Fragment f, Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			String valueValue = ctx.transform(value);
			
			if (StringUtils.isNotEmpty(valueValue)){
				Dimensions dims = f.getDimensions();
				dims.set(id, valueValue, overwrite);
			}
		}
	}

}
