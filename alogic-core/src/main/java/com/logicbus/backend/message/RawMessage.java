package com.logicbus.backend.message;

import java.io.InputStream;
import java.io.OutputStream;

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
 */
public class RawMessage implements Message {
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


	public void write(OutputStream out, Context doc) {
		Context.writeToOutpuStream(out, buf.toString(), doc.getEncoding());
	}


	public void read(InputStream in, Context doc) {
		String data = Context.readFromInputStream(in, doc.getEncoding());
		buf.append(data);
		
		contentType = "text/plain;charset=" + doc.getEncoding();
	}


	public boolean doRead(Context doc) {
		return true;
	}


	public boolean doWrite(Context doc) {
		return true;
	}


	public String getContentType(Context doc) {
		return "text/plain;charset=" + doc.getEncoding();
	}

	protected String contentType = "text/plain";
	
	public void setContentType(String _contentType){
		contentType = _contentType;
	}
}
