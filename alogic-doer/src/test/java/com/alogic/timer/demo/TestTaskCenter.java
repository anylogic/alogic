package com.alogic.timer.demo;

import com.alogic.doer.client.TaskCenterTaskCommitter;
import com.alogic.timer.core.Doer;
import com.alogic.timer.core.Scheduler;
import com.alogic.timer.matcher.Interval;
import com.anysoft.util.Settings;

public class TestTaskCenter {
	public static void main(String[] args) {
		Settings settings = Settings.get();
		settings.SetValue("doer.master","java:///com/alogic/doer/demo/doer.demo.xml#com.alogic.doer.demo.Demo");		
		
		Scheduler scheduler = new Scheduler.Simple();
		scheduler.setTaskCommitter(new TaskCenterTaskCommitter());
	
		scheduler.schedule("demo", new Interval(1000), new Doer.Quiet());
		
		scheduler.start();
		
		scheduler.join(20000);
		
		scheduler.stop();
	}
}
