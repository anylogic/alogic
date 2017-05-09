package com.alogic.xscript.plugins;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 针对字符串数组进行循环
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
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
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String[] values = ctx.transform(in).split(delimeter);
		
		if (values.length > 0){
			for (String value:values){
				ctx.SetValue(id, value);
				super.onExecute(root, current, ctx, watcher);
			}
		}
	}
	
}
