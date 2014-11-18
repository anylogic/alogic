package com.logicbus.backend.message;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.IOTools;
import com.logicbus.backend.Context;

/**
 * Raw消息
 * 
 * @author duanyy
 * 
 * @since 1.0.4
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 * @version 1.4.0 [20141117 duanyy] <br>
 * - Message被改造为接口 <br>
 * - MessageDoc暴露InputStream和OutputStream <br>
 * 
 */
public class RawMessage implements Message {
	protected static final Logger logger = LogManager.getLogger(RawMessage.class);	
	/**
	 * 消息文本
	 */
	protected StringBuffer buf = new StringBuffer();
	
	/**
	 * 获取消息文本
	 * @return 消息文本
	 */
	public StringBuffer getBuffer(){
		return buf;
	}
	
	/**
	 * to string
	 */
	public String toString(){
		return buf.toString();
	}

	public void init(MessageDoc ctx) {
		InputStream in = null;
		try {
			in = ctx.getInputStream();
			String data = Context.readFromInputStream(in, ctx.getEncoding());
			buf.append(data);
			contentType = "text/plain;charset=" + ctx.getEncoding();
		}catch (Exception ex){
			logger.error("Error when reading data from inputstream",ex);
		}finally{
			IOTools.close(in);
		}
	}

	public void finish(MessageDoc ctx) {
		OutputStream out = null;
		try {
			ctx.setResponseContentType(contentType);
			out = ctx.getOutputStream();
			Context.writeToOutpuStream(out, buf.toString(), ctx.getEncoding());
		}catch (Exception ex){
			logger.error("Error when writing data to outputstream",ex);
		}finally{
			IOTools.close(out);
		}
	}
	
	protected String contentType = "text/plain";
	
	public void setContentType(String _contentType){
		contentType = _contentType;
	}

}
