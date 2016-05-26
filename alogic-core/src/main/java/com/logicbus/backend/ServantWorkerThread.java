package com.logicbus.backend;

import java.util.concurrent.CountDownLatch;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;

/**
 * 服务员工作线程
 * 
 * @author duanyy
 * @version 1.0.2 [20140407 duanyy]<br>
 * - 采用{@link java.util.concurrent.CountDownLatch CountDownLatch}来和主进程通讯.<br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - Servant体系抛弃MessageDoc <br>
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - 淘汰MessageDoc，采用Context替代 <br>
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - bizlog增加报文长度 <br>
 * - 在action中提前写出报文 <br>
 * - 增加trace日志 <br>
 * 
 * @version 1.6.5.7 [20160525 duanyy] <br>
 * - trace可选择关闭 <br>
 */
public class ServantWorkerThread extends Thread {
	/**
	 * 当前工作的服务员
	 */
	private Servant m_servant = null;
	
	/**
	 * Count Down Latch
	 */
	protected CountDownLatch latch = null;
	
	/**
	 * 上下文
	 */
	private Context m_ctx = null;
	
	private TraceContext traceCtx = null;
	
	public ServantWorkerThread(Servant _servant,Context _ctx,CountDownLatch _latch,TraceContext trace){
		m_servant = _servant;
		m_ctx = _ctx;
		latch = _latch;
		traceCtx = trace;
	}

	
	/**
	 * 线程运行主函数
	 */
	public void run(){
		TraceContext tc = null;
		if (traceCtx != null){
			tc = Tool.start(traceCtx.sn(), traceCtx.order());
		}
		boolean error = false;
		try
		{
			m_servant.actionBefore(m_ctx);
			m_servant.actionProcess(m_ctx);
			m_servant.actionAfter(m_ctx);
		}catch (ServantException ex){
			error = true;
			ex.printStackTrace();
			m_servant.actionException(m_ctx ,ex);
		}catch (Exception ex){
			error = true;
			ex.printStackTrace();
			m_servant.actionException( m_ctx, 
					new ServantException("core.fatalerror",ex.getMessage()));
		}catch (Throwable t){
			error = true;
			t.printStackTrace();
			m_servant.actionException( m_ctx, 
					new ServantException("core.fatalerror",t.getMessage()));
		}finally{
			if (latch != null){
				//告知，事情已经做完
				latch.countDown();
			}
			if (traceCtx != null){
				Tool.end(tc, "ALOGIC", "SyncCall", error?"FAILED":"OK", "");
			}
		}
	}
}
