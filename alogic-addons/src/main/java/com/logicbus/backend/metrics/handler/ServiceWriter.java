package com.logicbus.backend.metrics.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.anysoft.metrics.core.Dimensions;
import com.anysoft.metrics.core.Fragment;
import com.anysoft.metrics.handler.SummaryWriter;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.remote.client.ClientException;
import com.logicbus.remote.client.HttpClient;
import com.logicbus.remote.client.JsonBuffer;
import com.logicbus.remote.client.Parameter;

/**
 * 服务调用输出
 * 
 * @author duanyy
 * @since 1.2.8
 * 
 * @version 1.3.0.3 [20141111 duanyy] <br>
 * - 从server.ip变量中获取本机IP <br>
 */
public class ServiceWriter extends SummaryWriter {

	
	protected void write(Map<String, Fragment> _data,long t) {
		//免失败模式开启
		//如果连续错误次数超过3次，则连续10分钟不再尝试，直接返回为true
		if (errorTimes > 3 && System.currentTimeMillis() - lastTryTime < 10 * 60 * 1000)
			return ;
		
		if (url == null || url.length() <= 0){
			//没有配置服务器URL
			return;
		}
		
		Parameter para = client.createParameter()
		.param("t",String.valueOf(System.currentTimeMillis()));
		
		if (app != null && app.length() >= 0){
			para.param("a", app);
		}
		
		JsonBuffer result = new JsonBuffer();
		
		{
			//组织input
			Map<String,Object> root = result.getRoot();
			
			List<Object> data = new ArrayList<Object>();
			Collection<Fragment> values = _data.values();
		
			if (host == null){
				Settings settings = Settings.get();
				host = settings.GetValue("host", "${server.ip}:${server.port}");
			}
			for (Fragment item:values){
				Map<String,Object> map = new HashMap<String,Object>(5);
				
				Dimensions dims = item.getDimensions();
				
				if (dims != null){
					dims.lpush(host,app);
				}
				
				item.toJson(map);
				data.add(map);
			}
			
			root.put("data", data);
			
			para.param("count", String.valueOf(data.size()));
		}
		try {	
			client.invoke(url, para, result,result);
			errorTimes = 0;
		} catch (ClientException e) {
			errorTimes ++;			
		}finally{		
			lastTryTime = System.currentTimeMillis();
		}
	}

	
	protected void onConfigure(Element e, Properties p) {
		url = PropertiesConstants.getString(p,"url","",true);
		app = PropertiesConstants.getString(p,"app","${server.app}",true);
		client = new HttpClient(p);
	}
	protected String host = null;
	protected String app;
	protected String url;
	protected HttpClient client;
	
	//连续错误次数
	private int errorTimes = 0;
	//上次尝试时间
	private long lastTryTime = 0;	
}
