package com.logicbus.backend.metrics.handler;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.stream.MetricsSummaryWriter;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
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
 * 
 * @version 1.6.6.13 [duanyy 20170112] <br>
 * - 采用新的指标框架 <br>
 */
public class RemoteWriter extends MetricsSummaryWriter{
	
	/**
	 * 远程调用
	 */
	protected Call theCall = null;
	
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

}