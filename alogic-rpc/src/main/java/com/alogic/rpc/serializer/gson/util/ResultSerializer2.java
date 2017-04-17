package com.alogic.rpc.serializer.gson.util;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.alogic.rpc.Result;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

/**
 * Result的序列化器2
 * 
 * @author yyduan
 * @since 1.6.8.7
 */
public class ResultSerializer2 extends Serializer<Result>{
	protected String sign = "${server.app}:${server.ip}:${server.port}";

	@Override
	public Result deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		Result ret = new Result.Default();
		JsonObject obj = json.getAsJsonObject();
		ret.result(getAttr(obj,"code","core.ok"), getAttr(obj,"reason","It's ok"), getAttr(obj,"duration",0));
		Throwable t = getChild(obj, "throw", Throwable.class, context);
		ret.setThrowable(t);
		
		JsonObject contractRoot = obj.getAsJsonObject("contractRoot");
		if (contractRoot != null){
			JsonObject svcCont = contractRoot.getAsJsonObject("svcCont");
			if (svcCont != null){
				String module = getAttr(svcCont,"resultType","");
				if (StringUtils.isNotEmpty(module)){
					try {
						Class<?> resultClazz = Settings.getClassLoader().loadClass(module);
						JsonObject data = svcCont.getAsJsonObject("resultObject");
						if (data != null){
							ret.ret(context.deserialize(data, resultClazz));
						}
					}catch (Exception ex){
						LOG.error("Can not find result class " + module);
					}
				}else{
					LOG.error("I do not known the result type");
				}
			}
		}
		return ret;
	}

	@Override
	public JsonElement serialize(Result src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject ret = new JsonObject();
		
		setAttr(ret,"code",src.code());
		setAttr(ret,"reason",src.reason());
		setAttr(ret,"host",src.host());
		setAttr(ret,"duration",src.duration());
		setChild(ret,"throw",src.getThrowable(),context);
		
		JsonObject contractRoot = new JsonObject();
		
		JsonObject tcpCont = new JsonObject();
		setAttr(tcpCont,"transactionId",src.sn());
		setAttr(tcpCont,"rspTime",System.currentTimeMillis());
		setAttr(tcpCont,"sign",sign);
		
		contractRoot.add("tcpCont", tcpCont);
		
		JsonObject svcCont = new JsonObject();
		setAttr(svcCont,"resultCode",src.code());
		setAttr(svcCont,"resultMsg",src.reason());
		
		Object data = src.ret();
		if (data != null){
			svcCont.add("resultObject", context.serialize(data));
			setAttr(svcCont,"resultType",data.getClass().getName());
		}
		contractRoot.add("svcCont", svcCont);
		ret.add("contractRoot", contractRoot);
		return ret;
	}

	@Override
	public void configure(Properties p) {
		// nothing to do
		sign = PropertiesConstants.getString(p,"rpc.sign",sign);
	}	
}	