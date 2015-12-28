package com.anysoft.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.cache.Cacheable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 简单缓存对象的实现
 * 
 * @author duanyy
 * 
 * @since 1.0.12
 * @version 1.3.0 [20140727 duanyy]<br>
 * - Cachable修正类名为Cacheable <br>
 * 
 * @version 1.4.4 [20140912 duanyy]<br>
 * - JsonSerializer中Map参数化<br>
 * 
 * @version 1.6.3.2 [20150213 duanyy] <br>
 * - 接口{@link com.anysoft.cache.Cacheable Cacheable}增加了{@link com.anysoft.cache.Cacheable#expire() Cacheable.expire}方法 <br>
 * 
 * @version 1.6.4.17 [20151216 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 * 
 * @version 1.6.4.20 [20151222 duanyy] <br>
 * - 根据sonar建议优化代码 <br>
 */
public class SimpleModel extends Properties implements Cacheable {
	protected String id = "";
	protected HashMap<String,Field> fields = new HashMap<String,Field>(); // NOSONAR
	
	
	public SimpleModel(String theId){
		id = theId;
	}
	
	@Override
	public void fromXML(Element root) {
		fields.clear();
		
		NodeList nodeList = XmlTools.getNodeListByPath(root, "fields/field");
		if (nodeList != null && nodeList.getLength() > 0){
			int length = nodeList.getLength();
			for (int i = 0; i < length ; i ++){
				Node n = nodeList.item(i);
				
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				String name = e.getAttribute("id");
				String value = e.getAttribute("value"); // NOSONAR
				String coder = e.getAttribute("coder"); // NOSONAR
				coder = StringUtils.isEmpty(coder) ? "Default":coder; // NOSONAR
				
				if (StringUtils.isNotBlank(name) && StringUtils.isNotEmpty(value)){
					Field field = new Field();
					
					field.name = name;
					field.coder = coder;
					
					String isRaw = e.getAttribute("isRaw");
					
					Coder aCoder = CoderFactory.newCoder(field.coder);
					if (aCoder == null || BooleanUtils.toBoolean(isRaw)){ // NOSONAR
						field.value = value;
					}else{
						String key = e.getAttribute("key");
						field.value = aCoder.decode(value,key);
					}
					
					fields.put(field.name, field);
				}
			}
		}
	}

	@Override
	public void toXML(Element root) {
		root.setAttribute("id", id);
		
		Collection<Field> collection = fields.values();
		
		if (!collection.isEmpty()){
			Document doc = root.getOwnerDocument();			
			Element fieldsElem = doc.createElement("fields"); // NOSONAR
			
			for (Field field:collection){
				Element fieldElem = doc.createElement("field");				
				fieldElem.setAttribute("id", field.name);				
				fieldElem.setAttribute("coder", field.coder);				
				Coder coder = CoderFactory.newCoder(field.coder);
				if (coder != null){
					String key = coder.createKey();
					fieldElem.setAttribute("value", coder.encode(field.value,key));
					if (StringUtils.isNotEmpty(key)){ // NOSONAR
						fieldElem.setAttribute("key", key);
					}
				}else{
					fieldElem.setAttribute("value", field.value);
				}				
				fieldsElem.appendChild(fieldElem);
			}
			
			root.appendChild(fieldsElem);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromJson(Map<String,Object> root) { // NOSONAR
		fields.clear();
		
		Object fieldsObject = root.get("fields");
		if (fieldsObject != null && fieldsObject instanceof List){
			for (Object fieldObject:(List<Object>)fieldsObject){
				if (!(fieldObject instanceof Map)){
					continue;
				}
				
				Map<String,Object> data = (Map<String,Object>)fieldObject;
				String value = (String)data.get("value");
				String coderId = (String)data.get("coder");
				String isRaw = (String)data.get("isRaw");
				String name = (String)data.get("id");
				
				coderId = (coderId == null || coderId.length() <= 0)?"Default":coderId;
				if (name != null && name.length() > 0 && value != null){
					Field field = new Field();
					
					field.name = name;
					field.coder = coderId;
					Coder coder = CoderFactory.newCoder(field.coder);
					if (coder == null || BooleanUtils.toBoolean(isRaw)){ // NOSONAR
						field.value = value;
					}else{
						field.value = coder.decode(value,(String)data.get("key"));
					}
					
					fields.put(field.name, field);
				}
			}
		}
	}

	@Override
	public void toJson(Map<String,Object> root) {
		JsonTools.setString(root, "id", id);
		
		Collection<Field> collection = fields.values();
		
		if (!collection.isEmpty()){
			
			List<Object> fieldList = new ArrayList<Object>(); // NOSONAR
			
			for (Field field:collection){
				Map<String,Object> fieldMap = new HashMap<String,Object>();				 // NOSONAR
				Coder coder = CoderFactory.newCoder(field.coder);
				if (coder != null){
					String key = coder.createKey();			
					fieldMap.put("value", coder.encode(field.value,key));
					if (StringUtils.isNotEmpty(key)){ // NOSONAR
						fieldMap.put("key", key);
					}
				}else{
					fieldMap.put("value", field.value);
				}				
				fieldMap.put("coder", field.coder);
				fieldMap.put("id", field.name);
				
				fieldList.add(fieldMap);
			}
			
			root.put("fields", fieldList);
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isExpired() {
		return false;
	}
	@Override
	public void expire(){
		// do nothing
	}

	protected static class Field {
		protected String name;
		protected String value;
		protected String coder;
	}

	
	@Override
	public void Clear() {
		fields.clear();
	}

	@Override
	protected String _GetValue(String id) {
		Field found = fields.get(id);
		return found == null ? "" : found.value;
	}

	@Override
	protected void _SetValue(String id, String value) {
		Field found = fields.get(id);
		if (found == null){
			found = new Field();
			found.name = id;
			found.value = value;
			found.coder = "Default";
			fields.put(id, found);
		}else{
			found.value = value;
		}		
	}
 
}
