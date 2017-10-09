package com.alogic.remote.xscript.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.jayway.jsonpath.JsonPath;

/**
 * 将当前文档的部分内容拷贝到主文档
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class Copy extends AbstractLogiclet{
	
	protected String jsonPath;
	protected String tag = "data";
	protected String masterId = "masterId";
	
	public Copy(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
		jsonPath = PropertiesConstants.getString(p, "path", jsonPath,true);
	}
	
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		if (current instanceof JsonObject){
			XsObject json = ctx.getObject(masterId);
			if (json == null){
				throw new BaseException("core.no_master_doc","It must be in a remote-as-json context,check your script.");
			}
			if (!(json instanceof JsonObject)){
				throw new BaseException("core.no_master_doc","The master doc is not a JsonObject.");
			}
			
			@SuppressWarnings("unchecked")
			Map<String,Object> jsonNode = (Map<String,Object>)json.getContent();
			String tagValue = ctx.transform(tag);
			if (StringUtils.isNotEmpty(tagValue)){
				if (StringUtils.isNotEmpty(jsonPath)){
					Object result = JsonPath.read(current.getContent(), jsonPath);
					if (result == null){
						throw new BaseException("core.location",String.format("Can not location path %s in current doc", jsonPath));
					}
					jsonNode.put(tagValue, result);
				}else{
					jsonNode.put(tagValue, current.getContent());
				}
			}
		}else{
			throw new BaseException("core.not_supported",
					String.format("Tag %s does not support protocol %s",this.getXmlTag(),root.getClass().getName()));
		}
	}
}
