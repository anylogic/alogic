package demo;

import java.util.concurrent.CountDownLatch;

import com.anysoft.util.Settings;
import com.logicbus.redis.util.RedisLock;

public class RedisLockDemo {

	public static void main(String[] args) {
		final Settings settings = Settings.get();
		
		settings.SetValue("redis.master", "java:///com/logicbus/redis/context/redis.xml#" + RedisLockDemo.class.getName());

		int threadCnt = 100;
		final CountDownLatch latch = new CountDownLatch(threadCnt);
		Runnable runnable = new Runnable(){

			@Override
			public void run() {
				RedisLock locker = new RedisLock("test",settings);
				
				try {
					if (locker.tryLock()){
						System.out.println(String.format("[%d]Got the lock..", Thread.currentThread().getId()));
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					latch.countDown();
					locker.unlock();
				}
			}
			
		};
		
		for (int i= 0 ;i < threadCnt ; i ++){
			(new Thread(runnable)).start();
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
