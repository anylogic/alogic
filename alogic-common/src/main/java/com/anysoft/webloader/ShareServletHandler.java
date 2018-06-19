package com.anysoft.webloader;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.anysoft.util.Settings;

/**
 * URL分享转发服务
 * 
 * @author yyduan
 * @since 1.6.11.37 
 */
public class ShareServletHandler implements ServletHandler{
	/**
	 * 允许的方法
	 */
	protected String methodAllow = "GET,PUT,POST";
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
	}

	@Override
	public void doService(HttpServletRequest request,HttpServletResponse response, String method)throws ServletException, IOException {
		if (method.equals("options")){
			response.setHeader("Allow", methodAllow);
		}else{	
			ShareTool tool = Settings.get().getToolkit(ShareTool.class);			
			String rcId = request.getPathInfo();
			if (StringUtils.isEmpty(rcId) || rcId.length() < 1){
				response.sendError(404, String.format("core.e1008:Resource %s does not exist.",rcId));
			}else{
				String encodedPath = rcId.substring(1);
				String path = tool.decodePath(encodedPath);
				if (path.equals(encodedPath)){
					response.sendError(404, String.format("core.e1008:Resource %s does not exist.",rcId));
				}else{
					request.getRequestDispatcher(path).forward(request, response);
				}
			}
		}
	}

	@Override
	public void destroy() {

	}

}
