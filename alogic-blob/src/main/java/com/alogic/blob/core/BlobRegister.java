package com.alogic.blob.core;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.logicbus.backend.ServantException;

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
	abstract public static class Abstract implements BlobRegister{

		@Override
		public void configure(Element _e, Properties _properties)
				throws BaseException {
			XmlElementProperties p = new XmlElementProperties(_e,_properties);
			configure(p);			
		}

		@Override
		public void configure(Properties p) throws BaseException {
			
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
