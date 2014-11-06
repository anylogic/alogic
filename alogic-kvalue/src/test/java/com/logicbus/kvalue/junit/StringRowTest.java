package com.logicbus.kvalue.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.anysoft.util.Settings;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.kvalue.core.StringRow;
import com.logicbus.kvalue.core.Table;

public class StringRowTest {

	@Test
	public void testSetString() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			//设置为harry
			StringRow row = (StringRow)table.select("yourname", true);
			row.set("harry");
			assertFalse(row.get("jerry").equals("harry") != true);
			
			//覆盖为jerry
			row.set("jerry");
			assertFalse(row.get("harry").equals("jerry") != true);
			
			//设置为harry,且设定有效期100s
			row.set("harry", 100*1000, false, false);
			assertFalse(row.get("jerry").equals("harry") != true);
			assertFalse(row.ttl() < 0);
			
			//设置为jerry，Row存在，可以覆盖
			row.set("jerry",100 * 1000,true,false);
			assertFalse(row.get("harry").equals("jerry") != true);
			//设置为jerry,Row不存在，不可以覆盖
			assertFalse(row.delete() != true);
			row.set("jerry",100 * 1000,true,false);
			assertFalse(row.exists() == true);
			
			//设置为jerry,Row不存在，可以覆盖
			row.set("jerry",100 * 1000,false,true);
			assertFalse(row.get("harry").equals("jerry") != true);
			
			//设置为harry,Row存在，不可以覆盖
			row.set("harry",100 * 1000,false,true);
			assertFalse(row.get("harry").equals("jerry") != true);
			
			assertFalse(row.delete() != true);
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testGet() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			StringRow row = (StringRow)table.select("testget", true);
			
			//对于不存在的Row
			assertFalse(row.get("nil").equals("nil") != true);
			
			//对于存在的Row
			row.set("harry");
			assertFalse(row.get("nil").equals("harry") != true);

			assertFalse(row.delete() != true);

		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testSetRange() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			StringRow row = (StringRow)table.select("greeting", true);
			row.set("hello world");
			
			assertFalse(row.setRange(6, "Redis") != 11);
			assertFalse(row.get("nil").equals("hello Redis") != true);
			
			//如果Row不存在
			assertFalse(row.delete() != true);
			assertFalse(row.setRange(6, "Redis") != 11);
			assertFalse(row.get("nil").trim().equals("Redis") != true);
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testAppend() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			StringRow row = (StringRow)table.select("greeting", true);
			row.delete();
			
			assertFalse(row.append("hello") != 5);
			assertFalse(row.append("Redis") != 10);
			assertFalse(row.get("nil").equals("helloRedis") != true);
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testStrlen() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			StringRow row = (StringRow)table.select("greeting", true);
			row.delete();
			
			assertFalse(row.strlen() != 0);
			row.set("Redis");
			assertFalse(row.strlen() != 5);
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

}
