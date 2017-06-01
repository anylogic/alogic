package com.alogic.doer.demo;

import com.alogic.doer.client.TaskSubmitter;
import com.alogic.doer.core.TaskCenter;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Settings;

public class Demo {

	public static void main(String[] args) {
		Settings p = Settings.get();
		p.SetValue("tc.master", 
				"java:///com/alogic/doer/demo/doer.demo.xml#" + Demo.class.getName());
		
		TaskCenter tc = TaskCenter.TheFactory.get();
		
		tc.start();
		
		TaskSubmitter.submit("hello", new DefaultProperties());
		TaskSubmitter.submit("hello", new DefaultProperties());
		TaskSubmitter.submit("hello", new DefaultProperties());
		TaskSubmitter.submit("hello", new DefaultProperties());
		TaskSubmitter.submit("hello", new DefaultProperties());
		TaskSubmitter.submit("hello", new DefaultProperties());
		
		tc.join(10000);
		
		tc.stop();
	}

}
