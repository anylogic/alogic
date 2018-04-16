package com.alogic.redirect;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.load.Loader;
import com.alogic.redirect.loader.FromInner;
import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.anysoft.webloader.ServletConfigProperties;
import com.anysoft.webloader.ServletHandler;

/**
 * Web跳转器
 * @author yyduan
 * @since 1.6.11.26
 */
public class Redirector implements ServletHandler,XMLConfigurable,Configurable{
	/**
	 * a logger of slf4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(Redirector.class);
	
	/**
	 * 缺省Redirector配置文件
	 */
	protected static final String DEFAULT = "java:///com/alogic/redirect/redirect.xml#" + Redirector.class.getName();
	
	/**
	 * 缺省编码
	 */
	protected String encoding = "utf-8";
	
	/**
	 * 跳转路径配置
	 */
	protected Loader<RedirectPath> paths = null;
	
	/**
	 * 路径匹配
	 */
	protected static final Pattern pathPattern = Pattern.compile("(/[\\w|.]+/[\\w|.]+)(/.+)");
		
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		ServletConfigProperties props = new ServletConfigProperties(servletConfig);
		
		String master = PropertiesConstants.getString(props, "redirect.master", DEFAULT);
		String secondary = PropertiesConstants.getString(props, "redirect.secondary", DEFAULT);
		ResourceFactory rf = Settings.getResourceFactory();

		InputStream in = null;
		try {
			in = rf.load(master, secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				configure(doc.getDocumentElement(), props);
			}
		}catch (Exception ex){
			LOG.error("Can not init redirector with file : " + master);
		}finally{
			IOTools.close(in);
		}
	}
	
	@Override
	public void configure(Properties props) {
		encoding = PropertiesConstants.getString(props, "http.encoding", encoding);	
	}
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);

		Element loaderElem = XmlTools.getFirstElementByPath(e, "loader");
		if (loaderElem != null){
			try {
				Factory<Loader<RedirectPath>> f = new Factory<Loader<RedirectPath>>();
				paths = f.newInstance(loaderElem, props, "module",FromInner.class.getName());
			}catch (Exception ex){
				LOG.error("Can not create loader with " + XmlTools.node2String(loaderElem));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		configure(props);
	}

	protected RedirectPath loadRedirectPath(String id){
		return paths == null ? null : paths.load(id, true);
	}
	
	@Override
	public void doService(HttpServletRequest request,
			HttpServletResponse response, String method)
			throws ServletException, IOException {
		String redirectId = null;
		String path = null;
		
		String requestURI = request.getRequestURI();
		Matcher m = pathPattern.matcher(requestURI);
		if (m.find()){
			redirectId = m.group(1);
			path = m.group(2);
		}else{
			redirectId = requestURI;
		}
		RedirectPath redirectPath = loadRedirectPath(redirectId);
		if (redirectPath == null){
			response.sendError(404,String.format("core.e1003:Can not find redirect path %s",redirectId));
			return ;
		}
		
		response.sendRedirect(getRedirectURL(redirectPath,path,request,response));
	}

	protected String getRedirectURL(RedirectPath redirectPath,String path,HttpServletRequest request, HttpServletResponse response) {
		StringBuffer buf = new StringBuffer(redirectPath.getTargetPath());
		
		if (StringUtils.isNotEmpty(path)){
			buf.append(path);
		}
		
		String query = request.getQueryString();
		if (StringUtils.isNotEmpty(query)){
			buf.append("?").append(query);
		}
				
		return buf.toString();
	}

	@Override
	public void destroy() {
		// nothing to do
	}


}
