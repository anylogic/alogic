package com.anysoft.util;


/**
 * Pair
 * 
 * @author duanyy
 *
 * @param <K>
 * @param <V>
 * 
 * @since 1.3.6
 * @version 1.6.4.16 [duanyy 20151110] <br>
 * - 根据sonar建议优化代码 <br>
 */
public interface Pair<K, V>{
	/**
	 *  获取Key
	 * @return Key
	 */
	public K key();
	/**
	 * 获取Value
	 * @return Key
	 */
	public V value();
	
	public static class Default<K,V> implements Pair<K,V>{
		protected K key = null;
		protected V value = null;
		
		public Default(K k,V v){
			key = k;
			value = v;
		}

		@Override
		public int hashCode(){
			if (key == null) return 0;
			return key.hashCode();
		}
		
		@Override
		public boolean equals(Object other){
			if (this == other){ 
				return true;
			}
			
			@SuppressWarnings("unchecked")
			Default<K,V> another = (Default<K,V>)other;
			
			if (another == null || another.key ==  null){
				return false;
			}
			
			if (key == null){ 
				return false;
			}
			
			return key.equals(another.key);
		}
		
		@Override
		public K key() {
			return key;
		}

		@Override
		public V value() {
			return value;
		}
		
		@Override
		public String toString(){
			return key.toString() + "=" + value.toString();
		}
	}
}
