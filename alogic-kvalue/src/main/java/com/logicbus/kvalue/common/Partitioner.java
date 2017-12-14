package com.logicbus.kvalue.common;

import java.util.HashMap;
import com.anysoft.util.Factory;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;

/**
 * 分区器
 * <br>
 * 用于对Key进行分区
 * @author duanyy
 *
 */
public interface Partitioner extends XMLConfigurable,AutoCloseable,Reportable{
	/**
	 * 根据Key的取值判断该Key分配在哪个分区
	 * 
	 * @param key
	 * @return 分区实例
	 */
	public Partition getPartition(final String key);
	
	public static class TheFactory extends Factory<Partitioner>{
		public String getClassName(String _module) {
			String found = alias.get(_module);
			return found == null ? _module : found;
		}
		
		public static HashMap<String,String> alias = new HashMap<String,String>();
		
		static {
			alias.put("SimpleHash", SimpleHash.class.getName());
			alias.put("GroupHash", GroupHash.class.getName());
			alias.put("ConsistentHash", ConsistentHash.class.getName());
			alias.put("Simple", SimpleHash.class.getName());
			alias.put("Group", GroupHash.class.getName());
			alias.put("Consistent", ConsistentHash.class.getName());
		}
	}
}
