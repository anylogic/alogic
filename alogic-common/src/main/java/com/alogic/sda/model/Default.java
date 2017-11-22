package com.alogic.sda.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.sda.SecretDataArea;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 缺省实现
 * @author yyduan
 * @since 1.6.10.8
 */
public class Default extends SecretDataArea.Abstract{
	/**
	 * 编解码器
	 */
	protected Coder coder = null;
	protected String key = "alogic";
	
	/**
	 * Field
	 */
	protected Map<String,Field> fields = new HashMap<String,Field>();
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		key = PropertiesConstants.getString(p,"key", key);
		coder = CoderFactory.newCoder(PropertiesConstants.getString(p,"coder", "Default"));
	}
	
	@Override
	public void configure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);
		
		NodeList children = XmlTools.getNodeListByPath(e, "field");
		
		for (int i = 0 ;i < children.getLength() ; i ++){
			Node n = children.item(i);
			if (Node.ELEMENT_NODE != n.getNodeType()){
				continue;
			}
			
			Element elem = (Element)n;
			
			String id = elem.getAttribute("id");
			if (StringUtils.isNotEmpty(id)){
				XmlElementProperties pp = new XmlElementProperties(elem,props);
				Field f = new Field(id,
						PropertiesConstants.getString(pp,"value","",true),
						PropertiesConstants.getBoolean(pp,"raw",true,true));
				
				fields.put(id, f);
			}
			
		}
		configure(props);
	}
	
	@Override
	public String getField(String field, String current) {
		Field found = fields.get(field);
		return found != null ? (found.raw ? found.value : coder.decode(found.value, key))
				: current;
	}

	/**
	 * SDA的Field
	 * @author yyduan
	 *
	 */
	public static class Field {
		public String id;
		public String value;
		public boolean raw;
		
		public Field(final String id,final String value,final boolean raw){
			this.id = id;
			this.value = value;
			this.raw = raw;
		}
	}
}
