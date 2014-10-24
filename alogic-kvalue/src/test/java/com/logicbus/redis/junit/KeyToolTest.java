package com.logicbus.redis.junit;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.anysoft.util.IOTools;
import com.logicbus.redis.client.Client;
import com.logicbus.redis.params.ScanParams;
import com.logicbus.redis.result.ScanResult;
import com.logicbus.redis.toolkit.ListTool;
import com.logicbus.redis.toolkit.SetTool;
import com.logicbus.redis.toolkit.StringTool;

public class KeyToolTest {
	public static final String redisHost = "192.168.150.101";
	public static final int redisPort = 6379;
	@Test
	public void testDel() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			
			tool.set("name", "redis");
			
			//删除单个key
			assertFalse(tool.del("name") != 1);
			
			//删除一个不存在的key
			assertFalse(tool.exist("phone"));
			assertFalse(tool.del("phone") != 0);
			
			//删除多个Key
			tool.set("name", "redis");
			tool.set("type", "key-value store");
			tool.set("website", "redis.com");

			assertFalse(tool.del("name","type","website") != 3);
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testExist() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			
			tool.set("db", "redis");
			
			assertFalse(!tool.exist("db"));
			assertFalse(tool.del("db") != 1);
			assertFalse(tool.exist("db"));
			
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testType() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool stringTool = (StringTool) client.getToolKit(StringTool.class);
			stringTool.set("weather", "sunny");
			ListTool listTool = (ListTool) client.getToolKit(ListTool.class);
			listTool.lpush("book_list","programming in scala","programming in Java");
			SetTool setTool = (SetTool) client.getToolKit(SetTool.class);
			setTool.sadd("pet", "dog","cat");
			
			assertEquals(stringTool.type("weather"),"string");
			assertEquals(listTool.type("book_list"),"list");
			assertEquals(setTool.type("pet"),"set");
			
			assertFalse(stringTool.del("weather","book_list","pet") != 3);
			
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testKeys() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			tool.mset("one","1","two","2","three","3","four","4");
			
			Set<String> keys = tool.keys("*o*");
			assertFalse(keys.size() != 3);
			
			keys = tool.keys("t??");
			assertFalse(keys.size() != 1);
			
			assertFalse(tool.del("one","two","three","four") != 4);
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testRename() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			tool.set("message","Hello world");
			
			tool.rename("message", "greeting");
			
			assertFalse(tool.exist("message"));
			assertFalse(!tool.exist("greeting"));
			
			assertFalse(tool.rename("fake_key", "never_exists"));

			tool.set("pc","lenovo");
			tool.set("personal", "dell");
			assertFalse(!tool.rename("pc", "personal"));
			assertEquals(tool.get("pc", "false"),"false");
			assertEquals(tool.get("personal", "false"),"lenovo");
			
			assertFalse(tool.del("greeting","personal") != 2);
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testRenamenx() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			tool.set("player", "MPlayer");
			
			assertFalse(tool.exist("player2"));
			assertFalse(!tool.renamenx("player", "player2"));
			
			tool.set("animal", "bear");
			tool.set("animal2", "butterfly");
			assertFalse(tool.renamenx("animal", "animal2"));
			assertEquals(tool.get("animal", ""), "bear");
			assertEquals(tool.get("animal2",""),"butterfly");
			
			assertFalse(tool.del("player2","animal","animal2") != 3);
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testScan() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			
			for (int i = 0 ;i < 20 ; i ++){
				tool.set("key" + i, "value" + i);
			}
			
			String startCursor = "0";
			String cursor = startCursor;
			int total = 0;
			do {
				ScanResult<String> result = tool.scan(cursor, (new ScanParams()).count(5).match("key*"));
				List<String> keys = result.getResult();
				cursor = result.getCursor();
				total += keys.size();
				
				if (cursor.equals(startCursor))
					break;
			}while (true);
			
			for (int i = 0 ;i < 20 ; i ++){
				tool.del("key"+i);
			}
			
			assertFalse(total != 20);
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testExpire() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			tool.set("page", "www.google.com");
			
			assertFalse(!tool.expire("page", 10, TimeUnit.SECONDS));
			
			Thread.sleep(15 * 1000);
			assertFalse(tool.exist("page"));
			
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testExpireat() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			tool.set("page", "www.google.com");
			
			assertFalse(!tool.expireat("page", System.currentTimeMillis() + 10000));
			
			Thread.sleep(15 * 1000);
			assertFalse(tool.exist("page"));
			
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testTtl() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			
			assertFalse(tool.ttl("not_exists")!=-2);
			
			tool.set("testttl","value");
			assertFalse(tool.ttl("testttl")!=-1);
			
			tool.del("testttl");
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}

	@Test
	public void testPersist() {
		Client client = new Client(redisHost, redisPort);
		try {
			StringTool tool = (StringTool) client.getToolKit(StringTool.class);
			tool.set("page", "www.google.com");
			
			assertFalse(!tool.expire("page", 10, TimeUnit.SECONDS));
			assertFalse(!tool.persist("page"));
			
			assertFalse(tool.ttl("page")!=-1);
			
			tool.del("page");
		} catch (Exception ex) {
			fail("An exception was caught,msg:" + ex.getMessage());
		} finally {
			IOTools.close(client);
		}
	}
}
