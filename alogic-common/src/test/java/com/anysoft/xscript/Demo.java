package com.anysoft.xscript;

import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;

public class Demo {

	public static void main(String[] args) {
		Statement stmt = XScriptTool.compile(
				"java:///com/anysoft/xscript/Helloworld.xml#com.anysoft.xscript.Demo", Settings.get());
		
		Properties p = new DefaultProperties("Default",Settings.get());
		
		p.SetValue("id", "alogic");
		p.SetValue("name", "alogic");
		
		XScriptTool.execute(stmt, p, new ExecuteWatcher.Default());
	}

}
