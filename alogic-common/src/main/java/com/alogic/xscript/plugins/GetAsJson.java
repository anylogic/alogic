package com.alogic.xscript.plugins;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
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
 * 
 * @version 1.6.9.1 [20170516 duanyy] <br>
 * - 修复部分插件由于使用新的文档模型产生的兼容性问题 <br>
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
	protected void onExecute(XsObject root,
			XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		if (current instanceof JsonObject){
			Map<String,Object> jsonCurrent = (Map<String, Object>) current.getContent();
			String v = ctx.transform(content);
			if (StringUtils.isNotEmpty(v)){
				Object template = provider.parse(v);
				
				if (template instanceof Map){
					if (extend){
						Map<String,Object> data = (Map<String,Object>)template;
						Iterator<Entry<String,Object>> iter = data.entrySet().iterator();
						while (iter.hasNext()){
							Entry<String,Object> entry = iter.next();
							jsonCurrent.put(entry.getKey(), entry.getValue());
						}
						super.onExecute(root, current, ctx, watcher);
					}else{
						String tagValue = ctx.transform(tag);
						if (StringUtils.isNotEmpty(tagValue)){
							jsonCurrent.put(tagValue, template);
							JsonObject newCurrent = new JsonObject(tagValue,(Map<String,Object>)template);
							super.onExecute(root, newCurrent, ctx, watcher);
						}
					}
				}else{
					String tagValue = ctx.transform(tag);
					if (StringUtils.isNotEmpty(tagValue)){
						jsonCurrent.put(tagValue, template);
					}
				}
			}		
		}else{
			throw new BaseException("core.e1000",
					String.format("Tag %s does not support protocol %s",
							getXmlTag(),root.getClass().getName()));	
		}
	}
}
