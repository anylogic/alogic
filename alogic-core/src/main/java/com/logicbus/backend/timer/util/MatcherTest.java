package com.logicbus.backend.timer.util;

import java.util.Date;

import com.logicbus.backend.timer.Matcher;
import com.logicbus.backend.timer.matcher.Crontab;
import com.logicbus.backend.timer.matcher.Daily;
import com.logicbus.backend.timer.matcher.Hourly;
import com.logicbus.backend.timer.matcher.Monthly;
import com.logicbus.backend.timer.matcher.Weekly;

public class MatcherTest {
	public static void test(int step,int count,Matcher matcher){
		long current = System.currentTimeMillis();
		Date __last = null;
		Date __now;
		for (; count > 0 ; current += step,count--){
			__now = new Date(current);
			if (matcher.match(__last, __now, null)){
				System.out.println("Time matched " + __now);
				__last = __now;
			}
		}
	}
	public static void main(String [] args){
		//测试Hourly，调度框架步长:1分钟 ,执行60 * 24次，共1天
		System.out.println("Test Hourly begin...");
		MatcherTest.test(1000*60,60*24,new Hourly("05,10,20,30"));
		System.out.println("Test Hourly end.");
		//测试daily，调度框架步长:1分钟 ,执行60 * 24 * 10次，共10天
		System.out.println("Test Daily begin...");
		MatcherTest.test(1000*60,60*24*10,new Daily("30","08,12"));
		System.out.println("Test Daily end.");
		//测试weekly，调度框架步长:1分钟 ,执行60 * 24 * 50次，共50天
		System.out.println("Test Weekly begin...");
		MatcherTest.test(1000*60,60*24*50,new Weekly("30,40","08,12","1"));
		System.out.println("Test Weekly end.");
		//测试weekly，调度框架步长:1分钟 ,执行60 * 24 * 100次，共100天
		System.out.println("Test Monthly begin...");
		MatcherTest.test(1000*60,60*24*100,new Monthly("30","08","1,3,20"));
		System.out.println("Test Monthly end.");
		
		//测试Crontab，调度框架步长:1分钟 ,执行60 * 24 * 100次，共100天
		System.out.println("Test Monthly begin...");
		MatcherTest.test(1000*60,60*24*100,new Crontab("0-20/5 00 * * MON-SUN/2"));
		System.out.println("Test Monthly end.");
	}
}
