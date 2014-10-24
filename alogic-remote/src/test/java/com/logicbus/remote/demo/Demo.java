package com.logicbus.remote.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.anysoft.util.JsonTools;
import com.anysoft.util.Settings;
import com.logicbus.remote.context.CallSource;
import com.logicbus.remote.core.Builder;
import com.logicbus.remote.core.BuilderFactory;
import com.logicbus.remote.core.Call;
import com.logicbus.remote.core.Parameters;
import com.logicbus.remote.core.Result;

public class Demo {

	public static void main(String[] args) {
		//确保环境变量中的call.master指向context配置文件
		Settings settings = Settings.get();
		settings.SetValue("call.master", "java:///com/logicbus/remote/demo/call.context.demo.xml");
		
		testCall("aSimulator",null);
		
		testCall("aHttpCall",null);

		//测试简单类型的Builder
		{
			ResultListener listener = new ResultListener(){

				
				public void resultProcess(Result result) {
					System.out.println("=================================================================================");
					System.out.println("armId:" + result.getData("armId", BuilderFactory.STRING));
					System.out.println("duration:" + result.getData("duration", BuilderFactory.INTEGER));
					System.out.println("notexisted:" + result.getData("notexisted", BuilderFactory.INTEGER));
					System.out.println("=================================================================================");
				}
				
			};
			
			testCall("aSimulator",listener);
			
			testCall("aHttpCall",listener);
		}
		
		//测试数组数据
		{
			ResultListener listener = new ResultListener(){

				
				public void resultProcess(Result result) {
					System.out.println("=================================================================================");

					List<Object> arls = result.getData("arls", BuilderFactory.LIST);
					for (int i = 0 ;i < arls.size() ; i ++){
						System.out.println("arls[" + i + "]:" + arls.get(i));
					}
					
					List<Object> ports = result.getData("ports", BuilderFactory.LIST);
					for (int i = 0 ;i < arls.size() ; i ++){
						System.out.println("ports[" + i + "]:" + ports.get(i));
					}
					
					System.out.println("=================================================================================");
				}
				
			};
			
			testCall("aSimulator",listener);
			
			testCall("aHttpCall",listener);
		}
		
		//自定义Builder
		{
			final Builder<List<ARL>> builder = new Builder<List<ARL>>(){

				
				public Object serialize(String id, List<ARL> o) {
					return o;
				}

				
				public List<ARL> deserialize(String id, Object json) {
					if (!(json instanceof List)){
						return null;
					}

					@SuppressWarnings("unchecked")
					List<Object> data = (List<Object>)json;
					
					List<ARL> result = new ArrayList<ARL>();
					for (Object o:data){
						if (o instanceof Map){
							@SuppressWarnings("unchecked")
							Map<String,Object> _o = (Map<String,Object>)o;
							ARL arl = new ARL();
							
							arl.ip = JsonTools.getString(_o, "ip","");
							arl.port = JsonTools.getInt(_o, "port",0);
							arl.priority = JsonTools.getInt(_o, "priority",1);
							arl.weight = JsonTools.getInt(_o, "weight",2);
							
							result.add(arl);
						}
					}
					
					return result;
				}
				
			};
			
			ResultListener listener = new ResultListener(){

				
				public void resultProcess(Result result) {
					System.out.println("=================================================================================");

					List<ARL> arls = result.getData("arls", builder);
					for (int i = 0 ;i < arls.size() ; i ++){
						System.out.println("arls[" + i + "]:" + arls.get(i));
					}
					
					List<Object> ports = result.getData("ports", BuilderFactory.LIST);
					for (int i = 0 ;i < arls.size() ; i ++){
						System.out.println("ports[" + i + "]:" + ports.get(i));
					}
					
					System.out.println("=================================================================================");
				}
				
			};
			
			testCall("aSimulator",listener);
			
			testCall("aHttpCall",listener);
		}
	}

	public static void testCall(String id,ResultListener listener){
		//to get call
		Call call = CallSource.getCall(id);
		
		if (call == null){
			//可能在context中没有定义指定的call，返回为空
			System.out.println("Can not find a call named:" + id);
		}else{
			try {
				//首先，获取一个Parameters对象，以便输入参数
				Parameters paras = call.createParameter();
				
				//进行服务调用
				Result result = call.execute(paras);
			
				//对Result进行操作
				if (result.getCode().equals("core.ok")){
					//服务返回ok
					System.out.println("Code: " + result.getCode());
					System.out.println("Reason: " + result.getReason());
					System.out.println("Host: " + result.getHost());
					System.out.println("Serial: " + result.getGlobalSerial());
					System.out.println("Duration(on server):" + result.getDuration() + "ms");
					
					if (listener != null){
						listener.resultProcess(result);
					}
				}else{
					System.out.println("A remote error is returned.[" + result.getCode() + "]" + result.getReason());
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

	public static interface ResultListener{
		public void resultProcess(Result result);
	}
	
	public static class ARL {
		public int port;
		public int weight;
		public int priority;
		public String ip;
		
		public String toString(){
			return "port=" + port + "&weight=" + weight + "&priority=" + priority + "&ip=" + ip;
		}
	}
}
