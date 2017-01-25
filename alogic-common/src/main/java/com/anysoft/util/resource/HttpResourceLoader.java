package com.anysoft.util.resource;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.anysoft.util.BaseException;
import com.anysoft.util.URLocation;

/**
 * 基于Http的资源装入器
 * @author duanyy
 *
 */
public class HttpResourceLoader implements ResourceLoader {

	/**
	 * 装载输入流
	 * @param _url URL
	 * @param _context 上下文
	 * @return 输入流实例
	 * @throws BaseException 当Http连接出现错误时抛出
	 */	
	
	public InputStream load(URLocation _url, Object _context)
			throws BaseException {
		try {
			URL url = _url.makeURL();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			return conn.getInputStream();
		} catch (Exception e) {
			throw new BaseException(HttpResourceLoader.class.getName(),
					"Can not open url:" + _url.toString(), e);
		}
	}
	/**
	 * 生成资源的标准URL
	 * 
	 * @param _url URL
	 * @param _context 上下文
	 * @return　资源对应的URL
	 * @throws BaseException 当路径格式不正确时抛出
	 */
	
	public URL createURL(URLocation _url, Object _context) throws BaseException {
		try {
			return _url.makeURL();
		} catch (MalformedURLException e) {
			throw new BaseException(HttpResourceLoader.class.getName(),
					"Can not open url:" + _url.toString(), e);
		}
	}
}
