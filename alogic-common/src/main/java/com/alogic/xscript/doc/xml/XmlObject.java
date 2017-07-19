package com.alogic.xscript.doc.xml;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alogic.xscript.doc.XsArray;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.XsPrimitiveArray;
import com.anysoft.util.XmlTools;

/**
 * 基于xml报文的对象
 * 
 * @author yyduan
 * @since 1.6.8.14
 * 
 * @version 1.6.9.3 [20170615 duanyy] <br>
 * - 增加判断文档是否为空的方法 <br>
 * 
 * @version 1.6.8.6 [20170719 duanyy] <br>
 * - 避免属性值为空的情况 <br>
 */
public class XmlObject implements XsObject {
	
	/**
	 * Xml element
	 */
	protected Element content = null;
	protected String xmlTag;
	
	public XmlObject(String tag,Element content){
		this.xmlTag = tag;
		this.content = content;
	}
	
	@Override
	public boolean isNull() {
		return content == null || !content.hasChildNodes();
	}
	
	@Override
	public Object getContent() {
		return content;
	}

	@Override
	public String getTag() {
		return xmlTag;
	}

	@Override
	public void addProperty(String name, String value) {
		if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)){
			Document doc = this.content.getOwnerDocument();
			Element ele = doc.createElement(name);
			ele.appendChild(doc.createTextNode(value));
			this.content.appendChild(ele);
		}
	}

	@Override
	public void addProperty(String name, Number value) {
		if (StringUtils.isNotEmpty(name) && value != null){
			Document doc = this.content.getOwnerDocument();
			Element ele = doc.createElement(name);
			ele.appendChild(doc.createTextNode(String.valueOf(value)));
			this.content.appendChild(ele);
		}
	}

	@Override
	public void addProperty(String name, boolean value) {
		Document doc = this.content.getOwnerDocument();
		Element ele = doc.createElement(name);
		ele.appendChild(doc.createTextNode(BooleanUtils.toStringTrueFalse(value)));
		this.content.appendChild(ele);
	}

	@Override
	public boolean remove(String name) {
		Element property = XmlTools.getFirstElementByTagName(this.content, name);
		if (property != null){
			return this.content.removeChild(property) != null;
		}
		return false;
	}
	
	@Override
	public boolean hasProperty(String name) {
		Element property = XmlTools.getFirstElementByTagName(this.content, name);
		return property != null;
	}

	@Override
	public String getProperty(String name, String dft) {
		Element property = XmlTools.getFirstElementByTagName(this.content, name);
		if (property == null){
			return dft;
		}
		
		Node node = property.getFirstChild();
		if (node == null){
			return dft;
		}
		
		if (node.getNodeType() != Node.TEXT_NODE){
			return dft;
		}
		
		String found = node.getNodeValue();
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		return found;
	}

	@Override
	public long getProperty(String name, long dft) {
		String found = getProperty(name,"");
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		try {
			return Long.parseLong(found);
		}catch (NumberFormatException ex){
			return dft;
		}
	}

	@Override
	public int getProperty(String name, int dft) {
		String found = getProperty(name,"");
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		try {
			return Integer.parseInt(found);
		}catch (NumberFormatException ex){
			return dft;
		}
	}

	@Override
	public boolean getProperty(String name, boolean dft) {
		String found = getProperty(name,"");
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		return BooleanUtils.toBoolean(found);
	}

	@Override
	public float getProperty(String name, float dft) {
		String found = getProperty(name,"");
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		try {
			return Float.parseFloat(found);
		}catch (NumberFormatException ex){
			return dft;
		}
	}

	@Override
	public double getProperty(String name, double dft) {
		String found = getProperty(name,"");
		
		if (StringUtils.isEmpty(found)){
			return dft;
		}
		
		try {
			return Double.parseDouble(found);
		}catch (NumberFormatException ex){
			return dft;
		}
	}

	@Override
	public XsArray getArrayChild(String name, boolean create) {
		return new XmlArray(name,this.content);
	}

	@Override
	public XsPrimitiveArray getPrimitiveArrayChild(String name, boolean create) {
		return new XmlPrimitiveArray(name,this.content);
	}

	@Override
	public XsObject getObjectChild(String name, boolean create) {
		Element found = XmlTools.getFirstElementByTagName(this.content, name);
		if (found == null){
			if (create){
				found = this.content.getOwnerDocument().createElement(name);
				this.content.appendChild(found);
			}else{
				return null;
			}
		}
		
		return new XmlObject(name,found);
	}

	public static void main(String[] args){
		try {
			Document xmlDoc = XmlTools.newDocument();
			xmlDoc.appendChild(xmlDoc.createElement("root"));
			XsObject doc = new XmlObject("root",xmlDoc.getDocumentElement());
			
			doc.addProperty("id", "alogic");
			doc.addProperty("name", "eason");
			
			XsObject child = doc.getObjectChild("child", true);
			child.addProperty("id", "child");
			child.addProperty("name", "ddd");
			
			XsArray array = doc.getArrayChild("array", true);
			
			XsObject item = array.newObject();
			item.addProperty("id", "ddd");
			array.add(item);
			
			item = array.newObject();
			item.addProperty("id", "dddd");
			array.add(item);
			
			XsPrimitiveArray array2 = doc.getPrimitiveArrayChild("array2", true);
			array2.add("dddd");
			array2.add(1000);
			
			System.out.println(XmlTools.node2String((Node)doc.getContent()));
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
