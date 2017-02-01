package com.logicbus.remote.impl.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.loadbalance.LoadBalance;
import com.anysoft.loadbalance.LoadBalanceFactory;
import com.anysoft.selector.FieldList;
import com.anysoft.selector.Selector;
import com.anysoft.util.BaseException;
import com.anysoft.util.Counter;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.remote.client.ClientException;
import com.logicbus.remote.client.HttpClient;
import com.logicbus.remote.client.JsonBuffer;
import com.logicbus.remote.client.Parameter;
import com.logicbus.remote.core.Call;
import com.logicbus.remote.core.CallException;
import com.logicbus.remote.core.Parameters;
import com.logicbus.remote.core.Result;
import com.logicbus.remote.util.CallStat;


/**
 * 基于Http请求的实现
 * 
 * @author duanyy
 *
 * @since 1.2.9
 * 
 * @version 1.2.9.1 [20141017 duanyy]
 * - 实现Reportable接口
 * - 增加Counter模型
 * 
 * @version 1.2.9.3 [20141021 duanyy]
 * - 增加负载均衡机制，支持多个URI
 * - 增加结果数据的ID和Path映射
 * @version 1.6.3.21 [20150507 duanyy] <br>
 * - 增加全局序列号的支持 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class HttpCall implements Call {
	protected static Logger logger = LoggerFactory.getLogger(HttpCall.class);

	public void close() throws Exception {
		// nothing to do
	}

	
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		Properties p = new XmlElementProperties(_e,_properties);

		//destinations
		NodeList dests = XmlTools.getNodeListByPath(_e, "dests/dest");
		if (dests != null)
		{
			for (int i = 0 ; i < dests.getLength() ; i ++){
				Node n = dests.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				HttpDestination dest = new HttpDestination();
				dest.configure(e, p);
				
				destinations.add(dest);
			}
		}
		
		//queryParameters
		Element qp = XmlTools.getFirstElementByPath(_e, "request/query");
		if (qp != null){
			queryParameters = new FieldList();
			queryParameters.configure(qp, p);
		}
		
		//argument data
		Element ad = XmlTools.getFirstElementByPath(_e, "request/data");
		if (ad != null){
			arguments = new FieldList();
			arguments.configure(ad, p);
		}
		//idpaths
		NodeList ips = XmlTools.getNodeListByPath(_e, "response/data/field");
		if (ips != null)
		{
			for (int i = 0 ; i < ips.getLength() ; i ++){
				Node n = ips.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element e = (Element)n;
				
				String id = e.getAttribute("id");
				String path = e.getAttribute("path");
				
				if (id == null || id.length() <= 0 || path == null || path.length() <= 0){
					continue;
				}
				
				idPaths.put(id, path);
			}
		}
		//loadbalance
		{
			String lbModule = p.GetValue("loadbalance.module", "Rand");
			
			LoadBalanceFactory<HttpDestination> f = new LoadBalanceFactory<HttpDestination>();
			
			loadBalance = f.newInstance(lbModule, p);
		}

		
		client = new HttpClient(p);
		
		stat = createCounter(p);
	}

	
	public Parameters createParameter() {
		return new HttpParameters();
	}

	public Result execute(Parameters paras)
			throws CallException {
		return execute(paras,null);
	}	
	
	public Result execute(Parameters paras, String globalSerial) throws CallException {
		Parameter p = null;
		
		if (paras != null && queryParameters != null){
			Selector[] fields = queryParameters.getFields();
			if (fields != null && fields.length > 0){
				p = client.createParameter();
				
				for (Selector s:fields){
					if (!s.isOk()){
						continue;
					}
					String id = s.getId();
					String value = s.select(paras);

					p.param(id, value);
				}
			}
		}
		
		JsonBuffer buffer = new TheBuffer();
		if (globalSerial != null && globalSerial.length() > 0){
			buffer.SetValue("globalSerial", globalSerial);
		}
		
		if (paras != null && arguments != null){
			Selector[] fields = arguments.getFields();
			if (fields != null && fields.length > 0){
				Map<String,Object> root = buffer.getRoot();
				
				for (Selector s:fields){
					if (!s.isOk()){
						continue;
					}
					String id = s.getId();
					
					Object data = paras.getData(id);
					if (data != null){
						root.put(id, data);
					}
				}
			}
		}
		
		long start = System.currentTimeMillis();
		boolean error = false;
		HttpDestination dest = null;
		try {	
			dest = loadBalance.select(paras.toString(), buffer, destinations);
			if (dest == null){
				throw new CallException("core.nodests","Can not find a valid destination to call.");
			}
			client.invoke(dest.getURI(), p, buffer,buffer);
		} catch (ClientException e) {
			error = true;
			throw new CallException(e.getCode(),e.getMessage(),e);
		}finally{
			long _duration = System.currentTimeMillis() - start;
			if (stat != null){
				stat.count(_duration, error);
			}
			if (dest != null){
				dest.count(_duration, error);
			}
		}
		return new HttpResult(buffer,idPaths);
	}

	/**
	 * URL中的参数
	 */
	private FieldList queryParameters = null;

	/**
	 * Http entity中的数据
	 */
	private FieldList arguments = null;
	
	/**
	 * Http Client
	 */
	protected HttpClient client = null;
	
	/**
	 * 统计模型
	 */
	protected Counter stat = null;
	
	protected List<HttpDestination> destinations = new ArrayList<HttpDestination>();

	protected Map<String,String> idPaths = new HashMap<String,String>();
	
	protected LoadBalance<HttpDestination> loadBalance = null;
	
	protected Counter createCounter(Properties p){
		String module = PropertiesConstants.getString(p,"call.stat.module", CallStat.class.getName());
		try {
			return Counter.TheFactory.getCounter(module, p);
		}catch (Exception ex){
			logger.warn("Can not create call counter:" + module + ",default counter is instead.");
			return new CallStat(p);
		}
	}
	
	
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("module", getClass().getName());
			
			Document doc = xml.getOwnerDocument();
			
			{
				Element _runtime = doc.createElement("runtime");
				
				if (stat != null)
				{
					Element _stat = doc.createElement("stat");
					stat.report(_stat);
					_runtime.appendChild(_stat);
				}
				
				xml.appendChild(_runtime);
			}
		}
	}

	
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("module", getClass().getName());
			
			{
				Map<String,Object> _runtime = new HashMap<String,Object>();
				
				if (stat != null){
					Map<String,Object> _stat = new HashMap<String,Object>();
					stat.report(_stat);
					_runtime.put("stat", _stat);
				}
				
				json.put("runtime", _runtime);
			}
		}
	}

	private static class TheBuffer extends JsonBuffer{
		public String[] getRequestAttributeNames() {
			return new String[]{"GlobalSerial"};
		}		
	}
}
