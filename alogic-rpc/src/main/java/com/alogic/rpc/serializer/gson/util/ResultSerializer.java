package com.alogic.rpc.serializer.gson.util;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.alogic.rpc.Result;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

/**
 * 对rpc中Result进行序列化和反序列化
 * 
 * @author yyduan
 * @since 1.6.8.7
 */
public class ResultSerializer extends Serializer<Result>{
	
	@Override
	public void configure(Properties p) {
		// nothing to do
	}	
	
	@Override
	public Result deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		Result ret = new Result.Default();
		JsonObject obj = json.getAsJsonObject();
		ret.result(getAttr(obj,"code","core.ok"), getAttr(obj,"reason","It's ok"), getAttr(obj,"duration",0));
		ret.sn(getAttr(obj,"sn",""),getAttr(obj,"order",""));
		Throwable t = getChild(obj, "throw", Throwable.class, context);
		ret.setThrowable(t);
		
		JsonObject data = obj.getAsJsonObject("return");
		if (data != null){
			String module = getAttr(data,"t","");
			if (StringUtils.isNotEmpty(module)){
				try {
					Class<?> clazz = Settings.getClassLoader().loadClass(module);
					JsonElement v = data.get("v");
					if (v != null){
						ret.ret(context.deserialize(v, clazz));
					}
				} catch (ClassNotFoundException e) {
					LOG.error("Can not load class.",e);
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
		
		setAttr(ret,"sn",src.sn());
		setAttr(ret,"order",src.order());
		
		setChild(ret,"throw",src.getThrowable(),context);
		
		Object data = src.ret();
		if (data != null){
			JsonObject returnObject = new JsonObject();
			setChild(returnObject,"v",data,context);
			setAttr(returnObject,"t",data.getClass().getName());
			ret.add("return", returnObject);
		}
		return ret;
	}		
}	