package com.alogic.xscript.plugins;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.alogic.xscript.doc.xml.XmlObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;
import com.jayway.jsonpath.JsonPath;

/**
 * Location
 * @author yyduan
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 */
public class Location extends Segment {
	protected String path;
	
	public Location(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		path = PropertiesConstants.getString(p, "path", path);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(XsObject root,XsObject current,final LogicletContext ctx,final ExecuteWatcher watcher){
		XsObject newCurrent = current;
		if (StringUtils.isNotEmpty(path)){
			if (root instanceof JsonObject){
				Object result = JsonPath.read(current, path);
				if (result instanceof Map<?,?>){
					newCurrent = new JsonObject("node",(Map<String,Object>)result);
				}else{
					logger.error("Can not locate the path:" + path);
				}				
			}else{
				if (root instanceof XmlObject){
					Node result = XmlTools.getNodeByPath((Element)current.getContent(), path);
					if (result != null && result instanceof Element){
						newCurrent = new XmlObject("node",(Element)result);
					}else{
						logger.error("Can not locate the path:" + path);
					}
				}else{
					throw new BaseException("core.not_supported",String.format("Tag %s does not support protocol %s",getXmlTag(),root.getClass().getName()));	
				}
			}
		}
		super.onExecute(root, newCurrent, ctx, watcher);
	}
}
