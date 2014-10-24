package com.logicbus.remote.client;


/**
 * 客户端代理类
 * 
 * @author duanyy
 * @since 1.0.4
 * 
 * @version 1.2.3 [20140619 duanyy]
 * - 修改Client
 */
abstract public class Client {
	/**
	 * 创建参数实例
	 * @return
	 * @throws ClientException
	 */
	abstract public Parameter createParameter();

	public int invoke(String id,Response res) throws ClientException{
		return invoke(id,null,res,null);
	}	
	
	public int invoke(String id,Parameter p,Response res) throws ClientException{
		return invoke(id,p,res,null);
	}

	public int invoke(String id,Response res,Request req) throws ClientException{
		return invoke(id,null,res,req);
	}		
		
	/**
	 * 服务调用
	 * @param id 服务ID
	 * @param parameter 参数
	 * @param para
	 * @param result
	 * @return
	 * @throws ClientException
	 */
	abstract public int invoke(String id,Parameter p,Response res,Request req) throws ClientException;
}
