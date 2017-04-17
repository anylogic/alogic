package com.alogic.rpc.serializer.gson.util;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.alogic.rpc.InvokeContext;
import com.alogic.rpc.Parameters;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.google.gson.JsonArray;
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
public class ParametersSerializer extends Serializer<Parameters>{
	
	@Override
	public void configure(Properties p) {
		// nothing to do
	}	
	
	@Override
	public JsonElement serialize(Parameters src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject ret = new JsonObject();
		
		setAttr(ret,"sn",src.sn());
		setAttr(ret,"order",src.order());
		
		Object[] params = src.params();
		if (params != null && params.length > 0){
			JsonArray array = new JsonArray();
			for (int i = 0 ;i < params.length ; i ++){
				Object p = params[i];
				JsonObject item = new JsonObject();
				setAttr(item,"t",p.getClass().getName());
				setChild(item,"v",p,context);
				array.add(item);
			}
			ret.add("params", array);
		}
		
		InvokeContext ctx = src.context();
		if (ctx != null){
			setChild(ret, "ctx", ctx, context);
		}
		
		return ret;
	}

	@Override
	public Parameters deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		Parameters ret = new Parameters.Default();
		
		JsonObject obj = json.getAsJsonObject();
		ret.sn(getAttr(obj,"sn",""));
		ret.order(getAttr(obj,"order",""));
		
		JsonArray params = obj.getAsJsonArray("params");
		if (params != null){
			for (int i = 0 ;i < params.size() ; i ++){
				JsonObject p = params.get(i).getAsJsonObject();
				String module = getAttr(p,"t","");
				if (StringUtils.isNotEmpty(module)){
					try {
						Class<?> clazz = Settings.getClassLoader().loadClass(module);
						JsonElement v = p.get("v");
						if (v != null){
							ret.params(context.deserialize(v, clazz));
						}
					} catch (ClassNotFoundException e) {
						LOG.error("Can not load class.",e);
					}					
				}
			}
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
