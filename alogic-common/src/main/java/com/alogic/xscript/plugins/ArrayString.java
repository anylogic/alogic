package com.alogic.xscript.plugins;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsArray;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonArray;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * String形式的数组对象
 * 
 * @author yyduan
 * @since 1.6.11.28
 */
public class ArrayString extends AbstractLogiclet {
	protected String id = "$array";
	protected String value = "";
	protected String dft = "";
	
	public ArrayString(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getString(p,"id",id,true);
		
		value = PropertiesConstants.getRaw(p,"value",value);
		dft = PropertiesConstants.getRaw(p,"dft",dft);
	}
	
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		XsArray list = ctx.getObject(id);
		if (list != null && list instanceof JsonArray){
			JsonArray jsonList = (JsonArray)list;
			String v = PropertiesConstants.transform(ctx, value, dft);
			if (StringUtils.isNotEmpty(v)){
				@SuppressWarnings("unchecked")
				List<Object> content = (List<Object>) jsonList.getContent();
				content.add(v);
			}
		}
	}	
}
