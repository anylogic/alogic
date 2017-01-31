package com.alogic.terminal;

import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * Shell
 * @author duanyy
 * @since 1.1.10.10
 */
public interface Terminal extends Configurable,XMLConfigurable,AutoCloseable,Reportable{
	
	/**
	 * 连接服务器
	 */
	public void connect();
	
	/**
	 * 执行指令
	 * @param cmd 指令
	 * @param resolver 结果解析器
	 * @return 指令执行结果
	 */
	public int exec(Resolver resolver,String...cmd);
	
	
	/**
	 * 执行指令
	 * @param command 指令
	 * @return 指令执行结果
	 */
	public int exec(Command command);
	
	/**
	 * 断开服务器
	 */
	public void disconnect();
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements Terminal{

		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml,"module",getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
			}
		}
	}
}
