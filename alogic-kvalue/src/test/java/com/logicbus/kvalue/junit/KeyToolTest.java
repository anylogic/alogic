package com.logicbus.kvalue.junit;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.anysoft.util.Settings;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.kvalue.core.StringRow;
import com.logicbus.kvalue.core.Table;

public class KeyToolTest {

	@Test
	public void testDelete() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			//已经存在的Row
			StringRow row = (StringRow)table.select("name", true);
			assertFalse(row.set("redis") != true);
			assertFalse(row.delete() != true);

			//不存在的Row
			StringRow notExistRow = (StringRow)table.select("notexist", true);
			assertFalse(notExistRow.exists() == true);
			assertFalse(notExistRow.delete() == true);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testExists() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			//已经存在的Row
			StringRow row = (StringRow)table.select("name", true);
			assertFalse(row.set("redis") != true);
			assertFalse(row.exists() != true);
			assertFalse(row.delete() != true);
			
			//不存在的Row
			StringRow notExistRow = (StringRow)table.select("notexist", true);
			assertFalse(notExistRow.exists() == true);
			assertFalse(notExistRow.delete() == true);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testType() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			StringRow row = (StringRow)table.select("name", true);
			assertFalse(row.set("redis") != true);
			assertFalse(row.exists() != true);
			assertFalse(row.type().equals("String") != true);
			assertFalse(row.delete() != true);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testTtlLongTimeUnit() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			StringRow row = (StringRow)table.select("page", true);
			row.set("www.google.com");
		
			//设置ttl为 10s,等待15s之后，检查Key是否存在
			assertFalse(row.ttl(10, TimeUnit.SECONDS) != true);
			Thread.sleep(15 * 1000);
			assertFalse(row.exists() != false);

		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testTtlAt() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			StringRow row = (StringRow)table.select("page2", true);
			row.set("www.google.com");
		
			//设置ttl为 10s,等待15s之后，检查Key是否存在
			assertFalse(row.ttlAt(System.currentTimeMillis() + 10000, TimeUnit.MILLISECONDS) != true);
			Thread.sleep(15 * 1000);
			assertFalse(row.exists() != false);

		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testTtl() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			StringRow row = (StringRow)table.select("not_exists", true);
			
			assertFalse(row.ttl()!=-2);
			
			row = (StringRow)table.select("testttl", true);
			row.set("value");
			assertFalse(row.ttl()!=-1);
			assertFalse(row.delete() != true);

		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testPersist() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("str_test");
			assertFalse(table == null);

			StringRow row = (StringRow)table.select("page3", true);
			
			row.set("www.google.com");
			
			assertFalse(!row.ttl(10, TimeUnit.SECONDS));
			assertFalse(!row.persist());
			
			assertFalse(row.ttl()!=-1);
			
			assertFalse(row.delete() != true);
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

}
