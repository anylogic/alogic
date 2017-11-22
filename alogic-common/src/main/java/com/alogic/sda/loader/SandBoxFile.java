package com.alogic.sda.loader;

import com.alogic.sda.SecretDataArea;
import com.alogic.sda.model.Default;
import com.alogic.load.Loader;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;


/**
 * 从沙箱文件中装入
 * 
 * @author yyduan
 * @since 1.6.10.8
 */
public class SandBoxFile extends Loader.HotFile<SecretDataArea>{
	protected String dftClass = Default.class.getName();
	@Override
	protected String getObjectDftClass() {
		return dftClass;
	}
	
	@Override
	public void configure(Properties p){
		dftClass = PropertiesConstants.getString(p, "dftClass", "");
		super.configure(p);
	}
}
