package com.anysoft.webloader;

import org.apache.commons.lang3.StringUtils;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.code.Coder;
import com.anysoft.util.code.CoderFactory;

/**
 * 共享URL工具
 * 
 * @since 1.6.11.37 
 * 
 * @version 1.6.11.44 [20180713 duanyy] <br>
 * - 缺省的urlBase修改为${webcontext.vroot}/share/ <br>
 */
public class ShareTool {
	/**
	 * 用来加密路径的key
	 */
	protected String key = "alogic";
	
	/**
	 * URL的基础路径
	 */
	protected String urlBase = "${webcontext.vroot}/share/";
	
	/**
	 * 加解密工具
	 */
	protected Coder coder = null;	
	
	protected Properties settings;
	
	public ShareTool(Properties p){
		settings = p;
		key = PropertiesConstants.getString(p,"share.key","alogic");
		urlBase = PropertiesConstants.getString(p,"share.base",urlBase);
		coder = CoderFactory.newCoder(PropertiesConstants.getString(p,"share.coder","DES3"));			
	}
	
	/**
	 * 对url路径编码
	 * @param path url路径
	 * @return 编码后的路径
	 */
	public String encodePath(String path){
		return urlBase + coder.encode(path, key);
	}
	
	public String encodePath(String patternKey,Object... vals){
		String pattern = PropertiesConstants.getString(settings,patternKey,"");
		if (StringUtils.isNotEmpty(pattern)){
			return urlBase + coder.encode(String.format(pattern, vals), key);
		}else{
			return urlBase;
		}
	}	
	
	/**
	 * 对url路径解码
	 * @param encodedPath 编码的路径
	 * @return 解码后的路径
	 */
	public String decodePath(String encodedPath){
		return coder.decode(encodedPath,key);
	}
}
