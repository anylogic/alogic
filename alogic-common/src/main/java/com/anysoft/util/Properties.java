package com.anysoft.util;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.formula.DataProvider;

/**
 * 变量集
 * @author duanyy
 * 
 * @version 1.2.3 [20140725 duanyy] <br>
 * - 增加loadFromString <br>
 * 
 * @version 1.6.1.1 [20141118 duanyy] <br>
 * - 实现DataProvider接口 <br>
 * 
 * @version 1.6.3.30 [20150714 duanyy] <br>
 * - 修正缺省值为null的空指针问题 <br>
 * 
 * @version 1.6.4.43 [20160411 duanyy] <br>
 * - DataProvider增加获取原始值接口 <br>
 * 
 * @version 1.6.7.7 [20170126 duanyy] <br>
 * - 增加loadFrom系列方法，用于从Json对象，Element节点，Element属性列表中装入变量列表 <br>
 * 
 * @version 1.6.8.10 [20170418 duanyy] <br>
 * - 在装入xml配置文件时，可从env中获取变量 <br>
 * 
 */
abstract public class Properties implements DataProvider{
	/**
	 * logger of log4j
	 */
	protected static Logger logger = LoggerFactory.getLogger(Properties.class);	
	/**
	 * 变量域
	 */
	protected String domain = "Default";
	/**
	 * 父节点
	 */
	protected Properties parent = null;
	/**
	 * 变量开始字符
	 */
	public static final String VariableStart = "${";
	/**
	 * 变量结束字符
	 */
	public static final String VariableEnd = "}";
	
	/**
	 * 构造函数
	 * @param _domain 变量域
	 * @param _parent 父节点
	 */
	public Properties(String _domain,Properties _parent){
		domain = _domain;
		parent = _parent;
	}
	
	/**
	 * 构造函数
	 * @param _domain 变量域
	 * @see #Properties(String, Properties)
	 */
	public Properties(String _domain){
		this(_domain,null);
		
	}
	
	/**
	 * 构造函数
	 */	
	public Properties(){
		this("Default",null);
	}
	
	/**
	 * 获取变量域
	 * @return domain
	 */
	public String getDomain(){
		return domain;
	}
	
	/**
	 * 设置变量域
	 * @param _domain 
	 */
	public void setDomain(String _domain){
		domain = _domain;
	}
	
	/**
	 * 设置变量集的父节点
	 * @param _parent 父节点
	 */
	public Properties PutParent(Properties _parent){
		Properties temp = parent;
		parent = _parent;
		return temp;
	}
	
	/**
	 * 利用变量集变量来计算给定字符串
	 * @param pattern 字符串
	 * @return 计算后的字符串
	 */
	public String transform(String pattern){
		return FillValue("",pattern);
	}
	
	/**
	 * 从字符串装入变量
	 *  
	 * <p>从一个字符串装入变量，字符串的语法如下：</p>
	 * <code>var1=val1;var2=val2</code>
	 * <p>从上面的字符串中可以解析出2个变量:var1和var2，其取值分别是val1和val2</p>
	 */
	public void loadFromString(String _para){
		loadFromString(_para,";","=");
	}
	
	/**
	 * 从字符串装入变量
	 * @param _para 字符串
	 * @param delimeter1 间隔符
	 * @param delimeter2 间隔符
	 * 
	 * @since 1.2.3
	 */
	public void loadFromString(String _para,String delimeter1,String delimeter2){
		StringTokenizer __t = new StringTokenizer(_para,delimeter1);
	     while (__t.hasMoreTokens()) {
	         String __pair = __t.nextToken();
	         int __index = __pair.indexOf(delimeter2);
	         if (__index < 0){
	        	 continue;
	         }
	         String __name = __pair.substring(0,__index);
	         String __value = __pair.substring(__index + 1,__pair.length());
	         SetValue(__name,__value);
	     }
	}
	
	/**
	 * 从JSON对象的属性列表中装入
	 * @param json Json对象
	 * 
	 * @since  1.6.7.7
	 */
	public void loadFrom(Map<String,Object> json){
		Iterator<Entry<String,Object>> iter = json.entrySet().iterator();
		
		while (iter.hasNext()){
			Entry<String,Object> entry = iter.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			
			if (value instanceof String || value instanceof Number){
				SetValue(key,value.toString());
			}
		}
	}
	
