package com.alogic.tracer;


import com.anysoft.util.Factory;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;

/**
 * 工具类
 * 
 * 提供一下基础的静态方法。
 * 
 * @author duanyy
 * @since 1.6.5.3
 * 
 * @version 1.6.5.7 [20160525 duanyy] <br>
 * - 当tracer的enable()为true的时候，才会开启tracer <br>
 * 
 * @version 1.6.6.13 [20170111 duanyy] <br>
 * - 修正Tool.Get()的并发性问题 <br>
 * 
 * @version 1.6.7.1 [20170117 duanyy] <br>
 * - trace日志调用链中的调用次序采用xx.xx.xx.xx字符串模式 <br>
 * 
 * @version 1.6.7.3 [20170118 duanyy] <br>
 * - trace日志的时长单位改为ns <br>
 * - 新增com.alogic.tlog，替代com.alogic.tracer.log包; <br>
 * 
 * @version 1.6.7.21 [20170303 duanyy] <br>
 * - TLog增加parameter字段，便于调用者记录个性化参数 <br>
 */
public class Tool {
	
	/**
	 * Tracer的唯一实例
	 */
	private static Tracer instance = null;
	
	/**
	 * 获取唯一实例
	 * @return 唯一实例
	 */
	public static Tracer get() {
		if (instance == null) {
			synchronized (Tool.class) {
				if (instance == null){
					Settings settings = Settings.get();
					String module = PropertiesConstants.getString(settings,"tracer.module",StackTracer.class.getName());
					
					Factory<Tracer> f = new Factory<Tracer>();
					try{
						instance = f.newInstance(module, settings);
					}catch (Exception ex){
						instance = new StackTracer();
						instance.configure(settings);
					}
				}
			}
		}

		return instance;
	}
	
	/**
	 * 开始过程
	 * @return 上下文
	 */
	public static TraceContext start(){
		Tracer tracer = get();
		return tracer != null && tracer.enable() ? tracer.startProcedure() : null;
	}
	
	/**
	 * 以指定序列号和顺序开始过程
	 * @param sn 序列号
	 * @param order 顺序
	 * @return 上下文
	 */
	public static TraceContext start(String sn,String order){
		Tracer tracer = get();
		return tracer != null && tracer.enable() ? tracer.startProcedure(sn,order) : null;
	}
	
	/**
	 * 结束过程
	 * @param ctx 上下文
	 * @param type 过程类型
	 * @param name 过程名称
	 * @param result 过程调用结果
	 * @param note 结果说明
	 * @param contentLength 内容大小
	 */
	public static void end(TraceContext ctx,String type,String name,String result,String note,long contentLength){
		Tracer tracer = get();
		if (tracer != null && tracer.enable()){
			tracer.endProcedure(ctx, type, name, result, note, contentLength);
		}
	}
	
	/**
	 * 结束过程
	 * @param ctx 上下文
	 * @param type 过程类型
	 * @param name 过程名称
	 * @param result 过程调用结果
	 * @param note 结果说明
	 * @param parameter 参数，参数的内容和编码由类型确定
	 * @param contentLength 内容大小
	 */
	public static void end(TraceContext ctx,String type,String name,String result,String note,String parameter,long contentLength){
		Tracer tracer = get();
		if (tracer != null && tracer.enable()){
			tracer.endProcedure(ctx, type, name, result, note, parameter, contentLength);
		}
	}	
	
	/**
	 * 结束过程
	 * @param ctx 上下文
	 * @param type 过程类型
	 * @param name 过程名称
	 * @param result 过程调用结果
	 * @param note 结果说明
	 */	
	public static void end(TraceContext ctx,String type,String name,String result,String note){
		Tracer tracer = get();
		if (tracer != null && tracer.enable()){
			tracer.endProcedure(ctx, type, name, result, note, 0);
		}
	}	
}
