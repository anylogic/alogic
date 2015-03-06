package com.anysoft.util;

import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 缺省的变量集实现
 * <p>本类基于{@link java.util.Hashtable}实现.</p>
 * @author hmyyduan
 *
 * @version 1.6.3.4 [duanyy 20150305] <br>
 * - 实现{@link com.anysoft.util.JsonSerializer JsonSerializer}和{@link com.anysoft.util.XmlSerializer XmlSerializer}接口<br>
 */
public class DefaultProperties extends Properties implements JsonSerializer,XmlSerializer{
	/**
	 * 变量集内容
	 */
	protected Hashtable<String, String> content = new Hashtable<String, String>();
	
	/**
	 * 构造函数
	 * @param _domain 变量域
	 * @param _parent 父节点
	 * @see Properties#Properties(String, Properties)
	 */
	public DefaultProperties(String _domain,Properties _parent){
		super(_domain,_parent);
	}
	
	/**
	 * 构造函数
	 * @param _domain 变量域
	 * @see Properties#Properties(String)
	 */
	public DefaultProperties(String _domain){
		super(_domain);
	}
	
	/**
	 * 构造函数
	 * @see Properties#Properties()
	 */
	public DefaultProperties(){
	}
	
	/**
	 * 获取变量集的内容
	 * @return 内容
	 */
	public Hashtable<String, String> getContent(){
		return content;
	}
	
	/**
	 * 向变量集中写入变量
	 * @param _name 变量名
	 * @param _value 变量值
	 */
	protected void _SetValue(String _name, String _value) {
		if (_value == null || _value.length() <= 0){
			content.remove(_name);
		}
		content.put(_name,_value);
	}

	/**
	 * 从变量集中提取出变量值
	 * @param _name 变量名
	 */
	protected String _GetValue(String _name) {
		String __value = (String)content.get(_name);
		if (__value == null){
			__value = "";
		}
		return __value;
	}
	
	/**
	 * 获取变量集中所有的变量名列表
	 * @return 变量名列表
	 */
	public Enumeration<String> keys(){return content.keys();} 
	
	/**
	 * 打印出变量集中的内容
	 * @param out 输出打印流
	 */
	public void list(PrintStream out){
		Enumeration<?> __keys = keys();
		while (__keys.hasMoreElements()){
			String __name = (String)__keys.nextElement();
			String __value = _GetValue(__name);
			out.print(__name);
			out.print("=");
			out.println(__value);
		}
	}
	
	/**
	 * 清除变量集内容
	 */
	public void Clear() {
		content.clear();		
	}
	
	/**
	 * 从另一实例中复制内容
	 * @param other 另一实例
	 */
	public void copyFrom(DefaultProperties other){
		Enumeration<?> __keys = other.keys();
		Clear();
		while (__keys.hasMoreElements()){
			String __name = (String)__keys.nextElement();
			String __value = other._GetValue(__name);
			SetValue(__name, __value);
		}
	}

	public void toXML(Element root) {
		//为了输出文件的美观，添加一个\n文件节点
		Document doc = root.getOwnerDocument();
		root.appendChild(doc.createTextNode("\n"));
		Enumeration<?> ids = keys();
		
		while (ids.hasMoreElements()){
			String id = (String)ids.nextElement();
			String value = _GetValue(id);
			if (value.length() <= 0 || id.length() <= 0){
				continue;
			}
			Element e = doc.createElement("parameter");
			e.setAttribute("id",id);
			e.setAttribute("value",value);
			root.appendChild(e);
			//为了输出文件的美观，添加一个\n文件节点
			root.appendChild(doc.createTextNode("\n"));
		}	
	}

	public void fromXML(Element e) {
		Clear();
		loadFrom(e);
	}

	public void toJson(Map<String, Object> json) {
		Iterator<Entry<String,String>> iter = content.entrySet().iterator();
		
		while (iter.hasNext()){
			Entry<String,String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();
			json.put(key, value);
		}
	}

	public void fromJson(Map<String, Object> json) {
		Clear();
		loadFrom(json);
	}
	
	public void loadFrom(Map<String,Object> json){
		Iterator<Entry<String,Object>> iter = json.entrySet().iterator();
		
		while (iter.hasNext()){
			Entry<String,Object> entry = iter.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			
			if (value instanceof String || value instanceof Number){
				content.put(key, value.toString());
			}
		}
	}
	
	public void loadFrom(Element root){
		NodeList nodeList = root.getChildNodes();	
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			if (node.getNodeName().equals("parameter")){
				Element e = (Element)node;
				String id = e.getAttribute("id");
				String value = e.getAttribute("value");
				//支持final标示,如果final为true,则不覆盖原有的取值
				boolean isFinal = e.getAttribute("final").equals("true")?true:false;
				if (isFinal){
					String oldValue = this._GetValue(id);
					if (oldValue == null || oldValue.length() <= 0){
						SetValue(id,value);
					}
				}else{
					SetValue(id,value);
				}
			}
		}
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		
		Iterator<Entry<String,String>> iter = content.entrySet().iterator();
		
		while (iter.hasNext()){
			Entry<String,String> entry = iter.next();
			String key = entry.getKey();
			String value = entry.getValue();
			buffer.append(key).append("=").append(value);
			if (iter.hasNext()){
				buffer.append(";");
			}
		}
		
		return buffer.toString();
	}
}
