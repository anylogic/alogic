package com.anysoft.webloader;

import javax.servlet.ServletContext;

import org.w3c.dom.Element;

import com.anysoft.util.Factory;
import com.anysoft.util.Properties;


/**
 * 将web.xml中的内容装入到ServletContext中
 *  
 * @author duanyy
 * 
 * @since 1.6.0.0
 *
 */
public interface WebXMLLoader {
	
	/**
	 * 将XML节点中的内容装入到ServletContext
	 * 
	 * @param settings
	 * @param root
	 * @param sc
	 * 
	 */
	public void load(Properties settings,Element root,ServletContext sc);
	
	/**
	 * 工厂类
	 * <p>
	 * 用于创建WebXMLLoader.
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<WebXMLLoader>{
		
	}
}
