package com.logicbus.remote.client;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.XmlTools;


/**
 * 基于XML的Buffer
 * @author duanyy
 *
 */
public class XMLBuffer extends Buffer {
	
	protected Document doc = null;
	protected Element root = null;

	public Document getDocument(){return doc;}
	public Element getRoot(){return root;}
	
	public XMLBuffer(){
		try {
			doc = XmlTools.newDocument("root");
			root = doc.getDocumentElement();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}		
	}
	
	public XMLBuffer(Document _doc){
		doc = _doc;
		root = doc.getDocumentElement();
	}	
	
	
	public void prepareBuffer(boolean flag){
		if (flag){
			//从对象写出到StringBuffer
			StringBuffer buf = getBuffer();
			buf.setLength(0);
			try {
				buf.append(XmlTools.node2String(doc));
			}catch (Exception ex){
				
			}
		}else{
			//从StringBuffer中解析对象
			StringBuffer buf = getBuffer();
			if (buf.length() > 0){
				try {
					doc = XmlTools.loadFromContent(buf.toString());
				} catch (Exception ex) {
					
				}
			}
			if (doc == null){
				try {
					doc = XmlTools.newDocument("root");
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}		
			root = doc.getDocumentElement();
		}
	}
}
