package com.alogic.zk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alogic.remote.backend.AppBackends;
import com.alogic.remote.backend.Backend;
import com.alogic.remote.cluster.Cluster;
import com.anysoft.util.BaseException;
import com.anysoft.util.JsonTools;
import com.anysoft.util.MapProperties;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.UPath;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

/**
 * 基于zk的Cluster
 * @author yyduan
 *
 */
public class ZooKeeperCluster extends Cluster.Abstract implements Watcher{
	/**
	 * a logger of log4j
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(ZooKeeperCluster.class);
	
	/**
	 * 在Zookeeper中的根节点位置
	 */	
	protected String rootPath = "${zookeeper.arm.root}";
	
	/**
	 * zk连接
	 */
	protected ZooKeeperConnector conn = null;
	
	/**
	 * 监听器
	 */
	protected com.anysoft.util.Watcher<AppBackends> listener = null;
	
	/**
	 * 后端节点缓存数据
	 */
	protected Map<String,AppBackends> backends = new ConcurrentHashMap<String,AppBackends>();
	
	@Override
	public void configure(Properties p) {
		super.configure(p);
		
		rootPath = PropertiesConstants.getString(p, "arm.root", rootPath);
		if (rootPath.length() <= 0){
			rootPath = "/alogic/global/arm";
		}	
		
		conn = new ZooKeeperConnector(p);
	}
	
	@Override
	public AppBackends load(String appId) {
		AppBackends found = backends.get(appId);
		if (found == null){
			synchronized(this){
				found = backends.get(appId);
				if (found == null){
					found = loadFromZK(appId);
					if (found != null){
						backends.put(appId, found);
					}
				}
			}
		}
		return found;
	}
	
	protected AppBackends loadFromZK(String appId){
		AppBackends found = null;
		
		try {
			UPath path = new UPath(rootPath + "/" + appId);
			boolean exist = existPath(path, null ,true);
			if (exist){
				found = new AppBackends(appId);
				String [] children = loadChildren(path, this,true);
				if (children != null){
					for (String child:children){
						UPath childPath = path.append(child);
						String data = loadData(childPath, null,true);
						if (data != null){
							Backend backend = createBackend(data);
							if (backend != null){
								found.addBackend(backend);
							}
						}
					}
				}
			}
		}catch (BaseException ex){
			LOG.error("Can not load app backends:" + appId,ex);
		}
		return found;		
	}

	@SuppressWarnings("unchecked")
	protected Backend createBackend(String data) {
		JsonProvider provider = JsonProviderFactory.createProvider();		
		Object jsonObj = provider.parse(data);
		
		if (jsonObj != null && jsonObj instanceof Map){
			Map<String,Object> mapObj = (Map<String,Object>) jsonObj;
			String ip = JsonTools.getString(mapObj, "ip", "");
			String port = JsonTools.getString(mapObj, "port", "");
			if (ip.length() > 0 && port.length() > 0){
				Backend.Default backend = new Backend.Default();
				backend.configure(new MapProperties(mapObj,Settings.get()));
				return backend;
			}
		}
		return null;
	}

	
	/**
	 * 装入ZooKeeper节点数据
	 * @param path 节点路径
	 * @param watcher 监听器
	 * @param ignoreException 是否忽略异常
	 * @return 节点数据
	 */
	protected String loadData(UPath path,Watcher watcher,boolean ignoreException){
		if (!conn.isConnected()){
			conn.connect();
		}
		return conn.loadData(path, watcher,ignoreException);		
	}
	
	/**
	 * 装入指定节点的所有子节点
	 * @param path
	 * @param watcher
	 * @param ignoreException 是否忽略异常
	 * @return 子节点列表
	 */
	protected String [] loadChildren(UPath path,Watcher watcher,boolean ignoreException){
		if (!conn.isConnected()){
			conn.connect();
		}
		return conn.loadChildren(path, watcher,ignoreException);
	}
	
	/**
	 * 是否存在指定的路径
	 * @param path 路径
	 * @param watcher 监听器
	 * @param ignoreException 是否忽略异常
	 * @return 是否存在指定的路径
	 */
	protected boolean existPath(UPath path,Watcher watcher,boolean ignoreException){
		if (!conn.isConnected()){
			conn.connect();
		}
		return conn.existPath(path, watcher,ignoreException);
	}
	
	@Override
	public void process(WatchedEvent event) {
		EventType type = event.getType();
		switch (type){
			case NodeChildrenChanged:
			{
				UPath path = new UPath(event.getPath());
				AppBackends appBackends = load(path.getId());
				if (appBackends != null){
					fireChangeEvent(path.getId(), appBackends);
				}
				break;
			}
			case NodeCreated:
			case NodeDataChanged:				
			case NodeDeleted:
			{
				//重新订阅
				if (!conn.isConnected()){
					conn.connect();
				}
				conn.existPath(new UPath(event.getPath()),this,true);
				break;
			}
			case None:
			{
				KeeperState state = event.getState();
				if (state == KeeperState.Expired){
					//过期了
					fireAllChangedEvent();
				}
			}
		}
	}
	
	@Override
	public void addWatcher(com.anysoft.util.Watcher<AppBackends> watcher) {	
		listener = watcher;
	}

	@Override
	public void removeWatcher(com.anysoft.util.Watcher<AppBackends> watcher) {
		listener = null;
	}

	/**
	 * 触发变更事件
	 * @param id
	 * @param data
	 */
	protected void fireChangeEvent(String id,AppBackends data){
		backends.remove(id);
		if (listener != null){
			listener.changed(id, data);
		}
	}
	
	protected void fireAllChangedEvent(){
		backends.clear();
		if (listener != null){
			listener.allChanged();
		}
	}
}