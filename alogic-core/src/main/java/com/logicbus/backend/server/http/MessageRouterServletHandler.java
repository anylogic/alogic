package com.logicbus.backend.server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.IOTools;
import com.anysoft.util.Settings;
import com.anysoft.webloader.ServletHandler;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.Context;
import com.logicbus.backend.DefaultNormalizer;
import com.logicbus.backend.Normalizer;
import com.logicbus.backend.message.MessageDoc;
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
	
	/**
	 * 通过post方式传递参数所对应的contentType
	 */
	protected String formContentType = "application/x-www-form-urlencoded";
	
	public void init(ServletConfig servletConfig) throws ServletException {
		Settings settings = Settings.get();
		encoding = settings.GetValue("http.encoding", encoding);
		defaultAllowOrigin = settings.GetValue("http.alloworigin",
				defaultAllowOrigin);
		formContentType = settings.GetValue("http.formContentType",
				formContentType);
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
		
		StringBuffer doc = new StringBuffer();
		MessageDoc msgDoc = null;
		Context ctx = null;
		try{
			//从request输入流中读入XML文档
			
			if (method.equals("post"))
			{
				try{
					String _contentType = request.getContentType();
					if (_contentType != null && ! _contentType.startsWith(formContentType)){
						doc = loadFromInputStream(doc,request.getInputStream());
					}
				}catch (Exception ex){
					// 没有输入XML文档
				}
			}
			String serial = request.getHeader("GlobalSerial");
			
			msgDoc = new MessageDoc(doc,encoding);
			ctx = new HttpContext(request,serial);	
			
			//规范化ID
			Path id = normalizer.normalize(ctx, request);
			
			{
				if (logger.getEffectiveLevel().equals(Level.DEBUG)){
					logger.debug("Invoking service:" + id);
					logger.debug("Input:");					
					logger.debug(msgDoc.toString());
				}
			}
			
			MessageRouter.action(id,msgDoc,ctx,ac);
			
			{
				if (logger.getEffectiveLevel().equals(Level.DEBUG)){
					logger.debug("Output:");
					logger.debug(msgDoc.toString());
				}
			}

		}catch (Exception ex){
			ex.printStackTrace();
			if (msgDoc != null){
				msgDoc.setReturn("core.fatalerror",ex.getMessage());
				logger.error("core.fatalerror:" + ex.getMessage());
			}
		}	
		finally {
			if (msgDoc != null){
				response.setContentType(msgDoc.getContentType());
				response.setCharacterEncoding(encoding);
				if (msgDoc.hasFatalError()){
					response.sendError(404, msgDoc.getReturnCode() + "(" + msgDoc.getReason() + ")");
				}else{
					msgDoc.output(response.getOutputStream(),ctx);
				}
			}
		}
	}
	
	/**
	 * 从InputStream中装入文本
	 * @param buf 文本对象
	 * @param in InputStream
	 * @return
	 */
	private StringBuffer loadFromInputStream(StringBuffer buf,InputStream in){
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		try {
            while ((line = reader.readLine()) != null) {
            	buf.append(line);
            	buf.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	IOTools.closeStream(in,reader);
        }
		return buf;
	}
	
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
