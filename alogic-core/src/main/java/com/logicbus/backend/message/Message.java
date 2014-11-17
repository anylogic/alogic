package com.logicbus.backend.message;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.logicbus.backend.Context;


/**
 * 消息
 * @author duanyy
 * @version 1.0.4 [20140410 duanyy] <br>
 * - 增加encoding成员
 * - {@link com.logicbus.backend.message.Message#output(OutputStream, HttpServletResponse) out}函数
 * 增加response参数，以便Message直接操作.
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 */
abstract public class Message {

	/**
	 * 文档
	 */
	protected MessageDoc msgDoc = null;
	
	protected Message(MessageDoc _doc){
		msgDoc = _doc;
	}
	
	/**
	 * 输出消息到输出流
	 * @param out 输出流
	 * @param ctx 上下文
	 */
	abstract public void output(OutputStream out,Context ctx);

	/**
	 * content-type
	 */
	protected String contentType = "text/xml;charset=utf-8";
	
	/**
	 * 设置content-type
	 * @param type content-type
	 */
	public void setContentType(String type){contentType = type;}
	
	/**
	 * 获取content-type
	 * 
	 * @return content-type
	 */
	public String getContentType(){return contentType;}
	
	/**
	 * 是否发生致命错误
	 * @return 
	 */
	public boolean hasFatalError(){
		String returnCode = msgDoc.getReturnCode();
		return !returnCode.equals("core.ok");
	}
}
