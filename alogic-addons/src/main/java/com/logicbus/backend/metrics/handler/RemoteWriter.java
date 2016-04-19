package com.logicbus.backend.metrics.handler;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.anysoft.context.Context;
import com.anysoft.metrics.core.Dimensions;
import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.handler.SummaryWriter;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.remote.context.CallSource;
import com.logicbus.remote.context.InnerContext;
import com.logicbus.remote.core.BuilderFactory;
import com.logicbus.remote.core.Call;
import com.logicbus.remote.core.Parameters;


/**
 * 通过远程调用输出指标数据
 * 
 * @author duanyy
 *
 * @version 1.3.0.3 [20141111 duanyy] <br>
 * - 从server.ip变量中获取本机IP <br>
 * 
 * @version 1.6.4.42 [duanyy 20160407] <br>
 * - 修正编译警告 <br>
 */
public class RemoteWriter extends SummaryWriter {
	
	protected Call theCall = null;
	
	protected String host;
	protected String app;
	protected String port;
	
	protected void write(Map<String, Fragment> _data,long t) {
		if (theCall != null){
			
			try {
				Parameters paras = theCall.createParameter();
	
				{				
					List<Object> data = new ArrayList<Object>();
					Collection<Fragment> values = _data.values();
				
					Settings settings = Settings.get();
					if (StringUtils.isEmpty(host)){
						host = PropertiesConstants.getString(settings,"server.ip", "unknown");
					}
					if (StringUtils.isEmpty(port)){
						port = PropertiesConstants.getString(settings,"server.port","unknown");
					}
					if (StringUtils.isEmpty(app)){
						app = PropertiesConstants.getString(settings,"server.app","unknown");
					}
					for (Fragment item:values){
						Map<String,Object> map = new HashMap<String,Object>(5);
						
						Dimensions dims = item.getDimensions();
						
						if (dims != null){
							dims.lpush(host,port,app);
						}
						
						item.toJson(map);
						data.add(map);
					}
					
					paras.param("data", data,BuilderFactory.LIST);
				}
				
				theCall.execute(paras);
			}catch (Exception ex){
				
			}
		}
	}

	
	protected void onConfigure(Element e, Properties p) {
		String callId = PropertiesConstants.getString(p, "callId", "metricsOutput");

		Context<Call> context = new InnerContext();	
		try {
			context.configure(e, p);
			theCall = context.get(callId);
			if (theCall == null){
				theCall = CallSource.getCall(callId);
			}
		}finally{
			IOTools.close(context);
		}
	}

}
