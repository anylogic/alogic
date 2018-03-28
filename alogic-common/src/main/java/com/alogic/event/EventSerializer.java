package com.alogic.event;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;


/**
 * 事件序列化器
 * 
 * 
 * @author yyduan
 *
 * @since 1.6.11.26
 */
public interface EventSerializer extends XMLConfigurable,Configurable{
	
	/**
	 * 将事件序列化为byte[]
	 * 
	 * @param queue 队列
	 * @param evt 事件
	 * @return byte[]形式的数据
	 */
	public byte[] serialize(String queue,Event evt);
	
	/**
	 * 将byte[]反序列化为Event
	 * @param queue 队列
	 * @param data byte[]形式的数据
	 * @return Event实例
	 */
	public Event deserialize(String queue,byte[] data);
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements EventSerializer{

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
	}
	
	/**
	 * 缺省实现
	 * @author yyduan
	 *
	 */
	public static class Default extends Abstract{
		/**
		 * 编码
		 */
		protected String encoding = "utf-8";
		
		protected static JsonProvider provider = null;	
		static {
			provider = JsonProviderFactory.createProvider();
		}
		
		@Override
		public byte[] serialize(String queue, Event evt) {
			Map<String,Object> map = new HashMap<String,Object>();
			evt.toJson(map);
			
			String result = provider.toJson(map);
			
			try {
				return result.getBytes(encoding);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Event deserialize(String queue, byte[] data) {
			try {
				String buf = new String(data,encoding);
				Object map = provider.parse(buf);				
				Event evt = new Event.Default("0","0",true);
				evt.fromJson((Map<String,Object>)map);				
				return evt;
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}

		@Override
		public void configure(Properties p) {
			encoding = PropertiesConstants.getString(p,"encoding",encoding);
		}
		
	}
}
