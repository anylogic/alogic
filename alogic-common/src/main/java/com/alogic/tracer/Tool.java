package com.alogic.tracer;

import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

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
 */
public class Tool {
	
	/**
	 * Tracer的唯一实例
	 */
	private static Tracer instance = null;
	
	/**
	 * a logger of log4j
	 */
	private static final Logger LOG = LogManager.getLogger(Tool.class);
	
	/**
	 * 缺省的配置文件
	 */
	private static final String DEFAULT = "java:///com/alogic/tracer/tracer.xml#" + Tool.class.getName();
	
	/**
	 * 获取唯一实例
	 * @return 唯一实例
	 */
	public static Tracer get() {
		if (instance == null) {
			synchronized (Tool.class) {
				if (instance == null){
					Settings settings = Settings.get();
	
					String secondary = settings.GetValue("trace.secondary", DEFAULT);
					String master = settings.GetValue("trace.master", secondary);
	
					ResourceFactory rf = Settings.getResourceFactory();
					InputStream in = null;
					try {
						in = rf.load(master, secondary, null);
						Document doc = XmlTools.loadFromInputStream(in);
						if (doc != null) {
							Factory<Tracer> factory = new Factory<Tracer>();
							instance = factory.newInstance(doc.getDocumentElement(), settings, "module",
									StackTracer.class.getName());
						}
					} catch (Exception ex) {
						LOG.error("Error occurs when load xml file,source=" + master, ex);
					} finally {
						IOTools.closeStream(in);
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
	public static TraceContext start(String sn,long order){
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
	 */	
	public static void end(TraceContext ctx,String type,String name,String result,String note){
		Tracer tracer = get();
		if (tracer != null && tracer.enable()){
			tracer.endProcedure(ctx, type, name, result, note, 0);
		}
	}	
}
