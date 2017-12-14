package com.alogic.blob.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Element;
import com.alogic.blob.core.BlobManager;
import com.alogic.blob.core.BlobReader;
import com.alogic.blob.core.BlobWriter;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;

/**
 * 自动图标集
 * @author duanyy
 * @since 1.6.4.18
 */
public class AutoIconSet extends BlobManager.Abstract{

	/**
	 * 路径
	 */
	private String home = "";
	
	/**
	 * 资源引导类
	 */
	private Class<?> bootstrap = getClass();	
	
	/**
	 * content type
	 */
	private String contentType = "image/*";	
	
	/**
	 * 保存多个图标资源的数组
	 */
	private List<ResourceBlobInfo> blobs = new ArrayList<ResourceBlobInfo>(); // NOSONAR
	
	public String getHome(){return home;}
	
	public Class<?> getBootstrap(){return bootstrap;}
	
	public String getContentType(){return contentType;}	
	
	@Override
	public void configure(Element pElement, Properties pProps){
		XmlElementProperties p = new XmlElementProperties(pElement,pProps);
		configure(p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		contentType = PropertiesConstants.getString(p,"contentType",contentType,true); // NOSONAR
		
		home = PropertiesConstants.getString(p,"home","/com/alogic/blob/icon/auto",true);
		
		String clazz = PropertiesConstants.getString(p,"bootstrap","",true); // NOSONAR
		if (clazz != null && clazz.length() > 0){
			ClassLoader cl = Settings.getClassLoader();
			
			try {
				bootstrap = cl.loadClass(clazz);
			}catch (Exception ex){
				logger.error("Can not load class:" + clazz,ex);
			}
		}
		
		scanResource(home,bootstrap);
	}
	
	/**
	 * 在home目录中扫描资源
	 * @param pHome 目录
	 * @param pBootstrap 资源引导类
	 */
	protected void scanResource(String pHome, Class<?> pBootstrap) {
		URL url = pBootstrap.getResource(pHome);
		
		if(url.toString().startsWith("file:")){
            File file;
			try {
				file = new File(url.toURI());
			} catch (URISyntaxException e) {
				logger.error("Can not scan home:" + pHome,e);
				return;
			}
			scanFileSystem(pHome,file);
        }
        else{
        	if (url.toString().startsWith("jar:")){
	        	JarFile jfile;
	        	try {
	        		String jarUrl = url.toString();
	        		jarUrl = jarUrl.substring(9, jarUrl.indexOf('!'));
					jfile = new JarFile(jarUrl);
				} catch (IOException e) {
					logger.error("Can not scan home:" + pHome,e);
					return;
				}
	        	scanJar(pHome,jfile);
        	}
        }
	}
	
	/**
	 * 扫描指定的jar
	 * @param home 路径
	 * @param pFile jar file
	 */
	protected void scanJar(String home,JarFile pFile){
        Enumeration<JarEntry> files = pFile.entries();
        while (files.hasMoreElements()) {
          JarEntry entry = files.nextElement();
          String name = entry.getName();
          if (name.startsWith(home.substring(1)) && (name.endsWith(".png") || name.endsWith(".jpg"))){
        	  resourceFound('/' + name);
          }
        } 		
	}

	protected void scanFileSystem(String pHome,File pFile){
        File[] files = pFile.listFiles();
        for (File item:files){
        	if (item.getName().endsWith(".png") ||
        			item.getName().endsWith(".jpg")){
        		resourceFound(pHome + "/" + item.getName());
        	}
        }		
	}
	
	protected void resourceFound(String path) {
		InputStream in = null;
		try {
			in = bootstrap.getResourceAsStream(path);
			if (in != null){
				String md5 = DigestUtils.md5Hex(in);
				ResourceBlobInfo info = new ResourceBlobInfo(id,contentType,md5,0,path);
				blobs.add(info);
			}else{
				logger.error("The resource is not valid :" + path);
			}
		}catch (Exception ex){
			logger.error("Failed to build resource:" + id,ex);
		}finally{
			IOTools.close(in);
		}
	}	
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			
			xml.setAttribute("home", home);
			xml.setAttribute("bootstrap", bootstrap.getName());
			xml.setAttribute("contentType", contentType);
			xml.setAttribute("count", String.valueOf(blobs.size()));
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			
			json.put("home", home);
			json.put("bootstrap", bootstrap.getName());
			json.put("contentType", contentType);
			json.put("count", blobs.size());
		}
	}
	
	@Override
	public BlobReader getFile(String id) {
		if (blobs.isEmpty()){
			return null;
		}
		int index = (id.hashCode() & Integer.MAX_VALUE) % blobs.size();
		return new ResourceBlobReader(blobs.get(index),bootstrap);
	}

	@Override
	public boolean existFile(String id) {
		return !blobs.isEmpty();
	}	
	
	@Override
	public String list(List<String> ids, String cookies,int limit) {
		for (ResourceBlobInfo info:blobs){
			ids.add(info.id());
		}
		return cookies;
	}		
	
	@Override
	public BlobWriter newFile(String contentType) {
		throw new BaseException("core.not_supported","This function is not suppurted yet."); // NOSONAR	
	}
	
	@Override
	public boolean deleteFile(String id) {
		throw new BaseException("core.e1000",
				"This function is not suppurted yet.");	
	}

	@Override
	public void commit(BlobWriter writer) {
		throw new BaseException("core.e1000",
				"This function is not suppurted yet.");	
	}

	@Override
	public void cancel(BlobWriter writer) {
		throw new BaseException("core.e1000",
				"This function is not suppurted yet.");	
	}
}
