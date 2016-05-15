package com.logicbus.backend;


import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.metrics.core.MetricsCollector;
import com.anysoft.util.Properties;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.ServiceDescription;

/**
 * 空访问控制
 * @author duanyy
 *
 * @version 1.2.8.2 [20141015 duanyy] <br>
 * - 实现Reportable和MetricsReportable <br>
 * 
 * @version 1.6.3.9 [20150324 duanyy] <br>
 * - 修正NothingAccessController无法实例化问题
 * @version 1.6.4.35 [20160315 duanyy] <br>
 * - 实现XMLConfigurable和Configurable接口 <br>
 * 
 * @version 1.6.5.5 [20160515 duanyy] <br>
 * - 增加reload接口 <br>
 */
public class NothingAccessController implements AccessController {
	@Override
	public void reload(String id){
		// nothing to do
	}
	
	public String createSessionId(Path serviceId, ServiceDescription servant,
			Context ctx) {
		return ctx.getClientIp() + ":" + serviceId.getPath();
	}

	
	public int accessStart(String sessionId, Path serviceId,
			ServiceDescription servant, Context ctx) {
		return 1;
	}

	
	public int accessEnd(String sessionId, Path serviceId,
			ServiceDescription servant, Context ctx) {
		return 0;
	}

	
	public void report(Element root) {
		if (root != null){
			root.setAttribute("module", getClass().getName());
		}
	}

	
	public void report(Map<String,Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
		}
	}
	
	public void report(MetricsCollector collector) {

	}


	@Override
	public void configure(Element e, Properties p) {

	}


	@Override
	public void configure(Properties p) {

	}
}
