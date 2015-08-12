package com.alogic.timer.demo;

import com.alogic.timer.core.Scheduler;
import com.alogic.timer.core.SchedulerFactory;
import com.alogic.timer.matcher.Interval;
import com.alogic.timer.matcher.Once;

public class TestFactory {

	public static void main(String[] args) {
		Scheduler scheduler = SchedulerFactory.get();
		scheduler.schedule("testOnce", new Once(), new Runnable(){
			public void run() {
				System.out.println("This will be scheduled once.");
			}
		});
		
		scheduler.schedule("testMore", new Interval(1000), new Runnable(){
			private int count = 0;
			public void run() {
				System.out.println("the count is " + count ++);
			}
		});
		
		scheduler.start();

		scheduler.join(20000);

		scheduler.stop();
	}

}
