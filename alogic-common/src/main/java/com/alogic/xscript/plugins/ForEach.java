package com.alogic.xscript.plugins;

import java.util.Map;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 针对字符串数组进行循环
 * 
 * @author duanyy
 *
 */
public class ForEach extends Segment{
	protected String in;
	protected String id = "$value";
	protected String delimeter=";";
	
	public ForEach(String tag, Logiclet p) {
		super(tag, p);
	}
	
	public void configure(Properties p){
		super.configure(p);
		in = PropertiesConstants.getRaw(p,"in","");
		delimeter = PropertiesConstants.getString(p,"delimeter",delimeter,true);
		id = PropertiesConstants.getString(p,"id",id,true);
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		String[] values = ctx.transform(in).split(delimeter);
		
		if (values.length > 0){
			for (String value:values){
				ctx.SetValue(id, value);
				super.onExecute(root, current, ctx, watcher);
			}
		}
	}
	
}
