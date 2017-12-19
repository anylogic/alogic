package com.alogic.event;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alogic.event.handler.Debug;
import com.anysoft.stream.Handler;
import com.anysoft.util.Factory;
import com.anysoft.util.IOTools;
import com.anysoft.util.KeyGen;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.anysoft.util.XmlTools;
import com.anysoft.util.resource.ResourceFactory;

/**
 * 事件总线
 * 
 * @author yyduan
 * 
 * @version 1.6.11.3 [20171219 duanyy] <br>
 * - 可以创建异步或同步事件 <br>
 */
public class EventBus extends Factory<Handler<Event>>{
	/**
	 * a logger of slf4j
	 */
	protected final static Logger LOG = LoggerFactory.getLogger(EventBus.class);
	
	/**
	 * 静态缺省的实例
	 */
	private static Handler<Event> INSTANCE = null;
	
	/**
	 * 缺省的配置文件路径
	 */
	public static final String DEFAULT = "java:///com/alogic/event/event.handler.xml#" + EventBus.class.getName();
	
	/**
	 * 获取缺省的Handler
	 * @return Handler实例
	 */
	public static Handler<Event> getDefault(){
		if (INSTANCE == null){
			synchronized (EventBus.class){
				if (INSTANCE == null){
					Settings p = Settings.get();
					INSTANCE = loadFromPath(
							PropertiesConstants.getString(p,"event.master",DEFAULT),
							PropertiesConstants.getString(p,"event.secondary",DEFAULT),
							p);
				}
			}
		}
		return INSTANCE;
	}
	
	/**
	 * 通过配置文件来创建Handler
	 * 
	 * @param path 主路径
	 * @param secondary 备用路径
	 * @param p 变量集
	 * @return Handler实例
	 */
	public static Handler<Event> loadFromPath(String path,String secondary,Properties p){
		ResourceFactory rf = Settings.getResourceFactory();
		Handler<Event> handler = null;
		InputStream in = null;		
		try {
			in = rf.load(path, secondary, null);
			Document doc = XmlTools.loadFromInputStream(in);
			if (doc != null){
				handler = loadFromElement(doc.getDocumentElement(),p);
			}
		}catch (Exception ex){
			LOG.error(ExceptionUtils.getStackTrace(ex));
		}finally{
			IOTools.close(in);
		}
		return handler;
	}
	
	/**
	 * 通过xml配置节点来创建Handler
	 * 
	 * @param root Xml节点
	 * @param p 环境变量集
	 * @return Handler实例
	 */
	public static Handler<Event> loadFromElement(Element root,Properties p){
		EventBus f = new EventBus();
		return f.newInstance(root, p, "module",Debug.class.getName());
	}
	
	/**
	 * 通过id和type构建一个事件实例
	 * @param id　事件id
	 * @param type 事件类型
	 */
	public static Event newEvent(String id,String type,boolean async){
		return new Event.Default(StringUtils.isEmpty(id)?newId():id, type,async);
	}
	
	/**
	 * 通过类型构建一个事件实例
	 * @param type　事件类型
	 * @return　事件实例
	 */
	public static Event newEvent(String type,boolean async){
		return new Event.Default(newId(), type,async);
	}
	
	/**
	 * 设置事件的属性
	 * @param e　事件实例
	 * @param k　属性key
	 * @param v 属性值
	 * @return　操作之后的事件
	 */
	public static Event setEventProperty(Event e,String k,String v,boolean overwrite){
		if (e != null){
			e.setProperty(k, v, overwrite);
		}
		return e;
	}
	
	/**
	 * 生成全局时间id
	 * @return 全局id
	 */
	public static String newId(){
		return String.format("%d%s",System.currentTimeMillis(),KeyGen.uuid(5, 0, 9));
	}
}
