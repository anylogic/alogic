package com.alogic.rpc.serializer.gson.util;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Properties;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * 基于gson的序列化和反序列化器
 * @author yyduan
 *
 * @param <D>
 * 
 * @since 1.6.8.7
 */
public abstract class Serializer <D> implements JsonSerializer<D>,JsonDeserializer<D>,Configurable,XMLConfigurable{
	/**
	 * a logger of slf4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(Serializer.class);
	protected void setAttr(JsonObject json,String id,String value){
		if (json != null){
			json.addProperty(id, value);
		}
	}
	
	protected void setAttr(JsonObject json,String id,long value){
		if (json != null){
			json.addProperty(id, value);
		}
	}
	
	protected void setAttr(JsonObject json,String id,int value){
		if (json != null){
			json.addProperty(id, value);
		}
	}
	
	protected void setAttr(JsonObject json,String id,boolean value){
		if (json != null){
			json.addProperty(id, value);
		}
	}	
	
	protected <C> void setChild(JsonObject json,String id,C child,JsonSerializationContext context){
		if (json != null){
			if (child == null){
				json.add(id, JsonNull.INSTANCE);
			}else{
				JsonElement tJson = context.serialize(child, child.getClass());
				if (tJson != null){
					json.add(id, tJson);
				}
			}
		}
	}
	
	protected <T> T getChild(JsonObject json,String id,Type typeOfT,JsonDeserializationContext context){
		JsonObject child = json.getAsJsonObject(id);
		if (child == null){
			return null;
		}
		T ret = context.deserialize(child, typeOfT);
		return ret;
	}
	
	protected String getAttr(JsonObject json,String id,String dft){
		JsonElement attr = json.get(id);
		if (attr == null){
			return dft;
		}			
		return attr.getAsString();
	}
	
	protected long getAttr(JsonObject json,String id,long dft){
		JsonElement attr = json.get(id);
		if (attr == null){
			return dft;
		}			
		return attr.getAsLong();
	}
	
	protected int getAttr(JsonObject json,String id,int dft){
		JsonElement attr = json.get(id);
		if (attr == null){
			return dft;
		}			
		return attr.getAsInt();
	}
	
	protected boolean getAttr(JsonObject json,String id,boolean dft){
		JsonElement attr = json.get(id);
		if (attr == null){
			return dft;
		}			
		return attr.getAsBoolean();
	}

	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		configure(props);
	}	
}