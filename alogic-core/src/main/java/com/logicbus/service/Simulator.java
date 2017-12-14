package com.logicbus.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;


/**
 * 模拟器
 * 
 * <br>
 * 模拟一个服务的执行过程，服务耗时满足正态分布，用于服务的并发压力测试。
 * 
 * @author duanyy
 * 
 * @since 1.1.0
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 将MessageDoc和Context进行合并整合 <br>
 */
public class Simulator extends AbstractServant {
	
	/**
	 * 平均耗时,缺省100ms
	 */
	protected int dftAvg = 100;
	
	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd){
		Properties props = sd.getProperties();
		
		dftAvg = PropertiesConstants.getInt(props, "avg", dftAvg);
	}

	@Override
	protected int onXml(Context ctx) {
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);
		
		int avg = getArgument("avg",dftAvg,ctx);
		Random r = new Random();
		
		int duration = (int)((r.nextGaussian()/4 + 1) * avg);
		
		try {
			TimeUnit.MILLISECONDS.sleep(duration);
		} catch (InterruptedException e) {
			throw new ServantException("core.e1006",e.getMessage());
		}
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		root.appendChild(doc.createTextNode("I have sleep " + duration + " ms."));
		
		return 0;
	}	
	
	@Override
	protected int onJson(Context ctx) {
		JsonMessage msg = (JsonMessage) ctx.asMessage(JsonMessage.class);
		
		int avg = getArgument("avg",dftAvg,ctx);
		Random r = new Random();
		
		int duration = (int)((r.nextGaussian()/4 + 1) * avg);
		
		try {
			TimeUnit.MILLISECONDS.sleep(duration);
		} catch (InterruptedException e) {
			throw new ServantException("core.e1006",e.getMessage());
		}
		
		msg.getRoot().put("msg", "I have sleep " + duration + " ms.");
		
		return 0;		
	}


}
