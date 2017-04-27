package com.alogic.remote;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

public class Demo {

	public static void main(String[] args) {
		try {
			ResourceFactory rf = Settings.getResourceFactory();
			
			InputStream in = rf.load("java:///com/alogic/remote/remote.client.xml#" + Demo.class.getName(), null);
			Document doc = XmlTools.loadFromInputStream(in);
			Element root = doc.getDocumentElement();
			
			Factory<Client> f = new Factory<Client>();
			Properties p = new DefaultProperties("default",Settings.get());
			
			Client client = f.newInstance(root, p, "module");
		
			//进行100次调用
			for (int i = 0 ;i < 100 ; i ++){
				Request request = client.build("post");
				Response res = null;
				try{			
					res = request.execute("/services/core/AclQuery","sn", p);					
					String text = res.asString();					
					System.out.println(text);
				}finally{
					IOTools.close(res,request);
				}
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

}
