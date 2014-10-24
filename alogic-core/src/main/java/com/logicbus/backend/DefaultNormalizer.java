package com.logicbus.backend;

import javax.servlet.http.HttpServletRequest;

import com.anysoft.util.Properties;
import com.logicbus.models.catalog.Path;


/**
 * 缺省实现的Normalizer
 * 
 * @author duanyy
 * 
 * @version 1.2.7.2 [20140910 duanyy] <br>
 * - 增加基于Properties的构造函数
 */
public class DefaultNormalizer implements Normalizer {

	public DefaultNormalizer(Properties settings) {

	}

	
	public Path normalize(Context ctx, HttpServletRequest request) {
		String path = request.getPathInfo();
		String queryString = request.getQueryString();
		
		
		String id;
		
		if (path == null || path.equals("/")){
			//查询所有服务信息
			id = "/core/ServiceQuery";
		}
		else
		{
			int start = path.indexOf("/");
			id = path.substring(start);
			if (queryString != null && queryString.equals("wsdl")){
				ctx.SetValue("service", id);
				id = "/core/ServiceDetailQuery";
			}else{
				if (queryString != null && queryString.equals("reload")){
					ctx.SetValue("service", id);
					id = "/core/ServiceReload";						
				}else{
					if (queryString != null && queryString.equals("resume")){
						ctx.SetValue("service", id);
						id = "/core/ServiceResume";
					}else{
						if (queryString != null && queryString.equals("pause")){
							ctx.SetValue("service", id);
							id = "/core/ServicePause";
						}
					}
				}
			}
		}
		
		return createPath(id,ctx,request);
	}

	/**
	 * 根据预生成的ID来生成路径
	 * 
	 * <br>
	 * 可在子类实现中更改所生成的路径，例如将某个目录的服务都重定向到某个服务，在将原路径设置为参数。<br>
	 * 
	 * {@code
	 * protected Path createPath(String id,Context ctx,HttpServletRequest request){
	 *       if (id.startsWith("/project")){
	 *       	ctx.setValue("path",id);
	 *       	return new Path("<another id>");
	 *       }else{
	 *       	return new Path(id);
	 *       }
	 * }
	 * }
	 * 
	 * @param id 服务ID
	 * @param ctx 上下文
	 * @param request Http请求
	 * @return 转换后的路径
	 */
	protected Path createPath(String id,Context ctx, HttpServletRequest request){
		return new Path(id);
	}
}
