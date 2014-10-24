package com.logicbus.service;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.dbcp.core.ConnectionPool;
import com.logicbus.dbcp.context.DbcpSource;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.together.Compiler;
import com.logicbus.together.ExecuteWatcher;
import com.logicbus.together.Logiclet;


/**
 * together在logicbus中的代理
 * 
 * @author duanyy
 * @since 1.1.0
 * 
 * @version 1.2.1 [20140613 duanyy]
 * - 增加对JSON的支持
 * - 增加执行Watcher
 * 
 * @version 1.2.9 [20141016 duanyy]
 * - 重写了dbcp
 */
public class LogicBusAgent extends Servant {
	
	public int actionProcess(MessageDoc msgDoc, Context ctx) throws Exception {
		String json = getArgument("json","false",msgDoc,ctx);
		if (json != null && json.equals("true")){
			return actionProcessJson(msgDoc,ctx);
		}else{
			return actionProcessXml(msgDoc,ctx);
		}
	}

	private int actionProcessXml(MessageDoc msgDoc, Context ctx)throws Exception {
		XMLMessage msg = (XMLMessage) msgDoc.asMessage(XMLMessage.class);
		
		String reload = getArgument("reload","false",msgDoc,ctx);
		
		if (reload.equals("true")){
			reloadProtocol();
		}
		
		if (dbSupport){
			DbcpSource ds = DbcpSource.get();
			ConnectionPool pool = ds.get(dsName);
			if (pool == null){
				throw new ServantException("core.sqlerror","Can not get a connection pool named " + dsName);
			}			
			Connection conn = pool.getConnection(3000,false);			
			if (conn == null) 
					throw new ServantException("core.sqlerror","Can not get a db connection : " + dsName);
			
			if (transactionSupport){
				conn.setAutoCommit(false);
			}else{
				conn.setAutoCommit(true);
			}
			
			ctx.setConnection(conn);
			
			try {
				Element root = msg.getRoot();
				if (logiclet != null){
					logiclet.execute(root, msg, ctx,watcher);
					if (logiclet.hasError()){
						throw new ServantException(logiclet.getCode(),logiclet.getReason());
					}
				}
				if (transactionSupport){
					conn.commit();
				}
			}catch (Exception ex){
				if (transactionSupport){
					conn.rollback();
				}
				throw ex;
			}finally{
				ctx.setConnection(null);
				pool.recycle(conn);
			}
		}else{
			Element root = msg.getRoot();
			if (logiclet != null){
				logiclet.execute(root, msg, ctx,watcher);
				if (logiclet.hasError()){
					throw new ServantException(logiclet.getCode(),logiclet.getReason());
				}
			}
		}
		return 0;		
	}

	@SuppressWarnings("rawtypes")
	private int actionProcessJson(MessageDoc msgDoc, Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage) msgDoc.asMessage(JsonMessage.class);
		
		String reload = getArgument("reload","false",msgDoc,ctx);
		
		if (reload.equals("true")){
			reloadProtocol();
		}
		
		if (dbSupport){
			DbcpSource ds = DbcpSource.get();
			ConnectionPool pool = ds.get(dsName);
			if (pool == null){
				throw new ServantException("core.sqlerror","Can not get a connection pool named " + dsName);
			}
			
			Connection conn = pool.getConnection(3000,false);				
			if (conn == null) 
					throw new ServantException("core.sqlerror","Can not get a db connection : " + dsName);
			
			
			if (transactionSupport){
				conn.setAutoCommit(false);
			}else{
				conn.setAutoCommit(true);
			}
			
			ctx.setConnection(conn);
			
			try {
				Map root = msg.getRoot();
				if (logiclet != null){
					logiclet.execute(root, msg, ctx,watcher);
					if (logiclet.hasError()){
						throw new ServantException(logiclet.getCode(),logiclet.getReason());
					}
				}
				if (transactionSupport){
					conn.commit();
				}
			}catch (Exception ex){
				if (transactionSupport){
					conn.rollback();
				}
				throw ex;
			}finally{
				ctx.setConnection(null);
				pool.recycle(conn);
			}
		}else{
			Map root = msg.getRoot();
			if (logiclet != null){
				logiclet.execute(root, msg, ctx,watcher);
				if (logiclet.hasError()){
					throw new ServantException(logiclet.getCode(),logiclet.getReason());
				}
			}
		}
		return 0;	
	}

	
	public void create(ServiceDescription sd) throws ServantException{
		super.create(sd);
		
		Properties props = sd.getProperties();		
		dbSupport = PropertiesConstants.getBoolean(props, "dbSupport", dbSupport);		
		dsName = PropertiesConstants.getString(props, "datasource", dsName);
		transactionSupport = PropertiesConstants.getBoolean(props, "transactionSupport", transactionSupport);	
		
		xrcMaster = PropertiesConstants.getString(props, "xrc.master", "${master.home}/servants/" + sd.getPath() + ".xrc");
		xrcSecondary = PropertiesConstants.getString(props, "xrc.secondary", "${secondary.home}/servants/" + sd.getPath() + ".xrc");
		
		watcherClassName = PropertiesConstants.getString(props, "together.watcher","");
		
		if (watcherClassName != null && watcherClassName.length() > 0){
			Settings settings = Settings.get();
			ClassLoader cl = (ClassLoader) settings.get("classLoader");
			
			ExecuteWatcher.TheFactory factory = new ExecuteWatcher.TheFactory(cl);
			try {
				watcher = factory.newInstance(watcherClassName, settings);
			}catch (Exception ex){
				logger.error("Can not create watcher " + watcherClassName + ",ignored.");
			}
		}
		
		reloadProtocol();
	}	
	
	protected void reloadProtocol(){
		Settings settings = Settings.get();
		ResourceFactory rm = (ResourceFactory) settings.get("ResourceFactory");
		if (null == rm){
			rm = new ResourceFactory();
		}
		
		Document doc = null;
		InputStream in = null;
		try {
			in = rm.load(xrcMaster, xrcSecondary);
			doc = XmlTools.loadFromInputStream(in);
			
			logiclet = Compiler.compile(doc.getDocumentElement(), Settings.get(),null);			
			if (logiclet == null){
				logger.error("Can not compile the document,xrc =" + xrcMaster);
			}
		} catch (Exception ex){
			logger.error("Error occurs when load xml file,source=" + xrcMaster , ex);
		}finally {
			IOTools.closeStream(in);
		}
	}
	
	/**
	 * 协议文档主URI
	 */
	protected String xrcMaster = "";
	
	/**
	 * 协议文档备URI
	 */
	protected String xrcSecondary = "";
	
	/**
	 * Logiclet根节点
	 */
	protected Logiclet logiclet = null;
	
	/**
	 * 是否需要数据库连接,缺省是false
	 */
	protected boolean dbSupport = false;
	
	/**
	 * 是否需要事务支持,缺省是false
	 */
	protected boolean transactionSupport = false;
	
	/**
	 * 数据库数据源名称,缺省为Default
	 */
	protected String dsName = "Default";
	
	/**
	 * watcher类名
	 */
	protected String watcherClassName = "";
	
	/**
	 * 执行监视器
	 * 
	 * @since 1.2.1
	 */
	protected ExecuteWatcher watcher = null;
}
