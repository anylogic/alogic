package com.alogic.load;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Store
 * 
 * <p>
 * Store是在Loader的基础上增加存储接口
 * 
 * @author yyduan
 *
 * @param <O> 
 * 
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加有效期的判定 <br>
 */
public interface Store<O extends Loadable> extends Loader<O> {
	
	/**
	 * 存储对象
	 * @param id 对象id
	 * @param o 对象实例
	 * @param overwrite 是否覆盖
	 * 
	 */
	public void save(String id,O o,boolean overwrite);
	
	/**
	 * 删除指定的对象
	 * @param id 对象id
	 */
	public void del(String id);
	
	/**
	 * 基于本地内存ConcurrentHashMap的Store
	 * 
	 * @author yyduan
	 *
	 */
	public static class HashStore<O extends Loadable> extends Loader.Sinkable<O> implements Store<O>{
		protected Map<String,O> data = new ConcurrentHashMap<String,O>();
		
		@Override
		public void save(String id, O o, boolean overwrite) {
			O found = data.get(id);
			if (found == null){
				data.put(id, o);
			}else{
				if (overwrite){
					data.put(id, o);
				}
			}
		}

		@Override
		protected O loadFromSelf(String id, boolean cacheAllowed) {
			O found = null;
			if (cacheAllowed){
				found = data.get(id);
				if (isExpired(found)){
					data.remove(id);
					found = null;
				}
			}
			return found;
		}
		
		@Override
		public void del(String id){
			data.remove(id);
		}
	}
}
