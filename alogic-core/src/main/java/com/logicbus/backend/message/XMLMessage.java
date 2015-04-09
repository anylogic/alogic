package com.logicbus.backend.message;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
 * 
 * @version 1.6.1.1 [20141118 duanyy] <br>
 * - 修正没有读入的情况下,root为空的bug <br>
 * - MessageDoc暴露InputStream和OutputStream <br>
 * 
 * @version 1.6.1.2 [20141118 duanyy] <br>
 * - 支持MessageDoc的Raw数据功能 <br>
 * 
 * @version 1.6.2.1 [20141223 duanyy] <br>
 * - 增加对Comet的支持 <br>
 * 
 * @version 1.6.3.14 [20150409 duanyy] <br>
 * - 修正formContentType所取的参数名问题，笔误 <br>
 */
public class XMLMessage implements Message {
	protected static final Logger logger = LogManager.getLogger(XMLMessage.class);		
	protected Document xmlDoc = null;
	protected Element root = null;

	public Document getDocument(){return xmlDoc;}
	public Element getRoot(){return root;}

	public void init(MessageDoc ctx) {
		String data = null;
		{
			byte [] inputData = ctx.getRequestRaw();
			if (inputData != null){
				try {
					data = new String(inputData,ctx.getEncoding());
				}catch (Exception ex){
					
				}
			}
		}
		
		if (data == null){
			//当客户端通过form来post的时候，Message不去读取输入流。
			String _contentType = ctx.getReqestContentType();
			if (_contentType == null || !_contentType.startsWith(formContentType)){		
				InputStream in = null;
				try {
					in = ctx.getInputStream();
					data = Context.readFromInputStream(in, ctx.getEncoding());
				}catch(Exception ex){
					logger.error("Error when reading data from inputstream",ex);
				}finally{
					IOTools.close(in);
				}
			}
		}
		
		if (data != null && data.length() > 0){
			try {
				xmlDoc = XmlTools.loadFromContent(data);
			} catch (Exception ex) {
				logger.error("Error when parsing data from xml",ex);
			}
		}
		if (xmlDoc == null){
			try {
				xmlDoc = XmlTools.newDocument("root");
			} catch (ParserConfigurationException e) {
				
			}
		}
		
		root = xmlDoc.getDocumentElement();
	}
	public void finish(MessageDoc ctx,boolean closeStream) {
		root.setAttribute("code",ctx.getReturnCode());
		root.setAttribute("reason", ctx.getReason());
		root.setAttribute("duration", String.valueOf(ctx.getDuration()));
		root.setAttribute("host", ctx.getHost());
		root.setAttribute("serial", ctx.getGlobalSerial());
		
		OutputStream out = null;
		try {
			ctx.setResponseContentType("text/xml;charset=" + ctx.getEncoding());
			out = ctx.getOutputStream();
			XmlTools.saveToOutputStream(xmlDoc, out);
			out.flush();
		} catch (Exception ex) {
			logger.error("Error when writing data to outputstream",ex);
		}finally {
			if (closeStream)
				IOTools.closeStream(out);
		}
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
		formContentType = Settings.get().GetValue("http.formContentType",
				"application/x-www-form-urlencoded");
	}
	
}
