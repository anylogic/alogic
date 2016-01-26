package com.alogic.idu.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.alogic.blob.client.BlobTool;
import com.alogic.blob.core.BlobInfo;
import com.alogic.blob.core.BlobManager;
import com.alogic.blob.core.BlobWriter;
import com.alogic.cache.core.CacheStore;
import com.alogic.cache.core.MultiFieldObject;
import com.alogic.idu.util.IDUBase;
import com.anysoft.util.BaseException;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.MultiPartForm;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.processor.Preprocessor;
import com.logicbus.dbcp.sql.DBTools;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 为指定ID的对象上传附件
 * 
 * @author duanyy
 * @since 1.6.4.6
 */
public class AttachById extends IDUBase {
	protected String rootName = "data";
	protected String dataId = "id";
	protected String domain = "default";
	protected BlobManager blobManager = null;
	protected byte [] buffer = null;
	protected String sqlUpdate = "";
	
	protected Preprocessor processor = null;
	
	@Override
	protected void doIt(Context ctx, JsonMessage msg, Connection conn)
			throws Exception {
		//no use
	}

	@Override
	protected void onCreate(ServiceDescription sd, Properties p)
			throws ServantException {
		sqlUpdate = PropertiesConstants.getString(p, "sql.Update", sqlUpdate);
		rootName = PropertiesConstants.getString(p, "data.root", rootName);
		dataId = PropertiesConstants.getString(p,"dataGuard.id", dataId);
		
		processor = new Preprocessor(sqlUpdate);
		
		int bufferSize = PropertiesConstants.getInt(p, "bufferSize", 10240,true);
		buffer = new byte [bufferSize];
		
		domain = PropertiesConstants.getString(p,"blob.domain",domain);
		
		blobManager = BlobTool.getBlobManager(domain);
		if (blobManager == null){
			throw new ServantException("core.blob_not_found","Can not find a blob manager named: " + domain);
		}	
	}
	
	public int actionProcess(Context ctx) throws Exception {
		boolean json = getArgument("json",jsonDefault,ctx);
		if (json){
			MultiPartForm msg = (MultiPartForm) ctx.asMessage(MultiPartForm.class);
			ConnectionPool pool = getConnectionPool();
			Connection conn = pool.getConnection();
			boolean hasError = false;
			boolean autoCommit = conn.getAutoCommit();
			try {
				if (transactionSupport){
					conn.setAutoCommit(false);
				}
				doIt(ctx, msg, conn);
				if (transactionSupport){
					conn.commit();
				}
			} catch (BaseException ex){
				if ("core.sql_error".equals(ex.getCode())){
					hasError = true;
				}
				if (transactionSupport){
					conn.rollback();
				}
				throw ex;
			}finally {
				conn.setAutoCommit(autoCommit);
				pool.recycle(conn,hasError);
			}	
			return 0;
		}else{
			return onXml(ctx);
		}
	}

	/**
	 * 处理MultiPartForm消息
	 * @param ctx 上下文
	 * @param msg 消息
	 * @param conn 数据库连接
	 */
	protected void doIt(Context ctx, MultiPartForm msg, Connection conn) {
		String userId = getArgument("user.id","",ctx);
		String id = getArgument("id",ctx);
		String dataGuardObject = getArgument(dataId,id,ctx);
		if (!checkPrivilege(userId,dataGuardObject)){
			throw new ServantException("core.unauthorized","无权访问本服务，用户:" + userId);
		}

		List<FileItem> items = msg.getFileItems();
		if (items.size() > 0){
			Map<String,Object> result = new HashMap<String,Object>();
			
			for (FileItem item:items){
				if (item != null && !item.isFormField()){
					Map<String,Object> fileResult = new HashMap<String,Object>();

					fileResult.put("field", item.getFieldName());
					fileResult.put("name", item.getName());
					fileResult.put("size",item.getSize());

					BlobWriter writer = blobManager.newFile(item.getContentType());
					OutputStream out = writer.getOutputStream();
					InputStream in = null;
					try {
						in = item.getInputStream();
						
						try {
					        int size=0;  
					        
					        while((size=in.read(buffer))!=-1)  
					        {  
					        	out.write(buffer, 0, size);
					        }  
						}finally{
							IOTools.close(in,out);
						}
						
						BlobInfo info = writer.getBlobInfo();
						result.put("fileId", info.id());
						result.put("contentType", info.contentType());
						result.put("md5", info.md5());
						result.put("sucessful", "true");
						blobManager.commit(writer);
						
						ctx.SetValue(item.getFieldName(), info.id());
					}catch (Exception ex){
						result.put("sucessful", "false");		
						blobManager.cancel(writer);
					}
					
					result.put(item.getFieldName(), fileResult);
				}
			}
			
			msg.getRoot().put("files", result);
		}
		
		update(ctx,conn);
		
		clearCache(id);
		
		CacheStore cache = getCacheStore();
		
		MultiFieldObject found = cache.get(id, true);
		if (found != null){
			Map<String,Object> output = new HashMap<String,Object>();
			
			found.toJson(output);
			
			msg.getRoot().put(rootName, output);
		}
		
		bizLog(conn, userId, ctx.getClientIp(), id,ctx);
	}

	protected void update(Context ctx,Connection conn){
		List<Object> data = new ArrayList<Object>();
		String sql = processor.process(ctx, data);

		if (data.size() > 0){
			DBTools.update(conn, sql, data.toArray());
		}else{
			DBTools.update(conn, sql);
		}
	}
}
