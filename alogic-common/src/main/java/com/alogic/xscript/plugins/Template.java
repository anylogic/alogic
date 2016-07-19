package com.alogic.xscript.plugins;

import java.io.InputStream;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
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
 */
public class Template extends Segment {
	protected Object template = null;
	protected String tag = "data";
	
	public Template(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		tag = PropertiesConstants.getString(p, "tag", tag);
		
		String content = PropertiesConstants.getString(p, "content", "");
		JsonProvider provider = JsonProviderFactory.createProvider();
		if (StringUtils.isNotEmpty(content)){
			template = provider.parse(content);
		}else{
			String src = PropertiesConstants.getString(p, "src", "");
			if (StringUtils.isNotEmpty(src)){
				ResourceFactory resourceFactory = Settings.getResourceFactory();
				InputStream in = null;
				try {
					in = resourceFactory.load(src, null);
					template = provider.parse(in);
				}catch (Exception ex){
					logger.error("The file is not a valid json file,url = " + src,ex);
				}finally{
					IOTools.close(in);
				}				
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (current != null && template != null){
			current.put(tag, template);
			
			if (template instanceof Map){
				super.onExecute(root, (Map<String,Object>)template, ctx, watcher);
			}
		}
	}


}
