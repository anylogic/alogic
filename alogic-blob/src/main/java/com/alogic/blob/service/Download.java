package com.alogic.blob.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alogic.blob.client.BlobTool;
import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobManager;
import com.alogic.blob.core.BlobReader;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.Normalizer;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * Blob文件下载
 * 
 * @author duanyy
 * @since 1.6.3.28
 * 
 * @version 1.6.4.18 [duanyy 20151218] <br>
 * - 增加自动图标集 <br>
 */
public class Download extends Servant {
	protected byte [] buffer = null;
	
	@Override
	public void create(ServiceDescription sd){
		super.create(sd);
		Properties p = sd.getProperties();
		
		int bufferSize = PropertiesConstants.getInt(p, "bufferSize", 10240,true);
		
		buffer = new byte [bufferSize];
	}
	
	@Override
	public int actionProcess(Context ctx) {
		ctx.asMessage(BlobMessage.class);
		
		String fileId = getArgument("fileId",ctx); // NOSONAR
		String domain = getArgument("domain","default",ctx); // NOSONAR
		
		BlobManager manager = BlobTool.getBlobManager(domain);
		if (manager == null){
			throw new ServantException("clnt.e2007","Can not find a blob manager named " + domain);
		}
		
		BlobReader reader = manager.getFile(fileId);
		if (reader == null){
			throw new ServantException("clnt.e2007","Can not find a blob file named " + fileId);
		}
		
		BlobInfo info = reader.getBlobInfo();
		
		ctx.setResponseContentType(info.contentType());
		
		InputStream in = reader.getInputStream(0);

		try {
			OutputStream out = ctx.getOutputStream();
	        int size=0;  
	        
	        while((size=in.read(buffer))!=-1)  
	        {  
	        	out.write(buffer, 0, size);
	        }  
		}catch (IOException ex){
			logger.error(ExceptionUtils.getStackTrace(ex));
			throw new ServantException("core.e1004",ex.getMessage());
		}finally{
			IOTools.close(in);
		}
		
		return 0;
	}
	
	/**
	 * blob文件下载定制Message,不做啥事
	 * 
	 * @author duanyy
	 *
	 */
	public static class BlobMessage implements Message {
		@Override
		public void finish(Context ctx, boolean closeStream) {
			if (!"core.ok".equals(ctx.getReturnCode())){
				throw new ServantException(ctx.getReturnCode(),ctx.getReason());
			}
			OutputStream out = null;
			try {
				out = ctx.getOutputStream();
			} catch (IOException e) {
				logger.error("IO Exception",e);
			}finally{
				if (closeStream){
					IOTools.close(out);
				}
			}
		}
		@Override
		public void init(Context ctx) {
			// Nothing to do
		}
		@Override
		public String getContentType() {
			return "unknown";
		}
		@Override
		public long getContentLength() {
			return 0;
		}
	}
	
	/**
	 * Normalizer
	 * 
	 * <p>
	 * 用于转化服务路径
	 * 
	 * @author duanyy
	 * @version 1.6.4.4 [20150910 duanyy] <br>
	 * - 缺省代理路径改为/component/blob/Download
	 */
	public static class TheNormalizer implements Normalizer {
		protected String proxyServiceId = "/component/blob/Download";
		
		public TheNormalizer(Properties p){
			proxyServiceId = PropertiesConstants.getString(p, "normalizer.blob.id", proxyServiceId);
		}
		
		@Override
		public Path normalize(Context ctx, HttpServletRequest request) {
			String path = request.getPathInfo();
			String queryString = request.getQueryString();
			String domain = null;
			String fileId = null;
			
			if (path != null && path.length() > 0){
				int start = findStart(path);
				int pos = findPos(start,path);
				
				domain = trimSlash(path.substring(start,pos));
				
				if (pos >= path.length() - 1){
					fileId = null;
				}else{
					fileId = trimSlash(path.substring(pos+1));
				}
			}
		
			if (isNull(fileId)){
				if (!isNull(domain)){
					ctx.SetValue("fileId", domain);
					ctx.SetValue("domain", "default");
				}
			}else{
				if (!isNull(domain)){
					ctx.SetValue("domain", domain);
				}
				ctx.SetValue("fileId", fileId);
			}

			if (!isNull(queryString)){
				ctx.SetValue("query", queryString);
			}
			
			return new Path(proxyServiceId);
		}
	}	
	
	 private static boolean isNull(String value){
		return value == null || value.length() <= 0;
	}
	
	 private static int findPos(int start,String path){
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

	 private static int findStart(String path){
		int length = path.length();
		int found = 0;
		for ( ; found < length ; found++){
			if (path.charAt(found) != '/'){
				return found;
			}
		}
		return found;
	}	
	
	 private static String trimSlash(String str){
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
}
