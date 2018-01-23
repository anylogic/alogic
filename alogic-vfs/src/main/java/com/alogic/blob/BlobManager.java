package com.alogic.blob;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * Blob管理器
 * @author yyduan
 *
 */
public interface BlobManager extends XMLConfigurable,Configurable,Reportable{
	
	/**
	 * 获取id
	 * @return id
	 */
	public String getId();
	
	/**
	 * 新建Blob文件
	 * @param p 参数集
	 * @return BlobWriter实例
	 * 
	 */
	public BlobWriter newFile(Properties p);
	
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
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements BlobManager{
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(BlobManager.class);
		
		/**
		 * id
		 */
		protected String id;
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}
		
		@Override
		public void configure(Properties p){
			id = PropertiesConstants.getString(p,"id","",true);
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml, "module", getClass().getName());
				XmlTools.setString(xml,"id",getId());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
				JsonTools.setString(json, "id", getId());
			}
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String list(List<String> ids, String cookies, int limit) {
			throw new BaseException("core.e1000","This function is not suppurted yet.");	
		}
		
	}
}
