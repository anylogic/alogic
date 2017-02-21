package com.logicbus.backend.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.IOTools;
import com.logicbus.backend.Context;

/**
 * 基于Byte消息
 * @author duanyy
 * 
 * @since 1.6.1.1
 * 
 * @version 1.6.1.2 [20141118 duanyy] <br>
 * - 支持MessageDoc的Raw数据功能 <br>
 * @version 1.6.2.1 [20141223 duanyy] <br>
 * - 增加对Comet的支持 <br>
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - 淘汰MessageDoc，采用Context替代 <br>
 * - 增加getContentType和getContentLength <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.15 [20170221 duanyy] <br>
 * - 输出时设置Content-Length以便支持keepalive <br>
 */
public class ByteMessage implements Message {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LoggerFactory.getLogger(ByteMessage.class);
	/**
	 * 输入的字节流
	 */
	protected byte [] input = null;
	
	/**
	 * 输出的字节流
	 */
	protected byte [] output = null;
	
	/**
	 * content-type
	 */
	protected String contentType = "text/plain";	
	
	/**
	 * 获取输入的字节流
	 * @return 输入字节流
	 */
	public byte [] getInput(){return input;}
	
	/**
	 * 设置待输出的字节流数据
	 * @param _output 待输出的数据
	 */
	public void setOutput(byte [] _output){
		output = _output;
	}
	
	public void setContentType(String _contentType){
		contentType = _contentType;
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public long getContentLength() {
		return (input == null ? 0:input.length) + (output == null ? 0:output.length);
	}
	
	public void init(Context ctx) {
		input = ctx.getRequestRaw();
		if (input == null){
			InputStream in = null;
			try {
				in = ctx.getInputStream();
				input = readBytes(in);
			}catch (Exception ex){
				logger.error("Error when reading data from inputstream",ex);
			}finally{
				IOTools.close(in);
			}
		}
	}

	public void finish(Context ctx,boolean closeStream) {
		OutputStream out = null;
		try {
			ctx.setResponseContentType(contentType);
			ctx.setResponseContentLength(output.length);
			out = ctx.getOutputStream();
			if (output != null)
				writeBytes(out,output);
			
			out.flush();
		}catch (Exception ex){
			logger.error("Error when writing data to output stream",ex);
		}finally{
			if (closeStream){
				IOTools.close(out);
			}
		}
	}


	/**
	 * 从输入流中读取字节
	 * @param in 输入流
	 * @return 字节数组
	 * @throws IOException
	 */
	public static byte [] readBytes(InputStream in) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		byte[] buf =new byte[1024];  
        
        int size=0;  
          
        while((size=in.read(buf))!=-1)  
        {  
            bos.write(buf,0,size);  
        }  
		
		return bos.toByteArray();
	}
	
	/**
	 * 向输出流中写出数据
	 * @param out 输出流
	 * @param data 待写出的数据
	 * @throws IOException
	 */
	public static void writeBytes(OutputStream out,byte [] data) throws IOException {
		out.write(data);
	}
}
