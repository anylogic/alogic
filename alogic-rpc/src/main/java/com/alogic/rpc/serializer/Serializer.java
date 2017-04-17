package com.alogic.rpc.serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;


/**
 * 序列化器
 * 
 * @author duanyy
 * 
 * @since 1.6.7.15
 * @version 1.6.8.7 [20170417 duanyy]<br>
 * - 序列化接口增加上下文 <br>
 */
public interface Serializer extends Reportable,Configurable,XMLConfigurable{
	
	/**
	 * 从输入流中读取指定的对象
	 * 
	 * @param in 输入流
	 * @param clazz 对象的类
	 * @param ctx 上下文变量
	 * 
	 * @return 对象实例
	 */
	public <D> D readObject(InputStream in,Class<D> clazz,Properties ctx);
	
	/**
	 * 向输出流中写出对象
	 * @param out 输出流
	 * @param object 对象
	 * @param ctx 上下文变量
	 */
	public void writeObject(OutputStream out,Object object,Properties ctx);
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements Serializer{

		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
			}
		}

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
	}
}
