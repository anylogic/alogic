package com.alogic.sda;

import com.alogic.load.Loader;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;


/**
 * SDA工厂
 * 
 * 用于从指定的配置文件中装入SDA装载链
 * 
 * @author yyduan
 *
 * @since 1.6.10.8
 */
public class SDAFactory extends Loader.Manager<SecretDataArea>{

	/**
	 * 缺省的实例
	 */
	private static Loader<SecretDataArea> INSTANCE = null;
	
	/**
	 * 缺省配置文件地址
	 */
	protected static final String DEFAULT = "java:///com/alogic/sda/sda.default.xml#";
			
	@Override
	protected String getDefaultClass() {
		return "";
	}
	
	/**
	 * 获取一个缺省的Loader
	 * 
	 * 缺省Loader的配置文件通过环境变量sda.master和sda.secondary指定
	 * 
	 * @return Loader实例
	 */
	public static Loader<SecretDataArea> getDefault(){
		if (INSTANCE == null){
			synchronized (SDAFactory.class){
				if (INSTANCE == null){
					Settings p = Settings.get();
					String master = PropertiesConstants.getString(p, "sda.master", DEFAULT);
					String secondary = PropertiesConstants.getString(p, "sda.secondary", DEFAULT);					
					SDAFactory f = new SDAFactory();					
					INSTANCE = f.loadFrom(master, secondary);
				}
			}
		}
		return INSTANCE;
	}
}
