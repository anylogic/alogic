package com.logicbus.backend;

import javax.servlet.http.HttpServletRequest;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.models.catalog.Path;

/**
 * 基于app的代理地址的标准化
 * <br>
 * 负责将代理地址标准化，代理请求定向到固定的代理服务上。
 * <br>
 * 代理地址的语法为：
 * <br>
 * http://<ip>[:<port>]/proxy/<app>/<服务名>?<服务参数>
 * 
 * @author duanyy
 *
 * @since 1.2.7.2
 *  
 * @version 1.2.8.2 [20141010 duanyy]
 * - 修正app取值中的/问题
 * 
 */
public class AppProxyNormalizer implements Normalizer {

	protected String proxyServiceId = "/core/AppProxy";
	
	public AppProxyNormalizer(Properties p){
		proxyServiceId = PropertiesConstants.getString(p, "normalizer.appproxy.id", proxyServiceId);
	}
	
	
	public Path normalize(Context ctx, HttpServletRequest request) {
		String path = request.getPathInfo();
		String queryString = request.getQueryString();
		String app = null;
		String svc = null;
		
		if (path != null && path.length() > 0){
			int start = findStart(path);
			int pos = findPos(start,path);
			app = path.substring(start,pos);
			svc = path.substring(pos);
		}
	
		if (app != null && app.length() > 0){
			ctx.SetValue("app", app);
		}
		if (svc != null && svc.length() > 0){
			ctx.SetValue("service", svc);
		}
		if (queryString != null && queryString.length() > 0 ){
			ctx.SetValue("query", queryString);
		}
		
		return new Path(proxyServiceId);
	}

	static private int findPos(int start,String path){
		int length = path.length();
		int found = -1;
		boolean inSlash = true;
		for (int i = start ; i < length ; i ++){
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

	static private int findStart(String path){
		int length = path.length();
		int found = 0;
		for ( ; found < length ; found++){
			if (path.charAt(found) != '/'){
				return found;
			}
		}
		return found;
	}
}
