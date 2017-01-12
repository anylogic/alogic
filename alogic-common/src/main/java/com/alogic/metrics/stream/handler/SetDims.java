package com.alogic.metrics.stream.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.selector.Selector;
import com.anysoft.stream.Handler;
import com.anysoft.stream.SlideHandler;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.alogic.metrics.Dimensions;
import com.alogic.metrics.Fragment;

/**
 * 为指标增加维度
 * 
 * @author yyduan
 *
 * @since 1.6.6.13
 *
 */
public class SetDims extends SlideHandler<Fragment>{

	/**
	 * 待增加的维度
	 */
	protected List<Selector> dims = new ArrayList<Selector>();
	
	/**
	 * 缓存的维度值
	 */
	protected Map<String,String> values = new HashMap<String,String>();
	
	
	@Override
	protected void onHandle(Fragment f, long timestamp) {
		Dimensions dimensions = f.getDimensions();
		
		if (dimensions != null){
			for (Selector dim:dims){
				String dimId = dim.getId();
				String found = values.get(dimId);
				if (StringUtils.isEmpty(found)){
					found = dim.select(Settings.get());
					values.put(dimId, found);
				}
				
				dimensions.set(dimId, found, false);
			}
		}
		
		Handler<Fragment> handler = getSlidingHandler();
		if (handler != null){
			handler.handle(f, timestamp);
		}
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		NodeList nodeList = XmlTools.getNodeListByPath(e, "dims/dim");
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element elem = (Element)n;
			
			try{
				Selector selector = Selector.newInstance(elem, p);
				if (selector != null){
					dims.add(selector);
				}
			}catch (Exception ex){
				LOG.error(String.format("Can not create selector with %s",XmlTools.node2String(elem)));
			}
		}
		
	}
}
