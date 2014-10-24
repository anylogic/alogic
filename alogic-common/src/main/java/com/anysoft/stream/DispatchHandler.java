package com.anysoft.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 分发器
 * 
 * @author duanyy
 *
 * @param <data>
 * @since 1.4.0
 * 
 * @version 1.4.4 [20140917 duanyy]
 * - Handler:handle和flush方法增加timestamp参数，以便进行时间同步
 */
public class DispatchHandler<data extends Flowable> extends AbstractHandler<data> {

	protected Handler<data>[] children = null;
	
	protected int threadCnt = 10;
	
	
	protected void onHandle(data _data,long timestamp) {
		if (children != null){
			int idx = _data.hashCode() & Integer.MAX_VALUE % threadCnt;
			
			if (children[idx] != null){
				children[idx].handle(_data,timestamp);
			}
		}
	}

	
	protected void onFlush(long timestamp) {
		if (children != null){
			for (Handler<data> _handler:children){
				if (_handler != null){
					_handler.flush(timestamp);
				}
			}
		}
	}
	
	
	public void report(Element root){
		super.report(root);
		
		if (children != null){
			Document doc = root.getOwnerDocument();
			
			for (Handler<data> _handler:children){
				if (_handler != null){
					Element newHandler = doc.createElement(getHandlerType());
					_handler.report(newHandler);
					root.appendChild(newHandler);
				}
			}
		}
	}
	
	
	public void report(Map<String, Object> json){
		super.report(json);
		
		if (children != null){
			List<Object> array = new ArrayList<Object>(children.length);
			
			for (Handler<data> _handler:children){
				if (_handler != null){
					Map<String,Object> map = new HashMap<String,Object>();
					_handler.report(map);
					array.add(map);
				}
			}
			
			json.put(getHandlerType(), array);
		}
	}
	
	public void close() throws Exception{
		super.close();
		
		if (children != null){
			for (Handler<data> _handler:children){
				if (_handler != null){
					IOTools.close(_handler);
				}
			}
		}
	}	

	@SuppressWarnings("unchecked")
	
	protected void onConfigure(Element e, Properties p) {
		threadCnt = PropertiesConstants.getInt(p, "threadCnt", threadCnt,true);
		threadCnt = threadCnt <= 0 ? 10 : threadCnt;
		
		Element template = XmlTools.getFirstElementByPath(e, getHandlerType());
		
		if (template != null){
			children = new Handler[threadCnt];
			
			Properties child = new DefaultProperties("Default",p);
			
			Factory<Handler<data>> factory = new Factory<Handler<data>>();
			
			for (int i = 0 ;i < threadCnt ; i ++){
				child.SetValue("thread", String.valueOf(i));
				try {
					Handler<data> newHandler = factory.newInstance(template, child);
					children[i] = newHandler;
				}catch (Exception ex){
					logger.error("Can not create handler instance.",ex);
				}
			}
		}
	}
	protected static Logger logger = LogManager.getLogger(DispatchHandler.class);
}
