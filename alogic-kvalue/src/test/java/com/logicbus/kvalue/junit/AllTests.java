package com.logicbus.kvalue.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BitRowTest.class,IntegerRowTest.class,KeyToolTest.class,StringRowTest.class })
public class AllTests {

}
