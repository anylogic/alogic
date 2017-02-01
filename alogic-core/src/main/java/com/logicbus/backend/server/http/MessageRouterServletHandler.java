package com.logicbus.backend.server.http;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anysoft.util.PropertiesConstants;
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
 * 
 * @version 1.6.3.28 [20150708 duanyy] <br>
 * - 允许设置为缓存模式 <br>
 * 
 * @version 1.6.4.8 [20151013 duanyy] <br>
 * - CORS成了可选配置 <br>
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - 在MessageRouter中提前写出报文 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
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
	protected static Logger logger = LoggerFactory.getLogger(MessageRouterServletHandler.class);
	
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

	protected boolean cacheAllowed = false; 
	
	protected boolean corsSupport = true;
	
	public void init(ServletConfig servletConfig) throws ServletException {
		Settings settings = Settings.get();
		encoding = settings.GetValue("http.encoding", encoding);
		defaultAllowOrigin = settings.GetValue("http.alloworigin",
				defaultAllowOrigin);
		corsSupport = PropertiesConstants.getBoolean(settings, "http.cors", corsSupport);
		

		ac = (AccessController) settings.get("accessController");

		String _cacheAllowed = servletConfig.getInitParameter("cacheAllowed");
		cacheAllowed = (_cacheAllowed != null && _cacheAllowed.equals("true"))?true:false;
		
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
		
		if (cacheAllowed){
			response.setHeader("Cache-Control", "public");
		}else{
			response.setHeader("Expires", "Mon, 26 Jul 1970 05:00:00 GMT");
			response.setHeader("Last-Modified", "Mon, 26 Jul 1970 05:00:00 GMT");
			response.setHeader("Cache-Control", "no-cache, must-revalidate");
			response.setHeader("Pragma", "no-cache");
		}
		// 1.2.1 duanyy
		// to support CORS
		if (corsSupport){
			String origin = request.getHeader("Origin");
			response.setHeader("Access-Control-Allow-Origin", origin == null || origin.length() <= 0 ? defaultAllowOrigin : origin);
			response.setHeader("Access-Control-Allow-Credentials", "true");
		}
		
		HttpContext ctx = new HttpContext(request,response,encoding,interceptMode);
		Path id = normalizer.normalize(ctx, request);
		MessageRouter.action(id,ctx,ac);
	}

	public void destroy() {

	}

}
