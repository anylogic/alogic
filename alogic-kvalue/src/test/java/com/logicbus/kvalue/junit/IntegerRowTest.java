package com.logicbus.kvalue.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.anysoft.util.Settings;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.IntegerRow;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.kvalue.core.Table;


public class IntegerRowTest {

	@Test
	public void testIncr() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("int_test");
			assertFalse(table == null);

			IntegerRow row = (IntegerRow)table.select("rank", true);
			row.delete();
			
			//Row不存在时
			assertFalse(row.incr(30) != 30);
			assertFalse(row.get(0) != 30);
			
			//Row存在时
			assertFalse(row.incr(40) != 70);
			assertFalse(row.get(0) != 70);
			
			//递减操作
			assertFalse(row.incr(-30) != 40);
			assertFalse(row.get(0) != 40);

		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

}
