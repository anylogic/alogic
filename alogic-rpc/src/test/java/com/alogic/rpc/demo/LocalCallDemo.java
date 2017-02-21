package com.alogic.rpc.demo;

import com.alogic.rpc.Parameters;
import com.alogic.rpc.Result;
import com.alogic.rpc.call.local.LocalCall;
import com.alogic.rpc.demo.api.IHelloworld;
import com.alogic.rpc.demo.skeleton.HelloworldImpl;


public class LocalCallDemo {
	private LocalCallDemo(){
		
	}
	
	
	public static void main(String [] args){
		LocalCall call = new LocalCall();
		
		LocalCall.addMaping(IHelloworld.class.getName(), HelloworldImpl.class.getName());
		
		Parameters ctx = call.newParameters();
		ctx.params("Hello world");
		
		Result ret = call.invoke(IHelloworld.class.getName(),"sayHello",ctx);
		System.out.println(ret.ret());
	}
}
