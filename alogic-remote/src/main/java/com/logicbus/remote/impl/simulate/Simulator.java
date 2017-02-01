package com.logicbus.remote.impl.simulate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.anysoft.util.BaseException;
import com.anysoft.util.Counter;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;
import com.logicbus.remote.core.Call;
import com.logicbus.remote.core.CallException;
import com.logicbus.remote.core.DefaultParameters;
import com.logicbus.remote.core.Parameters;
import com.logicbus.remote.core.Result;
import com.logicbus.remote.util.CallStat;


/**
 * 远程调用模拟器
 * 
 * @author duanyy
 *
 * @since 1.2.9.3
 * @version 1.6.3.21 [20150507 duanyy] <br>
 * - 增加全局序列号的支持 <br>
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class Simulator implements Call {
	protected static Logger logger = LoggerFactory.getLogger(Simulator.class);
	
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
				
				SimulatedCase dest = new SimulatedCase();
				dest.configure(e, p);
				
				destinations.add(dest);
			}
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
			
			LoadBalanceFactory<SimulatedCase> f = new LoadBalanceFactory<SimulatedCase>();
			
			loadBalance = f.newInstance(lbModule, p);
		}

		stat = createCounter(p);
	}

	
	public Parameters createParameter() {
		return new DefaultParameters();
	}

	private StringBuffer loadFromInputStream(StringBuffer buf,InputStream in){
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		try {
            while ((line = reader.readLine()) != null) {
            	buf.append(line);
            	buf.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	IOTools.closeStream(in,reader);
        }
		return buf;
	}

	public Result execute(Parameters paras, String globalSerial)
			throws CallException {
		return execute(paras);
	}	
	
	public Result execute(Parameters paras) throws CallException {
		long start = System.currentTimeMillis();
		boolean error = false;
		SimulatedCase dest = null;
		try {	
			dest = loadBalance.select(paras.toString(), (DefaultParameters)paras, destinations);
			if (dest == null){
				throw new CallException("core.nodests","Can not find a valid destination to call.");
			}
			
			ResourceFactory rf = Settings.getResourceFactory();
			InputStream in = rf.load(dest.getURI(), null);
			StringBuffer buffer = new StringBuffer();
			loadFromInputStream(buffer,in);
			return new SimulatedResult(buffer.toString(),idPaths);
		} catch (Exception e) {
			error = true;
			throw new CallException("call.simulator",e.getMessage(),e);
		}finally{
			long _duration = System.currentTimeMillis() - start;
			if (stat != null){
				stat.count(_duration, error);
			}
			if (dest != null){
				dest.count(_duration, error);
			}
		}
		
	}

	/**
	 * 统计模型
	 */
	protected Counter stat = null;
	
	protected List<SimulatedCase> destinations = new ArrayList<SimulatedCase>();

	protected Map<String,String> idPaths = new HashMap<String,String>();
	
	protected LoadBalance<SimulatedCase> loadBalance = null;
	
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
}
