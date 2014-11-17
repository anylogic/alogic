package com.logicbus.backend.message;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.IOTools;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;

/**
 * XML 消息
 * @author duanyy
 *
 * @version 1.0.4 [20140410 duanyy] <br>
 * - 提升encoding为父类成员 <br>
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - Message被改造为接口 <br>
 */
public class XMLMessage implements Message {
	protected Document xmlDoc = null;
	protected Element root = null;

	public Document getDocument(){return xmlDoc;}
	public Element getRoot(){return root;}
		
	public void write(OutputStream out, Context doc) {
		root.setAttribute("code",doc.getReturnCode());
		root.setAttribute("reason", doc.getReason());
		root.setAttribute("duration", String.valueOf(doc.getDuration()));
		root.setAttribute("host", doc.getHost());
		root.setAttribute("serial", doc.getGlobalSerial());
		
		try {
			XmlTools.saveToOutputStream(xmlDoc, out);
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			IOTools.closeStream(out);
		}
	}
	
	public void read(InputStream in, Context doc) {
		try {
			String data = Context.readFromInputStream(in, doc.getEncoding());
			if (data != null && data.length() > 0){
				xmlDoc = XmlTools.loadFromContent(data);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			IOTools.close(in);
		}
		
		if (xmlDoc == null){
			try {
				xmlDoc = XmlTools.newDocument("root");
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}		
		root = xmlDoc.getDocumentElement();
	}
	
	public boolean doRead(Context doc) {
		//当客户端通过form来post的时候，Message不去读取输入流。
		String contentType = doc.getReqestContentType();
		return !(contentType!=null&&contentType.startsWith(formContentType));
	}
	
	public boolean doWrite(Context doc) {
		return true;
	}
	
	public String getContentType(Context doc) {
		return "text/xml;charset=" + doc.getEncoding();
	}
	
	public String toString(){
		try {
			return XmlTools.node2String(xmlDoc);
		} catch (TransformerException e) {
			return xmlDoc.toString();
		}
	}
	
	protected static String formContentType = "application/x-www-form-urlencoded";
	static {
		formContentType = Settings.get().GetValue("http.alloworigin",
				"application/x-www-form-urlencoded");
	}	
}
