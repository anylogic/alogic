package com.logicbus.comet;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.w3c.dom.Element;

import com.logicbus.backend.AbstractServant;
import com.logicbus.backend.Context;
import com.logicbus.backend.Comet;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.JsonMessage;
import com.logicbus.backend.message.XMLMessage;
import com.logicbus.models.servant.ServiceDescription;

public class CometDemo extends AbstractServant {

	@Override
	protected void onDestroy() {

	}

	@Override
	protected void onCreate(ServiceDescription sd) throws ServantException {
		getServer();
	}
	
	protected int onJson(Context ctx) throws Exception{
		JsonMessage msg = (JsonMessage) ctx.asMessage(JsonMessage.class);
		Map<String,Object> root = msg.getRoot();
		if (ctx.supportedComet()){
			//支持comet
			Comet comet = ctx.getComet();
			Object _msg = comet.getObject("msg");
			if (_msg == null){
				if (comet.isInitial()){
					// first time
					comet.suspend(1000);
					
					MessageServer server = getServer();
					server.register(comet);
				}else{
					// time out
					root.put("msg", "timeout");
				}
			}else{
				root.put("msg", _msg.toString());
			}
		}else{
			root.put("msg", "Not support comet");
		}
		return 0;
	}
	
	protected int onXml(Context ctx) throws Exception{
		XMLMessage msg = (XMLMessage)ctx.asMessage(XMLMessage.class);
		Element root = msg.getRoot();
		if (ctx.supportedComet()){
			//支持comet
			Comet comet = ctx.getComet();
			Object _msg = comet.getObject("msg");
			if (_msg == null){
				if (comet.isInitial()){
					// first time
					comet.suspend(1000);
					
					MessageServer server = getServer();
					server.register(comet);
				}else{
					// time out
					root.setAttribute("msg", "timeout");
				}
			}else{
				root.setAttribute("msg", _msg.toString());
			}
		}else{
			root.setAttribute("msg", "Not support comet");
		}
		return 0;
	}	
	
	protected static MessageServer server = null;
	
	synchronized public static MessageServer getServer(){
		if (server == null){
			server = new MessageServer();
			server.start();
		}
		
		return server;
	}
	
	public static class MessageServer extends Thread{
		protected Set<Comet> comets = new ConcurrentSkipListSet<Comet>(new Comparator<Comet>(){

			public int compare(Comet o1, Comet o2) {
				return o1.hashCode() - o2.hashCode();
			}
			
		});
		public void register(Comet comet){
			comets.add(comet);
			logger.info("register" + comets.size());
		}
		
		public void unregister(Comet comet){
			comets.remove(comet);
			logger.info("unregister" + comets.size());
		}
		
		public void run(){
			int i = 0;
			while (i++ < 10000){
				try {
					sleep(1000);
				}catch (Exception ex){
					
				}
				logger.info(comets.size());
				Iterator<Comet> iter = comets.iterator();
				while (iter.hasNext()){
					Comet c = iter.next();
					c.setObject("msg", "Message " + i + " received");
					logger.info("Message arrived");
					c.resume();
				}
				comets.clear();
			}
		}
	}
}
