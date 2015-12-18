package com.alogic.sequence.client;

import com.alogic.sequence.context.SequenceSource;
import com.alogic.sequence.core.SequenceGenerator;

/**
 * Sequeuece工具类
 * 
 * @author duanyy
 * @since 1.6.3.5
 * 
 * @version 1.6.4.19 [duanyy 20151218] <br>
 * - 按照SONAR建议修改代码 <br>
 */
public class SeqTool {
	
	private SeqTool(){
		
	}
	/**
	 * 提取一个Long的全局序列号
	 * 
	 * @return 全局序列号
	 */
	public static long nextLong(){
		return nextLong("default");
	}	
	
	/**
	 * 提取一个Long的全局序列号
	 * 
	 * @param id 序列号的域id
	 * @return 全局序列号
	 */
	 public static long nextLong(String id){
		SequenceSource src = SequenceSource.get();
		
		SequenceGenerator generator = src.get(id);
		return generator.nextLong();
	}

	/**
	 * 提取一个String的全局随机码
	 * 
	 * @return 全局序列号
	 */
	 public static String nextString(){
		return nextString("default");
	}
	
	/**
	 * 提取一个String的全局随机码
	 * 
	 * @param id 序列号的域id
	 * @return 全局序列号
	 */
	 public static String nextString(String id){
		SequenceSource src = SequenceSource.get();
		
		SequenceGenerator generator = src.get(id);
		return generator.nextString();
	}
}
