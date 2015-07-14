package com.logicbus.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Random;

import com.anysoft.formula.DataProvider;
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
 * 
 * @version 1.6.2.1 [20141218 duanyy] <br>
 * - 增加对Comet技术的支持<br>
 * 
 * @version 1.6.3.10 [20140324 duanyy] <br>
 * - 增加忽略本次输出的功能 <br>
 * 
 * @version 1.6.3.30 [20150714 duanyy] <br>
 * - 全局序列号只包含数字和字母 <br>
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
		return String.valueOf(System.currentTimeMillis()) + randomString(7);
	}
	
	/**
	 * 字符表
	 */
	protected static final char[] Chars = {
	      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
	      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
	      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
	      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
	      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
	      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
	      '8', '9'
	 };
	
	/**
	 * 按照指定宽度生成随机字符串
	 * @param _width 字符串的宽度
	 * @return 随机字符串
	 */
	static protected String randomString(int _width){
		int width = _width <= 0 ? 6 : _width;
		char [] ret = new char[width];
		Random ran = new Random();
		for (int i = 0 ; i < width ; i ++){
			int intValue = ran.nextInt(62) % 62;
			ret[i] = Chars[intValue];
		}
		
		return new String(ret);
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
		}
	}	
	
	/**
	 * 忽略本次输出
	 */
	public void ignore(){
		ignored = true;
	}
	
	protected boolean isIgnore(){
		return ignored;
	}
	
	protected boolean ignored = false;
}
