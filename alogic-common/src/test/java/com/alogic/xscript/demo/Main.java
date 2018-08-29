package com.alogic.xscript.demo;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.alogic.xscript.doc.xml.XmlObject;
import com.anysoft.util.CommandLine;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

public class Main {

	public static void runAsJson(String src,Properties p){
		Script script = Script.create(src, p);
		if (script == null){
			System.out.println("Fail to compile the script");
			return;
		}
		long start = System.currentTimeMillis();
		Map<String,Object> root = new HashMap<String,Object>();
		XsObject doc = new JsonObject("root",root);
		LogicletContext ctx = new LogicletContext(p);
		script.execute(doc, doc, ctx, new ExecuteWatcher.Quiet());
		
		System.out.println("Script:" + src);
		System.out.println("Duration:" + (System.currentTimeMillis() - start) + "ms");
		
		JsonProvider provider = JsonProviderFactory.createProvider();
		System.out.println("#########################################################");
		System.out.println(provider.toJson(root));				
		System.out.println("#########################################################");
	}
	
	public static void runAsXml(String src,Properties p){
		try {
			Script script = Script.create(src, p);
			if (script == null){
				System.out.println("Fail to compile the script");
				return;
			}
			long start = System.currentTimeMillis();
			Document doc = XmlTools.newDocument("root");
			XsObject root = new XmlObject("root",doc.getDocumentElement());
			LogicletContext ctx = new LogicletContext(p);
			script.execute(root, root, ctx, new ExecuteWatcher.Quiet());
			
			System.out.println("Script:" + src);
			System.out.println("Duration:" + (System.currentTimeMillis() - start) + "ms");
			
			System.out.println("#########################################################");
			System.out.println(XmlTools.node2String(doc));				
			System.out.println("#########################################################");
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Settings settings = Settings.get();		
		settings.addSettings(new CommandLine(args));		
		
		//runAsJson("java:///xscript/getAsJson.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/setAsJson.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/hash.xml#com.alogic.xscript.demo.Main",settings);

		//runAsXml("java:///xscript/hash.xml#com.alogic.xscript.demo.Main",settings);	
		//runAsJson("java:///xscript/now.xml#com.alogic.xscript.demo.Main",settings);	
		//runAsJson("java:///xscript/incr.decr.xml#com.alogic.xscript.demo.Main",settings);			
		//runAsJson("java:///xscript/async.xml#com.alogic.xscript.demo.Main",settings);
		
		//runAsJson("java:///xscript/foreach.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/repeat.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/rand.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/function.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/setmulti.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/arraystring.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/eval.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/counter.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/arrayset.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/sp.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/plus.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/xml.xml#com.alogic.xscript.demo.Main",settings);
		//runAsJson("java:///xscript/regex.xml#com.alogic.xscript.demo.Main",settings);
		runAsJson("java:///xscript/tree.xml#com.alogic.xscript.demo.Main",settings);
	}

}
