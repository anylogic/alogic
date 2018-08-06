package com.logicbus.backend;

import org.apache.commons.lang3.StringUtils;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 混合模式访问控制器
 * @author yyduan
 * @since 1.6.11.39
 * 
 * @version 1.6.11.47 [20180806 duanyy] <br>
 * - 修正配置参数取值问题 <br>
 */
public class MixedAccessController extends HybirdAccessController{
	/**
	 * 读取acGroupId的参数id
	 */
	protected String acGroupKeyId = "x-alogic-ac";
	
	/**
	 * any
	 */
	protected String acGroupAny = "any";
	
	@Override
	protected String getGroupId(Path serviceId, ServiceDescription servant,Context ctx){
		String serviceAcGroup = servant.getAcGroup();
		if (acGroupAny.equals(serviceAcGroup)){
			String clientAcGroupId = ctx.getRequestHeader(acGroupKeyId);
			if (StringUtils.isNotEmpty(clientAcGroupId)){
				return clientAcGroupId;
			}
		}		
		return serviceAcGroup;
	}
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		acGroupKeyId = PropertiesConstants.getString(p,"acGroupKeyId",acGroupKeyId);
		acGroupAny = PropertiesConstants.getString(p,"acGroupAny",acGroupAny);
	}	
}
