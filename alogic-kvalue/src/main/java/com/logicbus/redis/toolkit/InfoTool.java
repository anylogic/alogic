package com.logicbus.redis.toolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.anysoft.util.IOTools;
import com.logicbus.redis.client.Connection;
import com.logicbus.redis.client.Toolkit;
import com.logicbus.redis.util.SafeEncoder;

/**
 * 信息工具
 * @author yyduan
 *
 */
public class InfoTool extends Toolkit {

	public InfoTool(Connection _conn) {
		super(_conn);
	}
	
	public static enum Command {
		INFO;
		public final byte [] raw;		
		Command(){
			raw = SafeEncoder.encode(name());
		}
	}
	
	public void _info(final String section){
		sendCommand(Command.INFO.raw, section);
	}
	
	public String info(final String section){
		_info(section);
		return getBulkReply();
	}	
	
	public void info(final String section,Map<String,Object> result){
		String ret = info(section);
		if (StringUtils.isNotEmpty(ret)){
			BufferedReader reader = new BufferedReader(new StringReader(ret));
			String line = null;
			try{
				while ((line = reader.readLine()) != null){
					if (line.startsWith("#")){
						continue;
					}
					
					String[] vals = line.split(":");
					if (vals.length == 2){
						String k = vals[0];
						String v = vals[1];
						if (StringUtils.isNotEmpty(k) && StringUtils.isNotEmpty(v)){
							result.put(k, v);
						}
					}
				}
			}catch (IOException ex){
				
			}finally{
				IOTools.close(reader);
			}
		}
	}
	
	public void server(Map<String,Object> result){
		info("server",result);
	}
	
	public void clients(Map<String,Object> result){
		info("clients",result);
	}
	
	public void memory(Map<String,Object> result){
		info("memory",result);
	}
	
	public void persistence(Map<String,Object> result){
		info("persistence",result);
	}
	
	public void stats(Map<String,Object> result){
		info("stats",result);
	}
	
	public void replication(Map<String,Object> result){
		info("replication",result);
	}
	
	public void cpu(Map<String,Object> result){
		info("cpu",result);
	}
	
	public void commandstats(Map<String,Object> result){
		info("commandstats",result);
	}
	
	public void cluster(Map<String,Object> result){
		info("cluster",result);
	}
	
	public void keyspace(Map<String,Object> result){
		info("keyspace",result);
	}
	
	public void all(Map<String,Object> result){
		info("all",result);
	}
	
	public void defaults(Map<String,Object> result){
		info("default",result);
	}
}
