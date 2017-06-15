package com.logicbus.backend;

import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.Fragment.Method;
import com.alogic.metrics.Measures;
import com.alogic.metrics.impl.DefaultFragment;
import com.alogic.metrics.stream.MetricsCollector;
import com.alogic.metrics.stream.MetricsHandlerFactory;
import com.anysoft.stream.Handler;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.models.servant.ServiceDescription;

/**
 * Servant虚基类
 * 
 * @author duanyy
 * @since 1.2.3
 * 
 * @version 1.2.5 [20140722 duanyy] <br>
 * - Servant的destroy方法改为close <br>
 * 
 * @version 1.2.8 [20140914 duanyy] <br>
 * - 增加指标收集体系 <br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - Servant体系抛弃MessageDoc <br>
 * 
 * @version 1.6.4.29 [20160126 duanyy] <br>
 * - 清除Servant体系中处于deprecated的方法 <br>
 * 
 * @version 1.6.6.13 [20170109 duanyy] <br>
 * - 采用新的指标接口 <br>
 */
public abstract class AbstractServant extends Servant implements MetricsCollector {

	protected boolean jsonDefault = true;	
	
	protected static Handler<Fragment> metricsHandler = null;
	
	@Override
	public int actionProcess(Context ctx) throws Exception{
		boolean json = getArgument("json",jsonDefault,ctx);
		if (json){
			return onJson(ctx);
		}else{
			return onXml(ctx);
		}
	}

	public void create(ServiceDescription sd){
		super.create(sd);
		
		metricsHandler = MetricsHandlerFactory.getClientInstance();
		
		jsonDefault = PropertiesConstants.getBoolean(sd.getProperties(),"jsonDefault",jsonDefault);
		onCreate(sd);
	}
	
	public void close(){
		super.close();
		onDestroy();
	}
	
	protected abstract void onDestroy();

	protected abstract void onCreate(ServiceDescription sd);
	
	/**
	 * 以XML协议进行服务处理
	 * @param ctx 上下文
	 * @return 结果
	 * @throws Exception
	 * 
	 * @since 1.4.0
	 */
	protected int onXml(Context ctx) throws Exception{ // NOSONAR
		throw new ServantException("core.not_supported",
				"Protocol XML is not suppurted.");		
	}
	
	/**
	 * 以JSON协议进行服务处理
	 * @param ctx 上下文
	 * @return 结果
	 * @throws Exception
	 * @since 1.4.0
	 */
	protected abstract int onJson(Context ctx) throws Exception; // NOSONAR
	
	@Override
	public void metricsIncr(Fragment fragment){
		if (metricsHandler != null){
			Dimensions dims = fragment.getDimensions();
			if (dims != null){
				dims.set("svc", getDescription().getPath(), false);
			}
			metricsHandler.handle(fragment,System.currentTimeMillis());
		}
	}
	
	public void metricsIncr(String mId,String measure,long value,Method m){
		Fragment f = new DefaultFragment(mId);
		
		Measures meas = f.getMeasures();
		if (meas != null){
			meas.set(measure, value,m);
		}
		
		metricsIncr(f);
	}
	
	public void metricsIncr(String mId,String measure,double value,Method m){
		Fragment f = new DefaultFragment(mId);
		
		Measures meas = f.getMeasures();
		if (meas != null){
			meas.set(measure, value,m);
		}
		
		metricsIncr(f);		
	}
	
	public void metricsIncr(String mId,String measure,long value){
		metricsIncr(mId,measure,value,Method.sum);
	}
	
	public void metricsIncr(String mId,long value){
		metricsIncr(mId,"v",value,Method.sum);
	}	
	
	public void metricsIncr(String mId,String measure,double value){
		metricsIncr(mId,measure,value,Method.sum);
	}	
	
	public void metricsIncr(String mId,double value){
		metricsIncr(mId,"v",value,Method.sum);
	}		
}
