package com.logicbus.backend.metrics.handler;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.metrics.Fragment;
import com.alogic.metrics.stream.MetricsSummaryWriter;
import com.alogic.remote.call.BuilderFactory;
import com.alogic.remote.call.Call;
import com.alogic.remote.call.Parameters;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;


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
 * 
 * @version 1.6.6.13 [duanyy 20170112] <br>
 * - 采用新的指标框架 <br>
 * 
 * @version 1.6.8.13 [duanyy 20170427] <br>
 * - 采用alogic-rpc中提供的远程调用框架 <br>
 */
public class RemoteWriter extends MetricsSummaryWriter{
	
	/**
	 * 远程调用
	 */
	protected Call theCall = null;
	
	protected int logCnt = 0;
	
	@Override
	protected void write(Map<String, Fragment> fragments, long t) {
		if (theCall != null){
			try {
				Parameters paras = theCall.createParameter();
	
				{				
					List<Object> data = new ArrayList<Object>();
					Collection<Fragment> values = fragments.values();
				
					for (Fragment item:values){
						Map<String,Object> map = new HashMap<String,Object>(5);
						item.toJson(map);
						data.add(map);
					}
					
					paras.param("data", data,BuilderFactory.LIST);
				}
				
				theCall.execute(paras);
			}catch (Exception ex){
				if (logCnt % 120 == 0){
					LOG.warn("Failed to send metrics to insight.Cnt="+logCnt);
					LOG.warn(fragments.toString());
				}
				logCnt ++;				
			}			
		}
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		XmlElementProperties props = new XmlElementProperties(e,p);
		Element callElem = XmlTools.getFirstElementByPath(e, "call");
		if (callElem != null){
			Factory<Call> factory = new Factory<Call>();
			try {
				theCall = factory.newInstance(callElem, props, "module");
			}catch (Exception ex){
				LOG.error(String.format("Can not create call instance by %s", 
						XmlTools.node2String(callElem)));
			}
		}
	}
	
	public void close()  {
		IOTools.close(theCall);
		super.close();
	}

}