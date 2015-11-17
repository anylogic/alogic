package com.anysoft.rrm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Reportable;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * 环形数据归档
 * 
 * @author duanyy
 *
 */
public class RRArchive<data extends RRData> implements Reportable,Configurable,XMLConfigurable {

	/**
	 * 获取ID
	 * @return id
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * 获取数据个数
	 * @return 数据个数
	 */
	public int getRows(){return rows;}		
	
	/**
	 * 获取数据的时间区域
	 * @return 时间区域
	 */
	public long getStep(){return step;}	
	
	public RRArchive(String _id){
		id = _id;
	}
	
	/**
	 * 更新RRA的数据
	 * @param fragment
	 */
	public synchronized void update(long timestamp,data fragment){
		timestamp = (timestamp / step)*step;
		
		int index = (int)(timestamp/step) % rows;
		if (rrds[index] == null){
			//当前槽位是空的
			rrds[index] = fragment;
			rrds[index].timestamp(timestamp);
		}else{
			//当前槽位不是空的
			if (rrds[index].timestamp() < timestamp){
				//有可能是前面时间周期的内容，覆盖掉
				rrds[index].timestamp(timestamp);
			}else{
				//有可能是当前周期的内容，在上面做加法
				rrds[index].incr(fragment);
			}
		}
	}

	/**
	 * 获取迭代器，用于遍历数据
	 * @return 迭代器
	 */
	public Iterator<data> iterator(){
		return new MyIterator();
	}
	
	/**
	 * 获取当前周期数据
	 * @return data
	 */
	public RRData current(){
		int current = (int)(now() / step) % rows;
		return rrds[current];
	}
	
	public long now(){
		return (System.currentTimeMillis() / step) * step; 
	}
	
	/**
	 * 数据归档中的数据
	 */
	protected RRData [] rrds = null;

	/**
	 * id
	 */
	protected String id;
	
	/**
	 * 数据个数，缺省30个
	 */
	protected int rows = 30;

	/**
	 * 每条数据的时间区域,60秒
	 */
	protected long step = 60 * 1000;

	
	@Override
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("id", id);
			xml.setAttribute("rows", String.valueOf(rows));
			xml.setAttribute("step", String.valueOf(step));
			String detail = xml.getAttribute("hist");
			if (detail != null && detail.equals("true")){
				Document doc = xml.getOwnerDocument();
				
				Iterator<data> iter = iterator();
				long now = now();
				while (iter.hasNext()){
					Element _d = doc.createElement("d");
					data _data = iter.next();
					if (_data != null){
						now = _data.timestamp();
						_data.report(_d);
					}else{
						now = now - step;
					}
					_d.setAttribute("t", String.valueOf(now));
					xml.appendChild(_d);
				}
			}else{
				//输出当前
				Document doc = xml.getOwnerDocument();
				
				Element _current = doc.createElement("current");
				
				long now = now();
				RRData current = rrds[(int)(now() / step) % rows];
				if (current != null){
					current.report(_current);
				}
				_current.setAttribute("t", String.valueOf(now));
				xml.appendChild(_current);
			}
		}
	}

	@Override
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("id", id);
			json.put("rows", rows);
			json.put("step", step);
			
			boolean detail = JsonTools.getBoolean(json, "hist", false);
			
			if (detail){
				List<Object> _d = new ArrayList<Object>();
				
				Iterator<data> iter = iterator();
				long now = now();
				while (iter.hasNext()){
					data _data = iter.next();
					Map<String,Object> map = new HashMap<String,Object>();
					if (_data != null){
						now = _data.timestamp();
						_data.report(map);
					}else{
						now = now - step;
					}
					map.put("t", now);
					_d.add(map);
				}
				
				json.put("d", _d);
			}else{
				//输出当前
				Map<String,Object> _current = new HashMap<String,Object>();
				
				long now = now();
				RRData current = rrds[(int)(now() / step) % rows];
				if (current != null){
					current.report(_current);
				}
				_current.put("t", now);
				
				json.put("current", _current);
			}
		}
	}

	@Override
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);		
		configure(p);
	}
	
	@Override
	public void configure(Properties p) throws BaseException {
		
		//先从环境变量rrm.${id}.rows中获取
		rows = PropertiesConstants.getInt(p, String.format("rrm.%s.rows",id), 0);
		if (rows <= 0){
			//如果没有获取到，从rrm.rows中获取
			rows = PropertiesConstants.getInt(p, String.format("rrm.rows",id), 30);
			rows = rows <= 0 ? 30 : rows;
		}
		
		step = PropertiesConstants.getLong(p, String.format("rrm.%s.step",id), 0);
		if (step <= 0){
			//如果没有获取到，从rrm.rows中获取
			step = PropertiesConstants.getLong(p, String.format("rrm.step",id), 60 * 1000);
			step = step <= 0 ? 60 * 1000 : step;
		}
		rrds = new RRData[rows];
	}	
	
	/**
	 * 迭代器
	 * @author duanyy
	 *
	 */
	public class MyIterator implements Iterator<data>{
		protected int current = 0;
		protected int count = 0;
		protected long now = 0;
		public MyIterator(){
			now = RRArchive.this.now();
			current = (int)( now / RRArchive.this.step) % RRArchive.this.rows;
		}
		@Override
		public boolean hasNext() {
			return count < RRArchive.this.rows;
		}

		@Override
		public data next() {
			@SuppressWarnings("unchecked")
			data value = (data) RRArchive.this.rrds[current];
			current --;
			count ++;
			if (current < 0){
				current = RRArchive.this.rows - 1;
			}
			//请注意：next()返回结果可能为空
			
			if (value != null && value.timestamp() < now - count * RRArchive.this.step){
				//如果该槽位不为空，可能是前面周期的内容
				return null;
			}
			
			return value;
		}

		@Override
		public void remove() {
			now = RRArchive.this.now();
			current = (int)now % RRArchive.this.rows;
			count = 0;
		}
	}
}
