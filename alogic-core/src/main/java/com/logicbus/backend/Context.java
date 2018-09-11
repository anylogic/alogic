package com.logicbus.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.alogic.xscript.LogicletContext;
import com.anysoft.formula.DataProvider;
import com.anysoft.util.DefaultProperties;
import com.logicbus.backend.message.Message;

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
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - 不再从MessageDoc上继承 <br>
 * - 增加报文长度 <br>
 * - 增加全局调用次序 <br>
 * 
 * @version 1.6.5.24 [20160718 duanyy] <br>
 * - 增加getPathInfo方法 <br>
 * 
 * @version 1.6.7.1 [20170117 duanyy] <br>
 * - trace日志调用链中的调用次序采用xx.xx.xx.xx字符串模式 <br>
 * 
 * @version 1.6.7.4 [20170118 duanyy] <br>
 * - 服务耗时单位改为ns <br>
 * 
 * @version 1.6.7.15 [20170221 duanyy] <br>
 * - 增加设置Content-Length接口 <br>
 * 
 * @version 1.6.9.8 [20170821 duanyy] <br>
 * - 服务上下文增加keyword关键字，和tlog对接; <br>
 * 
 * @version 1.6.10.9 [20171124 duanyy] <br>
 * - 规范化URL和URI的取值 <br>
 * 
 * @version 1.6.11.45 [duanyy 20180722] <br>
 * - 增加getHostDomain方法 <br>
 * 
 * @version 1.6.11.59 [20180911 duanyy] <br>
 * - 增加基于Context的LogicletContext实现;
 */
public abstract class Context extends DefaultProperties implements DataProvider{
	/**
	 * 文档编码
	 */
	protected String encoding = "utf-8";

	/**
	 * 结果代码
	 */
	protected String returnCode = "core.ok";
	
	/**
	 * 原因
	 */
	protected String reason = "OK";
	
	/**
	 * 业务关键字
	 */
	protected String keyword = "";
	
	/**
	 * 时间戳
	 */
	protected long timestamp = System.currentTimeMillis();

	/**
	 * the start time
	 */
	private long m_start_time;
	
	/**
	 * the end time
	 */
	private long m_end_time;	
	
	/**
	 * 消息实例
	 */
	protected Message msg = null;	
	
	protected boolean ignored = false;	
	
	/**
	 * 是否允许cache
	 */
	protected boolean enableClientCache = false;
	
	/**
	 * 构造上下文
	 * 
	 * @param _encoding
	 */
	protected Context(String _encoding){
		encoding = _encoding;
	}	
	
	/**
	 * 获取文档编码
	 * @return 编码
	 */
	public String getEncoding(){return encoding;}
	
	public long getTimestamp(){return timestamp;}
	
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
	 * @return 原因
	 */
	public String getReason(){return reason;}
	
	/**
	 * 获取业务关键字
	 * @return 业务关键字
	 */
	public String getKeyword(){return keyword;}
	
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
	 */	
	public void setReturn(String _code,String _reason){
		returnCode = _code;
		reason = _reason;
	}
	
	public void setKeyword(String keyword){
		this.keyword = keyword;
	}
	
	public void enableClientCache(boolean enable){
		this.enableClientCache = enable;
	}
	
	public boolean enableClientCache(){
		return this.enableClientCache;
	}

