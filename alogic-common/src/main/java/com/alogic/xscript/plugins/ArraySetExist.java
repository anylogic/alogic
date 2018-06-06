package com.alogic.xscript.plugins;

import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 检查集合成员是否存在
 * 
 * @author yyduan
 * @since 1.6.11.34
 */
public class ArraySetExist extends AbstractLogiclet {
	protected String pid = "$set";
	protected String $value = "";
	protected String id;

	public ArraySetExist(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		pid = PropertiesConstants.getString(p,"pid",pid,true);
		id = PropertiesConstants.getString(p,"id","$" + this.getXmlTag(),true);
		$value = PropertiesConstants.getRaw(p,"member",$value);
	}
	
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		Set<String> list = ctx.getObject(pid);
		boolean exist = false;
		if (list != null){
			String value = PropertiesConstants.transform(ctx, $value, "");
			if (StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(id)){
				exist = list.contains(value);
			}
		}		
		if (StringUtils.isNotEmpty(id)){
			ctx.SetValue(id, BooleanUtils.toStringTrueFalse(exist));
		}
	}	
}
