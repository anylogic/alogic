package com.logicbus.backend.server.http;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.anysoft.util.Settings;
import com.anysoft.webloader.ServletHandler;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.DefaultNormalizer;
import com.logicbus.backend.Normalizer;
import com.logicbus.backend.server.MessageRouter;
import com.logicbus.models.catalog.Path;

/**
 * 基于anyWebLoader的ServletHandler
 * 
 * @author duanyy
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 * 
 * @version 1.0.7 [20140418 duanyy] <br>
 * - 增加全局序列号功能,从Http头的GlobalSerial变量获取前端传入的全局序列号
 * 
 * @version 1.2.1 [20140614 duanyy] <br>
 * - 支持跨域服务调用
 * 
 * @version 1.2.6 [20140807 duanyy] <br>
 * - ServantPool和ServantFactory插件化
 * 
 * @version 1.2.7.2 [20140910 duanyy] <br>
 * - Normalizer降级为Servlet级别对象
 * 
 * @version 1.3.0.1 [20141031 duanyy] <br>
 * - 解决问题：框架截获了post方式的body数据，导致post过来的form数据无法获取
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 * 
 * @version 1.6.1.2 [20141118 duanyy] <br>
 * - 支持HttpContext的数据截取,通过Servlet的变量intercept.mode来控制 <br>
 */
public class MessageRouterServletHandler implements ServletHandler {
	/**
	 * 访问控制器
	 */
	protected AccessController ac = null;
	
	/**
	 * 路径标准化
	 */
	protected Normalizer normalizer = null;
	
	/**
	 * 是否开启拦截模式
	 */
	protected boolean interceptMode = false;
	
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LogManager.getLogger(MessageRouterServletHandler.class);
	
	/**
	 * 是否已经获取服务器信息
	 */
	protected static boolean getServerInfo = false;
	
	/**
	 * 编码
	 */
	protected static String encoding = "utf-8";
	
	/**
	 * Access-Control-Allow-Origin
	 */
	protected static String defaultAllowOrigin = "*";
	
	public void init(ServletConfig servletConfig) throws ServletException {
		Settings settings = Settings.get();
		encoding = settings.GetValue("http.encoding", encoding);
		defaultAllowOrigin = settings.GetValue("http.alloworigin",
				defaultAllowOrigin);
		ac = (AccessController) settings.get("accessController");

		String normalizerClass = servletConfig.getInitParameter("normalizer");
		normalizerClass = normalizerClass == null
				|| normalizerClass.length() <= 0 ? "com.logicbus.backend.DefaultNormalizer"
				: normalizerClass;

		logger.info("Normalizer is initializing,module:" + normalizerClass);
		try {
			Normalizer.TheFactory ncf = new Normalizer.TheFactory(
					Settings.getClassLoader());
			normalizer = ncf.newInstance(normalizerClass,settings);
		} catch (Throwable t) {
			normalizer = new DefaultNormalizer(settings);
			logger.error("Failed to initialize Normalizer.Using default:"
					+ DefaultNormalizer.class.getName());
		}
		{
			String _interceptMode = servletConfig.getInitParameter("intercept.mode");
			
			if (_interceptMode != null && _interceptMode.equals("true")){
				interceptMode = true;
			}
		}
	}

	
	public void doService(HttpServletRequest request,
			HttpServletResponse response, String method)
			throws ServletException, IOException {
		if (!getServerInfo){
			Settings settings = Settings.get();
			settings.SetValue("server.host", request.getLocalAddr());
			settings.SetValue("server.port", String.valueOf(request.getLocalPort()));
			logger.info("Get server info:" + settings.GetValue("server.host", "") + ":" + settings.GetValue("server.port",""));
			getServerInfo = true;
		}
		
		response.setHeader("Expires", "Mon, 26 Jul 1970 05:00:00 GMT");
		response.setHeader("Last-Modified", "Mon, 26 Jul 1970 05:00:00 GMT");
		response.setHeader("Cache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		
		// 1.2.1 duanyy
		// to support CORS
		String origin = request.getHeader("Origin");
		response.setHeader("Access-Control-Allow-Origin", origin == null || origin.length() <= 0 ? defaultAllowOrigin : origin);
		
		HttpContext ctx = new HttpContext(request,response,encoding,interceptMode);
		try{
			//规范化ID
			Path id = normalizer.normalize(ctx, request);
			MessageRouter.action(id,ctx,ac);
		}catch (Exception ex){
			ex.printStackTrace();
			if (ctx != null){
				ctx.setReturn("core.fatalerror",ex.getMessage());
				logger.error("core.fatalerror:" + ex.getMessage());
			}
		}	
		finally {
			if (ctx != null){
				ctx.finish();
			}
		}
	}

	public void destroy() {

	}

}
