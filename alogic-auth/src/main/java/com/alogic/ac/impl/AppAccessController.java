package com.alogic.ac.impl;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.ac.ACMAccessController;
import com.alogic.ac.AccessAppKey;
import com.alogic.ac.AccessVerifier;
import com.alogic.ac.loader.aak.AAKCached;
import com.alogic.ac.loader.av.AVCached;
import com.alogic.load.Loader;
import com.anysoft.util.BaseException;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;
import com.logicbus.backend.Context;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 基于AppKey验证的访问控制器
 * 
 * @author yyduan
 * @since 1.6.10.6
 */
public class AppAccessController extends ACMAccessController{
	
	/**
	 * App Key 装载器
	 */
	protected Loader<AccessAppKey> aakLoader = null;
	
	/**
	 * Access Verifier 装载器
	 */
	protected Loader<AccessVerifier> avLoader = null;
	
	/**
	 * 读取appKey的参数id
	 */
	protected String appKeyId = "x-alogic-app";
	
	/**
	 * 缺省的会话id
	 */
	protected String dftSessionId = "anonymous";
	
	@Override
	public void configure(Properties props) {		
		super.configure(props);
		
		appKeyId = PropertiesConstants.getString(props, "appKeyId", appKeyId);
		dftSessionId = PropertiesConstants.getString(props, "dftAppId", dftSessionId);
	}
	
	@Override
	protected void onConfigure(Element e, Properties p) {		
		configure(p);
		
		Element aakElem = XmlTools.getFirstElementByPath(e, "aak");
		if (aakElem != null){
			Factory<Loader<AccessAppKey>> f = new Factory<Loader<AccessAppKey>>();
			try {
				aakLoader = f.newInstance(aakElem, p, "loader", AAKCached.class.getName());
			}catch (Exception ex){
				LOG.error("Can not create loader from element:" + XmlTools.node2String(aakElem));
			}
		}
		
		Element avElem = XmlTools.getFirstElementByPath(e, "verifier");
		if (avElem != null){
			Factory<Loader<AccessVerifier>> f = new Factory<Loader<AccessVerifier>>();
			try {
				avLoader = f.newInstance(avElem, p, "loader", AVCached.class.getName());
			}catch (Exception ex){
				LOG.error("Can not create loader from element:" + XmlTools.node2String(aakElem));
			}
		}
	}
	
	@Override
	public String createSessionId(Path serviceId, ServiceDescription servant,
			Context ctx) {
		if (servant.getVisible().equals("protected")){
			String appKey = ctx.getRequestHeader(appKeyId);
			if (StringUtils.isEmpty(appKey)){
				appKey = PropertiesConstants.getRaw(ctx, appKeyId, "");
			}
			
			if (StringUtils.isEmpty(appKey)){
				return getClientIp(ctx);
			}
			
			AccessAppKey keyInfo = findAppKeyInfo(appKey);
			if (keyInfo == null){
				throw new BaseException("clnt.e2004",String.format("AppKey %s does not exist", appKey));
			}		
			
			AccessVerifier verifier = findAccessVerifier(keyInfo.getVerifier());
			if (verifier == null){
				throw new BaseException("core.e1200",String.format("Current verifier %s is not valid.", keyInfo.getVerifier()));
			}
			
			if (!verifier.verify(keyInfo, ctx)){
				throw new BaseException("clnt.e2005",String.format("Failed to verify by app key %s",appKey));
			}
			
			return keyInfo.getAppId();
		}else{
			return dftSessionId;
		}
	}
	
	/**
	 * 在装载器中查找AppKey信息
	 * @param appKey app key
	 * @return AppKey信息
	 */
	protected AccessAppKey findAppKeyInfo(String appKey){
		return aakLoader == null ? null : aakLoader.load(appKey, true);
	}
	
	/**
	 * 在装载器中查找访问验证器
	 * @param id 验证器id
	 * @return 验证器实例
	 */
	protected AccessVerifier findAccessVerifier(String id){
		return avLoader == null ? null : avLoader.load(id, true);
	}
}