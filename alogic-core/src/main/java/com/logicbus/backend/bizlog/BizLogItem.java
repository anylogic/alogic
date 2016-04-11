package com.logicbus.backend.bizlog;

import java.lang.reflect.Field;

import com.anysoft.stream.Flowable;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 日志项
 * 
 * @author duanyy
 * 
 * @since 1.2.3
 * 
 * @version 1.2.7 [20140828 duanyy] <br>
 * - 通过com.anysoft.stream来实现
 * 
 * @version 1.2.7.1 [20140902 duanyy] <br>
 * - 出于节约内存的考虑，去掉host属性 <br>
 * 
 * @version 1.6.4.5 [20150910 duanyy] <br>
 * - 统计维度不再包含client信息 <br>
 * 
 * @version 1.6.4.11 [20151116 duanyy] <br>
 * - 增加logType属性 <br>
 * 
 * @version 1.6.4.43 [20160411 duanyy] <br>
 * - DataProvider增加获取原始值接口 <br>
 */
public class BizLogItem implements Comparable<BizLogItem>,Flowable {
	
	/**
	 * 全局序列号
	 */
	public String sn;
	
	/**
	 * 服务ID
	 */
	public String id;
	
	/**
	 * 调用者
	 */
	public String client;
	
	/**
	 * 调用者IP
	 */
	public String clientIP;
	
	/**
	 * 日志级别
	 */
	public ServiceDescription.LogType logType;
	
	/**
	 * 结果代码
	 */
	public String result;
	
	/**
	 * 结果原因
	 */
	public String reason;
	
	/**
	 * 开始时间
	 */
	public long startTime;
	
	/**
	 * 服务时长
	 */
	public long duration;
	
	/**
	 * 请求URL
	 */
	public String url;
	
	/**
	 * 服务文档内容
	 */
	public String content;
	
	
	public int compareTo(BizLogItem other) {		
		return sn.compareTo(other.sn);
	}

	
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
	
	public Object getContext(String varName) {
		return null;
	}

	
	public String getStatsDimesion() {
		return id + "%" + result;
	}	
	
	
	public int hashCode(){
		return sn.hashCode();
	}
}
