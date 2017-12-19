package com.alogic.metrics.impl;

import java.util.HashMap;
import java.util.Map;

import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.Measures;
import com.anysoft.util.JsonTools;


/**
 * 缺省实现
 * @author duanyy
 *
 * @since 1.6.6.13
 *
 */
public class DefaultFragment implements Fragment{
	/**
	 * id
	 */
	protected String id = "default";
	
	/**
	 * 类型
	 */
	protected String type = "metrics";
	
	/**
	 * 维度
	 */
	protected Dimensions dims = null;

	/**
	 * 量度
	 */
	protected Measures meas = null;
	
	/**
	 * 时间戳
	 */
	protected long timestamp = System.currentTimeMillis();

	public DefaultFragment(){
		
	}
	
	public DefaultFragment(String mId){
		id = mId;
	}
	
	public DefaultFragment(String mId,String t){
		id = mId;
		type = t;
	}
	
	@Override
	public void toJson(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "id", id);
			JsonTools.setLong(json, "t", timestamp);
			JsonTools.setString(json,"type",type);
			JsonTools.setString(json, "v", "1.0");
			
			if (dims != null){
				dims.toJson(json);
			}
			
			if (meas != null){
				meas.toJson(json);
			}
		}
	}

	@Override
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			id = JsonTools.getString(json, "id", id);
			timestamp = JsonTools.getLong(json,"t",System.currentTimeMillis());
			type = JsonTools.getString(json, "type", type);
			
			Measures measures = getMeasures();
			measures.fromJson(json);
			
			Dimensions dimensions = getDimensions();
			dimensions.fromJson(json);
		}
	}
	
	@Override
	public Fragment incr(Fragment other) {
		getMeasures().incr(other.getMeasures());
		return this;
	}	

	@Override
	public String getStatsDimesion() {
		return (dims != null) ? (id + ":" + type + ":" + dims.toString()) : id + ":" + type;
	}

	@Override
	public String getValue(String varName, Object context, String defaultValue) {
		if ("mId".equals(varName)){
			return id;
		}
		if ("mType".equals(varName)){
			return type;
		}
		String found = null;
		if (dims != null){
			found = dims.getValue(varName, context, null);
		}
		
		if (found == null && meas != null){
			found = meas.getValue(varName, context, defaultValue);
		}
		return (found == null) ? defaultValue : found;
	}

	@Override
	public String getRawValue(String varName, Object context, String dftValue) {
		if ("mId".equals(varName)){
			return id;
		}
		if ("mType".equals(varName)){
			return type;
		}	
		
		String found = null;
		if (dims != null){
			found = dims.getRawValue(varName, context, null);
		}
		
		if (found == null && meas != null){
			found = meas.getRawValue(varName, context, dftValue);
		}
		return (found == null) ? dftValue : found;
	}

	@Override
	public Object getContext(String varName) {
		return this;
	}

	@Override
	public String id() {
		return id;
	}
	
	@Override
	public boolean isAsync(){
		return true;
	}
	
	@Override
	public String type(){
		return type;
	}
	
	public void id(String mId){
		id = mId;
	}
	
	public void type(String t){
		type = t;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public Dimensions getDimensions() {
		if (dims == null){
			dims = newDimensions();
		}
		
		return dims;
	}

	/**
	 * 创建量度列表实例
	 * @return 实例
	 */
	protected Dimensions newDimensions() {
		return new DefaultDimensions();
	}

	@Override
	public Measures getMeasures() {
		if (meas == null){
			meas = newMeasures();
		}
		return meas;
	}

	protected Measures newMeasures() {
		return new DefaultMeasures();
	}
	
	public static void main(String[] args){
		Fragment f1 = new DefaultFragment("metrics.host");
		
		f1.getDimensions()
		.set("host", "192.168.1.1", true)
		.set("app", "alogic", true);
		
		f1.getMeasures().set("times", 100L,Method.sum).set("error", 1);
		
		Fragment f2 = new DefaultFragment("metrics.host");
		
		f2.getDimensions()
		.set("host", "192.168.1.1", true)
		.set("app", "alogic", true);
		
		f2.getMeasures().set("times", 100L).set("error", 1).set("min", 10L);
		
		Fragment f3 = f1.incr(f2);
		Map<String,Object> result = new HashMap<String,Object>();
		f3.toJson(result);
		System.out.println(JsonTools.map2text(result));
	}

}
