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
 * @version 1.6.10.1 [20170910 duanyy] <br>
 * - id参数改为可计算参数 <br>
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
		
		id = PropertiesConstants.getRaw(p,"id",id);
		value = PropertiesConstants.getRaw(p, "value", value);
		overwrite = PropertiesConstants.getBoolean(p, "overwrite", overwrite,true);
	}

	@Override
	protected void onExecute(Fragment f, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String idValue = ctx.transform(id);
		if (StringUtils.isNotEmpty(idValue)){
			String valueValue = ctx.transform(value);
			
			if (StringUtils.isNotEmpty(valueValue)){
				Dimensions dims = f.getDimensions();
				dims.set(idValue, valueValue, overwrite);
			}
		}
	}

}
