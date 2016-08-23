package com.alogic.xscript.plugins;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

/**
 * 从变量中解析出Json对象，并扩展或添加到当前文档节点
 * 
 * @author duanyy
 *
 * @since 1.6.6.1
 */
public class GetAsJson extends Segment{
	protected String tag = "data";
	protected String content = "";
	protected boolean extend = false;
	protected JsonProvider provider = JsonProviderFactory.createProvider();
	
	public GetAsJson(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getRaw(p,"tag",tag);
		content = PropertiesConstants.getRaw(p,"content","");
		extend = PropertiesConstants.getBoolean(p,"extend",extend);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String v = ctx.transform(content);
		if (StringUtils.isNotEmpty(v)){
			Object template = provider.parse(v);
			
			if (template instanceof Map){
				if (extend){
					Map<String,Object> data = (Map<String,Object>)template;
					Iterator<Entry<String,Object>> iter = data.entrySet().iterator();
					while (iter.hasNext()){
						Entry<String,Object> entry = iter.next();
						current.put(entry.getKey(), entry.getValue());
					}
					super.onExecute(root, current, ctx, watcher);
				}else{
					String tagValue = ctx.transform(tag);
					if (StringUtils.isNotEmpty(tagValue)){
						current.put(tagValue, template);
						super.onExecute(root, (Map<String,Object>)template, ctx, watcher);
					}
				}
			}else{
				String tagValue = ctx.transform(tag);
				if (StringUtils.isNotEmpty(tagValue)){
					current.put(tagValue, template);
				}
			}
		}		
	}
}
