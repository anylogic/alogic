package com.logicbus.backend.acm;

import com.anysoft.cache.CacheManager;
import com.anysoft.cache.Provider;
import com.anysoft.util.Factory;
import com.anysoft.util.Settings;

/**
 * ACM的缓存管理器
 * @author duanyy
 *
 * @since 1.2.3
 */
public class ACMCacheManager extends CacheManager<AccessControlModel> {

	public ACMCacheManager(Provider<AccessControlModel> provider){
		super(provider);
	}
	
	synchronized public static ACMCacheManager get(){
		if (instance == null){
			Settings settings = Settings.get();
			
			String className = settings.GetValue("acm.provider", "com.logicbus.backend.acm.XMLResourceACMProvider");
			
			ClassLoader cl = (ClassLoader)settings.get("classLoader");
			
			Factory<Provider<AccessControlModel>> factory = new Factory<Provider<AccessControlModel>>(cl);
			
			Provider<AccessControlModel> provider = factory.newInstance(className, settings);
			
			instance = new ACMCacheManager(provider);
		}
		return instance;
	}
	
	private static ACMCacheManager instance = null;
}