	/**
	 * 从Element节点的属性列表中装入
	 * @param elem Element节点
	 * 
	 * @since  1.6.7.7
	 */
	public void loadFromElementAttrs(Element elem){
		if (elem != null){
			NamedNodeMap attrs = elem.getAttributes();
			for (int i = 0 ;i < attrs.getLength() ; i ++){
				Node n = attrs.item(i);
				if (Node.ATTRIBUTE_NODE != n.getNodeType()){
					continue;
				}
				Attr attr = (Attr)n;
				
				String key = attr.getName();
				String value = attr.getValue();

				if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)){
					SetValue(key,value);
				}
			}
		}
	}
	
	/**
	 * 从Element中装入
	 * @param root Element节点
	 * 
	 * @since  1.6.7.7
	 */
	public void loadFrom(Element root){
		NodeList nodeList = root.getChildNodes();	
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			if ("parameter".equals(node.getNodeName())){
				Element e = (Element)node;
				String id = XmlTools.getString(e,"id","");
				String value = XmlTools.getString(e,"value","");
				if (StringUtils.isEmpty(id) || StringUtils.isEmpty(value)){
					continue;
				}
				
				boolean fromEnv = XmlTools.getBoolean(e, "fromEnv", false);
				if (fromEnv){
					value = System.getenv(value);
					if (StringUtils.isEmpty(value)){
						value = XmlTools.getString(e, "dft", "");
					}
				}else{
					boolean fromProperties = XmlTools.getBoolean(e, "fromProperties", false);
					if (fromProperties){
						value = System.getProperty(value);
						if (StringUtils.isEmpty(value)){
							value = XmlTools.getString(e, "dft", "");
						}
					}
				}
				
				//支持final标示,如果final为true,则不覆盖原有的取值
				boolean isFinal = XmlTools.getBoolean(e, "final", false);
				if (isFinal){
					String oldValue = GetValue(id, "", false,false);
					if (StringUtils.isEmpty(oldValue)){
						SetValue(id,value);
						boolean system = XmlTools.getBoolean(e, "system", false);
						if (system){
							System.setProperty(id, value);
							logger.info(String.format("Set system property:%s=%s", id,value));
						}
					}
				}else{
					SetValue(id,value);
					boolean system = XmlTools.getBoolean(e, "system", false);
					if (system){
						System.setProperty(id, value);
						logger.info(String.format("Set system property:%s=%s", id,value));
					}					
				}
			}
		}	
	}	
	
	/**
	 * 设置变量
	 * @param _name 变量名
	 * @param _value 变量值
	 */
	public void SetValue(String _name,String _value){
		_SetValue(_name,_value);
	}
	
	/**
	 * 获取变量值
	 * @param _name 变量名
	 * @param _defaultValue 缺省值
	 * @param _bVariable 是否计算变量
	 * @param _bNoParent 是否取父节点的变量
	 * @return 变量值
	 */
	public String GetValue(String _name,String _defaultValue,boolean _bVariable,boolean _bNoParent){
		String __value;
		__value = _GetValue(_name);
		
		if (__value == null || __value.length() <= 0){
			//在本变量集没有找到
			if (parent != null && !_bNoParent){
				//定义了父节点
				__value = parent.GetValue(_name,_defaultValue,false,false);
				if (__value == _defaultValue){
					//没有找到，尝试采用parent.模式
					int __index = _name.indexOf("parent.");
					if (__index == 0){
						__value = parent.GetValue(
								_name.substring(7),
								_defaultValue,
								false,
								false
								);	
					}
				}
			}
			else{
				__value = _defaultValue;
			}
		}
		//在本变量集找到
		if (_bVariable && __value != null && __value.length() > 0){
			//计算变量
			__value = FillValue(_name,__value);
		}
		return __value;
	}
	
	/**
	 * 进行变量填充式计算
	 * @param _name 最初的变量名
	 * @param _value 计算的原始值
	 * @return　计算后的取值
	 */
	public String FillValue(String _name,String _value){
		int __startIndex = _value.indexOf(VariableStart);
		if (__startIndex < 0){
			//没有找到变量
			return _value;
		}
		
		//在变量之前的字符串
		String __tmpValue = _value.substring(0,__startIndex);
		int __endIndex = _value.indexOf(VariableEnd,__startIndex + VariableStart.length());
		if (__endIndex < 0){
			//没有发现变量结束符
			return __tmpValue;
		}
		//变量名
		String __varName = _value.substring(
				__startIndex + VariableStart.length(),
				__endIndex - VariableStart.length() + VariableStart.length()
				);
		
		//在变量之后的字符串
		String __afterValue = _value.substring(__endIndex + VariableEnd.length());
		
		//取变量的值
		String __varValue;
		if (!__varName.equals(_name)){ 
			__varValue = GetValue(__varName,"",false,false);
			if (__varValue.length() >= 0){
				__tmpValue += __varValue;
			}
			__tmpValue += __afterValue;
			return FillValue(_name,__tmpValue);
		}
		return __tmpValue;
	}
	/**
	 * 获取变量值
	 * @param _name 变量名
	 * @param _defaultValue 缺省值
	 * @param _bVariable 是否计算变量
	 * @return 变量值
	 */	
	public String GetValue(String _name,String _defaultValue,boolean _bVariable){
		return GetValue(_name,_defaultValue,_bVariable,false);
	}
	
	/**
	 * 获取变量值
	 * @param _name 变量名
	 * @param _defaultValue 缺省值
	 * @return 变量值
	 */		
	public String GetValue(String _name,String _defaultValue){
		return GetValue(_name,_defaultValue,true,false);
	}
	
	/**
	 * 设置变量
	 * @param _name 变量名
	 * @param _value 变量值
	 */
	abstract protected void _SetValue(String _name,String _value);
	
	/**
	 * 获取变量
	 * @param _name 变量名
	 * @return 变量值
	 */
	abstract protected String _GetValue(String _name);
	
	/**
	 * 清楚变量
	 */
	abstract public void Clear();
	
	public String getValue(String varName, Object context, String defaultValue) {
		return GetValue(varName, defaultValue);
	}

	public Object getContext(String varName) {
		return cookies;
	}	
	
	@Override
	public String getRawValue(String varName, Object context, String dftValue) {
		return GetValue(varName,dftValue,false,false);
	}	
	
	protected static Object cookies = new Object();
}