package com.alogic.xscript.plugins;

import org.apache.commons.lang3.StringUtils;
import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Remove用于删除工作文档当前节点的指定子节点
 * 
 * @author yyduan
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public class Remove extends AbstractLogiclet {
	protected String id = "$rem";
	protected String tag = "";

	public Remove(String tag, Logiclet p) {
		super(tag, p);
	}

	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getString(p,"id","$" + getXmlTag(),true);
		tag = PropertiesConstants.getRaw(p,"tag",tag);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			String v = ctx.transform(tag);
			if (StringUtils.isNotEmpty(v)){
				ctx.SetValue(id,Boolean.toString(current.remove(v)));
			}else{
				ctx.SetValue(id,Boolean.toString(false));
			}
		}
	}
}