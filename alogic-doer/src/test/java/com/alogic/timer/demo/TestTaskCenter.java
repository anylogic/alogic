package com.alogic.timer.demo;

import com.alogic.doer.client.TaskCenterTaskCommitter;
import com.alogic.timer.core.Scheduler;
import com.alogic.timer.matcher.Interval;
import com.alogic.timer.matcher.Once;
import com.anysoft.util.Settings;

public class TestTaskCenter {
	public static void main(String[] args) {
		Settings settings = Settings.get();
		settings.SetValue("doer.master","java:///com/alogic/doer/demo/doer.demo.xml#com.alogic.doer.demo.Demo");		
		
		Scheduler scheduler = new Scheduler.Simple();
		scheduler.setTaskCommitter(new TaskCenterTaskCommitter());
		
		scheduler.schedule("demo2", new Once(), new Runnable(){
			public void run() {
				System.out.println("This will be scheduled once.");
			}
		});
		
		scheduler.schedule("demo", new Interval(1000), new Runnable(){
			public void run() {
				System.out.println("testMore.");
			}
		});
		
		scheduler.start();
		
		for (int i = 0 ; i < 20 ; i ++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		
		scheduler.stop();
	}
}
