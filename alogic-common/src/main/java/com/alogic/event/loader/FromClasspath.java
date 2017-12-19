package com.alogic.event.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;

import com.alogic.load.Loader;
import com.alogic.event.Process;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;

/**
 * 从类加载路径装入Process
 * 
 * @author yyduan
 * @since 1.6.11.1
 * 
 * @version 1.6.11.2 [20171218 duanyy] <br>
 * - 增加扫描路径时的日志 <br>
 */
public class FromClasspath extends Loader.Abstract<Process>{
	/**
	 * 类路径
	 */
	protected String path;
	
	/**
	 * 引导类
	 */
	protected String bootstrap;
	
	/**
	 * 预加载的对象
	 */
	private Map<String,Process> processes = new ConcurrentHashMap<String,Process>();
	
	@Override
	public Process load(String id, boolean cacheAllowed) {
		return processes.get(id);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);		
		
		path = PropertiesConstants.getString(p, "path", "");
		bootstrap = PropertiesConstants.getString(p, "bootstrap", "");
		
		if (StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(bootstrap)){
			ClassLoader cl = Settings.getClassLoader();			
			try {
				Class<?> clazz = cl.loadClass(bootstrap);
				scanResource(path,clazz);
			}catch (Exception ex){
				LOG.error("Can not load class " + bootstrap);
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}else{
			LOG.error("Can not scan class path,because the path or the bootstap is null");
		}
	}
	
	/**
	 * 从指定的CLASSPATH路径中扫描资源
	 * 
	 * @param home 路径
	 * @param clazz 引导类
	 */
	protected void scanResource(String home, Class<?> clazz) {
		URL url = clazz.getResource(home);
		
		if(url.toString().startsWith("file:")){
            File file;
			try {
				file = new File(url.toURI());
			} catch (URISyntaxException e) {
				LOG.error("Can not scan home:" + path,e);
				LOG.error(ExceptionUtils.getStackTrace(e));
				return;
			}
			LOG.info(String.format("Scan event processor in %s",url.toString()));
			scanFileSystem(home,file,clazz);
        }
        else{
        	if (url.toString().startsWith("jar:")){
	        	JarFile jfile;
	        	try {
	        		String jarUrl = url.toString();
	        		jarUrl = jarUrl.substring(9, jarUrl.indexOf('!'));
					jfile = new JarFile(jarUrl);
				} catch (IOException e) {
					LOG.error("Can not scan home:" + path,e);
					LOG.error(ExceptionUtils.getStackTrace(e));
					return;
				}
	        	LOG.info(String.format("Scan event processor in %s",url.toString()));
	        	scanJar(home,jfile,clazz);
        	}
        }		
	}

	/**
	 * 扫描指定的Jar
	 * @param home Jar中的路径
	 * @param jarFile jarFile 
	 */
	protected void scanJar(String home, JarFile jarFile,Class<?> clazz) {
		Enumeration<JarEntry> files = jarFile.entries();
		while (files.hasMoreElements()) {
			JarEntry entry = files.nextElement();
			String name = entry.getName();
			if (name.endsWith(".xml")) {
				int end = name.lastIndexOf('/');
				String path = '/' + name.substring(0, end);
				if (path.equals(home)) {
					// got
					LOG.info(String.format("Found event processor : %s",entry.getName()));
					String classPath = "/" + name;;
					InputStream in = null;
					try {
						in = clazz.getResourceAsStream(path);
						Document doc = XmlTools.loadFromInputStream(in);
						Process p = new Process.Default();
						p.configure(doc.getDocumentElement(), Settings.get());
						if (!p.isNull()) {
							processes.put(p.getId(), p);
						}
					} catch (Exception ex) {
						LOG.error("Can not load file:" + classPath);
						LOG.error(ExceptionUtils.getStackTrace(ex));
					} finally {
						IOTools.close(in);
					}
				}
			}
		}
	}

	/**
	 * 扫描文件系统
	 * @param path
	 * @param file
	 * @param clazz
	 */
	protected void scanFileSystem(String path, File file,Class<?> clazz) {
        File[] files = file.listFiles();
        for (File item:files){
        	if (item.getName().endsWith(".xml")){
        		LOG.info(String.format("Found event processor : %s",item.getName()));
        		InputStream in = null;
        		try {
        			in = new FileInputStream(item);
        			Document doc = XmlTools.loadFromInputStream(in);
        			Process p = new Process.Default();
        			p.configure(doc.getDocumentElement(), Settings.get());
        			if (!p.isNull()){
        				processes.put(p.getId(), p);
        			}
        		}catch (Exception ex){
        			LOG.error("Can not load file:" + item.getPath());
        			LOG.error(ExceptionUtils.getStackTrace(ex));
        		}finally{
        			IOTools.close(in);
        		}
        	}
        }	
	}
}
