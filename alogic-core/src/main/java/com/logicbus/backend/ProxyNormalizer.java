package com.logicbus.backend;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.models.catalog.Path;

/**
 * 代理地址的标准化
 * <br>
 * 负责将代理地址标准化，代理请求定向到固定的代理服务上。
 * <br>
 * 代理地址的语法为：
 * <br>
 * http://<ip>[:<port>]/proxy/<实际服务器IP>[:<实际服务器端口>]/<服务名>?<服务参数>
 * 
 * @author duanyy
 *
 * @since 1.2.7.2
 * 
 */
public class ProxyNormalizer implements Normalizer {

	protected String proxyServiceId = "/core/Proxy";
	
	protected Logger logger = LogManager.getLogger(ProxyNormalizer.class);
	
	public ProxyNormalizer(Properties p){
		proxyServiceId = PropertiesConstants.getString(p, "normalizer.proxy.id", proxyServiceId);
	}
	
	
	public Path normalize(Context ctx, HttpServletRequest request) {
		String path = request.getPathInfo();
		String queryString = request.getQueryString();
		String host = null;
		String svc = null;
		
		if (path != null && path.length() > 0){
			int pos = findPos(path);
			host = path.substring(0,pos);
			host = trimSlash(host);
			svc = path.substring(pos);
		}

		if (host != null && host.length() > 0){
			ctx.SetValue("host", host);
		}
		if (svc != null && svc.length() > 0){
			ctx.SetValue("service", svc);
		}
		if (queryString != null && queryString.length() > 0 ){
			ctx.SetValue("query", queryString);
		}
		
		return new Path(proxyServiceId);
	}

	static private String trimSlash(String str){
		int length = str.length();
		int start = 0;
		
		for (int i = 0 ;i < length ; i ++){
			if (str.charAt(i) != '/'){
				start = i;
				break;
			}
		}
		
		int end = length - 1;
		for (int i = length - 1; i >= 0 ; i --){
			if(str.charAt(i) != '/'){
				end = i;
				break;
			}
		}
		
		if (end > start){
			return str.substring(start,end + 1);
		}else{
			return "";
		}
	}
	
	static private int findPos(String path){
		int length = path.length();
		int found = -1;
		boolean inSlash = true;
		for (int i = 0 ; i < length ; i ++){
			if (inSlash){
				if (path.charAt(i) != '/'){
					inSlash = false;
				}
			}else{
				if (path.charAt(i) == '/'){
					found = i;
					break;
				}
			}
		}
		if (found < 0){
			found = path.length();
		}
		return found;
	}
	
}
