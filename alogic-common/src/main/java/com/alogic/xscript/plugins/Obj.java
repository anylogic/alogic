package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 在当前文档增加一个对象
 * 
 * @author duanyy
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.11.31 [20170522 duanyy] <br>
 * - 增加是否新增的变量输出;
 * 
 */
public class Obj extends Segment {
	protected String tag = "data";
	protected String id = "$obj";
	
	public Obj(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id",id,true);
		tag = PropertiesConstants.getRaw(p,"tag",tag);
	}
	
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String tagValue = ctx.transform(tag);
		if (StringUtils.isNotEmpty(tagValue)){
			XsObject template = current.getObjectChild(tagValue, false);
			if (template != null){
				ctx.SetValue(id, "true");
				super.onExecute(root,template, ctx, watcher);
			}else{
				template = current.getObjectChild(tagValue, true);
				ctx.SetValue(id, "false");
				super.onExecute(root,template, ctx, watcher);				
			}
		}
	}	
}
