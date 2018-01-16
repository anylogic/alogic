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
 * @version 1.6.11.10 [20180116 duanyy] <br>
 * - 去掉定位失败的警告 <br>
 * 
 */

public class Location extends Segment {
	protected String jsonPath;
	protected String xmlPath;
	public Location(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		jsonPath = PropertiesConstants.getString(p, "jsonPath", jsonPath);
		xmlPath = PropertiesConstants.getString(p, "xmlPath", xmlPath);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onExecute(XsObject root, XsObject current,
			final LogicletContext ctx, final ExecuteWatcher watcher) {
		XsObject newCurrent = current;
		if (root instanceof JsonObject) {
			if (StringUtils.isNotEmpty(jsonPath)){
				Object result = JsonPath.read((Map<String,Object>)current.getContent(), jsonPath);
				if (result instanceof Map<?, ?>) {
					newCurrent = new JsonObject("node",
							(Map<String, Object>) result);
					super.onExecute(root, newCurrent, ctx, watcher);
				}
			}else{
				log(String.format("[%s]json path is null",getXmlTag()),"error");
			}
		} else {
			if (root instanceof XmlObject) {
				if (StringUtils.isNotEmpty(xmlPath)){
					Node result = XmlTools.getNodeByPath(
							(Element) current.getContent(), xmlPath);
					if (result != null && result instanceof Element) {
						newCurrent = new XmlObject("node", (Element) result);
						super.onExecute(root, newCurrent, ctx, watcher);
					}
				}else{
					log(String.format("[%s]xml path is null",getXmlTag()),"error");
				}
			} else {
				throw new BaseException("core.e1000", String.format(
						"Tag %s does not support protocol %s", getXmlTag(),
						root.getClass().getName()));
			}
		}
		
	}
}
