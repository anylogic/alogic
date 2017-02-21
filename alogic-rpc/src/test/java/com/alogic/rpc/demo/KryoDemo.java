package com.alogic.rpc.demo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoDemo {

	public static void main(String[] args) {
		Kryo kryo = new Kryo();
		
		Output out = new Output(1024);
		
		kryo.writeObject(out, 1000);
		
		byte[] buffer = out.getBuffer();
		
		Input in = new Input(buffer);
		
		System.out.println(kryo.readObject(in, int.class));
		
	}

}
