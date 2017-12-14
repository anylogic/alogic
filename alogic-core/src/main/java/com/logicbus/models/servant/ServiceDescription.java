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
 * 
 * @version 1.2.3 [20140617 duanyy]<br>
 * - 增加日志的相关属性 <br>
 * 
 * @version 1.2.4.4 [20140709 duanyy]<br>
 * - 增加LogType的设置方法 <br>
 * - 增加properties和arguments的设置方法 <br>
 * 
 * @version 1.2.5.4 [20140801 duanyy]<br>
 * - ServiceDescription变更为interface <br>
 * 
 * @version 1.2.8.2 [20141015 duanyy]<br>
 * - 实现Reportable <br>
 * 
 * @version 1.6.7.20 <br>
 * - 改造ServantManager模型,增加服务配置监控机制 <br>
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
	 * @return 日志类型
	 */
	public LogType getLogType();
	
	/**
	 * 启动配置监控
	 * @return 是否启用
	 */
	public boolean guard();
	
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
	 * 获取访问控制组id
	 * @return acGroupId
	 */
	public String getAcGroup();
	
	/**
	 * 获取所需的权限项
	 * @return　privilege
	 */
	public String getPrivilege();
	
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
	 * @return 服务路径
	 */
	public String getPath();
	
	/**
	 * 获得服务实现代码
	 * @return 服务实现代码
	 */
	public String getModule();

	/**
	 * 获取参数变量集
	 * @return 参数变量集
	 */
	public Properties getProperties();
	
	/**
	 * 获取服务依赖库文件列表
	 * @return 文件列表
	 */
	public String [] getModules();
}
