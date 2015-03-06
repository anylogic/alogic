package com.alogic.doer.demo;

import java.util.HashMap;
import java.util.Map;

import com.alogic.doer.client.TaskSubmitter;
import com.anysoft.util.Settings;

public class Demo {

	public static void main(String[] args) {
		Settings settings = Settings.get();
		settings.SetValue("doer.master","java:///com/alogic/doer/demo/doer.demo.xml#com.alogic.doer.demo.Demo");
		
		Map<String,String> parameters = new HashMap<String,String>();
		
		parameters.put("name", "yyduan");
		
		TaskSubmitter.submit("job", "demo", parameters);
		TaskSubmitter.submit("job1", "demo", parameters);
		TaskSubmitter.submit("job2", "demo", parameters);
		TaskSubmitter.submit("job3", "demo", parameters);
	}

}
