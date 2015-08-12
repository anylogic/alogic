package com.alogic.timer.demo;

import com.alogic.timer.core.Scheduler;
import com.alogic.timer.core.ThreadPoolTaskCommitter;
import com.alogic.timer.matcher.Interval;
import com.alogic.timer.matcher.Once;

public class Simple {

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler.Simple();
		scheduler.setTaskCommitter(new ThreadPoolTaskCommitter());
		
		scheduler.schedule("testOnce", new Once(), new Runnable(){
			public void run() {
				System.out.println("This will be scheduled once.");
			}
		});
		
		scheduler.schedule("testMore", new Interval(1000), new Runnable(){
			public void run() {
				System.out.println("testMore.");
			}
		});
		
		scheduler.start();
		
		scheduler.join(20000);
		
		scheduler.stop();
	}
}
