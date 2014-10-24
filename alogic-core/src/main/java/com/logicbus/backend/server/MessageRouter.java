package com.logicbus.backend.server;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.Context;
import com.logicbus.backend.QueuedServantFactory;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.ServantFactory;
import com.logicbus.backend.ServantPool;
import com.logicbus.backend.ServantWorkerThread;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 消息路由器
 * 
 * @author duanyy
 * 
 * @version 1.0.1 [20140402 duanyy] <br>
 * - {@link com.logicbus.backend.AccessController AccessController}有更新<br>
 * 
 * @version 1.0.2 [20140407 duanyy] <br>
 * - 采用{@link java.util.concurrent.CountDownLatch CountDownLatch}来和工作进程通讯.<br>
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 改进消息传递模型 <br>
 * 
 * @version 1.2.2 [20140417 duanyy] <br>
 * - 增加非线程调度模式
 * 
 * @version 1.2.3 [20140617 duanyy] <br>
 * - 增加业务日志的采集功能
 * 
 * @version 1.2.6 [20140807 duanyy] <br>
 * - ServantPool和ServantFactory插件化
 * 
 * @version 1.2.6.4 [20140820 duanyy] <br>
 * - 修正servant实例无法获取到，抛出NullPointException问题
 * 
 * @version 1.2.7 [20140828 duanyy] <br>
 * - 重写BizLogger
 * 
 * @version 1.2.7.1 [20140902 duanyy] <br>
 * - BizLogItem去掉host属性
 * 
 * @version 1.2.7.2 [20140910 duanyy] <br>
 * - 修正bizlog日志中client的取值
 * 
 * @version 1.2.8 [20140917 duanyy] <br>
 * - Handler:handle和flush方法增加timestamp参数，以便时间同步
 */
public class MessageRouter {
	
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LogManager.getLogger(MessageRouter.class);
	
	/**
	 * 服务调用
	 * @param id 服务id
	 * @param mDoc 消息文档
	 * @param ctx 上下文
	 * @param ac 访问控制器
	 * @return 
	 */
	static public int action(Path id,MessageDoc mDoc,Context ctx,AccessController ac){
		mDoc.setStartTime(System.currentTimeMillis());
		
		ServantPool pool = null;
		Servant servant = null;		
		String sessionId = "";
		
		try{
			ServantFactory factory = servantFactory;
			pool = factory.getPool(id);		
			if (!pool.isRunning()){
				throw new ServantException("core.service_paused",
						"The Service is paused:service id:" + id);
			}

			int priority = 0;
			
			if (null != ac){
				sessionId = ac.createSessionId(id, pool.getDescription(), ctx);
				priority = ac.accessStart(sessionId,id, pool.getDescription(), ctx);
				if (priority < 0){
					logger.info("Unauthorized Access:" + ctx.getClientIp() + ",url:" + ctx.getRequestURI());
					mDoc.setReturn("client.permission_denied","Permission denied！service id: "+ id);
					return 0;
				}
			}

			servant = pool.borrowObject(priority);
			if (servant == null){
				logger.warn("Can not get a servant from pool in the limited time,check servant.queueTimeout variable.");
				mDoc.setReturn("core.time_out", "Can not get a servant from pool in the limited time,check servant.queueTimeout variable.");
			}else{
				if (!threadMode){
					//在非线程模式下,不支持服务超时
					execute(servant,mDoc,ctx);
				}else{
					CountDownLatch latch = new CountDownLatch(1);
					ServantWorkerThread thread = new ServantWorkerThread(servant,mDoc,ctx,latch);
					thread.start();
					if (!latch.await(servant.getTimeOutValue(), TimeUnit.MILLISECONDS)){
						mDoc.setReturn("core.time_out","Time out or interrupted.");
					}
					thread = null;
				}
			}
		}catch (ServantException ex){
			mDoc.setReturn(ex.getCode(), ex.getMessage());
			logger.error(ex.getCode() + ":" + ex.getMessage());
		}catch (Exception ex){
			mDoc.setReturn("core.fatalerror",ex.getMessage());
			logger.error("core.fatalerror:" + ex.getMessage(),ex);
		}catch (Throwable t){
			mDoc.setReturn("core.fatalerror",t.getMessage());
			logger.error("core.fatalerror:" + t.getMessage(),t);			
		}
		finally {
			if (pool != null){
				if (servant != null){
					pool.returnObject(servant);		
				}				
				pool.visited(mDoc.getDuration(),mDoc.getReturnCode());
				if (ac != null){
					ac.accessEnd(sessionId,id, pool.getDescription(), ctx);
				}				
			}			
			mDoc.setEndTime(System.currentTimeMillis());
			if (bizLogger != null){				
				//需要记录日志
				log(id,sessionId,pool == null ? null : pool.getDescription(),mDoc,ctx);
			}
		}
		return 0;
	}	
	
	protected static int log(Path id,String sessionId,ServiceDescription sd,MessageDoc mDoc,Context ctx){
		ServiceDescription.LogType logType = 
				(sd != null) ? sd.getLogType():ServiceDescription.LogType.brief;
		
		if (logType == ServiceDescription.LogType.none)
			return 0;
		
		BizLogItem item = new BizLogItem();
		
		item.sn = ctx.getGlobalSerial();
		item.id = id.toString();
		item.clientIP = ctx.getClientIp();
		//当无法取到sessionId时，直接取ip(当服务找不到时)
		item.client = sessionId != null && sessionId.length() > 0 ? sessionId : item.clientIP;
		//item.host = ctx.getHost();
		item.result = mDoc.getReturnCode();
		item.reason = mDoc.getReason();
		item.startTime = mDoc.getStartTime();
		item.duration = mDoc.getDuration();
		item.url = ctx.getRequestURI();
		item.content = logType == ServiceDescription.LogType.detail ? mDoc.toString() : null;
		
		bizLogger.handle(item,System.currentTimeMillis());
				
		return 0;
	}
	
	protected static int execute(Servant servant,MessageDoc mDoc,Context ctx) throws Exception {
		servant.actionBefore(mDoc, ctx);
		servant.actionProcess(mDoc, ctx);
		servant.actionAfter(mDoc, ctx);
		return 0;
	}
	
	protected static boolean threadMode = true;
	protected static BizLogger bizLogger = null;
	protected static ServantFactory servantFactory = null;
	static {
		Settings settings = Settings.get();
		
		//初始化threadMode
		threadMode = PropertiesConstants.getBoolean(settings, "servant.threadMode", true);
		
		bizLogger = (BizLogger) settings.get("bizLogger");
		
		servantFactory = (ServantFactory) settings.get("servantFactory");
		if (servantFactory == null){
			servantFactory = new QueuedServantFactory(settings);
		}
	}
}
