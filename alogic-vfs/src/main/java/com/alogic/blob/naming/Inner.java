package com.alogic.blob.naming;

import com.alogic.blob.BlobManager;
import com.alogic.blob.vfs.VFSBlobManager;
import com.alogic.naming.context.XmlInner;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * XML内部定义
 * 
 * @author yyduan
 *
 */
public class Inner extends XmlInner<BlobManager>{
	
	/**
	 * 缺省类
	 */
	protected String dftClass = VFSBlobManager.class.getName();

	@Override
	public void configure(Properties p) {
		dftClass = PropertiesConstants.getString(p,"dftClass", dftClass);
	}

	@Override
	public String getObjectName() {
		return "blob";
	}

	@Override
	public String getDefaultClass() {
		return dftClass;
	}

}
