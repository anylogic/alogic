package com.anysoft.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



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
 */
public class SimpleModel extends Properties implements Cacheable {
	protected String id = "";
	
	public SimpleModel(String theId){
		id = theId;
	}
	
	@Override
	public void fromXML(Element root) {
		fields.clear();
		
		NodeList nodeList = XmlTools.getNodeListByPath(root, "fields/field");
		if (nodeList != null && nodeList.getLength() > 0){
			
			for (int i = 0,length = nodeList.getLength() ; i < length ; i ++){
				Node n = nodeList.item(i);
				
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				String name = e.getAttribute("id");
				String value = e.getAttribute("value");
				String coder = e.getAttribute("coder");
				coder = (coder == null || coder.length() <= 0) ? "Default":coder;
				
				if (name == null || value == null){
					continue;
				}
				
				Field field = new Field();
				
				field.name = name;
				field.coder = coder;
				
				String isRaw = e.getAttribute("isRaw");
				
				Coder _coder = CoderFactory.newCoder(field.coder);
				if (_coder == null || (isRaw != null && isRaw.equals("true"))){
					field.value = value;
				}else{
					String key = e.getAttribute("key");
					field.value = _coder.decode(value,key);
				}
				
				fields.put(field.name, field);
			}
		}
	}

	@Override
	public void toXML(Element root) {
		root.setAttribute("id", id);
		
		Collection<Field> _fields = fields.values();
		
		if (!_fields.isEmpty()){
			Document doc = root.getOwnerDocument();			
			Element _fieldsElem = doc.createElement("fields");
			
			for (Field field:_fields){
				Element _fieldElem = doc.createElement("field");				
				_fieldElem.setAttribute("id", field.name);				
				_fieldElem.setAttribute("coder", field.coder);				
				Coder coder = CoderFactory.newCoder(field.coder);
				if (coder != null){
					String key = coder.createKey();
					_fieldElem.setAttribute("value", coder.encode(field.value,key));
					if (key != null && key.length() > 0){
						_fieldElem.setAttribute("key", key);
					}
				}else{
					_fieldElem.setAttribute("value", field.value);
				}				
				_fieldsElem.appendChild(_fieldElem);
			}
			
			root.appendChild(_fieldsElem);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fromJson(Map<String,Object> root) {
		fields.clear();
		
		Object _fieldsObject = root.get("fields");
		if (_fieldsObject != null && _fieldsObject instanceof List){
			for (Object _fieldObject:(List<Object>)_fieldsObject){
				if (!(_fieldObject instanceof Map)){
					continue;
				}
				
				Map<String,Object> _data = (Map<String,Object>)_fieldObject;
				String _value = (String)_data.get("value");
				String _coder = (String)_data.get("coder");
				String _isRaw = (String)_data.get("isRaw");
				String _name = (String)_data.get("id");
				
				_coder = (_coder == null || _coder.length() <= 0)?"Default":_coder;
				if (_name != null && _name.length() > 0 && _value != null){
					Field field = new Field();
					
					field.name = _name;
					field.coder = _coder;
					Coder coder = CoderFactory.newCoder(field.coder);
					if (coder == null || (_isRaw != null && _isRaw.equals("true"))){
						field.value = _value;
					}else{
						field.value = coder.decode(_value,(String)_data.get("key"));
					}
					
					fields.put(field.name, field);
				}
			}
		}
	}

	@Override
	public void toJson(Map<String,Object> root) {
		JsonTools.setString(root, "id", id);
		
		Collection<Field> _fields = fields.values();
		
		if (!_fields.isEmpty()){
			
			List<Object> _fieldList = new ArrayList<Object>();
			
			for (Field field:_fields){
				Map<String,Object> _field = new HashMap<String,Object>();				
				Coder coder = CoderFactory.newCoder(field.coder);
				if (coder != null){
					String key = coder.createKey();			
					_field.put("value", coder.encode(field.value,key));
					if (key != null && key.length() > 0){
						_field.put("key", key);
					}
				}else{
					_field.put("value", field.value);
				}				
				_field.put("coder", field.coder);
				_field.put("id", field.name);
				
				_fieldList.add(_field);
			}
			
			root.put("fields", _fieldList);
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
	
	protected HashMap<String,Field> fields = new HashMap<String,Field>();
	
	@Override
	public void Clear() {
		fields.clear();
	}

	
	protected String _GetValue(String id) {
		Field found = fields.get(id);
		return found == null ? "" : found.value;
	}

	
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
