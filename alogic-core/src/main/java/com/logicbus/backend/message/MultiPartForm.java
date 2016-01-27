package com.logicbus.backend.message;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.IOTools;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Settings;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.server.http.HttpContext;

/**
 * MultiPartForm
 * 
 * 处理multipart/form-data类型的输入数据，主要用于处理Http上传文件。
 * 
 * @author duanyy
 * @since 1.6.3.31
 * 
 * @version 1.6.4.30 [20160126 duanyy] <br>
 * - 文件上传消息处理透传Context对象 <br>
 */
public class MultiPartForm implements Message {
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LogManager.getLogger(MultiPartForm.class);
	
	protected static JsonProvider provider = null;
	static {
		provider = JsonProviderFactory.createProvider();
	}		
	
	protected static FileItemFactory factory = null;

	protected static final String ID = "uploadItemFactory";		
	
	/**
	 * 文件列表
	 */
	protected List<FileItem> fileItems = null;
	
	/**
	 * Json结构的根节点
	 */
	protected Map<String,Object> root = null;	
	
	/**
	 * 获取FileItemFactory
	 * @return FileItemFactory
	 */
	public static FileItemFactory getFileItemFactory(){
		if (factory == null){
			synchronized (MultiPartForm.class){
				if (factory == null){
					Settings settings = Settings.get();
					
					factory = (FileItemFactory)settings.get(ID);
					
					if (factory == null){
						factory = new DiskFileItemFactory(); 
					}
				}
			}
		}
		
		return factory;
	}
		

	/**
	 * 获取JSON结构的根节点
	 * 
	 * @return JSON结构的根节点
	 */
	public Map<String,Object> getRoot(){
		return root;
	}
	
	/**
	 * 获取文件列表
	 * @return 文件列表
	 */
	public List<FileItem> getFileItems(){
		return fileItems;
	}
	
	@Override
	public void init(MessageDoc ctx) {
		if (!(ctx instanceof HttpContext)){
			throw new ServantException("core.unsupported_context",
					"The context's class must be HttpContext when using UploadFiles");
		}
		
		HttpContext httpCtx = (HttpContext)ctx;
		
		ServletFileUpload fileUpload = new ServletFileUpload(getFileItemFactory());
		
		try {
			fileItems = fileUpload.parseRequest(httpCtx.getRequest());
		} catch (FileUploadException e) {
			logger.error(e);
			throw new ServantException("core.file_upload_exception",e.getMessage());
		}
		
		root = new HashMap<String,Object>(); // NOSONAR
	}

	@Override
	public void finish(MessageDoc ctx, boolean closeStream) {
		Map<String,Object> theRoot = getRoot();
		JsonTools.setString(theRoot, "code", ctx.getReturnCode());
		JsonTools.setString(theRoot, "reason", ctx.getReason());
		JsonTools.setString(theRoot, "duration", String.valueOf(ctx.getDuration()));
		JsonTools.setString(theRoot, "host", ctx.getHost());
		JsonTools.setString(theRoot, "serial", ctx.getGlobalSerial());
				
		OutputStream out = null;
		try {
			String data = provider.toJson(theRoot);
			
			String jsonp = ctx.GetValue("jsonp", "");
			if (jsonp != null && jsonp.length() > 0){
				data = jsonp + "(" + data + ")";
			}
			
			out = ctx.getOutputStream();
			ctx.setResponseContentType("application/json;charset=" + ctx.getEncoding());
			Context.writeToOutpuStream(out, data, ctx.getEncoding());
			out.flush();
		}catch (Exception ex){
			logger.error("Error when writing data to outputstream",ex);
		}finally{
			if (closeStream)
				IOTools.close(out);
		}		
	}

	/**
	 * 处理已上传的文件
	 * @param ctx 上下文
	 * @param cookies 处理的cookies
	 * @param handler 处理器
	 */
	public void handle(Context ctx,Object cookies,FileItemHandler handler){
		if (fileItems != null && !fileItems.isEmpty()){
			Map<String,Object> result = new HashMap<String,Object>(); // NOSONAR
			
			for (FileItem item:fileItems){
				if (item != null && !item.isFormField()){
					Map<String,Object> fileResult = new HashMap<String,Object>(); // NOSONAR

					fileResult.put("field", item.getFieldName());
					fileResult.put("name", item.getName());
					fileResult.put("size",item.getSize());

					handler.handle(ctx,cookies,item, fileResult);
					result.put(item.getFieldName(), fileResult);
				}
			}
			
			getRoot().put("files", result);
		}
	}

	/**
	 * FileItem处理器
	 * 
	 * @author duanyy
	 *
	 */
	public static interface FileItemHandler {
		/**
		 * 处理文件
		 * @param ctx 上下文
		 * @param cookies 处理的cookies
		 * @param item FileItem
		 * @param result 用于输出文件的处理状态
		 */
		public void handle(Context ctx,Object cookies,FileItem item,Map<String,Object> result);
	}
	
	/**
	 * Upload临时文件自动删除
	 * 
	 * @author duanyy
	 *
	 */
	public static class Cleaner implements ServletContextListener{
		@Override
		public void contextDestroyed(ServletContextEvent sce) {
			Settings settings = Settings.get();
			DiskFileItemFactory f = (DiskFileItemFactory)settings.get(ID);
			FileCleaningTracker tracker = f.getFileCleaningTracker();
			if (tracker != null){
				logger.info("The uploaded temp files will be deleted.");
				tracker.exitWhenFinished();
			}
		}
		@Override
		public void contextInitialized(ServletContextEvent sce) {
			Settings settings = Settings.get();
			logger.info("Files uploading will be supported.");
			
			DiskFileItemFactory f = new DiskFileItemFactory();
			f.setFileCleaningTracker(new FileCleaningTracker());
			
			settings.registerObject(ID, f);
		}
		
	}
}
