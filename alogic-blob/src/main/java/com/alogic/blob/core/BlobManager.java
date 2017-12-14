package com.alogic.blob.core;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * Blob管理器
 * 
 * @author duanyy
 * @since 1.6.3.28
 * @version 1.6.3.32 [duanyy 20150720] <br>
 * - 增加md5,content-type等信息 <br>
 * 
 * @version 1.6.4.7 [duanyy 20150916] <br>
 * - 增加文件扫描接口 <br>
 * - 增加虚基类实现 <br>
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public interface BlobManager extends XMLConfigurable,Configurable,Reportable{
	/**
	 * 获取id 
	 * @return id
	 */
	public String getId();
	/**
	 * 新建Blob文件
	 * @param contentType 文件的content-type
	 * @return BlobWriter实例
	 * 
	 */
	public BlobWriter newFile(String contentType);
	
	/**
	 * 查找已存在的Blob文件
	 * 
	 * @param id 文件ID
	 * @return BlobReader实例
	 */
	public BlobReader getFile(String id);
	
	/**
	 * Blob文件是否存在
	 * @param id 文件ID
	 * @return true|false
	 */
	public boolean existFile(String id);
	
	/**
	 * 删除文件
	 * @param id 文件id
	 */
	public boolean deleteFile(String id);
	
	/**
	 * 提交文件
	 * @param writer BlobWriter
	 */
	public void commit(BlobWriter writer);
	
	/**
	 * 取消文件注册
	 * @param writer BlobWriter
	 */
	public void cancel(BlobWriter writer);
	
	/**
	 * 扫描文件注册表（可能有的实现不支持）
	 * 
	 * @param ids 用来存储文件id的容器
	 * @param cookies 扫描上下文
	 * @param limit 限制记录数
	 * @return cookies 扫描上下文
	 */
	public String list(List<String> ids,String cookies,int limit);
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 * @since 1.6.4.7
	 */
	 public abstract static class Abstract implements BlobManager{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LoggerFactory.getLogger(BlobManager.class);
		
		/**
		 * id
		 */
		protected String id;
		
		@Override
		public String getId(){
			return id;
		}
		
		@Override
		public void configure(Element e, Properties props){
			XmlElementProperties p = new XmlElementProperties(e,props);
			configure(p);
		}
		
		@Override
		public void configure(Properties p){
			id = PropertiesConstants.getString(p,"id",id);
		}
	
		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
				xml.setAttribute("id", getId());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
				json.put("id", getId());
			}
		}
		
		@Override
		public String list(List<String> ids, String cookies,int limit) {
			throw new BaseException("core.e1000",
					"This function is not suppurted yet.");	
		}
	}
}
