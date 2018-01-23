package com.alogic.blob.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.blob.BlobManager;
import com.alogic.blob.BlobReader;
import com.alogic.blob.BlobWriter;
import com.alogic.blob.BlobInfo;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;

/**
 * 基于Java类路径资源的BlobManager
 * 
 * @author yyduan
 *
 */
public abstract class ResourceBlobManager extends BlobManager.Abstract{
	
	/**
	 * 从路径中匹配id的正则表达式
	 */
	protected static final Pattern idPattern = Pattern.compile("([^<>/\\\\|:\"\"\\*\\?]+)\\.\\w+$+");
	
	/**
	 * 路径
	 */
	protected String home = "";
	
	/**
	 * 资源引导类
	 */
	protected Class<?> bootstrap = getClass();	
	
	/**
	 * content type
	 */
	protected String contentType = "image/*";	
	
	/**
	 * 获取根路径
	 * @return home
	 */
	public String getHome(){return home;}
	
	/**
	 * 获取引导类
	 * @return 引导类
	 */
	public Class<?> getBootstrap(){return bootstrap;}
	
	/**
	 * 获取content-type
	 * @return content-type
	 */
	public String getContentType(){return contentType;}	
	
	@Override
	public BlobWriter newFile(Properties p) {
		throw new BaseException("core.e1000","This function is not suppurted yet.");	
	}

	@Override
	public boolean deleteFile(String id) {
		throw new BaseException("core.e1000","This function is not suppurted yet.");	
	}
	
	@Override
	public void report(Element xml) {
		if (xml != null){
			super.report(xml);
			XmlTools.setString(xml, "home", this.getHome());
			XmlTools.setString(xml, "bootstrap", this.getBootstrap().getName());
			XmlTools.setString(xml, "contentType", this.getContentType());
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			super.report(json);
			JsonTools.setString(json, "home", this.getHome());
			JsonTools.setString(json,"bootstrap",this.getBootstrap().getName());
			JsonTools.setString(json, "contentType", this.getContentType());
		}
	}	

	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		contentType = PropertiesConstants.getString(p,"contentType",contentType,true); // NOSONAR
		
		home = PropertiesConstants.getString(p,"home","/com/alogic/blob/icon/auto",true);
		
		String clazz = PropertiesConstants.getString(p,"bootstrap","",true); // NOSONAR
		if (clazz != null && clazz.length() > 0){
			ClassLoader cl = Settings.getClassLoader();
			
			try {
				bootstrap = cl.loadClass(clazz);
			}catch (Exception ex){
				LOG.error("Can not load class:" + clazz,ex);
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
				LOG.error("Can not scan home:" + pHome,e);
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
					LOG.error("Can not scan home:" + pHome,e);
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

	/**
	 * 扫描文件系统
	 * @param pHome 文件系统路径
	 * @param pFile 文件系统
	 */
	protected void scanFileSystem(String pHome,File pFile){
        File[] files = pFile.listFiles();
        for (File item:files){
        	if (item.getName().endsWith(".png") ||
        			item.getName().endsWith(".jpg")){
        		resourceFound(pHome + "/" + item.getName());
        	}
        }		
	}
	
	/**
	 * 发现资源
	 * @param path 路径
	 */
	protected void resourceFound(String path) {
		InputStream in = null;
		try {
			in = bootstrap.getResourceAsStream(path);
			if (in != null){
				Matcher m = idPattern.matcher(path);
				if (m.find()){
					String id = m.group(1);
					if (StringUtils.isNotEmpty(id)){
						resourceFound(id,new ResourceBlobInfo(id,path,getContentType()));
					}
				}
			}else{
				LOG.error("The resource is not valid :" + path);
			}
		}catch (Exception ex){
			LOG.error("Failed to build resource:" + id,ex);
		}finally{
			IOTools.close(in);
		}
	}
	
	/**
	 * 发现可用资源
	 * @param id 资源id
	 * @param info BlobInfo
	 */
	abstract protected void resourceFound(String id,ResourceBlobInfo info);
	
	/**
	 * BlobInfo
	 * @author yyduan
	 *
	 */
	public static class ResourceBlobInfo extends BlobInfo.Abstract{
		protected String path;
		public ResourceBlobInfo(String id,String path,String contentType) {
			super(id,contentType);
			this.path = path;
		}
		
		public String getPath(){
			return path;
		}
	}
	
	/**
	 * Reader
	 * @author yyduan
	 *
	 */
	public static class ResourceBlobReader implements BlobReader{

		/**
		 * blob info
		 */
		protected ResourceBlobInfo info;
		
		/**
		 * 引导类
		 */
		protected Class<?> bootstrap = getClass();
		
		public ResourceBlobReader(ResourceBlobInfo info,Class<?> bootstrap){
			this.info = info;
			this.bootstrap = bootstrap;
		}
		
		@Override
		public InputStream getInputStream(long offset) {
			String path = info.getPath();
			return bootstrap.getResourceAsStream(path);
		}

		@Override
		public void finishRead(InputStream in) {
			IOTools.close(in);
		}

		@Override
		public BlobInfo getBlobInfo() {
			return info;
		}
		
	}
}
