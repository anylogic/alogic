package com.logicbus.models.servant;

import com.anysoft.util.Properties;
import com.anysoft.util.Reportable;
import com.anysoft.util.XmlSerializer;
import com.anysoft.util.JsonSerializer;


/**
 * 服务描述
 * 
 * @author duanyy
 * @version 1.0.3 [20140410 duanyy]<br>
 * + 增加调用参数列表<br>
 * 
 * @version 1.0.8 [20140421 duanyy]<br>
 * - 修正{@link com.logicbus.models.servant.ServiceDescription#getArgument(String) getArgument(String)}空指针错误. <br>
 * 
 * @version 1.2.3 [20140617 duanyy]<br>
 * - 增加日志的相关属性
 * 
 * @version 1.2.4.4 [20140709 duanyy]<br>
 * - 增加LogType的设置方法
 * - 增加properties和arguments的设置方法
 * 
 * @version 1.2.5.4 [20140801 duanyy]<br>
 * - ServiceDescription变更为interface
 * 
 * @version 1.2.8.2 [20141015 duanyy]<br>
 * - 实现Reportable
 */
public interface ServiceDescription extends XmlSerializer,JsonSerializer,Reportable{
	/**
	 * 业务日志的类型
	 * 
	 * <br>
	 * 分为三种类型:<br>
	 * - none <br>
	 * - brief <br>
	 * - detail <br>
	 * 
	 * @author duanyy
	 * @since 1.2.3
	 * 
	 */
	public enum LogType {none,brief,detail};
	
	/**
	 * 获取日志类型
	 * @return
	 */
	public LogType getLogType();
	
	/**
	 * 获得服务ID
	 * @return 服务ID
	 */
	public String getServiceID();
	
	/**
	 * 获得服务的可见性
	 * @return 可见性
	 */
	public String getVisible();
	
	/**
	 * 获得服务名称
	 * @return name
	 */
	public String getName();
	
	/**
	 * 获取服务说明
	 * @return 服务说明
	 */
	public String getNote();
	
	/**
	 * 获得服务路径
	 * @return
	 */
	public String getPath();
	
	/**
	 * 获得服务实现代码
	 * @return
	 */
	public String getModule();

	/**
	 * 获取参数变量集
	 * @return
	 */
	public Properties getProperties();
	
	/**
	 * 获取服务以来库文件列表
	 * @return 
	 */
	public String [] getModules();
	
	/**
	 * 获取服务调用参数列表
	 * @return
	 * @since 1.0.3
	 */
	public Argument [] getArgumentList();
	
	/**
	 * 获取指定ID的参数
	 * @param id 参数Id
	 * @return 
	 */
	public Argument getArgument(String id);	
}
