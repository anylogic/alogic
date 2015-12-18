package com.alogic.blob.core;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;


/**
 * 文件注册器
 * 
 * <p>文件注册器用于保存文件的元数据信息，如md5码，content-type等
 * 
 * @author duanyy
 * @since 1.6.3.32 
 * @version 1.6.4.7 [duanyy 20150916] <br>
 * - 增加文件扫描接口 <br>
 * - 增加虚基类实现 <br>
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */
public interface BlobRegister extends XMLConfigurable,Configurable,Reportable{
	/**
	 * 查找Blob文件信息
	 * @param id 文件id
	 * @return BlobInfo
	 */
	public BlobInfo find(String id);
	
	/**
	 * 注册Blob文件信息
	 * @param info BlobInfo
	 */
	public void add(BlobInfo info);
	
	/**
	 * 删除文件信息
	 * @param id 文件id
	 */
	public void delete(String id);
	
	/**
	 * 扫描文件注册表（可能有的实现不支持）
	 * 
	 * @param ids 用来存储文件id的容器
	 * @param cookies 扫描上下文
	 * @return cookies 扫描上下文
	 */
	public String list(List<String> ids,String cookies,int limit);
	
	/**
	 * 虚基类
	 * 
	 * @author duanyy
	 *
	 */
	 public abstract static class Abstract implements BlobRegister{
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LogManager.getLogger(BlobRegister.class);
		
		@Override
		public void configure(Element e, Properties props){
			XmlElementProperties p = new XmlElementProperties(e,props);
			configure(p);			
		}

		@Override
		public void configure(Properties p){
			// nothing to do
		}		
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				xml.setAttribute("module", getClass().getName());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				json.put("module", getClass().getName());
			}
		}

		@Override
		public String list(List<String> ids, String cookies,int limit) {
			throw new BaseException("core.not_supported",
					"This function is not suppurted yet.");	
		}
	}
}
