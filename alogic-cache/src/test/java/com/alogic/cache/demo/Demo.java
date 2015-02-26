package com.alogic.cache.demo;

import com.alogic.cache.context.CacheSource;
import com.alogic.cache.core.CacheStore;
import com.alogic.cache.core.MultiFieldObject;
import com.anysoft.util.Settings;

public class Demo {

	public static void main(String[] args) {
		Settings settings = Settings.get();
		
		settings.SetValue("cache.master", 
				"java:///com/alogic/cache/demo/cache.demo.xml#com.alogic.cache.context.CacheSource");
		
		CacheStore cache = CacheSource.get().get("state");
		
		for (int i = 0 ; i < 10 ; i ++){
			MultiFieldObject value = cache.load("S0C", true);
			if (value != null)
				System.out.println(value.toString());
		}
		
		
	}

}
