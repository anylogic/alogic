package com.anysoft.util;
import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * XML的工具
 * @author duanyy
 * @version 1.6.4.41 [20160401 duanyy] <br>
 * - 增加XML属性操作方法 <br>
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - node2string不再抛出异常，以便使用 <br>
 * 
 * @version 1.6.11.60 [20180912 duanyy] <br>
 * - loadFromContent增加encoding支持 <br>
 */
public class XmlTools {
	/**
	 * logger
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(XmlTools.class);
	
	private XmlTools(){
		
	}
	
	/**
	 * 获取指定属性的整型值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param dftValue 缺省值
	 * @return 整型值
	 * 
	 * @since 1.6.4.41
	 */
	public static int getInt(Element e,String attr,int dftValue){
		String value = e.getAttribute(attr);
		
		if (StringUtils.isEmpty(value)){
			return dftValue;
		}
		
		try {
			return Integer.parseInt(value);
		}catch(NumberFormatException ex){
			return dftValue;
		}
	}
	
	/**
	 * 设置指定属性的整型值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param value 属性值
	 * 
	 * @since 1.6.4.41
	 */
	public static void setInt(Element e,String attr,int value){
		if (StringUtils.isNotEmpty(attr)){
			e.setAttribute(attr, String.valueOf(value));
		}
	}
	
	/**
	 * 获取指定属性的long值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param dftValue 缺省值
	 * @return long型值
	 * 
	 * @since 1.6.4.41
	 */
	public static long getLong(Element e,String attr,long dftValue){
		String value = e.getAttribute(attr);
		
		if (StringUtils.isEmpty(value)){
			return dftValue;
		}
		
		try {
			return Long.parseLong(value);
		}catch(NumberFormatException ex){
			return dftValue;
		}		
	}
	
	/**
	 * 设置指定属性的long值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param value 属性值
	 * 
	 * @since 1.6.4.41
	 */
	public static void setLong(Element e,String attr,long value){
		if (StringUtils.isNotEmpty(attr)){
			e.setAttribute(attr, String.valueOf(value));
		}
	}
	
	/**
	 * 获取指定属性的String值
	 * @param e XML节点
	 * @param attr 
	 * @param dftValue
	 * @return String值
	 * 
	 * @since 1.6.4.41
	 */
	public static String getString(Element e,String attr,String dftValue){
		String value = e.getAttribute(attr);
		if (StringUtils.isEmpty(value)){
			return dftValue;
		}
		return value;
	}
	
	/**
	 * 设置指定属性的String值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param value String值
	 * 
	 * @since 1.6.4.41
	 */
	public static void setString(Element e,String attr,String value){
		if (StringUtils.isNotEmpty(attr) && StringUtils.isNotEmpty(value)){
			e.setAttribute(attr, value);
		}
	}
	
	/**
	 * 获取指定属性的float值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param dftValue 缺省值
	 * @return float值
	 * 
	 * @since 1.6.4.41
	 */
	public static float getFloat(Element e,String attr,float dftValue){
		String value = e.getAttribute(attr);
		
		if (StringUtils.isEmpty(value)){
			return dftValue;
		}
		
		try {
			return Float.parseFloat(value);
		}catch(NumberFormatException ex){
			return dftValue;
		}		
	}
	
	/**
	 * 设置指定属性的float值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param value 缺省值
	 * 
	 * @since 1.6.4.41
	 */
	public static void setFloat(Element e,String attr,float value){
		if (StringUtils.isNotEmpty(attr)){
			e.setAttribute(attr, String.valueOf(value));
		}
	}
	
