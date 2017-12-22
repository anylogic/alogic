package com.anysoft.rrm;

import java.util.Map;

import org.w3c.dom.Element;

import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.Measures;
import com.alogic.metrics.Fragment.Method;
import com.alogic.metrics.impl.DefaultFragment;
import com.alogic.metrics.stream.MetricsCollector;
import com.anysoft.rrm.RRData.Abstract;

/**
 * 服务指标
 * @author yyduan
 * @since 1.6.11.4 
 */
public class ServiceMetrics extends Abstract{

	/**
	 * 总调用次数
	 */
	protected long totalTimes = 0;

	/**
	 * 错误的次数
	 */
	protected long errorTimes = 0;

	/**
	 * 平均调用时间
	 */
	protected long durationAvg = -1;

	/**
	 * 最大调用时间
	 */
	protected long durationMax = -1;

	/**
	 * 最少调用时间
	 */
	protected long durationMin = -1;

	public ServiceMetrics(String metricsId) {
		super(metricsId);
	}	
	
	public void incr(RRData fragment) {
		if (fragment instanceof ServiceMetrics) {
			ServiceMetrics sm = (ServiceMetrics) fragment;

			if (durationAvg < 0) {
				durationAvg = sm.durationAvg;
			} else {
				durationAvg = (durationAvg * totalTimes + sm.durationAvg * sm.totalTimes)
						/ (sm.totalTimes + totalTimes);
			}

			totalTimes += sm.totalTimes;
			errorTimes += sm.errorTimes;

			if (durationMax < 0) {
				durationMax = sm.durationMax;
			} else {
				if (durationMax < sm.durationMax) {
					durationMax = sm.durationMax;
				}
			}

			if (durationMin < 0) {
				durationMin = sm.durationMin;
			} else {
				if (durationMin > sm.durationMin) {
					durationMin = sm.durationMin;
				}
			}
		}
	}

	public void report(Element xml) {
		if (xml != null) {
			xml.setAttribute("total", String.valueOf(totalTimes));
			xml.setAttribute("error", String.valueOf(errorTimes));
			xml.setAttribute("avg", String.valueOf(durationAvg));
			xml.setAttribute("max", String.valueOf(durationMax));
			xml.setAttribute("min", String.valueOf(durationMin));
		}
	}

	public void report(Map<String, Object> json) {
		if (json != null) {
			json.put("total", totalTimes);
			json.put("error", errorTimes);
			json.put("avg", durationAvg);
			json.put("max", durationMax);
			json.put("min", durationMin);
		}
	}

	public void count(long duration, boolean error) {
		totalTimes++;
		if (error) {
			errorTimes++;
		}
		durationAvg = duration;
		durationMax = duration;
		durationMin = duration;
	}

	public RRData copy() {
		ServiceMetrics other = new ServiceMetrics(id());

		other.durationAvg = durationAvg;
		other.durationMax = durationMax;
		other.durationMin = durationMin;
		other.totalTimes = totalTimes;
		other.errorTimes = errorTimes;
		return other;
	}
	
	@Override
	public void report(MetricsCollector collector) {
		if (collector != null){
			Fragment f = new DefaultFragment("svc.thpt");
			Dimensions dims = f.getDimensions();
			if (dims != null){
				dims.set("svc", id(), false);
			}
			Measures meas = f.getMeasures();
			if (meas != null){
				meas.set("max",durationMax,Method.max);
				meas.set("min", durationMin,Method.min);
				meas.set("avg", durationAvg,Method.avg);
				meas.set("tms", totalTimes,Method.sum);
				meas.set("err", errorTimes,Method.sum);
			}
			collector.metricsIncr(f);			
		}
	}
}