package com.logicbus.kvalue.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.anysoft.util.Settings;
import com.logicbus.kvalue.context.KValueSource;
import com.logicbus.kvalue.core.BitRow;
import com.logicbus.kvalue.core.Schema;
import com.logicbus.kvalue.core.Table;

public class BitRowTest {

	@Test
	public void test() {
		try {
			Settings settings = Settings.get();
			settings.SetValue("kvalue.master", "java:///com/logicbus/kvalue/context/kvalue.xml");
			
			Schema schema = KValueSource.getSchema("redis");
			assertFalse(schema == null);
			
			Table table = schema.getTable("bit_test");
			assertFalse(table == null);

			BitRow row = (BitRow)table.select("bits", true);
			row.delete();
			
			assertFalse(row.bitCount(0, -1) != 0);
			row.setBit(0, true);
			row.setBit(3, true);
			
			assertFalse(row.bitCount(0, -1) != 2);
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

}
