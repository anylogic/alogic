package com.logicbus.backend.server;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.util.BaseException;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.backend.AccessController;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.ServantFactory;
import com.logicbus.backend.ServantPool;
import com.logicbus.backend.ServantWorkerThread;
import com.logicbus.backend.bizlog.BizLogItem;
import com.logicbus.backend.bizlog.BizLogger;
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
 * 
 * @version 1.3.0.1 [20141029 duanyy] <br>
 * - 当所访问的服务不存在时，以一个统一的服务名(/core/Null)来进行日志记录
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - Servant体系抛弃MessageDoc <br>
 * 
 * @version 1.6.4.11 [20151116 duanyy] <br>
 * - 日志类型为none的服务日志也将输出到bizlog
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - bizlog增加报文长度 <br>
 * - 在action中提前写出报文 <br>
 * - 增加trace日志 <br>
 * 
 * @version 1.6.7.3 [20170118 duanyy] <br>
 * - 对tlog的开启开关进行了统一 <br>
 * 
 * @version 1.6.7.4 [20170118 duanyy] <br>
 * - 服务耗时单位改为ns <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.7.15 [20170221 duanyy] <br>
 * - 增加bizlog.enable环境变量，以便关闭bizlog <br>
 * - 增加acm.enable环境变量，以便关闭ac控制器 <br>
 * 
 * @version 1.6.7.20 <br>
 * - 改造ServantManager模型,增加服务配置监控机制 <br>
 * 
 * @version 1.6.8.3 [20170328 duanyy] <br>
 * - 修正tlog输出，将参数和错误原因分离开来 <br>
 */
public class MessageRouter {
	
	/**
	 * a logger of log4j
	 */
	protected static Logger logger = LoggerFactory.getLogger(MessageRouter.class);
	
	/**
	 * 服务调用
	 * @param id 服务id
	 * @param ctx 上下文
	 * @param ac 访问控制器
	 * @return 调用结果
	 */
	static public int action(Path id,Context ctx,AccessController ac){		
		ServantPool pool = null;
		Servant servant = null;		
		String sessionId = "";
		
		TraceContext tc = null;
		if (tracerEnable){
			tc = Tool.start(ctx.getGlobalSerial(), ctx.getGlobalSerialOrder());
		}
		try{
			//访问开始
			ctx.setStartTime(System.nanoTime());
			
			//获取服务实例池
			ServantFactory factory = servantFactory;
			pool = factory.getPool(id);		
			if (!pool.isRunning()){
				throw new ServantException("core.service_paused",
						"The Service is paused:service id:" + id);
			}

			//通过访问控制器获取访问优先级
			int priority = 0;			
			if (acmEnable && null != ac){
				sessionId = ac.createSessionId(id, pool.getDescription(), ctx);
				priority = ac.accessStart(sessionId,id, pool.getDescription(), ctx);
				if (priority < 0){
					logger.info("Unauthorized Access:" + ctx.getClientIp() + ",url:" + ctx.getRequestURI());
					ctx.setReturn("client.permission_denied","Permission denied！service id: "+ id);
					return 0;
				}
			}

			//从服务实例池中拿服务实例，并执行
			servant = pool.borrowObject(priority);
			if (servant == null){
				logger.warn("Can not get a servant from pool in the limited time,check servant.queueTimeout variable.");
				ctx.setReturn("core.time_out", "Can not get a servant from pool in the limited time,check servant.queueTimeout variable.");
			}else{
				if (!threadMode){
					//在非线程模式下,不支持服务超时
					execute(servant,ctx);
				}else{
					CountDownLatch latch = new CountDownLatch(1);
					ServantWorkerThread thread = new ServantWorkerThread(servant,ctx,latch,tc != null ? tc.newChild() : null);
					thread.start();
					if (!latch.await(servant.getTimeOutValue(), TimeUnit.MILLISECONDS)){
						ctx.setReturn("core.time_out","Time out or interrupted.");
					}
					thread = null;
				}
			}
		}catch (ServantException ex){
			ctx.setReturn(ex.getCode(), ex.getMessage());
			logger.error(ex.getCode() + ":" + ex.getMessage());
		}catch (BaseException ex){
			ctx.setReturn(ex.getCode(), ex.getMessage());
			logger.error(ex.getCode() + ":" + ex.getMessage());
		}catch (Exception ex){
			ctx.setReturn("core.fatalerror",ex.getMessage());
			logger.error("core.fatalerror:" + ex.getMessage(),ex);
		}catch (Throwable t){
			ctx.setReturn("core.fatalerror",t.getMessage());
			logger.error("core.fatalerror:" + t.getMessage(),t);			
		}
		finally {
			ctx.setEndTime(System.nanoTime());
			if (ctx != null){
				ctx.finish();
			}
			if (pool != null){
				if (servant != null){
					pool.returnObject(servant);		
				}				
				pool.visited(ctx.getDuration(),ctx.getReturnCode());
				if (acmEnable && ac != null){
					ac.accessEnd(sessionId,id, pool.getDescription(), ctx);
				}				
			}						
			if (bizlogEnable && bizLogger != null){				
				//需要记录日志
				log(id,sessionId,pool == null ? null : pool.getDescription(),ctx);
			}
			if (tracerEnable){
				boolean ok = ctx.getReturnCode().equals("core.ok");
				Tool.end(tc, "ALOGIC", id.getPath(), ok ?"OK":"FAILED", ctx.getReason(),ctx.getQueryString(), ctx.getContentLength());
			}
		}
		return 0;
	}	
	
	protected static int log(Path id,String sessionId,ServiceDescription sd,Context ctx){
		ServiceDescription.LogType logType = 
				(sd != null) ? sd.getLogType():ServiceDescription.LogType.brief;	
		BizLogItem item = new BizLogItem();
		
		item.logType = logType;
		item.sn = ctx.getGlobalSerial();
		item.id = (sd != null)?id.toString():"/core/Null";
		item.clientIP = ctx.getClientIp();
		//当无法取到sessionId时，直接取ip(当服务找不到时)
		item.client = sessionId != null && sessionId.length() > 0 ? sessionId : item.clientIP;
		//item.host = ctx.getHost();
		item.result = ctx.getReturnCode();
		item.reason = ctx.getReason();
		item.startTime = ctx.getTimestamp();
		item.duration = ctx.getDuration();
		item.url = ctx.getRequestURI();
		item.content = logType == ServiceDescription.LogType.detail ? ctx.toString() : null;
		item.contentLength = ctx.getContentLength();
		
		bizLogger.handle(item,System.currentTimeMillis());
				
		return 0;
	}
	
	protected static int execute(Servant servant,Context ctx) throws Exception {
		servant.actionBefore( ctx);
		servant.actionProcess( ctx);
		servant.actionAfter( ctx);
		return 0;
	}
	
	protected static boolean threadMode = true;
	protected static boolean tracerEnable = false;
	protected static BizLogger bizLogger = null;
	protected static ServantFactory servantFactory = null;
	protected static boolean bizlogEnable = true;
	protected static boolean acmEnable = true;
	static {
		Settings settings = Settings.get();
		
		//初始化threadMode
		threadMode = PropertiesConstants.getBoolean(settings, "servant.threadMode", true);
		tracerEnable = PropertiesConstants.getBoolean(settings, "tracer.servant.enable", false);
		bizlogEnable = PropertiesConstants.getBoolean(settings, "bizlog.enable", true);
		acmEnable = PropertiesConstants.getBoolean(settings, "acm.enable", true);
		bizLogger = (BizLogger) settings.get("bizLogger");
		servantFactory = (ServantFactory) settings.get("servantFactory");
	}
}
