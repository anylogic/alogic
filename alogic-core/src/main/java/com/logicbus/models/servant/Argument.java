package com.logicbus.models.servant;

import com.anysoft.util.JsonSerializer;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlSerializer;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;

/**
 * 服务调用参数
 * @author duanyy
 *
 * @since 1.0.3
 * 
 * @version 1.0.8 [20140420 duanyy]<br>
 * - 增加从Message中获取参数功能，见{@link com.logicbus.models.servant.Argument#getValue(Message, Context) getValue(Message, Context)} <br>
 * - 增加cached属性
 * 
 * @version 1.2.0 [20140609 duanyy]<br>
 * - 优化getter的初始化
 * 
 * @version 1.2.4.4 [20140709 duanyy]<br>
 * - 增加copyFrom方法
 * 
 * @version 1.2.5.4 [20140801 duanyy]<br>
 * - Argument变更为interface <br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 抛弃MessageDoc <br>
 * 
 * @version 1.6.4.29 [20160126 duanyy] <br>
 * - 清除Servant体系中处于deprecated的方法 <br>
 */
public interface Argument extends XmlSerializer,JsonSerializer{
	
	/**
	 * 获取Id
	 * @return Id
	 */
	public String getId();
	
	/**
	 * 获取缺省值
	 * @return 缺省值
	 */
	public String getDefaultValue();
	
	/**
	 * 是否可选
	 * @return 是否可选
	 */
	public boolean isOption();
	
	/**
	 * 是否需要缓存
	 * @return 是否需要缓存
	 * 
	 * @since 1.0.8
	 */
	public boolean isCached();
	/**
	 * 获取getter
	 * @return getter类名
	 */
	public String getGetter();
	
	/**
	 * 获取gettter的参数
	 * @return 参数
	 */
	public String getGetterParameters();

	/**
	 * 获取getter的参数列表
	 * @return 参数列表
	 */
	public Properties getParameter();
	
	/**
	 * 读取参数值
	 * @param ctx 上下文
	 * @return 参数值
	 * @throws ServantException
	 */
	public String getValue(Context ctx) throws ServantException;
	
	/**
	 * 获取参数值
	 * @param msg 服务消息
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.0.8
	 * 
	 */
	public String getValue(Message msg,Context ctx)throws ServantException;
	
}
