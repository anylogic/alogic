package com.alogic.blob.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.blob.core.BlobManager;
import com.alogic.blob.core.BlobReader;
import com.alogic.blob.core.BlobWriter;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 基于ClassPath的BlobManager
 * 
 * @author duanyy
 * @since 1.6.4.7
 */
public class ResourceBlobManager extends BlobManager.Abstract{
	
	/**
	 * blobs
	 */
	private Hashtable<String,ResourceBlobInfo> blobs = new Hashtable<String,ResourceBlobInfo>();
	
	/**
	 * 路径
	 */
	private String home = "";
	
	public String getHome(){return home;}
	
	/**
	 * 资源引导类
	 */
	private Class<?> bootstrap = getClass();
	
	public Class<?> getBootstrap(){return bootstrap;}
	
	/**
	 * content type
	 */
	private String contentType = "image/*";
	
	public String getContentType(){return contentType;}
	
	@Override
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		configure(p);
		
		NodeList nodeList = XmlTools.getNodeListByPath(_e, "file");
		
		if (nodeList.getLength() > 0){
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				String fileId = e.getAttribute("id");
				if (fileId != null && fileId.length() > 0){
					resourceFound(home,fileId);
				}
			}
		}
	}
	
	@Override
	public void configure(Properties p) throws BaseException {
		super.configure(p);
		
		contentType = PropertiesConstants.getString(p,"contentType",contentType,true);
		
		home = PropertiesConstants.getString(p,"home","",true);
		
		String clazz = PropertiesConstants.getString(p,"bootstrap","",true);
		if (clazz != null){
			ClassLoader cl = Settings.getClassLoader();
			
			try {
				bootstrap = cl.loadClass(clazz);
			}catch (Exception ex){
				
			}
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
			IOTools.close();
		}
	}

	@Override
	public BlobWriter newFile(String contentType) {
		throw new BaseException("core.not_supported",
				"This function is not suppurted yet.");	
	}

	@Override
	public BlobReader getFile(String id) {
		ResourceBlobInfo found = blobs.get(id);
		return found == null ? null : new ResourceBlobReader(found,bootstrap);
	}

	@Override
	public boolean existFile(String id) {
		return blobs.contains(id);
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
		Enumeration<String> keys = blobs.keys();
		
		while (keys.hasMoreElements()){
			ids.add(keys.nextElement());
		}
		
		return cookies;
	}
}
