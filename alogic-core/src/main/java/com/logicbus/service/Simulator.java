package com.logicbus.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.ServantException;
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
public class Simulator extends Servant {

	
	public int actionProcess(Context ctx) throws Exception {
		XMLMessage msg = (XMLMessage) ctx.asMessage(XMLMessage.class);
		
		int _avg = getArgument("avg",avg,ctx);
		Random r = new Random();
		
		int duration = (int)((r.nextGaussian()/4 + 1) * _avg);
		
		TimeUnit.MILLISECONDS.sleep(duration);
		
		Document doc = msg.getDocument();
		Element root = msg.getRoot();
		
		root.appendChild(doc.createTextNode("I have sleep " + duration + " ms."));
		
		return 0;
	}

	
	public void create(ServiceDescription sd) throws ServantException{
		super.create(sd);
		
		Properties props = sd.getProperties();
		
		avg = PropertiesConstants.getInt(props, "avg", avg);
	}
	
	/**
	 * 获取Int型的参数
	 */	
	protected int getArgument(String id,int defaultValue,Context ctx) throws ServantException{
		String value = getArgument(id,"",ctx);
		if (value == null || value.length() <= 0){
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		}catch (Exception ex){
			return defaultValue;
		}
	}
	
	/**
	 * 平均耗时,缺省100ms
	 */
	protected int avg = 100;

}
