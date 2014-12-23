package com.logicbus.backend;

/**
 * Comet
 * @author duanyy
 * @since 1.6.2.1
 */
public interface Comet {
	/**
	 * to suspend this request
	 * @param timeout 
	 * @since 1.6.2.1
	 */
	public void suspend(long timeout);
	
	/**
	 * to resume this request
	 * @since 1.6.2.1
	 */
	public void resume();
	
	/**
	 * whether is initial
	 * @return true/false
	 */
	public boolean isInitial();
	
	/**
	 * to set object with the given id
	 * @param id id of object
	 * @param data object
	 */
	public void setObject(String id,Object data);
	
	/**
	 * to get object by the given id
	 * @return object
	 */
	public Object getObject(String id);
	
	/**
	 * to remove the object with the given id
	 */
	public void removeObject(String id);
}
