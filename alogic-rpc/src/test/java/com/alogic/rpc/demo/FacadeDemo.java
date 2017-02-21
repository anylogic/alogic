package com.alogic.rpc.demo;

import com.alogic.rpc.call.local.LocalCall;
import com.alogic.rpc.demo.api.IHelloworld;
import com.alogic.rpc.demo.skeleton.HelloworldImpl;
import com.alogic.rpc.facade.FacadeFactoryImpl;
import com.anysoft.util.Settings;

public class FacadeDemo {

	public static void main(String[] args) {
		Settings settings = Settings.get();
		settings.SetValue("rpc.master", "java:///conf/rpc.xml#App");
		
		LocalCall.addMaping(IHelloworld.class.getName(), HelloworldImpl.class.getName());
		
		FacadeFactoryImpl factory = new FacadeFactoryImpl();
		
		IHelloworld helloworld = factory.getInterface("local",IHelloworld.class);
		
		System.out.println(helloworld.sayHello("Helloworld"));
	}

}
