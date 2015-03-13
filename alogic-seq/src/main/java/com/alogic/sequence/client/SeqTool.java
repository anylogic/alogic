package com.alogic.sequence.client;

import com.alogic.sequence.context.SequenceSource;
import com.alogic.sequence.core.SequenceGenerator;

/**
 * Sequeuece工具类
 * 
 * @author duanyy
 * @since 1.6.3.5
 */
public class SeqTool {
	
	/**
	 * 提取一个Long的全局序列号
	 * 
	 * @return 全局序列号
	 */
	static public long nextLong(){
		return nextLong("default");
	}	
	
	/**
	 * 提取一个Long的全局序列号
	 * 
	 * @param id 序列号的域id
	 * @return 全局序列号
	 */
	static public long nextLong(String id){
		SequenceSource src = SequenceSource.get();
		
		SequenceGenerator generator = src.get(id);
		return generator.nextLong();
	}

	/**
	 * 提取一个String的全局随机码
	 * 
	 * @return 全局序列号
	 */
	static public String nextString(){
		return nextString("default");
	}
	
	/**
	 * 提取一个String的全局随机码
	 * 
	 * @param id 序列号的域id
	 * @return 全局序列号
	 */
	static public String nextString(String id){
		SequenceSource src = SequenceSource.get();
		
		SequenceGenerator generator = src.get(id);
		return generator.nextString();
	}
	
	public static void main(String [] args){
		for (int i = 0 ; i < 10000; i ++){
			System.out.println(nextLong());
		}
	}
}
