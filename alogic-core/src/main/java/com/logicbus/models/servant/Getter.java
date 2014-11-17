package com.logicbus.models.servant;

import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.backend.message.MessageDoc;

/**
 * 服务调用参数Getter
 * 
 * @author duanyy
 *
 * @since 1.0.3
 * 
 * @version 1.0.8 [20140420 duanyy]<br>
 * - 增加从Message中获取参数的接口，见{@link com.logicbus.models.servant.Getter#getValue(Argument, Message, Context) getValue(Argument, Message, Context)}
 * 
 */
public interface Getter {
	
	/**
	 * 获取参数值
	 * @param argu 参数定义
	 * @param msg 消息文档
	 * @param ctx 上下文
	 * @return
	 * @throws ServantException
	 */
	public String getValue(Argument argu,MessageDoc msg,Context ctx)throws ServantException;
	
	
	/**
	 * 获取参数值
	 * @param argu 参数定义
	 * @param msg 消息
	 * @param ctx 上下文
	 * @return
	 * @throws ServantException
	 * 
	 * @since 1.0.8
	 */
	public String getValue(Argument argu,Message msg,Context ctx) throws ServantException;
	
}
