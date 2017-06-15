package com.alogic.xscript.plugins;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsArray;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 为数组增加子项
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.9.3 [20170615 duanyy] <br>
 * - 当文档为空的时候，不加入到父节点中 <br>
 */
public class ArrayItem extends Segment {
	protected String id = "$array";
	
	public ArrayItem(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getString(p,"id",id,true);
	}
	
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		XsArray list = ctx.getObject(id);
		if (list != null){
			XsObject template = list.newObject();			
			super.onExecute(root, template, ctx, watcher);			
			if (!template.isNull()){
				list.add(template);
			}
		}
	}	
}