package com.alogic.tlog;

import java.lang.reflect.Field;

import com.anysoft.stream.Flowable;

/**
 * Trace日志记录
 * 
 * @author yyduan
 *
 * @since 1.6.7.3
 */
public class TLog implements Comparable<TLog>,Flowable{
	protected static final String PATTERN = 
			"%s|%s|%s|%s|%d|%d|%d|%s|%s";
	/**
	 * 序列号
	 */
	public String sn;
	
	/**
	 * 调用次序
	 */
	public String order;
	
	/**
	 * 方法类型
	 */
	public String type;
	
	/**
	 * 方法
	 */
	public String method;
	
	/**
	 * 开始时间
	 */
	public long startDate;
	
	/**
	 * 调用时长
	 */
	public long duration;
	
	/**
	 * 结果代码
	 */
	public String code;
	
	/**
	 * 原因
	 */
	public String reason;
	
	/**
	 * 内容长度
	 */
	public long contentLength;
	
	@Override
	public String id(){
		return sn;
	}	
	
	public String toString(){
		return String.format(PATTERN, sn,order,type,method,startDate,duration,contentLength,code,reason);
	}	
	
	@Override
	public String getValue(String varName, Object context, String defaultValue) {
		try {
			Class<?> clazz = this.getClass();
			Field field = clazz.getField(varName);
			if (field == null){
				return defaultValue;
			}
			Object found = field.get(this);
			return found.toString();
		}catch (Exception ex){
			return defaultValue;
		}
	}

	@Override
	public String getRawValue(String varName, Object context, String dftValue) {
		return getValue(varName,context,dftValue);
	}

	@Override
	public Object getContext(String varName) {
		return null;
	}

	@Override
	public String getStatsDimesion() {
		return code;
	}

	@Override
	public int compareTo(TLog o) {
		int ret = sn.compareTo(o.sn);
		if (ret == 0){
			ret = order.compareTo(o.order);
		}
		return ret;
	}

	public TLog sn(String sn) {
		this.sn = sn;
		return this;
	}
	
	public String sn(){
		return sn;
	}

	public TLog order(String order) {
		this.order = order;
		return this;
	}
	
	public String order(){
		return order;
	}
	
	public String type(){
		return type;
	}
	
	public TLog type(String type){
		this.type = type;
		return this;
	}

	public TLog method(String method) {
		this.method = method;
		return this;
	}
	
	public String method(){
		return method;
	}

	public TLog startDate(long startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public long startDate(){
		return startDate;
	}

	public TLog duration(long duration) {
		this.duration = duration;
		return this;
	}
	
	public long duration(){
		return duration;
	}

	public TLog code(String code) {
		this.code = code;
		return this;
	}
	
	public String code(){
		return code;
	}

	public TLog reason(String reason) {
		this.reason = reason;
		return this;
	}
	
	public String reason(){
		return reason;
	}
	
	public TLog contentLength(long length){
		this.contentLength = length;
		return this;
	}
	
	public long contentLength(){
		return this.contentLength;
	}

}
