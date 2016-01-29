package com.logicbus.models.servant;

import com.logicbus.backend.Context;
import com.logicbus.backend.message.Message;


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
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 抛弃MessageDoc <br>
 * 
 * @version 1.6.4.29 [20160126 duanyy] <br>
 * - 清除Servant体系中处于deprecated的方法 <br>
 */
public interface Getter {
	/**
	 * 获取参数值
	 * @param argu 参数定义
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.4.0
	 */
	public String getValue(Argument argu,Context ctx);
	
	/**
	 * 获取参数值
	 * @param argu 参数定义
	 * @param msg 消息
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.0.8
	 * 
	 */
	public String getValue(Argument argu,Message msg,Context ctx);
	
}
