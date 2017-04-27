package com.alogic.remote.cluster;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.Reportable;
import com.anysoft.util.Settings;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 集群管理器
 * @author yyduan
 * @since 1.6.8.12
 */
public interface ClusterManager extends Reportable,Configurable,XMLConfigurable,AutoCloseable{
	
	/**
	 * 获取指定id的集群
	 * @param id 集群id
	 * @return Cluster
	 */
	public Cluster getCluster(String id);
	
	/**
	 * 获取集群列表
	 * 
	 * @return 集群列表
	 */
	public Cluster [] getClusters();

	/**
	 * 获取缺省的集群
	 * @return 集群
	 */
	public Cluster getDefaultCluster();
	
	/**
	 * 工厂类
	 * @author duanyy
	 *
	 */
	public static class TheFactory extends Factory<ClusterManager>{
		
		/**
		 * a logger of log4j
		 */
		protected static final Logger logger = LoggerFactory.getLogger(ClusterManager.class);
		
		/**
		 * 缺省的配置文件
		 */
		private static final String DEFAULT = "java:///com/alogic/remote/cluster/clusters.xml#" 
				+ ClusterManager.class.getName();
		
		/**
		 * 唯一实例
		 */
		protected static ClusterManager instance = null;
		
		/**
		 * 获取实例
		 * @return 唯一实例
		 */
		public static ClusterManager get(){
			if (instance == null){
				synchronized (TheFactory.class){
					Settings settings = Settings.get();
					
					String secondary = settings.GetValue("cluster.secondary", DEFAULT);
					String master =  settings.GetValue("cluster.master", DEFAULT);
					
					ResourceFactory rf = Settings.getResourceFactory();
					InputStream in = null;
					try {
						in = rf.load(master,secondary, null);
						Document doc = XmlTools.loadFromInputStream(in);		
						if (doc != null){
							TheFactory factory = new TheFactory();
							instance = factory.newInstance(doc.getDocumentElement(), settings,"module",
									ClusterManagerImpl.class.getName());
						}
					}catch (Exception ex){
						logger.error("Error occurs when load xml file,source=" + master, ex);
					}finally {
						IOTools.closeStream(in);
					}
				}
			}
			
			return instance;
		}
	}		
}