package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsArray;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 在当前文档增加一个数组
 * 
 * @author duanyy
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.9.1 [20170516 duanyy] <br>
 * - 修复部分插件由于使用新的文档模型产生的兼容性问题 <br>
 */
public class Array extends Segment {
	protected String tag = "data";
	protected String id = "$array";
	
	public Array(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getString(p,"id",id,true);
		tag = PropertiesConstants.getRaw(p,"tag", tag);
	}
	
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		String tagValue = ctx.transform(tag);
		if (StringUtils.isNotEmpty(tagValue)){
			XsArray array = current.getArrayChild(tagValue, true);
			try {
				ctx.setObject(id, array);
				super.onExecute(root, current, ctx, watcher);
			}finally{
				ctx.removeObject(id);
			}
		}
	}		
}
