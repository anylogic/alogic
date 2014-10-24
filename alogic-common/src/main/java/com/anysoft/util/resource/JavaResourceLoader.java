package com.anysoft.util.resource;

import java.io.InputStream;
import java.net.URL;


import com.anysoft.util.BaseException;
import com.anysoft.util.Settings;
import com.anysoft.util.URLocation;

/**
 * 基于Java内部文件的资源装入器
 * @author duanyy
 * 
 * @version 1.0.4 [20140326 duanyy]
 * - 可定制装入资源的Java类，解决以前跨ClassLoader无法取资源文件的问题
 */
public class JavaResourceLoader implements ResourceLoader {
	/**
	 * 装载输入流
	 * @param _url URL
	 * @param _context 上下文
	 * @return 输入流实例
	 * @throws BaseException 当URL中不包含路径时抛出
	 * @see #load(String, Object)
	 */		
	
	public InputStream load(URLocation _url, Object _context)
			throws BaseException {
		if (!_url.hasPath()) {
			throw new BaseException(JavaResourceLoader.class.getName(),
					"Can not find path from url:" + _url.toString());
		}
		@SuppressWarnings("rawtypes")
		Class clazz = getCurrentClass(_url);
		return clazz.getResourceAsStream(_url.getPath());
	}

	/**
	 * 获取装入资源时所用的class
	 * @param _url URL
	 * @return class
	 */
	@SuppressWarnings("rawtypes")
	protected Class getCurrentClass(URLocation _url){
		String className = "";
		if (_url.hasFragment()){
			//将fragment当作className
			className = _url.getFragment();
			if (className == null || className.length() <= 0){
				if (_url.hasQuery()){
					className = _url.getQuery();
				}
			}
		}
		
		if (className == null || className.length() <= 0){
			return getClass();
		}
		
		ClassLoader cl = null;
		{
			Settings settings = Settings.get();
			cl = (ClassLoader) settings.get("classLoader");
		}
		if (cl == null){
			cl = Thread.currentThread().getContextClassLoader();
		}
		
		try {
			return cl.loadClass(className);
		} catch (ClassNotFoundException e) {
			return getClass();
		}
	}
	/**
	 * 生成资源的标准URL
	 * 
	 * @param _url URL
	 * @param _context 上下文
	 * @return　资源对应的URL
	 * @throws BaseException 
	 */
	
	public URL createURL(URLocation _url, Object _context) throws BaseException {
		if (!_url.hasPath()) {
			throw new BaseException(JavaResourceLoader.class.getName(),
					"Can not find path from url:" + _url.toString());
		}
		
		@SuppressWarnings("rawtypes")
		Class clazz = getCurrentClass(_url);
		return clazz.getResource(_url.getPath());
	}	
}
