package com.logicbus.backend.acm;

import java.util.HashSet;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Token持有者
 * <br>
 * 仅用于TokenCenter模式.
 * 
 * @author duanyy
 * @since 1.2.3
 */
public class TokenHolder {
	
	/**
	 * 所持有的Token
	 */
	protected HashSet<String> tokens = new HashSet<String>();
	
	/**
	 * 近期的Token
	 */
	protected HashSet<String> lastestTokens = new HashSet<String>();
	
	/**
	 * 转换周期，缺省30分钟
	 */
	protected long cycle = 30 * 60 * 1000;
	
	public TokenHolder(Properties props){
		cycle = PropertiesConstants.getLong(props, "acm.cycle", cycle);
	}
	
	public TokenHolder(){
		
	}
	
	private void change(){
		long now = System.currentTimeMillis();
		now = (now / cycle)*cycle;
		if (cycle != timestamp){
			timestamp = now;
			synchronized (this){
				HashSet<String> temp = tokens;
				tokens = lastestTokens;
				lastestTokens = temp;
				lastestTokens.clear();
			}
		}
	}
	
	/**
	 * 数据时间戳
	 */
	protected long timestamp = 0;
	
	/**
	 * 增加Token
	 * @param t id
	 */
	public void add(String t){
		change();
		tokens.add(t);
	}
	
	/**
	 * 删除Token
	 * @param t id
	 */
	public void remove(String t){
		change();
		tokens.remove(t);
	}
	
	/**
	 * 是否存在该Token
	 * @param t id
	 * @return
	 */
	public boolean exist(String t){
		boolean found = tokens.contains(t);
		if (found){
			lastestTokens.add(t);
		}
		change();
		return found;
	}
}
