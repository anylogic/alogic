package com.logicbus.backend.timer;

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.DateUtil;

public class DefaultLogListener implements TimerLogListener {
	protected static Logger logger = LogManager.getLogger(DefaultLogListener.class);
	public void logArrived(Timer timer, TimerLog log) {
		StringBuffer msg = new StringBuffer();
		msg.append("[");
		msg.append(DateUtil.formatDate(new Date(log.createTime),"yy/MM/dd HH:mm:ss"));
		msg.append("]");
		msg.append("[");
		msg.append(log.type);
		msg.append("]");
		msg.append(log.note);
		msg.append("(");
		msg.append(log.context);
		msg.append(")");
		synchronized (lock){
			logger.info(msg);
		}
	}
	protected static Object lock = new Object();
	
	public void logFlush() {
		// TODO Auto-generated method stub
		
	}
}
