package com.logicbus.backend;

import com.anysoft.metrics.core.Dimensions;
import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.core.Measures;
import com.anysoft.metrics.core.MetricsCollector;
import com.anysoft.metrics.core.MetricsHandler;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
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
 */
public abstract class AbstractServant extends Servant implements MetricsCollector {

	protected boolean jsonDefault = true;	
	
	protected static MetricsHandler metricsHandler = null;
	
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
		
		if (metricsHandler == null){
			synchronized (AbstractServant.class){
				if (metricsHandler == null){
					Settings settings = Settings.get();
					metricsHandler = (MetricsHandler) settings.get("metricsHandler");
				}
			}
		}
		
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
				dims.lpush(getDescription().getPath());
			}
			metricsHandler.handle(fragment,System.currentTimeMillis());
		}
	}
	
	public void metricsIncr(String id,String [] sDims,Object...values){
		Fragment f = new Fragment(id);
		
		Dimensions dims = f.getDimensions();
		if (dims != null)
			dims.lpush(sDims);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);
	}
	
	public void metricsIncr(String id,String [] sDims,Double...values){
		Fragment f = new Fragment(id);
		
		Dimensions dims = f.getDimensions();
		if (dims != null)
			dims.lpush(sDims);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);
	}
	
	public void metricsIncr(String id,String [] sDims,Long...values){
		Fragment f = new Fragment(id);
		
		Dimensions dims = f.getDimensions();
		if (dims != null)
			dims.lpush(sDims);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);		
	}
	
	public void metricsIncr(String id,Double...values){
		Fragment f = new Fragment(id);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);		
	}
	
	public void metricsIncr(String id,Long ...values){
		Fragment f = new Fragment(id);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);	
	}
	
	public void metricsIncr(String id,Object ...values){
		Fragment f = new Fragment(id);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(values);
		
		metricsIncr(f);	
	}	

}
