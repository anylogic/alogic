package com.logicbus.backend;

import com.anysoft.metrics.core.Dimensions;
import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.core.Measures;
import com.anysoft.metrics.core.MetricsCollector;
import com.anysoft.metrics.core.MetricsHandler;
import com.anysoft.util.Settings;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.models.servant.ServiceDescription;

/**
 * Servant虚基类
 * 
 * @author duanyy
 * @since 1.2.3
 * 
 * @version 1.2.5 [20140722 duanyy]
 * - Servant的destroy方法改为close
 * 
 * @version 1.2.8 [20140914 duanyy]
 * - 增加指标收集体系
 * 
 */
abstract public class AbstractServant extends Servant implements MetricsCollector {
	
	
	public int actionProcess(MessageDoc msg, Context ctx) throws Exception {
		String json = getArgument("json",jsonDefault,msg,ctx);
		if (json != null && json.equals("true")){
			return onJson(msg,ctx);
		}else{
			return onXml(msg,ctx);
		}
	}

	protected String jsonDefault = "true";
	
	public void create(ServiceDescription sd) throws ServantException{
		super.create(sd);
		
		if (metricsHandler == null){
			synchronized (lock){
				if (metricsHandler == null){
					Settings settings = Settings.get();
					metricsHandler = (MetricsHandler) settings.get("metricsHandler");
				}
			}
		}
		
		jsonDefault = sd.getProperties().GetValue("jsonDefault",jsonDefault);
		onCreate(sd);
	}
	
	public void close(){
		super.close();
		onDestroy();
	}
	
	abstract protected void onDestroy();

	abstract protected void onCreate(ServiceDescription sd) throws ServantException;

	abstract protected int onXml(MessageDoc msgDoc, Context ctx) throws Exception;

	abstract protected int onJson(MessageDoc msgDoc, Context ctx) throws Exception;
	
	
	public void metricsIncr(Fragment fragment){
		if (metricsHandler != null){
			Dimensions dims = fragment.getDimensions();
			if (dims != null){
				dims.lpush(getDescription().getPath());
			}
			metricsHandler.handle(fragment,System.currentTimeMillis());
		}
	}
	
	public void metricsIncr(String _id,String [] _dims,Object..._values){
		Fragment f = new Fragment(_id);
		
		Dimensions dims = f.getDimensions();
		if (dims != null)
			dims.lpush(_dims);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(_values);
		
		metricsIncr(f);
	}
	
	public void metricsIncr(String _id,String [] _dims,Double..._values){
		Fragment f = new Fragment(_id);
		
		Dimensions dims = f.getDimensions();
		if (dims != null)
			dims.lpush(_dims);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(_values);
		
		metricsIncr(f);
	}
	
	public void metricsIncr(String _id,String [] _dims,Long..._values){
		Fragment f = new Fragment(_id);
		
		Dimensions dims = f.getDimensions();
		if (dims != null)
			dims.lpush(_dims);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(_values);
		
		metricsIncr(f);		
	}
	
	public void metricsIncr(String _id,Double..._values){
		Fragment f = new Fragment(_id);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(_values);
		
		metricsIncr(f);		
	}
	
	public void metricsIncr(String _id,Long ..._values){
		Fragment f = new Fragment(_id);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(_values);
		
		metricsIncr(f);	
	}
	
	public void metricsIncr(String _id,Object ..._values){
		Fragment f = new Fragment(_id);
		
		Measures meas = f.getMeasures();
		if (meas != null)
			meas.lpush(_values);
		
		metricsIncr(f);	
	}	
	
	protected static Object lock = new Object();
	protected static MetricsHandler metricsHandler = null;
}
