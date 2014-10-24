package com.anysoft.util.textdotter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * TextDotter工厂类
 * 
 * 负责创建TextDotter实例，并管理系统内置的TextDotter实例。本类具有下列特性：<br>
 * - 通过XML文件来配置数据<br>
 * - 可通过父节点形成工厂链<br>
 * 
 * ### 配置目录<br>
 * 
 * 缺省的文件是本软件包的内置文件，路径为：<br>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * /com/anysoft/util/text/dotter/resource/TextDotter.xml
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * 
 * 通过构造函数{@link #TextDotterFactory()}和{@link #TextDotterFactory(TextDotterFactory)}将
 * 构造采用缺省路径的TextDotterFactory。<br>
 * 
 * 客户也可以指定自己的配置文件，采用构造函数{@link #TextDotterFactory(String)}
 * 和{@link #TextDotterFactory(String, TextDotterFactory)}。<br>
 * 
 * ### 配置文件<br>
 * 
 * 一个TextDotter配置文件采用XML文件，下面是一个样例：<br>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~{.xml}
 * <?xml version = "1.0"?> 
 * <dotter Id="shell"> 
 * <pattern Class="code-comment" Expr="#(.*)$" Flags="MULTILINE"/> 
 * <pattern Class="code-string-doublequoted" Expr="&quot;(?:\.|(\\\&quot;)|[^\&quot;&quot;\n])*&quot;"/> 
 * <pattern Class="code-string-singlequoted" Expr="&apos;(?:\.|(\\\&apos;)|[^\&apos;&apos;\n])*&apos;"/> 
 * <pattern Class="code-number" Expr="\b([\d]+(\.[\d]+)?|0x[a-f0-9]+)\b"/> 
 * <pattern Class="code-delimeter" Expr="\p{Punct}"/>
 * </dotter> 
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * 
 * ### 工厂链<br>
 * 
 * 有的时候，我们既希望使用软件包内置的TextDotter，又希望自己定制一些TextDotter，采用工厂链是一个好方法。<br>
 * TextDotterFactory允许客户端为其指定父节点，当其无法创建指定Id的TextDotter，将会委托给其父节点创建，采用这个机制，将最大限度的重用TextDotter配置。<br>
 * 
 * ### 内置的TextDotter<br>
 * 
 * 目前内置的TextDotter包括：<br>
 * - cplusplus C++的语法TextDotter;<br>
 * - java Java的语法TextDotter;<br>
 * - sql SQL语句的语法TextDotter;<br>
 * - javascript JavaScript的语法TextDotter;<br>
 * - csharp C#的语法TextDotter;<br>
 * - delphi Delphi的语法TextDotter;<br>
 * - shell Shell的语法TextDotter;<br>
 * 
 * @author szduanyy
 *
 */
public class TextDotterFactory {
	/**
	 * 创建新的TextDotter实例
	 * @param _id 实例的Id
	 * @return TextDotter实例
	 */
	public TextDotter newInstance(String _id){
		String id = _id.toLowerCase();	
		TextDotter found = dotters.get(id);
		if (found != null){
			return found;
		}
		return newIntanceFromParent(id);
	}
	
	/**
	 * 从父工厂创建实例
	 * @param id
	 * @return
	 */
	private TextDotter newIntanceFromParent(String id){
		if (parent != null){
			return parent.newInstance(id);
		}
		return new DefaultTextDotter(id);
	}
	
	/**
	 * 上级工厂
	 */
	protected TextDotterFactory parent;
	/**
	 * 缓冲起来的列表
	 */
	protected Hashtable<String,TextDotter> dotters = new Hashtable<String,TextDotter>();
	/**
	 * 构造函数
	 */
	public TextDotterFactory(){
		this("",(TextDotterFactory)null);
	}
	/**
	 * 构造函数
	 */	
	public TextDotterFactory(TextDotterFactory _parent){
		this("",_parent);
	}	
	/**
	 * 构造函数
	 */	
	public TextDotterFactory(String _uri){
		this(_uri,(TextDotterFactory)null);
	}
	/**
	 * 构造函数
	 */
	public TextDotterFactory(String _uri,TextDotterFactory _parent){
		parent = _parent;
		String uri = _uri;
		if (uri.length() <= 0){
			URL url = getClass().getResource("/com/anysoft/util/textdotter/resource/TextDotter.xml");
			try {
				uri = url.toURI().toString();
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			Document doc = XmlTools.loadFromURI(uri);
			Element root = doc.getDocumentElement();

			NodeList nodeList = root.getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				Element e = (Element) node;
				if (!e.getNodeName().equals("dotter")) {
					continue;
				}
				String id = e.getAttribute("Id");
				if (id.length() <= 0) {
					continue;
				}
				TextDotter dotter = loadInstance(id, e);

				if (dotter != null) {
					dotters.put(dotter.getId(), dotter);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private TextDotter loadInstance(String id,Element e) {
		DefaultTextDotter instance = new DefaultTextDotter(id);
		NodeList nodeList = e.getChildNodes();
		for (int i = 0 ; i < nodeList.getLength() ; i ++){
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			if (!node.getNodeName().equals("pattern")){
				continue;
			}

			XmlElementProperties childXrc = new XmlElementProperties((Element)node,null);
			String className = PropertiesConstants.getAttribute(childXrc, "Class","");
			String Flags = PropertiesConstants.getAttribute(childXrc, "Flags","");
			String Expr = PropertiesConstants.getAttribute(childXrc, "Expr","");
			String type = PropertiesConstants.getAttribute(childXrc, "Type", "Expr");
			if (className.length() <= 0 || Expr.length() <= 0){
				continue;
			}
			if (type.equals("Keywords")){
				instance.addItem(DefaultTextDotter.transKeywords(Expr), Flags, className);
			}else{
				instance.addItem(Expr, Flags, className);
			}				
		}
		return instance;
	}

	/**
	 * 缺省的实现
	 */
	protected static TextDotterFactory defaultInstance;
	
	/**
	 * 获取缺省的TextDotterFactory实例
	 * @return　TextDotterFactory实例
	 */
	public static TextDotterFactory getDefault(){
		return defaultInstance;
	}
	
	static {
		defaultInstance = new TextDotterFactory();
	}
}
