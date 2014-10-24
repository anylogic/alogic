package com.logicbus.kvalue.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 一致性Hash算法实现
 * @author duanyy
 *
 */
public class ConsistentHash extends AbstractPartitioner {
	protected int vnodesPerNode = 128;
	
	protected TreeMap<Long,String> nodes = new TreeMap<Long,String>();
	
	
	protected String getPartitionCase(String key) {
		SortedMap<Long,String> tail = nodes.tailMap(hash(key));
		if (tail == null || tail.size() == 0){
			return nodes.get(nodes.firstKey());
		}
		return tail.get(tail.firstKey());
	}

	
	protected void onConfigure(Element _e, Properties _p) {
		vnodesPerNode = PropertiesConstants.getInt(_p, "vnodesPerNode", vnodesPerNode,true);
		vnodesPerNode = vnodesPerNode <= 0 ? 128 : vnodesPerNode;
		
		Enumeration<String> cases = partitions.keys();
		
		while (cases.hasMoreElements()){
			String _case = cases.nextElement();
			for (int i = 0 ;i < vnodesPerNode ; i ++){
				Long _key = hash(_case + "vnode" + i);
				nodes.put(_key, _case);
			}
		}
	}

	/**
	 * MurMurHash算法实现
	 * @param key
	 * @return
	 */
	static protected Long hash(String key) {
		ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
		int seed = 0x1234ABCD;

		ByteOrder byteOrder = buf.order();
		buf.order(ByteOrder.LITTLE_ENDIAN);

		long m = 0xc6a4a7935bd1e995L;
		int r = 47;

		long h = seed ^ (buf.remaining() * m);

		long k;
		while (buf.remaining() >= 8) {
			k = buf.getLong();

			k *= m;
			k ^= k >>> r;
			k *= m;

			h ^= k;
			h *= m;
		}

		if (buf.remaining() > 0) {
			ByteBuffer finish = ByteBuffer.allocate(8).order(
					ByteOrder.LITTLE_ENDIAN);
			finish.put(buf).rewind();
			h ^= finish.getLong();
			h *= m;
		}

		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		buf.order(byteOrder);
		return h;
	}
}
