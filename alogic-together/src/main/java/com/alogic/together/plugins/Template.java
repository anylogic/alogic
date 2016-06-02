package com.alogic.together.plugins;

import java.io.InputStream;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.alogic.together.ExecuteWatcher;
import com.alogic.together.Logiclet;
import com.alogic.together.LogicletContext;
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
 *
 */
public class Template extends Segment {
	protected Map<String,Object> template = null;
	protected String id = "data";
	
	public Template(String tag, Logiclet p) {
		super(tag, p);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configure(Properties p) {
		super.configure(p);
		String content = PropertiesConstants.getString(p, "content", "");
		
		JsonProvider provider = JsonProviderFactory.createProvider();
		if (StringUtils.isNotEmpty(content)){
			template = (Map<String, Object>) provider.parse(content);
		}else{
			String src = PropertiesConstants.getString(p, "src", "");
			if (StringUtils.isNotEmpty(src)){
				ResourceFactory resourceFactory = Settings.getResourceFactory();
				InputStream in = null;
				try {
					in = resourceFactory.load(src, null);
					template = (Map<String, Object>) provider.parse(in);
				}catch (Exception ex){
					logger.error("The file is not a valid json file,url = " + src,ex);
				}finally{
					IOTools.close(in);
				}				
			}
		}
	}

	@Override
	protected void onExecute(Map<String, Object> root,
			Map<String, Object> current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (current != null && template != null){
			current.put(id, template);
			
			super.onExecute(root, current, ctx, watcher);
		}
	}


}