	/**
	 * 作为消息处理
	 * 
	 * @param clazz Message实现类
	 * @throws ServantException 当创建Message实例发生异常的时候，抛出异常代码为:core.instance_create_error
	 */
	public <message extends Message> Message asMessage(Class<message> clazz){
		if (msg != null)
			return msg;
		try {
			msg = (Message)clazz.newInstance();
			msg.init(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServantException("core.e1002",
					"Can not create instance of " + clazz.getName() + ":" + e.getMessage());
		}
		return msg;
	}
	
	@Override
	public String toString(){
		return msg == null ? null:msg.toString();
	}

	/**
	 * 完成服务，写出结果
	 */
	abstract public void finish();
	
	/**
	 * to get the client ip
	 * @return client ip
	 */
	abstract public String getClientIp();
	
	/**
	 * to get the client ip
	 * @return client ip
	 */
	abstract public String getClientRealIp();
	
	/**
	 * to get the request path
	 * @return request path
	 */
	abstract public String getPathInfo();
	
	/**
	 * 获取主机信息
	 * @return 主机信息
	 */
	abstract public String getHost();
	
	/**
	 * 获取主机域名
	 * @return 主机域名
	 */
	abstract public String getHostDomain();
	
	/**
	 * 获取请求路径
	 * @return request路径
	 */
	abstract public String getRequestURI();
	
	/**
	 * 获取请求的URL
	 * @return URL
	 */
	abstract public String getRequestURL();
	
	/**
	 * 获取请求的方法
	 * 
	 * @return 请求的方法,POST,GET等
	 * 
	 * @since 1.6.1.1
	 */
	abstract public String getMethod();
	
	/**
	 * 获取报文大小
	 * @return 报文大小
	 */
	abstract public long getContentLength();
	
	/**
	 * 获取全局序列号
	 * @return 全局序列号
	 * 
	 * @since 1.0.7
	 */
	abstract public String getGlobalSerial();
	
	/**
	 * 获取全局序列号的调用次序
	 * @return 调用次序
	 * 
	 * @since 1.6.5.6
	 */
	abstract public String getGlobalSerialOrder();
	
	/**
	 * 获取请求的Content-Type
	 * 
	 * @return Content-Type
	 */
	abstract public String getRequestContentType();
	
	/**
	 * 获取请求头信息
	 * @param id 信息ID
	 * @return 信息值
	 */
	abstract public String getRequestHeader(String id);
	
	/**
	 * 设置响应头的信息
	 * @param id 信息
	 * @param value 信息值
	 */
	abstract public void setResponseHeader(String id,String value);

	/**
	 * 设置响应的Content-Type
	 * @param contentType
	 */
	abstract public void setResponseContentType(String contentType);
	
	abstract public void setResponseContentLength(int contentLength);
	
	abstract public String getQueryString();
	
	/**
	 * 获取InputStream
	 * @return InputStream
	 * @since 1.6.1.1
	 */
	abstract public InputStream getInputStream() throws IOException;
	
	/**
	 * 获取OutputStram
	 * @return OutputStram
	 * @since 1.6.1.1
	 */
	abstract public OutputStream getOutputStream() throws IOException;
	
	/**
	 * 获取请求的输入数据
	 * 
	 * <p>
	 * 如果有必要，MessageDoc将提前截取请求数据，以byte数组的形式放在RequestRaw中。
	 * 
	 * @return byte[]形式的输入数据
	 */
	abstract public byte [] getRequestRaw();
	
	/**
	 * whether Comet is supported
	 * @return true if supported , false or not
	 * @since 1.6.2.1
	 */
	public boolean supportedComet(){
		return false;
	}
	
	public Comet getComet(){
		return null;
	}
	
	public boolean cometMode(){
		return false;
	}

	@Override
	public String getValue(String varName, Object context, String defaultValue) {
		return GetValue(varName, defaultValue);
	}

	@Override
	public Object getContext(String varName) {
		return this;
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
	 * @param out 输出流 
	 * @param doc 输出文档
	 * @param encoding 编码
	 */
	public static void writeToOutpuStream(OutputStream out,String doc,String encoding){
		try {
			out.write(doc.getBytes(encoding));
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}	
	
	public static void writeToOutpuStream(OutputStream _out,byte[] bytes){
		try {
			_out.write(bytes);
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
	
	public static class ServantLogicletContext extends LogicletContext {

		/**
		 * 客户端ip
		 */
		public static final String CLIENTIP = "$clientIp";
		
		/**
		 * 真实的客户端ip
		 */
		public static final String CLIENTIPREAL = "$clientIpReal";
		
		/**
		 * 本次服务的全局序列号
		 */
		public static final String SN = "$sn";
		
		/**
		 * 主机信息
		 */
		public static final String HOST = "$host";
		
		/**
		 * 主机域名
		 */
		public static final String HOST_DOMAIN = "$hostdomain";	
		
		/**
		 * 请求方法
		 */
		public static final String METHOD = "$method";
		
		/**
		 * 请求参数
		 */
		public static final String QUERY = "$query";
		
		/**
		 * 请求完整的URI
		 */
		public static final String URI = "$uri";
		
		/**
		 * 请求完整的URL
		 */
		public static final String URL = "$url";
		
		/**
		 * 请求的路径
		 */
		public static final String PATH = "$path";
		
		protected Context context = null;

		public ServantLogicletContext(Context ctx) {
			super(ctx);
			context = ctx;
		}
		
		@Override
		protected String _GetValue(String name) {
			if (context != null){
				switch(name){
					case CLIENTIP:
						return context.getClientIp();
					case CLIENTIPREAL:
						return context.getClientRealIp();
					case SN:
						return context.getGlobalSerial();
					case HOST:
						return context.getHost();
					case METHOD:
						return context.getMethod();
					case QUERY:
						return context.getQueryString();
					case URI:
						return context.getRequestURI();
					case PATH:
						return context.getPathInfo();
					case URL:
						return context.getRequestURL();
					case HOST_DOMAIN:
						return context.getHostDomain();					
				}
			}
			
			return super._GetValue(name);
		}	
	}
}
