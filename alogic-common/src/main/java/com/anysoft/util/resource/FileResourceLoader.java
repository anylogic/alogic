package com.anysoft.util.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.anysoft.util.BaseException;
import com.anysoft.util.URLocation;

/**
 * 基于文件系统的资源装入器
 * 
 * @author duanyy
 *
 */
public class FileResourceLoader implements ResourceLoader {

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
		// TODO Auto-generated method stub
		if (!_url.hasPath()) {
			throw new BaseException(FileResourceLoader.class.getName(),
					"Can not find path from url:" + _url.toString());
		}
		return load(_url.getPath(),_context);
	}
	
	/**
	 * 装入输入流
	 * @param _path 文件路径
	 * @param _context 上下文
	 * @return　输入流实例
	 * @throws BaseException 当指定路径文件不存在时抛出
	 */
	public InputStream load(String _path,Object _context)  throws BaseException {
		try {
			return  new FileInputStream(_path);
		} catch (FileNotFoundException e) {
			throw new BaseException(FileResourceLoader.class.getName(),
					"Can not find file:" + _path);
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
		// TODO Auto-generated method stub
		try {
			return _url.makeURL();
		} catch (MalformedURLException e) {
			throw new BaseException(HttpResourceLoader.class.getName(),
					"Can not open url:" + _url.toString(), e);
		}
	}

}
