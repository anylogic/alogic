package com.logicbus.backend;

import com.anysoft.pool.Pooled;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.models.servant.ServiceDescription;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务员(所有服务实现的基类)
 * 
 * @author duanyy
 * 
 * @version 1.0.3 [20140410 duanyy] <br>
 * - 增加调用参数读取的封装函数 <br>
 * 
 * @version 1.0.5 [20140412 duanyy] <br>
 * - 修改消息传递模型。<br>
 * 
 * @version 1.0.8 [20140412 duanyy] <br>
 * - 增加从Message获取参数功能 <br>
 * 
 * @version 1.2.4 [20140703 duanyy]<br>
 * - 实现Pooled接口
 * 
 * @version 1.2.5 [20140722 duanyy]<br>
 * - Pooled接口取消了desctory方法，增加了close方法
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 * 
 * @version 1.6.4.29 [20160126 duanyy] <br>
 * - 清除Servant体系中处于deprecated的方法 <br>
 * 
 * @version 1.6.7.4 [20170118 duanyy] <br>
 * - 服务耗时单位改为ns <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 * 
 * @version 1.6.9.8 [20170821 duanyy] <br>
 * - 服务上下文增加keyword关键字，和tlog对接; <br>
 */
public abstract class Servant implements Pooled{
	/**
	 * 服务描述
	 */
	protected ServiceDescription desc = null;
	
	/**
	 * 服务员的工作状态
	 */
	private int state;
	
	/**
	 * 服务调用超时时间
	 */
	private long timeOut = 3000;
	
	/**
	 * 工作状态:繁忙
	 */
	public static final int STATE_BUSY = 0;
	
	/**
	 * 工作状态：空闲
	 */
	public static final int STATE_IDLE = 1;
	
	/**
	 * a logger of log4j
	 */
	protected static final Logger logger = LoggerFactory.getLogger(Servant.class);	
	
	/**
	 * 构造函数
	 */
	public Servant(){
		
	}	
	
	/**
	 * 获取服务者的工作状态
	 * @return state
	 */
	public int getState(){return state;}
	
	/**
	 * 设置服务者的工作状态
	 * @param iState 工作状态
	 */
	public void setState(int iState){state = iState;}
	
	/**
	 * 初始化服务者
	 * 
	 * <br>
	 * 根据服务描述初始化服务者.
	 * 
	 * @param sd service description
	 */
	public void create(ServiceDescription sd){
		desc = sd;
		timeOut = PropertiesConstants.getLong(sd.getProperties(), "time_out", 3000);
	}

	/**
	 * 获取超时时长
	 * @return value
	 */
	public long getTimeOutValue(){
		return timeOut;
	}
	
	/**
	 * 判断是否已经超时
	 * @param startTime start time
	 * @return if time out return true,otherwise false.
	 */
	public boolean isTimeOut(long startTime){
		long current = System.currentTimeMillis();
		if (current - startTime > timeOut) 
			return true;
		return false;
	}
	
	/**
	 * 销毁服务
	 * 
	 * <br>
	 * 在{@link com.logicbus.backend.ServantPool ServantPool}类
	 * {@link com.logicbus.backend.ServantPool#close() close}时调用。
	 * 
	 */
	@Override
	public void close(){
		
	}

	/**
	 * 获取服务描述
	 * @return 服务描述
	 */
	public ServiceDescription getDescription(){
		return desc;
	}
	
	/**
	 * 读取参数
	 * @param id 参数ID
	 * @param dftValue 缺省值 
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.4.0
	 */
	public String getArgument(String id,String dftValue,Context ctx){
		return ctx.GetValue(id, dftValue);
	}
	
	/**
	 * 读取参数
	 * @param id 参数ID
	 * @param dftValue 缺省值 
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.6.4
	 */
	public long getArgument(String id,long dftValue,Context ctx){
		String value = getArgument(id,String.valueOf(dftValue),ctx);
		
		try {
			return Long.parseLong(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	/**
	 * 读取参数
	 * @param id 参数ID
	 * @param dftValue 缺省值 
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.6.4
	 */
	public int getArgument(String id,int dftValue,Context ctx){
		String value = getArgument(id,String.valueOf(dftValue),ctx);
		
		try {
			return Integer.parseInt(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}
	
	/**
	 * 读取参数
	 * @param id 参数ID
	 * @param dftValue 缺省值 
	 * @param ctx 上下文
	 * @return 参数值
	 * 
	 * @since 1.6.4
	 */
	public boolean getArgument(String id,boolean dftValue,Context ctx){
		String value = getArgument(id,Boolean.toString(dftValue),ctx);
		
		try {
			return BooleanUtils.toBoolean(value);
		}catch (NumberFormatException ex){
			return dftValue;
		}
	}	
	
	/**
	 * 读取参数
	 * @param id 参数ID
	 * @param ctx 上下文
	 * @return 参数值
	 */
	public String getArgument(String id,Context ctx){
		String value = ctx.GetValue(id, "");	
		if (StringUtils.isEmpty(value)){
			throw new ServantException("clnt.e2000",
					"Can not find parameter:" + id);
		}
		return value;
	}
	
	/**
	 * 服务处理过程
	 * @param ctx 上下文
	 * @return 处理结果
	 * 
	 * @since 1.4.0
	 */
	public abstract int actionProcess(Context ctx) ; // NOSONAR
	
	/**
	 * 服务处理即将开始
	 * @param ctx 上下文
	 * 
	 * @since 1.4.0
	 */
	public void actionBefore(Context ctx){
		// nothing to do
	}
	
	/**
	 * 服务处理已经结束
	 * @param ctx 上下文
	 * 
	 * @since 1.4.0
	 */
	public void actionAfter(Context ctx){
		ctx.setReturn("core.ok","OK");
	}
	
	/**
	 * 服务处理发生异常
	 * @param ctx 上下文
	 * @param ex 异常
	 * @since 1.4.0
	 */
	public void actionException(Context ctx,ServantException ex){
		ctx.setReturn(ex.getCode(), ex.getMessage());
	}
}
