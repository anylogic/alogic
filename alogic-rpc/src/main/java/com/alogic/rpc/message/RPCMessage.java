package com.alogic.rpc.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.IOTools;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.Message;

/**
 * RPC专用Message
 * 
 * @author duanyy
 * @since 1.6.7.15
 */
public class RPCMessage implements Message{
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LoggerFactory.getLogger(RPCMessage.class);

	protected String contentType = "text/plain";	
	
	/**
	 * 输入流
	 */
	protected InputStream in = null;
	
	/**
	 * 输出流
	 */
	protected OutputStream out = null;
	
	public InputStream getInputStream(){
		return in;
	}
	
	public OutputStream getOutputStream(){
		return out;
	}
	
	public String getContentType(Context doc) {
		return contentType;
	}
	
	public void setContentType(String _contentType){
		contentType = _contentType;
	}

	@Override
	public void init(Context ctx) {
		try {
			in = ctx.getInputStream();
			out = ctx.getOutputStream();
		} catch (IOException e) {
			in = null;
		}
	}

	public void finish(Context ctx,boolean closeStream) {
		try {
			if (out != null){
				ctx.setResponseContentType(contentType);
				out = ctx.getOutputStream();
				out.flush();
			}
		}catch (Exception ex){
			logger.error("Error when writing data to output stream",ex);
		}finally{
			if (closeStream){
				IOTools.close(out);
			}
		}
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public long getContentLength() {
		return 0;
	}
}
