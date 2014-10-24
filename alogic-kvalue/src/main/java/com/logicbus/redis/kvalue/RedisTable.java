package com.logicbus.redis.kvalue;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

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
 */
public class RedisTable implements Table {
	protected RedisContext source = null;
	protected Partitioner partitioner = null;
	protected boolean possessive = false;
	protected Table.DataType dataType = Table.DataType.String;
	
	public RedisTable(RedisContext redisSource) {
		source = redisSource;
	}

	
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

	
	public void report(Element xml) {

	}

	
	public void report(Map<String, Object> json) {

	}

	private String getKey(final String key){
		if (possessive) return key;
		return name + ":" + key;
	}
	
	
	public String getName() {
		return name;
	}

	protected String name;
	

	
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
