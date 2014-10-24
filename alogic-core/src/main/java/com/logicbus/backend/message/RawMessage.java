package com.logicbus.backend.message;

import java.io.OutputStream;

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
 * 
 */
public class RawMessage extends Message {
	/**
	 * 消息文本
	 */
	protected StringBuffer buf = null;
	
	public RawMessage(MessageDoc _doc,StringBuffer _buf) {
		super(_doc);
		buf = _buf;
		setContentType("text/plain;charset=" + msgDoc.getEncoding());
	}

	
	public void output(OutputStream out,Context ctx) {
		String encoding = msgDoc.getEncoding();
		try {
			out.write(buf.toString().getBytes(encoding));
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			IOTools.closeStream(out);
		}
	}
	
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

}
