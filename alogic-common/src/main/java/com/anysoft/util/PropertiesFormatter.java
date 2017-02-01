package com.anysoft.util;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 属性集格式化器
 * 
 * @author duanyy
 * @since 1.6.3.41
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public interface PropertiesFormatter extends Configurable,XMLConfigurable{
	/**
	 * 获取规格id
	 * @return id
	 */
	public String id();
	
	/**
	 * 按照规格输出数据到output
	 * @param output 输出
	 * @param data 数据提供者
	 */
	public void format(Map<String,Object> output,DataProvider data);
	
	/**
	 * 按照规格输出数据到output
	 * @param output 输出
	 * @param data 数据提供者
	 */		
	public void format(Element output,DataProvider data);	
		
	/**
	 * Abstract
	 * @author duanyy
	 * @since 1.6.3.41
	 */
	abstract public class Abstract implements PropertiesFormatter{
		protected static final Logger logger = LoggerFactory.getLogger(PropertiesFormatter.class);
		protected String id;
		protected String name;
		protected String note;
		protected String type = "String";
		
		public Abstract(){
			
		}
		
		public Abstract(String _id,String _name,String _note,String _type){
			id = _id;
			name = _name;
			note = _note;
			type = _type;
		}
		
		public String id() {
			return id;
		}
		
		public void configure(Properties p) throws BaseException {
			id = PropertiesConstants.getString(p,"id","",true);
			name = PropertiesConstants.getString(p,"name","",true);
			note = PropertiesConstants.getString(p,"note","",true);
			type = PropertiesConstants.getString(p,"type",type,true);
		}

		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);
		}
		
		public void report(Element xml) {
			if (xml != null){
				if (isNotNull(id)){
					xml.setAttribute("id", id);
				}
				if (isNotNull(name)){
					xml.setAttribute("name", name);
				}
				if (isNotNull(note)){
					xml.setAttribute("note", note);
				}
				
				xml.setAttribute("type", type);
			}
		}

		public void report(Map<String, Object> json) {
			if (json != null){
				if (isNotNull(id)){
					json.put("id", id);
				}
				if (isNotNull(name)){
					json.put("name", name);
				}
				if (isNotNull(note)){
					json.put("note", note);
				}
				json.put("type", type);
			}
		}
		
		protected boolean isNotNull(String value){
			return value != null && value.length() > 0;
		}
	}
	
	/**
	 * 缺省的Spec
	 * @author duanyy
	 * @since 1.6.3.41
	 */
	public class Default extends Abstract{
		protected Object context = null;
		protected String dftValue = "";
		
		public Default(){
			
		}
		
		public Default(String _id, String _name, String _note, String _type) {
			super(_id,_name,_note,_type);
		}
		
		public Default(String _id, String _name, String _note) {
			super(_id,_name,_note,"String");
		}

		public void configure(Properties p) throws BaseException {
			super.configure(p);
			dftValue = PropertiesConstants.getString(p,"defaultValue",dftValue,true);
		}
		
		public void format(Map<String,Object> output, DataProvider data) {
			//输出自身信息到对象
			report(output);	
			
			//查找属性值，输出
			if (context == null){
				context = data.getContext(id());
			}
			
			String value = data.getValue(id(), context, dftValue);
			if (isNotNull(value)){
				format(output,value);
			}
		}

		public void format(Element output, DataProvider data) {
			report(output);
			
			//查找属性值，输出
			if (context == null){
				context = data.getContext(id());
			}
			
			String value = data.getValue(id(), context, dftValue);
			if (isNotNull(value)){
				format(output,value);
			}
		}
		
		protected void format(Element out,String value){
			out.setAttribute("value", value);
		}
		
		protected void format(Map<String,Object> out,String value){
			out.put("value", value);
		}
	}
	
	/**
	 * 路径类的属性
	 * 
	 * @author duanyy
	 * @since 1.6.3.41
	 */
	public class Path extends Default {
		protected String delimeter = File.pathSeparator;
		protected void format(Element out,String value){
			Document doc = out.getOwnerDocument();

			String[] _values = value.split(delimeter);
			
			for (String _value:_values){
				if (isNotNull(_value)){
					Element item = doc.createElement("item");
					item.setAttribute("value", _value);
					out.appendChild(item);
				}
			}
			
		}
		
		protected void format(Map<String,Object> out,String value){
			String[] _values = value.split(delimeter);
			
			List<Object> _items = new ArrayList<Object>();
			
			for (String _value:_values){
				if (isNotNull(_value)){
					_items.add(_value);
				}
			}
			
			out.put("item", _items);
		}
	}
	
	/**
	 * 属性组
	 * 
	 * @author duanyy
	 * @since 1.6.3.41
	 */
	public class Group extends Abstract{
		/**
		 * 子属性
		 */
		protected List<PropertiesFormatter> children = new ArrayList<PropertiesFormatter>();
		
		public Group(){
			
		}
		
		public Group(String _id,String _name,String _note){
			super(_id,_name,_note,"Group");
		}
		
		public void add(PropertiesFormatter child){
			children.add(child);
		}
		
		public void add(String _id,String _name,String _note,String _type){
			add(new Default(_id,_name,_note,_type));
		}
		
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			Properties p = new XmlElementProperties(_e,_properties);
			configure(p);
			
			NodeList _children = XmlTools.getNodeListByPath(_e, "property");
			TheFactory factory = new TheFactory();
			
			for (int i = 0 ;i < _children.getLength() ; i ++){
				Node n = _children.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				try {
					PropertiesFormatter formatter = factory.newInstance(e, p, "module", Default.class.getName());
					if (formatter != null){
						children.add(formatter);
					}
				}catch (Exception ex){
					logger.error("Can not create property spec,ignored",ex);
				}
			}
		}
		
		public void format(Map<String,Object> output, DataProvider data) {
			//输出自身信息到对象
			report(output);
			
			List<Object> _children = new ArrayList<Object>();
			
			for (PropertiesFormatter formatter:children){
				HashMap<String,Object> property = new HashMap<String,Object>();
				formatter.format(property, data);
				_children.add(property);
			}
			
			output.put("property", _children);
		}

		public void format(Element output, DataProvider data) {
			Document doc = output.getOwnerDocument();
			report(output);
			
			for (PropertiesFormatter formatter:children){
				Element property = doc.createElement("property");
				formatter.format(property, data);
				output.appendChild(property);
			}
		}
	}
	
	/**
	 * 工厂类
	 * 
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<PropertiesFormatter>{
		public String getClassName(String _module) throws BaseException{
			if (_module.equals("Group")){
				return Group.class.getName();
			}
			if (_module.equals("Default")){
				return Default.class.getName();
			}
			if (_module.equals("Path")){
				return Path.class.getName();
			}
			return _module;
		}
		
		private static PropertiesFormatter systemProperties = null;
		private static Object lock = new Object();
		
		public static PropertiesFormatter SystemProperties(){
			if (systemProperties == null){
				synchronized (lock){
					systemProperties = fromURL("java:///com/anysoft/util/jvmSystemProperties.xml");
				}
			}
			return systemProperties;
		}
		
		private static PropertiesFormatter env = null;
		public static PropertiesFormatter Env(){
			if (env == null){
				synchronized (lock){
					env = fromURL("java:///com/anysoft/util/jvmEnv.xml");
				}
			}
			return env;
		}
		
		public static PropertiesFormatter fromURL(String url){
			ResourceFactory rm = Settings.getResourceFactory();
			if (null == rm){
				rm = new ResourceFactory();
			}
			
			Document doc = null;
			InputStream in = null;
			try {
				in = rm.load(url,null, null);
				doc = XmlTools.loadFromInputStream(in);		
			} catch (Exception ex){
				Abstract.logger.error("Error occurs when load xml file,source=" + url, ex);
			}finally {
				IOTools.closeStream(in);
			}	
			
			TheFactory factory = new TheFactory();
			PropertiesFormatter instance = null;
			
			if (doc != null){
				 instance = factory.newInstance(doc.getDocumentElement(), Settings.get(), "module", Group.class.getName());
			}
			
			return instance;
		}
	}
}
