package com.alogic.together.catalog;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.together.service.TogetherServant;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.DefaultServiceDescription;
import com.logicbus.models.servant.impl.ServantCatalogNodeImpl;
import com.logicbus.models.servant.impl.XMLResourceServantCatalog;

public class TogetherServantCatalog extends XMLResourceServantCatalog{
	
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
				
				scanResource(node,catalogNode,home,bootstrap);
			}catch (Exception ex){
				logger.error("Can not load class:" + clazz,ex);
			}
		}
		
		return catalogNode;
	}	
	
	protected void scanResource(Element element,ServantCatalogNodeImpl node, String home, Class<?> bootstrap) {
		URL url = bootstrap.getResource(home);
		
		if(url.toString().startsWith("file:")){
            File file;
			try {
				file = new File(url.toURI());
			} catch (URISyntaxException e) {
				logger.error("Can not scan home:" + home,e);
				return;
			}
			scanFileSystem(element,node,home,file,bootstrap);
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
	        	scanJar(element,node,home,jfile,bootstrap);
        	}
        }
	}

	protected void scanJar(Element element,ServantCatalogNodeImpl node, String home, JarFile jfile,Class<?> bootstrap) {
        Enumeration<JarEntry> files = jfile.entries();
        while (files.hasMoreElements()) {
          JarEntry entry = files.nextElement();
          String name = entry.getName();
          if (name.startsWith(home.substring(1)) && (name.endsWith(".xml"))){
        	  serviceFound(element,node,"/" + name,bootstrap);
          }
        } 
	}

	protected void scanFileSystem(Element element,ServantCatalogNodeImpl node, String home,File file,Class<?> bootstrap) {
        File[] files = file.listFiles();
        for (File item:files){
        	if (item.getName().endsWith(".xml")){
        		serviceFound(element,node,home + "/" + item.getName(),bootstrap);
        	}
        }	
	}

	private void serviceFound(Element element,ServantCatalogNodeImpl node, String src,Class<?> bootstrap) {
		//从xml文件名中获取服务id
		int end = src.lastIndexOf('.');
		int start = src.lastIndexOf('/');
		String id = src.substring(start + 1, end);
		
		Path childPath = node.getPath().append(id);
		DefaultServiceDescription sd = new DefaultServiceDescription(childPath.getId());
		sd.setModule(servant);
		sd.setName(id);
		sd.setNote(id);
		sd.setPath(childPath.getPath());
		String visible = element.getAttribute("visible");
		sd.setVisible(StringUtils.isNotEmpty(visible)?visible:"public");
		String log = element.getAttribute("log");
		sd.setLogType(StringUtils.isNotEmpty(log)?log:"brief");
		sd.getProperties().SetValue("script", src);
		sd.getProperties().SetValue("bootstrap", bootstrap.getName());
		node.addService(sd.getServiceID(), sd);
	}
}