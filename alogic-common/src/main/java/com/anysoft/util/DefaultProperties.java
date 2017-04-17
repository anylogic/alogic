package com.anysoft.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.resource.ResourceFactory;

/**
 * 缺省的变量集实现
 * <p>本类基于{@link java.util.Hashtable}实现.</p>
 * @author hmyyduan
 *
 * @version 1.6.3.4 [duanyy 20150305] <br>
 * - 实现{@link com.anysoft.util.JsonSerializer JsonSerializer}和{@link com.anysoft.util.XmlSerializer XmlSerializer}接口<br>
 * 
 * @version 1.6.7.7 [20170126 duanyy] <br>
 * - 迁移loadFrom系列方法到父类 <br>
 * 
 * @version 1.6.8.7 [20170412 duanyy] <br>
 * - DefaultProperties容器由Hashtable更改为HashMap <br>
 */
public class DefaultProperties extends Properties implements JsonSerializer,XmlSerializer{
	/**
	 * 变量集内容
	 */
	protected Map<String, String> content = new HashMap<String, String>();
	
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
	public Map<String, String> getContent(){
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
	public Set<String> keys(){return content.keySet();} 
	
	/**
	 * 打印出变量集中的内容
	 * @param out 输出打印流
	 */
	public void list(PrintStream out){
		Iterator<String> __keys = keys().iterator();
		while (__keys.hasNext()){
			String __name = (String)__keys.next();
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
	 * 装入指定的xrc文件，并读入xrc文件中的变量信息
	 * @param _url xrc文件的url
	 * @param secondary xrc文件的备用url
	 * @param _rm ResourceFactory实例
	 */
	public void addSettings(String _url,String secondary,ResourceFactory _rm){
		ResourceFactory rm = _rm;
		if (null == _rm){
			rm = new ResourceFactory();
		}
		
		InputStream in = null;
		try {
			in = rm.load(_url,secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);	
			if (doc != null){
				loadFrom(doc.getDocumentElement());
			}
		}catch (Exception ex){
			logger.error("Error occurs when load xml file,source=" + _url, ex);
		}finally {
			IOTools.closeStream(in);
		}
	}
	
	/**
	 * 从一个DefaultProperties复制变量列表
	 * @param p DefaultProperties实例
	 */
	public void addSettings(DefaultProperties p){
		Iterator<String> keys = p.keys().iterator();
		while (keys.hasNext()){
			String name = (String)keys.next();
			String value = p.GetValue(name,"",false,true);
			if (value != null && value.length() > 0)
				SetValue(name, value);
		}
	}
	
	/**
	 * 从另一实例中复制内容
	 * @param other 另一实例
	 */
	public void copyFrom(DefaultProperties other){
		Iterator<String> keys = other.keys().iterator();
		Clear();
		while (keys.hasNext()){
			String __name = (String)keys.next();
			String __value = other._GetValue(__name);
			SetValue(__name, __value);
		}
	}

	public void toXML(Element root) {
		//为了输出文件的美观，添加一个\n文件节点
		Document doc = root.getOwnerDocument();
		root.appendChild(doc.createTextNode("\n"));
		Iterator<String> ids = keys().iterator();
		
		while (ids.hasNext()){
			String id = (String)ids.next();
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
