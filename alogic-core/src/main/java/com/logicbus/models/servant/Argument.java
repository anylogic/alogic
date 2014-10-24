package com.logicbus.models.servant;

import com.anysoft.util.JsonSerializer;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlSerializer;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.backend.message.MessageDoc;

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
 * - Argument变更为interface
 */
public interface Argument extends XmlSerializer,JsonSerializer{
	
	/**
	 * 获取Id
	 * @return
	 */
	public String getId();
	
	/**
	 * 获取缺省值
	 * @return
	 */
	public String getDefaultValue();
	
	/**
	 * 是否可选
	 * @return
	 */
	public boolean isOption();
	
	/**
	 * 是否需要缓存
	 * @return 
	 * 
	 * @since 1.0.8
	 */
	public boolean isCached();
	/**
	 * 获取getter
	 * @return
	 */
	public String getGetter();
	
	/**
	 * 获取gettter的参数
	 * @return
	 */
	public String getGetterParameters();

	/**
	 * 获取getter的参数列表
	 * @return
	 */
	public Properties getParameter();
		
	/**
	 * 获取参数值
	 * @param msg 服务接口文档
	 * @param ctx 上下文
	 * @return 参数值
	 */
	public String getValue(MessageDoc msg,Context ctx)throws ServantException;
	
	/**
	 * 获取参数值
	 * @param msg 服务消息
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.0.8
	 */
	public String getValue(Message msg,Context ctx)throws ServantException;
	
}
