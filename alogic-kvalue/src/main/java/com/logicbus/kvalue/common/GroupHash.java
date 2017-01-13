package com.logicbus.kvalue.common;

import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlTools;

/**
 * 分组Hash算法实现
 * @author duanyy
 *
 */
public class GroupHash extends AbstractPartitioner {
	protected int vnodesCnt = 1000;
	protected TreeMap<Integer,String> nodes = new TreeMap<Integer,String>();
	
	protected String getPartitionCase(String key) {
		int idx = (key.hashCode() & Integer.MAX_VALUE) % vnodesCnt;
		SortedMap<Integer,String> tail = nodes.tailMap(idx);
		if (tail == null || tail.size() == 0){
			return nodes.get(nodes.firstKey());
		}
		return tail.get(tail.firstKey());
	}

	
	protected void onConfigure(Element _e, Properties _p) {
		vnodesCnt = PropertiesConstants.getInt(_p, "vnodesCnt", vnodesCnt,true);
		vnodesCnt = vnodesCnt <= 0 ? 1000 : vnodesCnt;
		
		NodeList nodeList = XmlTools.getNodeListByPath(_e, "groups/group");
		
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element groupElem = (Element)n;
			String _point = groupElem.getAttribute("point");
			String _case = groupElem.getAttribute("case");
			if (_point == null || _point.length() <= 0){
				continue;
			}
			if (_case == null || _case.length() <= 0){
				continue;
			}
			Integer _longPoint;
			try {
				_longPoint = Integer.parseInt(_point);
			}catch (Exception ex){
				continue;
			}
			nodes.put(_longPoint, _case);
		}
	}

}
