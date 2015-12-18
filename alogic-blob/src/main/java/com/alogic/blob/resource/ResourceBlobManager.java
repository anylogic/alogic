package com.alogic.blob.resource;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 * 基于ClassPath的BlobManager
 * 
 * @author duanyy
 * @since 1.6.4.7
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 * 
 */
public class ResourceBlobManager extends BlobManager.Abstract{
	
	/**
	 * blobs
	 */
	private Map<String,ResourceBlobInfo> blobs = new ConcurrentHashMap<String,ResourceBlobInfo>(); // NOSONAR
	
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
		
		home = PropertiesConstants.getString(p,"home","",true);
		
		String clazz = PropertiesConstants.getString(p,"bootstrap","",true); // NOSONAR
		if (clazz != null && clazz.length() > 0){
			ClassLoader cl = Settings.getClassLoader();
			
			try {
				bootstrap = cl.loadClass(clazz);
			}catch (Exception ex){
				logger.error("Can not load class:" + clazz,ex);
			}
		}
	}
	
	protected void resourceFound(String h,String id) {
		InputStream in = null;
		String path = h + "/" + id;
		try {
			in = bootstrap.getResourceAsStream(path);
			if (in != null){
				String md5 = DigestUtils.md5Hex(in);
				ResourceBlobInfo info = new ResourceBlobInfo(id,contentType,md5,0,path);
				blobs.put(id, info);
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
	public BlobWriter newFile(String contentType) {
		throw new BaseException("core.not_supported","This function is not suppurted yet."); // NOSONAR	
	}

	@Override
	public BlobReader getFile(String id) {
		ResourceBlobInfo found = blobs.get(id);
		return found == null ? null : new ResourceBlobReader(found,bootstrap);
	}

	@Override
	public boolean existFile(String id) {
		return blobs.containsKey(id);
	}

	@Override
	public boolean deleteFile(String id) {
		throw new BaseException("core.not_supported",
				"This function is not suppurted yet.");	
	}

	@Override
	public void commit(BlobWriter writer) {
		throw new BaseException("core.not_supported",
				"This function is not suppurted yet.");	
	}

	@Override
	public void cancel(BlobWriter writer) {
		throw new BaseException("core.not_supported",
				"This function is not suppurted yet.");	
	}

	@Override
	public String list(List<String> ids, String cookies,int limit) {
		Iterator<String> keys = blobs.keySet().iterator();
		
		while (keys.hasNext()){
			ids.add(keys.next());
		}
		
		return cookies;
	}
}
