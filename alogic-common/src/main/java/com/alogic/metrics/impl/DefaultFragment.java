package com.alogic.metrics.impl;

import java.util.Map;

import com.alogic.metrics.core.Dimensions;
import com.alogic.metrics.core.Fragment;
import com.alogic.metrics.core.Measures;


/**
 * 缺省实现
 * @author duanyy
 *
 */
public class DefaultFragment implements Fragment{
	/**
	 * id
	 */
	protected String id;
	
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

	public DefaultFragment(String mId){
		id = mId;
	}
	
	@Override
	public void toJson(Map<String, Object> json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fromJson(Map<String, Object> json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getStatsDimesion() {
		return (dims != null) ? (id + ":" + dims.toString()) : id;
	}

	@Override
	public String getValue(String varName, Object context, String defaultValue) {
		return (dims == null) ? defaultValue : dims.getValue(varName, context, defaultValue);
	}

	@Override
	public String getRawValue(String varName, Object context, String dftValue) {
		return (dims == null) ? dftValue : dims.getRawValue(varName, context, dftValue);
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
}
