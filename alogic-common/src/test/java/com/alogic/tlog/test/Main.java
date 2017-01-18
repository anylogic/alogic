package com.alogic.tlog.test;

import com.alogic.tracer.Tool;
import com.alogic.tracer.TraceContext;
import com.anysoft.util.Settings;

public class Main {

	public static void main(String[] args) {
		Settings settings = Settings.get();
		
		settings.addSettings("java:///com/alogic/tlog/settings.xml#" + Main.class.getName(), null, Settings.getResourceFactory());

		TraceContext ctx = Tool.start();
		
		foo();
		
		if (ctx != null){
			Tool.end(ctx, "Method", "Main", "ok", "asdasdas");
		}
	}

	private static void foo() {
		TraceContext ctx = Tool.start();
		
		foo1();
		foo2();
		
		if (ctx != null){
			Tool.end(ctx, "Method", "Main", "ok", "asdasdas");
		}		
	}

	private static void foo1() {
		TraceContext ctx = Tool.start();
		
		System.out.println("foo1");
		
		if (ctx != null){
			Tool.end(ctx, "Method", "Main", "ok", "asdasdas");
		}
	}
	
	private static void foo2() {
		TraceContext ctx = Tool.start();
		
		System.out.println("foo2");
		
		if (ctx != null){
			Tool.end(ctx, "Method", "Main", "ok", "asdasdas");
		}
	}	

}
