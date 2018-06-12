package com.alogic.cube.mdr;

import com.alogic.cube.mdr.Measure.Method;
import com.anysoft.util.Properties;


/**
 * 数据值
 * 
 * @author duanyy
 * @since 1.6.11.35
 */
public class DataValue  implements Summarable{
	/**
	 * measure
	 */
	protected Measure measure;
	
	/**
	 * 取值
	 */
	protected long value = 0;
	
	/**
	 * 记录个数
	 */
	protected long cnt = 0;
	
	protected DataValue(final Measure measure){
		this.measure = measure;
	}
	
	public long getValue(){
		return value;
	}
	
	public Measure getMeasure(){
		return measure;
	}
	
	public long getCount(){
		return cnt;
	}
	
	public void sum(final Properties provider){		
		long val = measure.getValue(provider);

		if (measure.getMethod() == Method.max){
			if (cnt <= 0){
				//初始化值
				value = val;
			}else{
				if (val > value){
					value = val;
				}
			}
		}else{
			if (measure.getMethod() == Method.min){
				if (cnt <= 0){
					value = val;
				}else{
					if (val < value){
						value = val;
					}
				}
			}else{
				value += val;
			}
		}
		cnt ++;
	}
}
