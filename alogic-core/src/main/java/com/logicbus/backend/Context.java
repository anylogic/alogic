package com.logicbus.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.anysoft.formula.DataProvider;
import com.anysoft.util.IOTools;
import com.anysoft.util.KeyGen;
import com.logicbus.backend.message.MessageDoc;

/**
 * 服务访问的上下文
 * 
 * <p>
 * 记录了本次访问的一些上下文信息，例如服务参数、客户端IP等
 * 
 * @author duanyy
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 改进消息传递模型 <br>
 * 
 * @version 1.0.7 [20140418 duanyy] <br>
 * - 增加生成全局序列号功能 <br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 * - 实现了DataProvider
 */
abstract public class Context extends MessageDoc implements DataProvider{

	protected Context(String _encoding) {
		super(_encoding);
	}

	public String getValue(String varName, Object context, String defaultValue) {
		return GetValue(varName, defaultValue);
	}

	public Object getContext(String varName) {
		return cookies;
	}	
	
	protected static Object cookies = new Object();
	
	/**
	 * 生成全局序列号
	 * <p>
	 * 根据简单的算法生成一个全部不重复的序列号。
	 * 
	 * @return 全局序列号
	 * 
	 * @since 1.0.7
	 * 
	 */
	public static String createGlobalSerial(){
		return String.valueOf(System.currentTimeMillis()) + KeyGen.getKey(7);
	}
	
	/**
	 * 从输入流中读入文本
	 * @param _in 输入流 
	 * @param _encoding 编码
	 * @return 文本
	 */
	public static String readFromInputStream(InputStream _in,String _encoding){
		StringBuffer buf = new StringBuffer(1024);
		String line = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(_in,_encoding));
            while ((line = reader.readLine()) != null) {
            	buf.append(line);
            	buf.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	IOTools.closeStream(_in,reader);
        }
		return buf.toString();
	}
	
	/**
	 * 向输出流输出文本
	 * 
	 * @param _out 输出流 
	 * @param _doc 输出文档
	 * @param _encoding 编码
	 */
	public static void writeToOutpuStream(OutputStream _out,String _doc,String _encoding){
		try {
			_out.write(_doc.getBytes(_encoding));
		}catch (Exception ex){
			ex.printStackTrace();
		}finally {
			IOTools.closeStream(_out);
		}
	}	
}