	/**
	 * 获取指定属性的double值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param dftValue 缺省值
	 * @return double值
	 * 
	 * @since 1.6.4.41
	 */
	public static double getDouble(Element e,String attr,double dftValue){
		String value = e.getAttribute(attr);
		
		if (StringUtils.isEmpty(value)){
			return dftValue;
		}
		
		try {
			return Double.parseDouble(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}
	
	/**
	 * 设置指定属性的double值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param value 属性值
	 * 
	 * @since 1.6.4.41
	 */
	public static void setDouble(Element e,String attr,double value){
		if (StringUtils.isNotEmpty(attr)){
			e.setAttribute(attr, String.valueOf(value));
		}
	}
	
	/**
	 * 获取指定属性的boolean值 
	 * @param e XML节点
	 * @param attr 属性名
	 * @param dftValue 属性值
	 * @return boolean值
	 * 
	 * @since 1.6.4.41
	 */
	public static boolean getBoolean(Element e,String attr,boolean dftValue){
		String value = e.getAttribute(attr);
		
		if (StringUtils.isEmpty(value)){
			return dftValue;
		}
		
		return BooleanUtils.toBoolean(value);
	}
	
	/**
	 * 设置指定属性的boolean值
	 * @param e XML节点
	 * @param attr 属性名
	 * @param value 属性值
	 * 
	 * @since 1.6.4.41
	 */
	public static void setBoolean(Element e,String attr,boolean value){
		if (StringUtils.isNotEmpty(attr)){
			e.setAttribute(attr, BooleanUtils.toString(value, "true", "false"));
		}
	}
	
	/**
	 * 创建一个新XML文档，并创建根目录
	 * @return XML文档实例
	 * @throws ParserConfigurationException
	 */
	public static Document newDocument(String _root) throws ParserConfigurationException{
	      DocumentBuilderFactory __factory = DocumentBuilderFactory.newInstance();
	      __factory.setNamespaceAware(true);
	      DocumentBuilder __db = __factory.newDocumentBuilder();
	      Document __doc = __db.newDocument();
	      Element __root = __doc.createElement(_root);
	      __doc.appendChild(__root);
	      return __doc;
	}
	/**
	 * 创建一个新XML文档
	 * @return XML文档实例
	 * @throws ParserConfigurationException
	 */
	public static Document newDocument() throws ParserConfigurationException{
	      DocumentBuilderFactory __factory = DocumentBuilderFactory.newInstance();
	      __factory.setNamespaceAware(true);
	      DocumentBuilder __db = __factory.newDocumentBuilder();
	      Document __doc = __db.newDocument();
	      return __doc;
	}	
	/**
	 * 从输入流装入XML文档
	 * @param _in 输入流
	 * @return XML文档实例
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document loadFromInputStream(InputStream _in) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory __factory = DocumentBuilderFactory.newInstance();
		__factory.setNamespaceAware(true);
		DocumentBuilder __db = __factory.newDocumentBuilder();
		return __db.parse(_in);
	}
	
	/**
	 * 从文件装入XML文档
	 * @param _file 文件实例
	 * @return XML文档实例
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadFromFile(File _file) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory __factory = DocumentBuilderFactory.newInstance();
		__factory.setNamespaceAware(true);
		DocumentBuilder __db = __factory.newDocumentBuilder();
		return __db.parse(_file);		
	}
	
	/**
	 * 从URI装入XML文档
	 * @param _uri 文档的URI
	 * @return XML文档实例
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadFromURI(String _uri) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory __factory = DocumentBuilderFactory.newInstance();
		__factory.setNamespaceAware(true);
		DocumentBuilder __db = __factory.newDocumentBuilder();
		return __db.parse(_uri);			
	}
	
	/**
	 * 从内容装入XML文档
	 * @param _content
	 * @return XML文档实例
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadFromContent(String _content) throws ParserConfigurationException, SAXException, IOException{
		return loadFromInputStream(new ByteArrayInputStream(_content.getBytes(encoding)));
	}
	
	/**
	 * 从内容装入XML文档
	 * @param _content
	 * @return XML文档实例
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadFromContent(String _content,String encoding) throws ParserConfigurationException, SAXException, IOException{
		return loadFromInputStream(new ByteArrayInputStream(_content.getBytes(encoding)));
	}	
	
	/**
	 * 写出到输出流
	 * @param _doc XML文档
	 * @param _out 输出流
	 * @throws TransformerException
	 * @throws IOException 
	 */
	public static void saveToOutputStream(Document _doc,OutputStream _out) throws TransformerException, IOException{
		saveToOutputStream(_doc,_out,true);
	}
	/**
	 * 写出到输出流
	 * @param _node XML文档
	 * @param _out 输出流
	 * @param out_head 是否输出XML头
	 * @throws TransformerException
	 * @throws IOException 
	 */	
	public static void saveToOutputStream(Node _node,OutputStream _out,boolean out_head) throws TransformerException{
		TransformerFactory __factory = TransformerFactory.newInstance();
		Transformer __transformer = __factory.newTransformer();
		if (!out_head){
			__transformer.setOutputProperty("omit-xml-declaration","yes");
		}
		else{
			__transformer.setOutputProperty("omit-xml-declaration","no");
		}
		__transformer.setOutputProperty("encoding",encoding);
		Source __source = new DOMSource(_node);
		Result __result = new StreamResult(_out);
		__transformer.transform(__source,__result);		
	}
	/**
	 * 将Node转化为String类型
	 * @param _node XML Node
	 * @return String
	 * @throws TransformerException
	 */
	public static String node2String(Node _node){
		try {
			TransformerFactory __factory = TransformerFactory.newInstance();
			Transformer __transformer = __factory.newTransformer();
			__transformer.setOutputProperty("omit-xml-declaration","yes");
			__transformer.setOutputProperty("encoding",encoding);
			Source __source = new DOMSource(_node);
			StringWriter writer = new StringWriter(1024);
			Result __result = new StreamResult(writer);
			__transformer.transform(__source,__result);		
			return writer.toString();
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return "error";
		}
	}
	
	public static String node2String(Node _node,String coding){
		try {
			TransformerFactory __factory = TransformerFactory.newInstance();
			Transformer __transformer = __factory.newTransformer();
			__transformer.setOutputProperty("omit-xml-declaration","yes");
			__transformer.setOutputProperty("encoding",coding);
			Source __source = new DOMSource(_node);
			StringWriter writer = new StringWriter(1024);
			Result __result = new StreamResult(writer);
			__transformer.transform(__source,__result);		
			return writer.toString();
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
			return "error";
		}
	}	
	
	/**
	 * 通过XSL格式化XML
	 * @param data XML文档
	 * @param xsl XSL模板
	 * @param result 转换的输出
	 * @param outHead 是否输出头
	 * @throws TransformerException
	 */
	public static void xslt(Document data,Document xsl,Result result,boolean outHead) throws TransformerException{
		TransformerFactory __factory = TransformerFactory.newInstance();
		Templates __templates = __factory.newTemplates(new DOMSource(xsl));
		Transformer __transformer = __templates.newTransformer();
		if (!outHead){
			__transformer.setOutputProperty("omit-xml-declaration","yes");
		}
		else{
			__transformer.setOutputProperty("omit-xml-declaration","no");
		}
		__transformer.setOutputProperty("encoding",encoding);
		__transformer.transform(new DOMSource(data),result);		
	}
	/**
	 * 通过XSL格式化XML，并输出到OutputStream
	 * @param data XML文档
	 * @param xsl XSL模板
	 * @param out OutputStream
	 * @param outHead 是否输出头
	 * @throws TransformerException
	 */
	public static void xslt(Document data,Document xsl,OutputStream out,boolean outHead) throws TransformerException{
		xslt(data,xsl,new StreamResult(out),outHead);
	}
	/**
	 * 通过XSL格式化XML，并输出到OutputStream
	 * @param data XML文档
	 * @param xsl XSL模板
	 * @param out OutputStream
	 * @throws TransformerException
	 */	
	public static void xslt(Document data,Document xsl,OutputStream out) throws TransformerException{
		xslt(data,xsl,new StreamResult(out),true);
	}	
	/**
	 * 通过XSL格式化XML，并输出到XML文档
	 * @param data XML文档
	 * @param xsl XSL模板
	 * @throws TransformerException
	 */		
	public static Document xslt(Document data,Document xsl) throws TransformerException, ParserConfigurationException{
		Document out = XmlTools.newDocument();
		xslt(data,xsl,new DOMResult(out),true);
		return out;
	}	
	
	/**
	 * 通过路径(XPath)查找节点
	 * @param root 根节点
	 * @param path XPath
	 * @return 查找结果
	 */
	public static Node getNodeByPath(Element root,String path){
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {
			XPathExpression expr = xpath.compile(path);
			return (Node) expr.evaluate(root, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		return null;
	}
	/**
	 * 通过路径(XPath)查找节点列表
	 * @param root 根节点
	 * @param path XPath
	 * @return 符合条件的节点列表
	 */
	public static NodeList getNodeListByPath(Element root,String path){
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {
			XPathExpression expr = xpath.compile(path);
			return (NodeList) expr.evaluate(root, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		return null;		
	}
	/**
	 * 将另外一个Element的所有子节点clone到Element
	 * @param root Element
	 * @param src 来源Element
	 */
	public static void Clone(Element root,Element src){
		Document doc = root.getOwnerDocument();
		NodeList nodes = src.getChildNodes();
		for (int i = 0 ; i < nodes.getLength() ; i ++){
			Node node = nodes.item(i);
			int nodeType = node.getNodeType();
			switch (nodeType){
				case Node.TEXT_NODE:
					root.appendChild(doc.createTextNode(node.getNodeValue()));
					break;
				case Node.ELEMENT_NODE:
					Element _element  = doc.createElement(node.getNodeName());
					
					//clone attribute
					{
						NamedNodeMap attrs = node.getAttributes();
						for (int j = 0 ; j < attrs.getLength() ; j ++){
							Node attr = attrs.item(j);
							_element.setAttribute(attr.getNodeName(), attr.getNodeValue());
						}
					}
					//clone children
					Clone(_element,(Element) node);
					root.appendChild(_element);
					break;
			}
		}
	}
	/**
	 * 通过路径(非XPath)查找节点
	 * @param root 根节点
	 * @param path Path
	 * @return 符合要求的节点
	 */
	public static Element getFirstElementByPath(Element root, String path) {
		String delim = "/";
		if (path.startsWith("/")){
			path = path.substring(1);
		}
		int index = path.indexOf(delim);
		String cur;
		String left = new String();
		if (index < 0) {
			cur = path;
		} else {
			cur = path.substring(0, index);
			left = path.substring(index + 1);
		}
		boolean ret = false;
		if (left.length() <= 0) {
			ret = true;
		}
		NodeList list = root.getChildNodes();
		Node find = null;
		for (int i = 0; i < list.getLength(); i++) {
			find = list.item(i);
			if (find.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (!((Element) find).getTagName().equals(cur))
				continue;
			if (ret)
				return (Element) find;
			return getFirstElementByPath((Element) find, left);
		}
		return null;
	}

	/**
	 * 通过TagName查找子节点
	 * @param parent 父节点
	 * @param tagName tagName
	 * @return 子节点
	 */
	public static Element getFirstElementByTagName(Element parent,String tagName) {
		NodeList nodeList = parent.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (node.getNodeName().equals(tagName))
				return (Element) node;
		}
		return null;
	}
	/**
	 * XML的encoding,缺省为utf-8
	 */
	private static String encoding = "utf-8";
	/**
	 * 设置缺省的encoding
	 * @param _encoding encoding
	 */
	public static void setDefaultEncoding(String _encoding){encoding = _encoding;}
}
