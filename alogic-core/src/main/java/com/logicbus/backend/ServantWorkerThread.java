package com.logicbus.backend;

import java.util.concurrent.CountDownLatch;

import com.logicbus.backend.message.MessageDoc;

/**
 * 服务员工作线程
 * 
 * @author duanyy
 * @version 1.0.2 [20140407 duanyy]<br>
 * - 采用{@link java.util.concurrent.CountDownLatch CountDownLatch}来和主进程通讯.<br>
 */
public class ServantWorkerThread extends Thread {
	/**
	 * 当前工作的服务员
	 */
	private Servant m_servant = null;
	
	/**
	 * 构造函数
	 * @param _servant 当前工作服务员
	 * @param _doc 本次调用消息文档
	 * @param _ctx 本次调用上下文
	 */
	public ServantWorkerThread(Servant _servant,MessageDoc _doc,Context _ctx,CountDownLatch _latch){
		m_servant = _servant;
		m_doc = _doc;
		m_ctx = _ctx;
		latch = _latch;
	}
	
	/**
	 * Count Down Latch
	 */
	protected CountDownLatch latch = null;
	
	/**
	 * 本次调用的消息文档
	 */
	private MessageDoc m_doc = null;
	
	/**
	 * 上下文
	 */
	private Context m_ctx = null;
	
	
	/**
	 * 线程运行主函数
	 */
	public void run(){
		try
		{
			m_servant.actionBefore(m_doc, m_ctx);
			m_servant.actionProcess(m_doc, m_ctx);
			m_servant.actionAfter(m_doc, m_ctx);
		}catch (ServantException ex){
			ex.printStackTrace();
			m_servant.actionException(m_doc, m_ctx ,ex);
		}catch (Exception ex){
			ex.printStackTrace();
			m_servant.actionException(m_doc, m_ctx, 
					new ServantException("core.fatalerror",ex.getMessage()));
		}catch (Throwable t){
			t.printStackTrace();
			m_servant.actionException(m_doc, m_ctx, 
					new ServantException("core.fatalerror",t.getMessage()));
		}finally{
			if (latch != null){
				//告知，事情已经做完
				latch.countDown();
			}
		}
	}
}
