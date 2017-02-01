package com.anysoft.webloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;


/**
 * Web库文件更新器
 * @author duanyy
 * @version 1.2.1 [20140709 duanyy] <br>
 * - 修正localLibHome设置不当造成的listFiles返回空指针问题 <br>
 * 
 * @version 1.6.2.1 [20141231 duanyy] <br>
 * - 设置缺省ClassLoader为Thread.currentThread().getContextClassLoader()
 * 
 * @version 1.6.3.7 [20150319 duanyy] <br>
 * - 缺省的ClassLoader可以进行配置 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class WebUpdater {
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LoggerFactory.getLogger(WebUpdater.class);	
	/**
	 * 是否自动检查并更新
	 */
	protected boolean autoUpdate = false;
	
	/**
	 * 本地的库文件目录
	 */
	protected String localLibHome = "${local.home}" + File.separator + "libs";
	
	/**
	 * 检查更新服务URL
	 */
	protected String masterURL = "${master.home}/update/lib.xml";
	
	/**
	 * 检查更新服务备用URL
	 */
	protected String secondaryURL = "${secondary.home}/update/lib.xml";
	
	/**
	 * 是否取线程的ClassLoader
	 * @since 1.6.3.7
	 */
	protected boolean threadClassLoader = true;
	
	/**
	 * 库文件信息
	 * @author duanyy
	 *
	 */
	public static class LibraryInfo {
		/**
		 * 文件名
		 */
		public String name;
		
		/**
		 * 本地完整路径
		 */
		public String localPath;
		
		/**
		 * 远程URL
		 */
		public String remoteURL;
		
		/**
		 * MD5摘要信息
		 */
		public String md5;
	}
	
	/**
	 * 库文件列表
	 */
	protected Vector<LibraryInfo> libs = new Vector<LibraryInfo>();
	
	/**
	 * 构造函数
	 * @param props 变量集
	 */
	public WebUpdater(Properties props){
		autoUpdate = PropertiesConstants.getBoolean(props, "updater.auto", autoUpdate);
		localLibHome = PropertiesConstants.getString(props, "updater.home",localLibHome);
		masterURL = PropertiesConstants.getString(props, "updater.metadata.master", masterURL);
		secondaryURL = PropertiesConstants.getString(props, "updater.metadata.secondary", secondaryURL);
		threadClassLoader = PropertiesConstants.getBoolean(props, "updater.threadClassLoader", threadClassLoader);
	}
	
	/**
	 * 检查并更新
	 */
	public void update(){
		if (autoUpdate){
			logger.info("Begin to update libary...");
			Document doc = getRemoteLibraryInfo(masterURL,secondaryURL);
			if (doc != null){
				Element root = doc.getDocumentElement();
				NodeList eModules = XmlTools.getNodeListByPath(root, "module");
				
				//清空libs
				libs.clear();
				for (int i = 0 ; i < eModules.getLength() ; i ++){
					Node n = eModules.item(i);
					if (n.getNodeType() != Node.ELEMENT_NODE){
						continue;
					}
					
					Element e = (Element)n;
					String jar = e.getAttribute("jar");
					String url = e.getAttribute("url");
					String md5 = e.getAttribute("md5");
					
					String localMd5 = getLocalLibraryInfo(localLibHome + File.separator + jar);
					if (localMd5 == null || localMd5.length() <= 0 || !localMd5.equals(md5)){
						//如果本地文件不存在，或者需要更新
						if (!downloadLibrary(localLibHome + File.separator + jar,url,md5)){
							//下载失败,采用老的MD5
							md5 = localMd5;
						}						
					}
					
					if (md5 != null){
						LibraryInfo info = new LibraryInfo();
						info.name = jar;
						info.localPath = localLibHome + File.separator + jar;
						info.remoteURL = url;
						info.md5 = md5;
						libs.add(info);	
					}
				}
				
				writeLibInfo();
			}else{
				logger.info("Can not get libary info,updating stopped.");
				//更新失败，将不做改动
			}
		}else{
			//如果不自动更新，只取本地文件
			File libFile = new File(localLibHome + File.separator + "lib.xml");
			if (libFile.exists()){
				//如果lib.xml文件存在
				//直接取
				Document doc;
				try {
					doc = XmlTools.loadFromFile(libFile);
					Element root = doc.getDocumentElement();
					NodeList eModules = XmlTools.getNodeListByPath(root, "module");
					
					libs.clear();
					for (int i = 0 ; i < eModules.getLength() ; i ++){
						Node n = eModules.item(i);
						if (n.getNodeType() != Node.ELEMENT_NODE){
							continue;
						}
						Element e = (Element)n;
						String jar = e.getAttribute("jar");
						String md5 = e.getAttribute("md5");
						String url = e.getAttribute("url");
						
						LibraryInfo lib = new LibraryInfo();
						lib.name = jar;
						lib.md5 = md5;
						lib.localPath = localLibHome + File.separator + jar;
						lib.remoteURL = url;
						
						libs.add(lib);
					}
				} catch (Exception e) {
					logger.error("Error occurs when reading lib.xml.", e);
				}
			}else{
				//取本地目录所有的jar文件
				File dir = new File(localLibHome);
				if (!dir.isDirectory()) {
					dir.mkdirs();
				}

				File[] jars = dir.listFiles(new FilenameFilter() {
					
					public boolean accept(File file, String name) {
						if (name.endsWith(".jar"))
							return true;
						return false;
					}
				});
				
				if (jars != null){
					libs.clear();
					
					for (File file:jars){
						LibraryInfo lib = new LibraryInfo();
						lib.name = file.getName();
						lib.md5 = getLocalLibraryInfo(file.getAbsolutePath());
						lib.localPath = localLibHome + File.separator + lib.name;
						lib.remoteURL = "";
						libs.add(lib);
					}
					
					writeLibInfo();
				}else{
					logger.error("Can not find jar files in dir:" + localLibHome + ",or the dir does not exist.");
				}
			}
		}
	}
	
	/**
	 * 将当前的库文件信息写入到本地
	 */
	protected void writeLibInfo() {
		File libFile = new File(localLibHome + File.separator + "lib.xml");
		if (libFile.exists()) {
			libFile.delete();
		}
		try {
			Document doc = XmlTools.newDocument("root");
			Element root = doc.getDocumentElement();
		
			for (LibraryInfo lib:libs){
				Element eModule = doc.createElement("module");
				eModule.setAttribute("jar", lib.name);
				eModule.setAttribute("url", lib.remoteURL);
				eModule.setAttribute("md5", lib.md5);
				root.appendChild(eModule);
			}
			
			XmlTools.saveToOutputStream(doc, new FileOutputStream(localLibHome + File.separator + "lib.xml"));
		} catch (Exception e) {
			logger.error("Error occurs when writing library", e);
		}
	}

	/**
	 * 下载库文件
	 * @param localFile 本地地址
	 * @param remoteURL 远程URL
	 * @param md5 MD5校验码
	 * @return 如果下载成功，返回true,反之false
	 */
	private boolean downloadLibrary(String localFile, String remoteURL, String md5) {
		InputStream in = null;
		FileOutputStream out = null;
		try {
			logger.info("Download library file:" + remoteURL);
			URL url = new URL(remoteURL);
			in = url.openStream();
			
			//我们先下载到一个临时文件中
			File tmpFile = new File(localFile + ".tmp");
			out = new FileOutputStream(tmpFile);
			
			byte[] buffer = new byte[1024];
			int byteread = 0;
			int bytesum = 0;
			while ((byteread = in.read(buffer)) != -1) {
				bytesum += byteread;
				out.write(buffer, 0, byteread);
			}
			in.close();
			out.close();
			
			//验证所下载的文件
			in = new FileInputStream(tmpFile);
			String tmpMd5 = DigestUtils.md5Hex(in);
			in.close();
			
			logger.info("File size is " + bytesum + " bytes");
			logger.info("MD5 is" + tmpMd5);
			if (md5.equals(tmpMd5)){
				//md5校验通过
				if (!tmpFile.renameTo(new File(localFile))){
					//听说File.renameTo不靠谱，如果失败，只好自己COPY
					copyFile(tmpFile,new File(localFile));
					//File.delete也不靠谱
					tmpFile.delete();
				}
				logger.info("Download library file, OK");
				return true;
			}else{
				//听说File.delete不靠谱，可能删除不掉
				tmpFile.delete();
				logger.info("Failed to check md5.");
				return false;
			}

		} catch (Exception e) {
			logger.error("Error occurs when downloading library file " + remoteURL,e);
			return false;
		}finally {
			IOTools.closeStream(in);
			IOTools.closeStream(out);
		}
	}

	/**
	 * 复制文件
	 * @param src 源文件
	 * @param dest 目标文件
	 * @return 是否成功
	 */
	protected static boolean copyFile(File src,File dest){
		if (dest.exists()){
			dest.delete();
		}
		FileOutputStream out = null;
		FileInputStream in = null;
		try {
			out = new FileOutputStream(dest);
			in = new FileInputStream(src);
		
			byte[] buffer = new byte[1204];
			int byteread = 0;
			while ((byteread = in.read(buffer)) != -1) {
				out.write(buffer, 0, byteread);
			}
			out.close();
			return true;
		}catch (Exception e){
			logger.error("Error occurs", e);
			return false;
		}finally{
			IOTools.closeStream(in);
			IOTools.closeStream(out);			
		}
	}
	
	/**
	 * 获取本地文件的MD5编码
	 * <br>
	 * 如果文件存在，则返回为null;如果存在，获取该文件的MD5摘要。
	 * 
	 * @param path 文件路径
	 * @return MD5编码
	 */
	public String getLocalLibraryInfo(String path){
		File file = new File(path);
		if (!file.exists() || !file.isFile()){
			//当文件不存在
			return null;
		}
		
		FileInputStream in = null;
		
		try {
			in = new FileInputStream(file);
			return DigestUtils.md5Hex(in);
		}catch (Exception ex){
			logger.error("Error occurs when reading file:" + path, ex);
			return null;
		}finally {
			IOTools.closeStream(in);
		}
	}
	
	/**
	 * 获取远程库文件信息
	 * @param master 更新检查服务URL
	 * @param secondary 更新检查服务备用URL
	 * @return 包含远程库文件的XML文档
	 */
	protected Document getRemoteLibraryInfo(String master, String secondary) {
		ResourceFactory rf = new ResourceFactory();
		InputStream in = null;
		try {
			in = rf.load(master,secondary, null);
			return XmlTools.loadFromInputStream(in);
		}catch (Exception ex){
			logger.error("Can not get remote libary info",ex);
			return null;
		}
		finally{
			IOTools.closeStream(in);
		}
	}

	/**
	 * 获取本地库文件的ClassLoader
	 * @return ClassLoader
	 */
	public ClassLoader getLibClassLoader(){
		URL [] urls = new URL[libs.size()];
		
		for (int i = 0 ; i < libs.size() ; i ++){
			String url = libs.get(i).localPath;
			File file = new File(url);
			try {
				urls[i] = file.toURI().toURL();
			} catch (Exception e) {
				logger.error("Error occurs", e);
			}			
		}
		
		ClassLoader cl = threadClassLoader ? Thread.currentThread().getContextClassLoader() : WebUpdater.class.getClassLoader();
		if (urls.length <= 0){
			return cl;
		}
		return new URLClassLoader(urls,cl);
	}
}
