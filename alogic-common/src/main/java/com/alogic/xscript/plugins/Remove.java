package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Remove用于删除工作文档当前节点的指定子节点
 * 
 * @author yyduan
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
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			String v = ctx.transform(tag);
			if (StringUtils.isNotEmpty(v)){
				ctx.SetValue(id,Boolean.toString(current.remove(v) != null));
			}else{
				ctx.SetValue(id,Boolean.toString(false));
			}
		}
	}
}