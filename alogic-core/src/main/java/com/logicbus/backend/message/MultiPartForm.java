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
 */
public class MultiPartForm implements Message {
	protected static final Logger logger = LogManager.getLogger(MultiPartForm.class);
	protected List<FileItem> fileItems = null;
	public List<FileItem> getFileItems(){
		return fileItems;
	}
	
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
			throw new ServantException("core.file_upload_exception",
					e.getMessage());
		}
		
		root = new HashMap<String,Object>();
	}

	public void finish(MessageDoc ctx, boolean closeStream) {
		Map<String,Object> _root = getRoot();
		JsonTools.setString(_root, "code", ctx.getReturnCode());
		JsonTools.setString(_root, "reason", ctx.getReason());
		JsonTools.setString(_root, "duration", String.valueOf(ctx.getDuration()));
		JsonTools.setString(_root, "host", ctx.getHost());
		JsonTools.setString(_root, "serial", ctx.getGlobalSerial());
				
		OutputStream out = null;
		try {
			String data = provider.toJson(_root);
			
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
	 * @param handler 处理器
	 */
	public void handle(FileItemHandler handler){
		if (fileItems.size() > 0){
			Map<String,Object> result = new HashMap<String,Object>();
			
			for (FileItem item:fileItems){
				if (item != null && !item.isFormField()){
					Map<String,Object> fileResult = new HashMap<String,Object>();

					fileResult.put("field", item.getFieldName());
					fileResult.put("name", item.getName());
					fileResult.put("size",item.getSize());

					handler.handle(item, result);
					result.put(item.getFieldName(), fileResult);
				}
			}
			
			getRoot().put("files", result);
		}
	}
	
	/**
	 * Json结构的根节点
	 */
	protected Map<String,Object> root = null;
	
	/**
	 * 获取JSON结构的根节点
	 * 
	 * @return JSON结构的根节点
	 */
	public Map<String,Object> getRoot(){
		return root;
	}
	
	protected static JsonProvider provider = null;
	
	static {
		provider = JsonProviderFactory.createProvider();
	}	
	
	protected static FileItemFactory factory = null;
	
	protected static Object lock = new Object();
	
	protected static String ID = "uploadItemFactory";
	
	/**
	 * 获取FileItemFactory
	 * @return FileItemFactory
	 */
	public static FileItemFactory getFileItemFactory(){
		if (factory == null){
			synchronized (lock){
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
	 * FileItem处理器
	 * 
	 * @author duanyy
	 *
	 */
	public static interface FileItemHandler {
		/**
		 * 处理文件
		 * @param item FileItem
		 * @param result 用于输出文件的处理状态
		 */
		public void handle(FileItem item,Map<String,Object> result);
	}
	
	/**
	 * Upload临时文件自动删除
	 * 
	 * @author duanyy
	 *
	 */
	public static class Cleaner implements ServletContextListener{
		public void contextDestroyed(ServletContextEvent sce) {
			Settings settings = Settings.get();
			DiskFileItemFactory factory = (DiskFileItemFactory)settings.get(ID);
			FileCleaningTracker tracker = factory.getFileCleaningTracker();
			if (tracker != null){
				logger.info("The uploaded temp files will be deleted.");
				tracker.exitWhenFinished();
			}
		}

		public void contextInitialized(ServletContextEvent sce) {
			Settings settings = Settings.get();
			logger.info("Files uploading will be supported.");
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setFileCleaningTracker(new FileCleaningTracker());
			
			settings.registerObject(ID, factory);
		}
		
	}
}
