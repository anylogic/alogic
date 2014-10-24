package com.anysoft.util.resource;

import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.anysoft.util.BaseException;
import com.anysoft.util.XmlTools;
import com.anysoft.util.URLocation;

/**
 * 资源工厂
 * 
 * 
 * @author duanyy
 *
 */
public class ResourceFactory {
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LogManager.getLogger(ResourceFactory.class);
	
	/**
	 * 装入资源的输入流
	 * 
	 * 先采用master去装入资源，如果不成功，再尝试用secondary去装入。
	 * 
	 * @param master 资源的URL
	 * @param secondary 备用的资源URL
	 * @param _context 上下文
	 * @return 输入流 
	 * @throws BaseException
	 */
	public InputStream load(String master,String secondary,Object _context)throws BaseException{
		InputStream in = null;
		try {
			in = load(master,_context);
		}catch (BaseException ex){
			if (secondary != null && secondary.length() > 0){
				logger.error("Can not load xrc,path=" + master, ex);
			}else{
				throw ex;
			}
		}
		if (in == null && secondary != null && secondary.length() > 0){
			logger.info("Can not load master xrc,try secondary xrc...path = " + secondary);
			in = load(secondary,_context);
		}
		return in;
	}
	
	/**
	 * 装入资源的输入流
	 * 
	 * <br>
	 * 通过URL中的scheme来识别ResourceLoader。先使用子类提供的{@link #getLoader(String)}来获取ResourceLoader，
	 * 如果子类没有定义，再从注册的ResourceLoader列表中获取。<br>
	 * 
	 * 可通过两种方式定制所使用的ResourceLoader<br>
	 * - 定制ResourceFactory的子类，重载{@link #getLoader(String)}函数，来获取ResourceLoader.<br>
	 * - 通过{@link #registerLoader(String, Class)}主动注册ResourceLoader.<br>
	 * 
	 * @param _path 资源的URL
	 * @param _context 上下文
	 * @return 输入流 
	 * @throws BaseException
	 */	
	public InputStream load(String _path, Object _context) throws BaseException {
		URLocation url = new URLocation(_path);
		String scheme = url.hasScheme() ? url.getScheme() : "java";
		ResourceLoader loader = getLoader(scheme);
		if (loader == null) {
			Class<ResourceLoader> _class = loaders.get(scheme);
			if (_class != null) {
				try {
					loader = _class.newInstance();
				} catch (Exception ex) {
					loader = null;
				}
			}
		}
		if (loader == null) {
			throw new BaseException(ResourceFactory.class.getName(),
					"Can not create ResourceLoader instance:" + _path);
		}
		return loader.load(url, _context);
	}
	/**
	 * 构造函数
	 */
	public ResourceFactory() {

	}
	
	/**
	 * 生成标准URL
	 * @param _path 字符串型URL
	 * @param _context 上下文
	 * @return URL
	 * @throws BaseException
	 */
	public URL createURL(String _path, Object _context) throws BaseException {
		URLocation url = new URLocation(_path);
		String scheme = url.hasScheme() ? url.getScheme() : "java";
		ResourceLoader loader = getLoader(scheme);
		if (loader == null) {
			Class<ResourceLoader> _class = loaders.get(scheme);
			if (_class != null) {
				try {
					loader = _class.newInstance();
				} catch (Exception ex) {
					loader = null;
				}
			}
		}
		if (loader == null) {
			throw new BaseException(ResourceFactory.class.getName(),
					"Can not create ResourceLoader instance:" + _path);
		}
		return loader.createURL(url, _context);		
	}
	
	/**
	 * 根据scheme获取ResourceLoader
	 * @param scheme scheme
	 * @return ResourceLoader
	 */
	protected ResourceLoader getLoader(String scheme) {
		return null;
	}

	/**
	 * 注册全局的ResourceLoader列表
	 * @param scheme scheme
	 * @param loader ResourceLoader的Class
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static void registerLoader(String scheme, Class loader) {
		loaders.put(scheme, loader);
	}

	/**
	 * 在全局ResourceLoader列表中注销指定的ResourceLoader
	 * @param scheme
	 */
	protected static void removeLoader(String scheme) {
		loaders.remove(scheme);
	}

	/**
	 * 全局ResourceLoader列表
	 */
	protected static Hashtable<String, Class<ResourceLoader>> loaders = new Hashtable<String, Class<ResourceLoader>>();

	/**
	 * 缺省条件下的ResourceLoader列表
	 */
	static {
		registerLoader("java", JavaResourceLoader.class);
		registerLoader("http", HttpResourceLoader.class);
		registerLoader("file", FileResourceLoader.class);
	}
	
	public static void main(String [] args){
		ResourceFactory factory = new ResourceFactory();
		try {
			//InputStream __in = factory.load("http://localhost:8080/logicbus/services/", null);
			//Document __doc = XmlTools.loadFromInputStream(__in);
			//System.out.println(XmlTools.node2String(__doc.getDocumentElement()));
						
			InputStream __in = factory.load("java:///com/anysoft/util/textdotter/resource/TextDotter.xml", null);
			Document __doc = XmlTools.loadFromInputStream(__in);
			System.out.println(XmlTools.node2String(__doc.getDocumentElement()));
			
			__in = factory.load("file:///D:/ecloud/logicbus/profile.xml", null);
			__doc = XmlTools.loadFromInputStream(__in);
			System.out.println(XmlTools.node2String(__doc.getDocumentElement()));			
			
		}catch (Exception ex){
			ex.printStackTrace();
		}		
	}
}
