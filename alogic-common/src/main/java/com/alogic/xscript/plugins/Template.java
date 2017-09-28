package com.alogic.xscript.plugins;

import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.resource.ResourceFactory;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

/**
 * Template
 * 
 * Tempate is used to create a template.
 * 
 * @author duanyy
 * @version 1.6.5.28 [20160719 duanyy] <br>
 * - 可以支持非map类型 <br>
 * 
 * @version 1.6.6.1 [20160823 duanyy] <br>
 * - 支持extend模式<br>
 * 
 * @version 1.6.10.2 [20170925 duanyy] <br>
 * - 修正无法工作的问题<br>
 */
public class Template extends Segment {
	protected String content = new String();
	protected String tag = "data";
	protected boolean extend = false;
	protected JsonProvider provider = JsonProviderFactory.createProvider();
	
	public Template(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		tag = p.GetValue("tag", tag, false, true);
		extend = PropertiesConstants.getBoolean(p,"extend",extend,true);
		content = PropertiesConstants.getString(p, "content", "",true);
		if (StringUtils.isEmpty(content)){
			String src = PropertiesConstants.getString(p, "src", "",true);
			if (StringUtils.isNotEmpty(src)){
				ResourceFactory resourceFactory = Settings.getResourceFactory();
				InputStream in = null;
				InputStreamReader reader = null;
				try {
					in = resourceFactory.load(src, null);
					reader = new InputStreamReader(in);
					
					StringBuffer strBuffer = new StringBuffer();
					char[] buffer = new char[2048];
					int length = -1;
					while ((length = reader.read(buffer)) != -1){
						strBuffer.append(buffer, 0, length);
					}
					content = strBuffer.toString();
				}catch (Exception ex){
					logger.error("The file is not a valid json file,url = " + src,ex);
				}finally{
					IOTools.close(in,reader);
				}				
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (current instanceof JsonObject) {
			Map<String, Object> jsonCurrent = (Map<String, Object>) current
					.getContent();
			if (StringUtils.isNotEmpty(content)) {
				Object template = provider.parse(content);

				if (template instanceof Map) {
					if (extend) {
						Map<String, Object> data = (Map<String, Object>) template;
						Iterator<Entry<String, Object>> iter = data.entrySet()
								.iterator();
						while (iter.hasNext()) {
							Entry<String, Object> entry = iter.next();
							jsonCurrent.put(entry.getKey(), entry.getValue());
						}
						super.onExecute(root, current, ctx, watcher);
					} else {
						String tagValue = ctx.transform(tag);
						if (StringUtils.isNotEmpty(tagValue)) {
							jsonCurrent.put(tagValue, template);
							super.onExecute(root,new JsonObject(tagValue,(Map<String,Object>)template), ctx,watcher);
						}
					}
				} else {
					String tagValue = ctx.transform(tag);
					if (StringUtils.isNotEmpty(tagValue)) {
						jsonCurrent.put(tagValue, template);
					}
				}
			}
		}else{
			throw new BaseException("core.not_supported",
					String.format("Tag %s does not support protocol %s",
							getXmlTag(),root.getClass().getName()));	
		}
	}
}
