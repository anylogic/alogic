package com.alogic.cache.demo;

import com.alogic.cache.context.CacheSource;
import com.alogic.cache.core.MultiFieldObject;
import com.anysoft.util.Settings;

public class Demo {

	public static void main(String[] args) {
		Settings settings = Settings.get();
		
		settings.SetValue("cache.master", 
				"java:///com/alogic/cache/demo/cache.demo.xml#com.alogic.cache.context.CacheSource");
		
		for (int i = 0 ; i < 10 ; i ++){
			MultiFieldObject value = CacheSource.get().getObject("state","S0A", true);
			if (value != null)
				System.out.println(value.toString());
		}
	}
}
