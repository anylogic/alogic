package com.alogic.rpc.serializer.gson.util;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.alogic.rpc.InvokeContext;
import com.alogic.rpc.Parameters;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

/**
 * 对rpc中Parameters进行序列化和反序列化
 * 
 * @author yyduan
 * @since 1.6.8.7
 */
public class ParametersSerializer2 extends Serializer<Parameters>{
	protected Class<?> requestClazz = null;
	
	@Override
	public void configure(Properties p) {

	}	
	
	public void resetRequestClass(Class<?> clazz){
		requestClazz = clazz;
	}
	
	@Override
	public JsonElement serialize(Parameters src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject ret = new JsonObject();
		
		setAttr(ret,"sn",src.sn());
		setAttr(ret,"order",src.order());
		
		InvokeContext ctx = src.context();
		if (ctx != null){
			setChild(ret, "ctx", ctx, context);
		}
		
		JsonObject contractRoot = new JsonObject();
		
		JsonObject svcCont = new JsonObject();
		
		Object[] params = src.params();
		if (params != null && params.length > 0){
			svcCont.add("requestObject", context.serialize(params[0]));
			setAttr(svcCont,"requestType",params[0].getClass().getName());
		}
		
		ret.add("contractRoot", contractRoot);
		
		return ret;
	}

	@Override
	public Parameters deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		Parameters ret = new Parameters.Default();
		
		JsonObject obj = json.getAsJsonObject();
		ret.sn(getAttr(obj,"sn",""));
		ret.order(getAttr(obj,"order",""));

		JsonObject contractRoot = obj.getAsJsonObject("contractRoot");
		if (contractRoot != null){
			JsonObject svcCont = contractRoot.getAsJsonObject("svcCont");
			if (svcCont != null){
				String module = getAttr(svcCont,"requestType","");
				LOG.info("The module is " + module);
				if (StringUtils.isNotEmpty(module)){
					try {
						Class<?> clazz = Settings.getClassLoader().loadClass(module);
						JsonObject data = svcCont.getAsJsonObject("requestObject");
						if (data != null){
							ret.params(context.deserialize(data, clazz));
						}
					}catch (Exception ex){
						LOG.error("Can not find result class " + module);
					}
				}else{
					if (requestClazz != null){
						JsonObject data = svcCont.getAsJsonObject("requestObject");
						if (data != null){
							ret.params(context.deserialize(data, requestClazz));
						}
					}else{
						LOG.error("I do not known the request type");
					}
				}
			}else{
				LOG.error("Can not find svcCont node");
			}
		}else{
			LOG.error("Can not find contractRoot node");
		}
		
		JsonObject ctx = obj.getAsJsonObject("ctx");
		if (ctx != null){
			InvokeContext instance = context.deserialize(ctx, InvokeContext.Default.class);
			if (instance != null){
				ret.context(instance);
			}
		}
		
		return ret;
	}
}