package com.alogic.metrics.xscript;

import org.apache.commons.lang3.StringUtils;
import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 增加维度
 * 
 * @author yyduan
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
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
	protected void onExecute(Fragment f, XsObject root,XsObject current, LogicletContext ctx,
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
