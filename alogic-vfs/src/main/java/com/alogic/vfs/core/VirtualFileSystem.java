package com.alogic.vfs.core;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 远程文件系统
 * 
 * @author duanyy

 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.13 [20170206 duanyy] <br>
 * - 写文件接口增加permissions参数，以便在创建文件时指定文件的权限 <br>
 * 
 * @version 1.6.9.3 [20170615 duanyy] <br>
 * - 增加move的方法 <br>
 */
public interface VirtualFileSystem extends AutoCloseable,Configurable,XMLConfigurable,Reportable{
	
	/**
	 * 获取ID
	 * @return id
	 */
	public String id();
	
	/**
	 * 列出指定路径下的文件列表(支持分页查询)
	 * 
	 * @param path 指定的路径
	 * @param offset 位置偏移
	 * @param limit 每次查询的文件个数
	 * @return 文件列表
	 */
	public List<String> listFiles(String path,int offset,int limit);
	
	/**
	 * 列出指定路径下的文件列表(支持分页查询)
	 * @param path 指定的路径
	 * @param pattern 文件名匹配模板
	 * @param offset 位置偏移
	 * @param limit 每次查询的文件个数
	 * @return 文件列表
	 */
	public List<String> listFiles(String path,String pattern,int offset,int limit);
	
	/**
	 * 列出指定路径下文件信息列表(支持分页查询)
	 * @param path 指定路径
	 * @param json 写出的JSON数据
	 * @param offset 位置偏移
	 * @param limit 每次查询的文件个数
	 */
	public void listFiles(String path,Map<String,Object> json,int offset,int limit);
	
	/**
	 * 列出指定路径下文件信息列表(支持分页查询)
	 * @param path 指定路径
	 * @param pattern 文件名匹配模板
	 * @param json 写出的JSON数据
	 * @param offset 位置偏移
	 * @param limit 每次查询的文件个数
	 */	
	public void listFiles(String path,String pattern,Map<String,Object> json,int offset,int limit);

	/**
	 * 删除指定路径的文件
	 * @param path 指定的文件路径
	 * @return 是否成功
	 */
	public boolean deleteFile(String path);
	
	/**
	 * 指定的路径是否存在
	 * @param path 指定的路径
	 * @return 如果该路径文件存在，返回true
	 */
	public boolean exist(String path);
	
	/**
	 * 移动文件到其他位置
	 * @param src 源文件路径
	 * @param dest 目的文件路径
	 * @param overwrite 是否覆盖
	 * @return 是否操作成功
	 */
	public boolean move(String src,String dest,boolean overwrite);
	
	/**
	 * 指定的路径是否目录
	 * @param path 指定的路径
	 * @return 如果该路径存在，返回true
	 */
	public boolean isDir(String path);
	
	/**
	 * 获取指定文件的大小
	 * @param path 文件路径
	 * @return 文件大小
	 */
	public long getFileSize(String path);	
	
	/**
	 * 获取指定的文件信息
	 * @param path 指定文件的路径
	 * @param json 文件信息输出
	 */
	public void getFileInfo(String path,Map<String,Object> json);
	
	/**
	 * 创建目录
	 * @param path 目录的路径
	 * @return 是否成功
	 */
	public boolean makeDirs(String path);		
	
	/**
	 * 读取文件
	 * @param path 文件的路径
	 * @return 文件的输入流，如果文件不存在或无权限，返回为空
	 */
	public InputStream readFile(String path);
	
	/**
	 * 完成读取文件
	 * @param path 文件的路径
	 * @param in 文件的输入流
	 */
	public void finishRead(String path,InputStream in);
	
	/**
	 * 写入文件
	 * @param path 文件的路径
	 * @return 文件的输出流，如果不允许创建新的文件 ，返回为空
	 */
	public OutputStream writeFile(String path);
	
	/**
	 * 写入文件
	 * @param path 文件的路径
	 * @param permissions 文件权限
	 * @return 文件的输出流，如果不允许创建新的文件 ，返回为空
	 */
	public OutputStream writeFile(String path,int permissions);	
	
	/**
	 * 完成文件写入
	 * @param path 文件的路径
	 * @param out 文件的输出流
	 */
	public void finishWrite(String path,OutputStream out);
	
	/**
	 * 虚拟实现
	 * @author duanyy
	 *
	 */
	public abstract static class Abstract implements VirtualFileSystem{
		/**
		 * a logger of log4j
		 */
		protected final Logger LOG = LoggerFactory.getLogger(VirtualFileSystem.class);
		
		/**
		 * 缺省的文件名模板
		 */
		protected String dftPattern = "[\\S]*";
	
		/**
		 * 文件系统的根目录的实际路径
		 */
		protected String root = "";

		protected String id = "";
		
		public String id(){
			return id;
		}
		
		protected String getRealPath(String path){
			return root + File.separatorChar + path;
		}		
		
		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p,"id","default",true);
			dftPattern = PropertiesConstants.getString(p,"dftPattern", dftPattern);
			root = PropertiesConstants.getString(p,"root", root);			
		}		
		
		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
		@Override
		public List<String> listFiles(String path,  int offset,
				int limit) {
			return listFiles(path,dftPattern,offset,limit);
		}

		@Override
		public void listFiles(String path,
				Map<String, Object> json, int offset, int limit) {
			listFiles(path,dftPattern,json,offset,limit);
		}	
		
		@Override
		public OutputStream writeFile(String path, int permissions) {
			return writeFile(path);
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("id", id());
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("dftPattern", dftPattern);
				xml.setAttribute("root", root);
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("id", id());
				json.put("module", getClass().getName());
				json.put("dftPattern", dftPattern);
				json.put("root", root);
			}
		}		
	}
}
