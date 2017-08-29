package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 计算处理过程的耗时
 * 
 * @author yyduan
 * 
 * @since 1.6.9.9
 * @version 1.6.9.9 [20170829 duanyy] <br>
 * - 增加Duration插件 <br>
 */
public class Duration extends Segment{
	protected String id = "";
	public Duration(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","",true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			long start = System.nanoTime();
			try {
				super.onExecute(root, current, ctx, watcher);
			}finally{
				ctx.SetValue(id, String.valueOf(System.nanoTime() - start));
			}
		}
		else{
			super.onExecute(root, current, ctx, watcher);
		}
	}
}
