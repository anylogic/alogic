package com.alogic.remote.xscript.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.remote.HttpConstants;
import com.alogic.remote.Request;
import com.alogic.remote.xscript.RequestHandler;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Pair;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 通过表单发送消息体
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class ByForm extends RequestHandler {
	/**
	 * 表单数据
	 */
	protected List<Pair<String,String>> formData = new ArrayList<Pair<String,String>>();
	protected String encoding = "utf-8";
	
	public ByForm(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
		encoding = PropertiesConstants.getString(p, "encoding", encoding);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "field");
		
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element elem = (Element)n;
			String id = elem.getAttribute("id");
			String value = elem.getAttribute("value");
			if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(value)){
				formData.add(new Pair.Default<String,String>(id,value));
			}
		}
	}	
	
	@Override
	protected void onExecute(final Request req, final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher) {
		
		req.setHeader(HttpConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");
		
		StringBuffer buffer = new StringBuffer();
		
		boolean first = true;
		for (Pair<String,String> p:formData){
			String id = p.key();
			String value = ctx.transform(p.value());
			if (StringUtils.isNotEmpty(value)){
				try {
					if (!first){
						buffer.append("&");
					}
					
					String encodeValue = URLEncoder.encode(value, encoding);
					buffer.append(id).append("=").append(encodeValue);
					first = false;
				} catch (UnsupportedEncodingException e) {
					log(String.format("Encoding %s is not supported", encoding));
				}				
			}
		}
		
		req.setBody(buffer.toString());
	}

}