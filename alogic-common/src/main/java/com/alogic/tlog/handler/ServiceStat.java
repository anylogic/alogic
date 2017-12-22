package com.alogic.tlog.handler;

import org.w3c.dom.Element;

import com.alogic.tlog.TLog;
import com.anysoft.rrm.RRModel;
import com.anysoft.rrm.RRModelManager;
import com.anysoft.rrm.ServiceMetrics;
import com.anysoft.stream.AbstractHandler;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;


/**
 * 服务统计
 * @author yyduan
 * 
 * @since 1.6.11.4
 *
 */
public class ServiceStat extends AbstractHandler<TLog>{

	protected RRModel<ServiceMetrics> rrm = null;
	protected RRModelManager rrmm = null;
	
	protected String getMetricsId(String svcId){
		return "svc.thpt:" + svcId;
	}	
	
	protected void onHandle(TLog tlog, long t) {
		
		String id = tlog.id();

		// 统计服务调用次数
		ServiceMetrics sm = new ServiceMetrics(id);
		sm.count(tlog.duration, !tlog.code().equals("core.ok"));
		rrm.update(t, sm);

		String metricsId = getMetricsId(id);
		// 统计每个服务的调用信息
		@SuppressWarnings("unchecked")
		RRModel<ServiceMetrics> srvRRM = (RRModel<ServiceMetrics>) rrmm
				.getModel(metricsId);
		if (srvRRM == null) {
			srvRRM = rrmm.addModel(metricsId, ServiceMetrics.class,
					Settings.get());
		}
		srvRRM.update(t, sm);
	}

	protected void onFlush(long t) {
		// 没有什么可以flush的
	}

	@SuppressWarnings("unchecked")
	protected void onConfigure(Element e, Properties p) {
		rrmm = RRModelManager.get();
		rrm = (RRModel<ServiceMetrics>) rrmm.getModel("metrics.service");
		if (rrm == null){
			rrm = rrmm.addModel("metrics.service", ServiceMetrics.class, p);
		}
	}
}