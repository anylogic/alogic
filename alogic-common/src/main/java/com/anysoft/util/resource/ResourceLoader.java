package com.anysoft.util.resource;

import java.io.InputStream;
import java.net.URL;

import com.anysoft.util.BaseException;
import com.anysoft.util.URLocation;


/**
 * 资源装入接口
 * 
 * @author duanyy
 */
public interface ResourceLoader {
	/**
	 * 装入资源
	 * @param _path 资源路径
	 * @param _context 上下文
	 * @return 输入数据流
	 * @throws BaseException
	 */
	public InputStream load(URLocation _url,Object _context) throws BaseException;
	
	/**
	 * 生成资源的标准URL
	 * 
	 * @param _url URL
	 * @param _context 上下文
	 * @return　资源对应的URL
	 * @throws BaseException
	 */
	public URL createURL(URLocation _url,Object _context)throws BaseException;
}