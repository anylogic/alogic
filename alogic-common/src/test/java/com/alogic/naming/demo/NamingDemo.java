package com.alogic.naming.demo;


import com.alogic.pool.Pool;
import com.alogic.pool.PoolNaming;
import com.alogic.pool.impl.Factory;
import com.alogic.pool.impl.Queued;
import com.alogic.pool.impl.Singleton;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;

public class NamingDemo {

	public static void main(String[] args) {
		Settings settings = Settings.get();		
		settings.SetValue("naming.master", "java:///com/alogic/naming/demo/naming.pool.xml#" + NamingDemo.class.getName());
		
		PoolNaming naming  = PoolNaming.get();
		
		Pool pool = naming.lookup("globalUser");
		
		for (int i = 0 ;i < 1000 ; i ++){
			User user = pool.borrowObject(0, 0);
			user.print();
			pool.returnObject(user);
		}
		
		pool = naming.lookup("user");
		
		for (int i = 0 ;i < 1000 ; i ++){
			User user = pool.borrowObject(0, 0);
			user.print();
			pool.returnObject(user);
		}
		
		pool = naming.lookup("pooledUser");
		
		for (int i = 0 ;i < 1000 ; i ++){
			User user = pool.borrowObject(0, 0);
			user.print();
			pool.returnObject(user);
		}
	}

	public static class User {
		protected String id;
		protected String name;
		
		public User(){
			id = KeyGen.uuid(10, 10);
			name = KeyGen.uuid(32, 64);
		}
		
		public void print(){
			System.out.println(String.format("%s/%s", id,name));
		}
	}
	
	public static class GlobalUserFactory extends Singleton{

		@Override
		public void configure(Properties p) {
			// nothing to do
		}

		@SuppressWarnings("unchecked")
		@Override
		protected <pooled> pooled createObject(int priority, int timeout) {
			return (pooled) new User();
		}

	}
	
	public static class UserFactory extends Factory{

		@Override
		public void configure(Properties p) {
			// nothing to do
		}

		@SuppressWarnings("unchecked")
		@Override
		protected <pooled> pooled createObject(int priority, int timeout) {
			return (pooled)new User();
		}
		
	}
	
	public static class UserQueue extends Queued{
		@SuppressWarnings("unchecked")
		@Override
		protected <pooled> pooled createObject() {
			return (pooled)new User();
		}
		
	}
}
