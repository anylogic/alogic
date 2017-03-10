package com.alogic.tlog;

import java.lang.reflect.Field;
import java.util.Map;

import com.anysoft.stream.Flowable;
import com.anysoft.util.JsonSerializer;
import com.anysoft.util.JsonTools;

/**
 * Trace日志记录
 * 
 * @author yyduan
 *
 * @since 1.6.7.3
 * 
 * @version 1.6.7.21 [20170303 duanyy] <br>
 * - 增加parameter字段，便于调用者记录个性化参数 <br>
 * 
 * @version 1.6.7.24 [20170310 duanyy] <br>
 * - 增加app和host字段 <br>
 */
public class TLog implements Comparable<TLog>,Flowable,JsonSerializer{
	protected static final String PATTERN = 
			"%s|%s|%s|%s|%s|%s|%d|%d|%d|%s|%s|%s";
	/**
	 * 序列号
	 */
	public String sn;
	
	/**
	 * 调用次序
	 */
	public String order;
	
	/**
	 * 应用id
	 */
	public String app;
	
	/**
	 * 主机(ip:port)
	 */
	public String host;
	
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
	 * 参数
	 */
	public String parameter;
	
	/**
	 * 内容长度
	 */
	public long contentLength;
	
	@Override
	public String id(){
		return sn;
	}	
	
	public String toString(){
		return String.format(PATTERN, sn,order,type,app,host,method,startDate,duration,contentLength,code,parameter,reason);
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
	
	public TLog app(String app){
		this.app = app;
		return this;
	}
	
	public String app(){
		return this.app;
	}
	
	public TLog host(String host){
		this.host = host;
		return this;
	}
	
	public String host(){
		return this.host;
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

	public String parameter(){
		return parameter;
	}
	
	public void parameter(String p){
		parameter = p;
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

	@Override
	public void toJson(Map<String, Object> json) {
		if (json != null){
			JsonTools.setString(json, "sn", sn());
			JsonTools.setString(json, "order", order());
			JsonTools.setString(json, "type", type());
			JsonTools.setString(json, "app", app());
			JsonTools.setString(json, "host", host());
			JsonTools.setString(json, "method", method());
			JsonTools.setLong(json, "startDate", startDate());
			JsonTools.setLong(json, "duration", duration());
			JsonTools.setLong(json, "contentLength", contentLength());
			JsonTools.setString(json, "code", code());
			JsonTools.setString(json, "parameter", parameter());
			JsonTools.setString(json, "reason", reason());
		}
	}

	@Override
	public void fromJson(Map<String, Object> json) {
		if (json != null){
			sn = JsonTools.getString(json,"sn","");
			order = JsonTools.getString(json,"order","");
			type = JsonTools.getString(json,"type","");
			app = JsonTools.getString(json,"app","");
			host = JsonTools.getString(json,"host","");
			method = JsonTools.getString(json,"method","");
			startDate = JsonTools.getLong(json,"startDate",0);
			duration = JsonTools.getLong(json,"duration",0);
			contentLength = JsonTools.getLong(json,"contentLength",0);
			code = JsonTools.getString(json,"code","");
			parameter = JsonTools.getString(json,"parameter","");
			reason = JsonTools.getString(json,"reason","");
		}
	}

}
