package com.logicbus.redis.kvalue;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
import com.logicbus.kvalue.common.Partition;
import com.logicbus.kvalue.common.Partitioner;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.Table;
import com.logicbus.redis.context.RedisContext;


/**
 * Redis Table
 * @author duanyy
 * 
 * @version 1.0.0.2 [20141108 duanyy] <br>
 * - 补充Reportable实现. <br>
 */
public class RedisTable implements Table {
	
	/**
	 * redis context，用于从中获取redis connection
	 */
	protected RedisContext source = null;
	
	/**
	 * 分区器
	 */
	protected Partitioner partitioner = null;
	
	/**
	 * 是否独占redis db
	 */
	protected boolean possessive = false;
	
	/**
	 * table的类型
	 */
	protected Table.DataType dataType = Table.DataType.String;
	
	/**
	 * 构造函数
	 * 
	 * @param redisSource 有效的context
	 */
	public RedisTable(RedisContext redisSource) {
		source = redisSource;
	}

	/**
	 * 装入配置
	 * 
	 * <br>
	 * Table的配置信息全部定义在XML节点中。
	 * 
	 * @param _e XML节点
	 * @param _properties 环境变量集
	 * 
	 */
	public void configure(Element _e, Properties _properties)
			throws BaseException {
		XmlElementProperties p = new XmlElementProperties(_e,_properties);
		name = PropertiesConstants.getString(p,"name", "",true);
		possessive = PropertiesConstants.getBoolean(p,"possessive", possessive);
	
		String _dataType = PropertiesConstants.getString(p,"dataType", dataType.name());
		dataType = DataType.from(_dataType);
		
		//partitioner
		{
			Element pElem = XmlTools.getFirstElementByPath(_e, "partitioner");
			if (pElem == null){
				throw new BaseException("core.nopartitioner","Can not find partitioner element,check your xml.");
			}
			
			Partitioner.TheFactory factory = new Partitioner.TheFactory();
			partitioner = factory.newInstance(pElem, p);
		}
		
	}

	/**
	 * 报告到XML
	 * 
	 * @param xml XML节点
	 */
	public void report(Element xml) {
		if (xml != null){
			xml.setAttribute("name", name);
			xml.setAttribute("possessive", Boolean.toString(possessive));
			xml.setAttribute("dataType", dataType.name());
			
			Document doc = xml.getOwnerDocument();
			
			if (partitioner != null){
				Element _partitioner = doc.createElement("partitioner");
				partitioner.report(_partitioner);
				xml.appendChild(_partitioner);
			}
		}
	}

	/**
	 * 报告到JSON
	 * 
	 * @param json JSON节点
	 */
	public void report(Map<String, Object> json) {
		if (json != null){
			json.put("name", name);
			json.put("possessive", possessive);
			json.put("dataType", dataType.name());
			
			if (partitioner != null){
				Map<String,Object> _partitioner = new HashMap<String,Object>();
				partitioner.report(_partitioner);
				json.put("partitioner", _partitioner);
			}
		}
	}

	/**
	 * 获取真正的key
	 * 
	 * @param key
	 * @return 加工之后的key
	 */
	private String getKey(final String key){
		if (possessive) return key;
		return name + ":" + key;
	}
	
	/**
	 * 获取Table名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 名称
	 */
	protected String name;
	
	/**
	 * select数据行
	 * 
	 * @param _key 数据行的key
	 * @param enableRWSplit 允许读写分离
	 * @return 数据行实例
	 */
	public KeyValueRow select(String _key, boolean enableRWSplit) {
		String key = getKey(_key);
		//通过Key获取该Key的所在分区
		Partition part = partitioner.getPartition(key);
		
		Class<? extends KeyValueRow> clazz = dataTypeMappping.get(dataType);
		try {
			Constructor<? extends KeyValueRow> constructor = clazz.getConstructor(
					Table.DataType.class,String.class,boolean.class,RedisContext.class,Partition.class
					);
			return constructor.newInstance(dataType,key,enableRWSplit,source,part);
		}catch (Exception ex){
			throw new BaseException("core.errorinstance","Can not create a KeyValueRow instance.",ex);
		}
	}

	protected static Hashtable<Table.DataType,Class<? extends KeyValueRow>> dataTypeMappping = null;
	
	static {
		dataTypeMappping = new Hashtable<Table.DataType,Class<? extends KeyValueRow>>();
		
		dataTypeMappping.put(DataType.Bit, RedisBitRow.class);
		dataTypeMappping.put(DataType.String, RedisStringRow.class);
		dataTypeMappping.put(DataType.Integer, RedisIntegerRow.class);
		dataTypeMappping.put(DataType.Float, RedisFloatRow.class);
		dataTypeMappping.put(DataType.ByteArray, RedisByteArrayRow.class);
		dataTypeMappping.put(DataType.Hash, RedisHashRow.class);
		dataTypeMappping.put(DataType.List, RedisListRow.class);
		dataTypeMappping.put(DataType.SortedSet, RedisSortedSetRow.class);
		dataTypeMappping.put(DataType.Set, RedisSetRow.class);
	}
}
