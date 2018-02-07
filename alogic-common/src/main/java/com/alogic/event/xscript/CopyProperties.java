package com.alogic.event.xscript;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alogic.event.Event;
import com.alogic.event.EventBuilder;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 从指定的文档属性中拷贝Event属性
 * 
 * @author yyduan
 * @since 1.6.11.16
 */
public class CopyProperties extends EventBuilder {
	protected String $tag = "property";
	
	public CopyProperties(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		$tag = PropertiesConstants.getRaw(p,"tag",$tag);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(Event e, XsObject root, XsObject current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		String tag = PropertiesConstants.transform(ctx, $tag, "");
		if (StringUtils.isNotEmpty(tag)){
			if (current instanceof JsonObject){
				Map<String,Object> content = (Map<String,Object>)current.getContent();				
				Object property = content.get(tag);
				if (property != null && property instanceof Map){
					Map<String,Object> map = (Map<String,Object>)property;
					Iterator<Entry<String,Object>> iter = map.entrySet().iterator();
					while (iter.hasNext()){
						Entry<String,Object> entry = iter.next();
						Object value = entry.getValue();
						if (value != null){
							e.setProperty(entry.getKey(), value.toString(), true);
						}
					}
				}
			}else{
				throw new BaseException("core.e1000",
						String.format("Tag %s does not support protocol %s",this.getXmlTag(),root.getClass().getName()));	
			}	
		}
	}
}