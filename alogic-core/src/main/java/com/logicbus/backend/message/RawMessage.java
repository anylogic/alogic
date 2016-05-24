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
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - Message被改造为接口 <br>
 * - MessageDoc暴露InputStream和OutputStream <br>
 * 
 * @version 1.6.1.2 [20141118 duanyy] <br>
 * - 支持MessageDoc的Raw数据功能 <br>
 * 
 * @version 1.6.2.1 [20141223 duanyy] <br>
 * - 增加对Comet的支持 <br>
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - 淘汰MessageDoc，采用Context替代 <br>
 * - 增加getContentType和getContentLength <br>
 * 
 */
public class RawMessage implements Message {
	protected static final Logger logger = LogManager.getLogger(RawMessage.class);	
	/**
	 * 消息文本
	 */
	protected StringBuffer buf = new StringBuffer();
	
	private long contentLength = 0;
	
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

	public void init(Context ctx) {
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
			InputStream in = null;
			try {
				in = ctx.getInputStream();
				data = Context.readFromInputStream(in, ctx.getEncoding());
			}catch (Exception ex){
				logger.error("Error when reading data from inputstream",ex);
			}finally{
				IOTools.close(in);
			}
		}
		
		if (data != null){
			contentLength += data.getBytes().length;
			buf.append(data);
		}
		
		contentType = "text/plain;charset=" + ctx.getEncoding();
	}

	public void finish(Context ctx,boolean closeStream) {
		OutputStream out = null;
		try {
			ctx.setResponseContentType(contentType);
			out = ctx.getOutputStream();
			byte [] bytes = buf.toString().getBytes(ctx.getEncoding());
			contentLength += bytes.length;
			Context.writeToOutpuStream(out, bytes);
			out.flush();
		}catch (Exception ex){
			logger.error("Error when writing data to outputstream",ex);
		}finally{
			if (closeStream)
			IOTools.close(out);
		}
	}
	
	protected String contentType = "text/plain";
	
	public void setContentType(String _contentType){
		contentType = _contentType;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

}
