package com.alogic.ac;

import org.w3c.dom.Element;

import com.alogic.ac.loader.acm.ACMCached;
import com.alogic.load.Loader;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于ACM访问控制器的虚基类
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public class ACMAccessController extends AbstractACMAccessController {

	/**
	 * ACM信息装载器
	 */
	protected Loader<AccessControlModel> acmLoader = null;
	
	/**
	 * 缺省的ACM
	 */
	protected AccessControlModel defaultAcm = null;
	
	@Override
	public void configure(Element e, Properties props) {
		XmlElementProperties p = new XmlElementProperties(e,props);
		
		Element acmElem = XmlTools.getFirstElementByPath(e, "acm");
		if (acmElem != null){
			Factory<Loader<AccessControlModel>> f = new Factory<Loader<AccessControlModel>>();
			try {
				acmLoader = f.newInstance(acmElem, p, "loader", ACMCached.class.getName());
				defaultAcm = acmLoader.load(PropertiesConstants.getString(props, "acm.default", "anonymous"), true);
			}catch (Exception ex){
				LOG.error("Can not create loader from element:" + XmlTools.node2String(acmElem));
			}
		}
		
		onConfigure(e,p);
	}
	
	/**
	 * Configure时间处理
	 * @param e XMLElement
	 * @param p 变量集
	 */
	protected void onConfigure(Element e, Properties p) {		
		configure(p);
	}

	@Override
	public void reload(String id) {
		if (acmLoader != null){
			AccessControlModel acm = acmLoader.load(id, false);
			if (acm == null){
				LOG.warn(String.format("ACM %s does not exist.",id));
			}else{
				LOG.info(String.format("ACM %s is reloaded.",id));
			}
		}
	}

	@Override
	public String createSessionId(Path serviceId, ServiceDescription servant,
			Context ctx) {
		return getClientIp(ctx);
	}

	@Override
	protected AccessControlModel getACM(String sessionId, Path serviceId,
			ServiceDescription servant, Context ctx) {
		AccessControlModel acm =  acmLoader == null ? null : acmLoader.load(sessionId, true);
		return acm == null ? defaultAcm : acm;
	}

}
