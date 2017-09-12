package com.alogic.metrics.xscript;

import org.apache.commons.lang3.StringUtils;
import com.alogic.metrics.Fragment;
import com.alogic.metrics.Measures;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 增加量度
 * @author yyduan
 *
 * @version 1.6.8.1 [20170320 duanyy] <br>
 * - 修正switch语句没有break的bug <br>
 * 
 * @version 1.6.8.8 [20170417 duanyy] <br
 * - 修正AddMeasure处理double值的bug <br>
 * 
 * 
 * @version 1.6.8.14 [20170509 duanyy] <br>
 * - 增加xscript的中间文档模型,以便支持多种报文协议 <br>
 * 
 * @version 1.6.10.1 [20170910 duanyy] <br>
 * - id参数改为可计算参数 <br>
 */
public class AddMeasure extends MetricsBuilder {
	protected String id = "";
	protected String value = "";
	protected Fragment.DataType type = Fragment.DataType.S;
	protected Fragment.Method method = Fragment.Method.sum;
	
	public AddMeasure(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		id = PropertiesConstants.getRaw(p,"id",id);
		value = PropertiesConstants.getRaw(p, "value", value);
		
		type = Fragment.DataType.valueOf(PropertiesConstants.getString(p,"type",type.name(),true));
		method = Fragment.Method.valueOf(PropertiesConstants.getString(p,"method",method.name(),true));
	}

	@Override
	protected void onExecute(Fragment f, XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		String idValue = ctx.transform(id);
		if (StringUtils.isNotEmpty(idValue)){			
			String valueValue = ctx.transform(value);			
			if (StringUtils.isNotEmpty(valueValue)){
				Measures meas = f.getMeasures();
				
				switch (type){
				case D:
					try {
						double doubleValue = Double.parseDouble(valueValue);
						meas.set(idValue, doubleValue,method);
					}catch (NumberFormatException ex){
						
					}	
					break;
				case L:
					try {
						long longValue = Long.parseLong(valueValue);
						meas.set(idValue, longValue,method);
					}catch (NumberFormatException ex){
						
					}
					break;
				default:
					meas.set(idValue, valueValue);
				}
			}
		}
	}

}