package com.alogic.together2.catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.together2.TogetherServiceDescription;
import com.alogic.together2.service.TogetherServant;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.models.servant.impl.ServantCatalogNodeImpl;
import com.logicbus.models.servant.impl.XMLResourceServantCatalog;

/**
 * 从Class路径装入服务目录
 * 
 * @author yyduan
 * @since 1.6.11.3
 * 
 * @version 1.6.11.4 [20171222 duanyy] <br>
 * - 优化异常处理信息 <br>
 * 
 */
public class FromClasspath extends XMLResourceServantCatalog{
	
	/**
	 * 缺省的服务实现类
	 */
	private String servant = TogetherServant.class.getName();
	
	@Override
	public void configure(Properties p) {
		servant = PropertiesConstants.getString(p,"servant",servant);		
		loadDocument(p);
	}	
	
	protected CatalogNode createCatalogNode(Path _path,Object _data){
		ServantCatalogNodeImpl catalogNode = new ServantCatalogNodeImpl(_path,_data);
		
		Element node = (Element)_data;
		
		String home = node.getAttribute("home");
		String clazz = node.getAttribute("bootstrap");
		if (StringUtils.isNotEmpty(home) && StringUtils.isNotEmpty(clazz)){
			ClassLoader cl = Settings.getClassLoader();			
			try {
				Class<?> bootstrap = cl.loadClass(clazz);
				scanResource(new XmlElementProperties(node,Settings.get()),catalogNode,home,bootstrap);
			}catch (Exception ex){
				logger.error(String.format("Failed to scan resource:%s#%s",home,clazz));
			}
		}
		
		return catalogNode;
	}	
	
	protected void scanResource(Properties p,ServantCatalogNodeImpl node, String home, Class<?> bootstrap) {
		URL url = bootstrap.getResource(home);
		
		if (url != null){
			if(url.toString().startsWith("file:")){
	            File file;
				try {
					file = new File(url.toURI());
				} catch (URISyntaxException e) {
					logger.error("Can not scan home:" + home,e);
					return;
				}
				scanFileSystem(p,node,home,file,bootstrap);
	        }
	        else{
	        	if (url.toString().startsWith("jar:")){
		        	JarFile jfile;
		        	try {
		        		String jarUrl = url.toString();
		        		jarUrl = jarUrl.substring(9, jarUrl.indexOf('!'));
						jfile = new JarFile(jarUrl);
					} catch (IOException e) {
						logger.error("Can not scan home:" + home,e);
						return;
					}
		        	scanJar(p,node,home,jfile,bootstrap);
	        	}
	        }
		}else{
			logger.warn(String.format("Can not find resource in %s#%s", home,bootstrap.getName()));
		}
	}

	protected void scanJar(Properties p,ServantCatalogNodeImpl node, String home, JarFile jfile,Class<?> bootstrap) {
        Enumeration<JarEntry> files = jfile.entries();
        while (files.hasMoreElements()) {
          JarEntry entry = files.nextElement();
          String name = entry.getName();
          if (name.endsWith(".xml")){
              int end = name.lastIndexOf('/');
              String path = '/' + name.substring(0, end);
              if (path.equals(home)){
            	  serviceFound(p,node,"/" + name,bootstrap);
              }      	  
          }

        } 
	}

	protected void scanFileSystem(Properties p,ServantCatalogNodeImpl node, String home,File file,Class<?> bootstrap) {
        File[] files = file.listFiles();
        for (File item:files){
        	if (item.getName().endsWith(".xml")){
        		serviceFound(p,node,home + "/" + item.getName(),bootstrap);
        	}
        }	
	}

	protected void serviceFound(Properties p,ServantCatalogNodeImpl node, String src,Class<?> bootstrap) {
		//从xml文件名中获取服务id
		int end = src.lastIndexOf('.');
		int start = src.lastIndexOf('/');
		String id = src.substring(start + 1, end);
		
		Path childPath = node.getPath().append(id);
		ServiceDescription sd = loadServiceDescription(id,childPath.getPath(),src,bootstrap,p);
		if (sd != null){
			logger.info(String.format("Service %s is found.", sd.getPath(),sd.getServiceID()));
			node.addService(sd.getServiceID(), sd);
		}
	}
	
	protected ServiceDescription loadServiceDescription(String id,String path,String url,Class<?> bootstrap,Properties p){
		TogetherServiceDescription sd = null;
		
		InputStream in = null;
		try {			
			in = bootstrap.getResourceAsStream(url);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				sd = new TogetherServiceDescription(id,path);
				sd.configure(doc.getDocumentElement(), p);				
			}
		}catch (Exception ex){
			logger.error("Can not load service from " + url);
			logger.error(ExceptionUtils.getStackTrace(ex));
		}finally{
			IOTools.close(in);
		}
		return sd;
	}
}