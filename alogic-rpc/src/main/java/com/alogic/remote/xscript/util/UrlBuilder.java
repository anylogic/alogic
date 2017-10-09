package com.alogic.remote.xscript.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.alogic.xscript.AbstractLogiclet;
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
 * 构造合法的URL
 * 
 * @author yyduan
 * @since 1.6.10.3
 */
public class UrlBuilder extends AbstractLogiclet{
	/**
	 * 输出变量id
	 */
	protected String id;
	
	/**
	 * URL的前缀
	 */
	protected String base = "";
	
	protected String encoding = "utf-8";
	
	/**
	 * 表单数据
	 */
	protected List<Pair<String,String>> paraData = new ArrayList<Pair<String,String>>();
	
	public UrlBuilder(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getString(p,"id","$" + this.getXmlTag(),true);
		base = PropertiesConstants.getRaw(p,"base",base);
		encoding = PropertiesConstants.getString(p, "encoding", encoding);
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "param");
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element elem = (Element)n;
			String id = elem.getAttribute("id");
			String value = elem.getAttribute("value");
			if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(value)){
				paraData.add(new Pair.Default<String,String>(id,value));
			}
		}
	}	
	
	@Override
	protected void onExecute(final XsObject root,final XsObject current, final LogicletContext ctx,
			final ExecuteWatcher watcher) {
		if (StringUtils.isNotEmpty(id)){
			StringBuffer buffer = new StringBuffer(ctx.transform(base));
			
			buffer.append("?");
			
			boolean first = true;
			for (Pair<String,String> p:paraData){
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
			ctx.SetValue(id, buffer.toString());
		}
	}

}