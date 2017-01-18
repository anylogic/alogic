package com.logicbus.backend;

import com.alogic.metrics.stream.MetricsReportable;
import com.anysoft.util.Reportable;
import com.logicbus.models.servant.ServiceDescription;



/**
 * 服务资源池
 * 
 * @author duanyy
 * @version 1.2.2 [20140617 duanyy]
 * - 改进同步模型
 * 
 * @version 1.2.5 [20140722 duanyy]
 * - Servant的destroy方法变更为close
 * 
 * @version 1.2.6 [20140807 duanyy]
 * - 变更为interface
 * 
 * @version 1.2.8.2 [20141014 duanyy]
 * - 扩展Reportable
 * - 扩展MetricsReportable
 * 
 * @version 1.2.9.1 [201e41017 duanyy]
 * - 减少getStat方法
 * 
 * @version 1.6.4.31 [20160129 duanyy] <br>
 * - 改造计数器体系 <br>
 * 
 * @version 1.6.7.4 [20170118 duanyy] <br>
 * - 淘汰com.anysoft.metrics包 ，改用新的指标框架<br>
 */
public interface ServantPool extends AutoCloseable,Reportable,MetricsReportable{
	/**
	 * 获得服务描述信息
	 * @return 服务描述信息
	 */
	public ServiceDescription getDescription();
	
	/**
	 * 重置本服务池的服务信息
	 * @param sd 服务描述信息
	 */
	public void reload(ServiceDescription sd);
	
	/**
	 * 暂停本服务池的使用
	 */
	public void pause();
	
	/**
	 * 恢复本服务池的使用
	 */
	public void resume();
	
	/**
	 * 是否处于正常使用状态
	 * @return 如果当前服务池可使用，返回为true
	 */
	public boolean isRunning();
	
	/**
	 * 服务池访问一次
	 * @param duration 本次调用时长
	 * @param code 本次调用的结果代码
	 */
	public void visited(long duration,String code);
	
	/**
	 * 从服务池中借取服务对象
	 * @param priority 优先级
	 * @return 服务对象
	 */
	public Servant borrowObject(int priority);
	
	/**
	 * 向服务池归还对象
	 * @param obj 服务对象
	 */
	public void returnObject(Servant obj);
	
	/**
	 * 获取服务池的健康度
	 * @return 健康度分数，百分制
	 * 
	 * @since 1.6.4.31
	 */
	public int getHealthScore();
	
	/**
	 * 获取服务池的活跃度
	 * @return 活跃度分数，百分制
	 * 
	 * @since 1.6.4.31
	 */
	public int getActiveScore();
}
