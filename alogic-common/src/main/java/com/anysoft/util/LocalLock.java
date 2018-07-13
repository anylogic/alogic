package com.anysoft.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地锁
 * @author yyduan
 * @since 1.6.11.44 
 */
public class LocalLock implements Lock {
	/**
	 * logger of slf
	 */
	protected static Logger logger = LoggerFactory.getLogger(LocalLock.class);
	
	/**
	 * 对象id
	 */
	protected String name;
	
	/**
	 * 等待锁时的睡眠间隔
	 */
	protected long interval = 2000L;
	
	protected static Set<String> locks = new HashSet<String>();

	public LocalLock(String name,Properties props){
		this.name = name;
		logger.info("Using " + getClass().getName() + " to lock.");
	}	
	
	@Override
	public void lock() {
		try {
			lockInterruptibly();
		} catch (InterruptedException e) {
			logger.info("Lock waiting thread has been interrupted,exit",e);
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		if (tryLock()){
			return ;
		}
		while (!tryLock()){
			try {
				logger.info("Can not get the lock . Waiting ....");
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				throw e;
			}
		}
	}

	@Override
	public Condition newCondition() {
		return null;
	}

	@Override
	public boolean tryLock() {
		boolean got =  lockId(name);
		if (got){
			logger.info("Got the lock..");
		}
		return got;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		if (tryLock()){
			return true;
		}
		
		//等待
		try {
			logger.info("Can not get the lock . Waiting ....");
			Thread.sleep(TimeUnit.MILLISECONDS.convert(time, unit));
		} catch (InterruptedException e) {
			throw e;
		}		

		return tryLock();
	}

	@Override
	public void unlock() {
		unlockId(name);
	}

	public static synchronized boolean lockId(String id){
		boolean exist =  locks.contains(id);
		if (exist){
			return false;
		}
		
		locks.add(id);
		return true;
	}
	
	public static synchronized boolean unlockId(String id){
		return locks.remove(id);
	}
}
