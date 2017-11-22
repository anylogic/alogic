package com.alogic.sda;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.load.Loadable;
import com.anysoft.util.Configurable;
import com.anysoft.util.JsonTools;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 私密数据区
 * 
 * @author yyduan
 * @since 1.6.10.8
 */
public interface SecretDataArea extends Loadable{
	
	/**
	 * 获取当前SDA的field的值
	 * @param field filed id
	 * @param current 当前值或缺省值
	 * @return 存贮在SDA中的值
	 */
	public String getField(String field,String current);
	
	public long getField(String field,long current);
	
	public int getField(String field,int current);
	
	public boolean getField(String field,boolean current);
	
	public double getField(String field,double current);
	
	public float getField(String field,float current);
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public static abstract class Abstract implements SecretDataArea,XMLConfigurable,Configurable{
		
		/**
		 * id
		 */
		protected String id;

		/**
		 * 数据加载时间戳
		 */
		protected long timestamp = System.currentTimeMillis();
		
		/**
		 * 数据的生存周期:30分钟
		 */
		public static final long TTL = 30 * 60 * 1000L;	
				
		@Override
		public String getId() {
			return id;
		}

		@Override
		public long getTimestamp() {
			return timestamp;
		}
		
		@Override
		public boolean isExpired() {
			return System.currentTimeMillis() - timestamp > TTL;
		}
	
		@Override
		public void expire(){
			timestamp = timestamp - TTL;
		}

		@Override
		public long getField(String field,long current){
			String value = this.getField(field, "");
			if (StringUtils.isEmpty(value)){
				return current;
			}
			
			try {
				return Long.parseLong(value);
			}catch (NumberFormatException ex){
				return current;
			}
		}
		
		@Override
		public int getField(String field,int current){
			String value = this.getField(field, "");
			if (StringUtils.isEmpty(value)){
				return current;
			}
			
			try {
				return Integer.parseInt(value);
			}catch (NumberFormatException ex){
				return current;
			}
		}
		
		@Override
		public boolean getField(String field,boolean current){
			String value = this.getField(field, "");
			if (StringUtils.isEmpty(value)){
				return current;
			}
			
			try {
				return BooleanUtils.toBoolean(value);
			}catch (NumberFormatException ex){
				return current;
			}			
		}
		
		@Override
		public double getField(String field,double current){
			String value = this.getField(field, "");
			if (StringUtils.isEmpty(value)){
				return current;
			}
			
			try {
				return Double.parseDouble(value);
			}catch (NumberFormatException ex){
				return current;
			}
		}
		
		@Override
		public float getField(String field,float current){
			String value = this.getField(field, "");
			if (StringUtils.isEmpty(value)){
				return current;
			}
			
			try {
				return Float.parseFloat(value);
			}catch (NumberFormatException ex){
				return current;
			}
		}
		
		@Override
		public void report(Element xml) {
			if (xml != null){
				XmlTools.setString(xml, "module", getClass().getName());
				XmlTools.setString(xml,"id",getId());
			}
		}

		@Override
		public void report(Map<String, Object> json) {
			if (json != null){
				JsonTools.setString(json,"module",getClass().getName());
				JsonTools.setString(json,"id",getId());
			}
		}

		@Override
		public void configure(Properties p) {
			id = PropertiesConstants.getString(p,"id",id);
		}

		@Override
		public void configure(Element e, Properties p) {
			XmlElementProperties props = new XmlElementProperties(e,p);
			configure(props);
		}
	}
}
