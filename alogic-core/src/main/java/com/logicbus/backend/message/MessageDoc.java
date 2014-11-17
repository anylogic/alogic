package com.logicbus.backend.message;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;

/**
 * 消息文档
 * @author duanyy
 * 
 * @version 1.0.4 [20140410 duanyy] <br>
 * - 增加对RawMessage的支持，见{@link com.logicbus.backend.message.MessageDoc#asRaw() asRaw} <br>
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 */
public class MessageDoc {	
	/**
	 * 结果代码
	 */
	protected String returnCode = "core.ok";
	
	/**
	 * 原因
	 */
	protected String reason = "It is ok.";
	
	/**
	 * 时长
	 */
	protected long duration = 0;

	/**
	 * the start time
	 */
	private long m_start_time;
	/**
	 * the end time
	 */
	private long m_end_time;
	
	/**
	 * to get the start time
	 * @return start time
	 */
	public long getStartTime(){return m_start_time;}
	
	/**
	 * to set the start time
	 * @param start_time start time
	 */
	public void setStartTime(long start_time){m_start_time = start_time;}
	
	/**
	 * to get the end time
	 * @return end time
	 */
	public long getEndTime(){return m_end_time;}

	/**
	 * to set the end time
	 * @param end_time end time
	 */
	public void setEndTime(long end_time){m_end_time = end_time;}	
	
	/**
	 * 获取结果代码
	 * @return 结果代码
	 */
	public String getReturnCode(){return returnCode;}
	
	/**
	 * 获取原因
	 * @return
	 */
	public String getReason(){return reason;}
	
	/**
	 * 获取时长
	 * @return 时长
	 */
	public long getDuration(){return m_end_time - m_start_time;}
	
	/**
	 * 设置调用结果
	 * 
	 * @param _code 结果代码
	 * @param _reason 原因
	 * @param _duration 调用时间
	 */	
	public void setReturn(String _code,String _reason){
		returnCode = _code;
		reason = _reason;
	}
	
	/**
	 * 消息文本
	 */
	protected StringBuffer doc;
	/**
	 * 文档编码
	 */
	protected String encoding = "utf-8";
	
	/**
	 * 获取文档编码
	 * @return 编码
	 */
	public String getEncoding(){return encoding;}
	
	/**
	 * constructor
	 * 
	 * @param _inDoc 消息文本
	 * @param _encoding 编码
	 */
	public MessageDoc(StringBuffer _inDoc,String _encoding){
		doc = _inDoc;
		encoding = _encoding;
	}
	
	/**
	 * 消息实例
	 */
	protected Message msg = null;
	
	/**
	 * 作为消息处理
	 * @param clazz
	 * @return
	 */
	public Message asMessage(Class<? extends Message> clazz) throws ServantException {
		if (msg != null)
			return msg;
		try {
			Constructor<? extends Message> constructor = 
					clazz.getConstructor(new Class<?>[]{MessageDoc.class,StringBuffer.class});
			msg = (Message)constructor.newInstance(new Object[]{this,doc});
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServantException("core.instance_create_error",
					"Can not create instance of " + clazz.getName() + ":" + e.getMessage());
		}
		return msg;
	}
	
	/**
	 * 获取文档的content-type
	 * @return content-type
	 */
	public String getContentType(){
		if (msg != null){
			return msg.getContentType();
		}
		return "text/xml;charset=utf-8";
	}
	
	/**
	 * 输出文档到输出流
	 * @param out
	 * @param response 
	 */
	public void output(OutputStream out, Context ctx){
		if (msg != null){
			msg.output(out,ctx);
		}
	}
	
	/**
	 * 是否存在致命错误
	 * @return
	 */
	public boolean hasFatalError(){
		return msg == null ? true : msg.hasFatalError();
	}
	
	/**
	 * to string
	 */
	public String toString(){
		if (msg == null)
			return doc.toString();
		return msg.toString();
	}
}
