package com.logicbus.backend.message;

import java.io.InputStream;
import java.io.OutputStream;

import com.logicbus.backend.Context;


/**
 * 消息
 * @author duanyy
 * @version 1.0.4 [20140410 duanyy] <br>
 * - 增加encoding成员
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 改造为接口 <br>
 */
public interface Message {
	/**
	 * 输出消息到输出流
	 * 
	 * @param out 输出流
	 * @param doc 消息文档
	 */
	public void write(OutputStream out,Context doc);

	/**
	 * 从输入流中读入消息
	 * 
	 * @param in 输入流
	 * @param doc 消息文档
	 */
	public void read(InputStream in,Context doc);
	
	/**
	 * 是否从输入流中输入
	 * @param doc 消息文档 
	 * @return 是否Read
	 */
	public boolean doRead(Context doc);
	
	/**
	 * 是否输出
	 * @param doc
	 * @return 是否Write
	 */
	public boolean doWrite(Context doc);
	
	/**
	 * 获取输出的ContentType
	 * @return ContentType
	 */
	public String getContentType(Context doc);
}
