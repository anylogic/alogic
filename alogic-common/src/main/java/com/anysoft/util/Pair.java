package com.anysoft.util;


/**
 * Pair
 * 
 * @author duanyy
 *
 * @param <Key>
 * @param <Value>
 * 
 * @since 1.3.6
 * 
 */
public interface Pair<Key, Value>{
	/**
	 *  获取Key
	 * @return
	 */
	public Key key();
	/**
	 * 获取Value
	 * @return
	 */
	public Value value();
	
	public static class Default<Key,Value> implements Pair<Key,Value>{
		protected Key key = null;
		protected Value value = null;
		
		public Default(Key _key,Value _value){
			key = _key;
			value = _value;
		}

		public int hashCode(){
			if (key == null) return 0;
			return key.hashCode();
		}
		
		public boolean equals(Default<Key,Value> another){
			if (this == another) return true;
			if (another == null || another.key ==  null){
				return false;
			}
			if (key == null) return false;
			
			return key.equals(another.key);
		}
		
		
		public Key key() {
			return key;
		}

		
		public Value value() {
			return value;
		}
		public String toString(){
			return key.toString() + "=" + value.toString();
		}
	}
}
