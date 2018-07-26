package com.alogic.lucene.core;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;

/**
 * Query构建器
 * 
 * @author yyduan
 * @since 1.6.11.31
 */
public interface QueryBuilder extends XMLConfigurable,Configurable{
	
	/**
	 * 根据上下文构建查询
	 * 
	 * @param ctx 上下文
	 * @return Query实例
	 */
	public Query build(Properties ctx,Analyzer analyzer);
	
	/**
	 * Occur
	 * @param ctx 上下文
	 * @return Occur
	 */
	public Occur getOccur(Properties ctx);
	
	/**
	 * 当前查询是否启用
	 * @param ctx 上下文
	 * @return true or false
	 */
	public boolean isEnable(Properties ctx);
	
	/**
	 * 虚基类
	 * @author yyduan
	 *
	 */
	public abstract static class Abstract implements QueryBuilder{
		/**
		 * a logger of slf4j
		 */
		protected static final Logger LOG = LoggerFactory.getLogger(QueryBuilder.class);
		
		/**
		 * 缺省为MUST
		 */
		protected String $occur = "MUST";
		
		protected String $enable = "true";
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			configure(props);
		}

		@Override
		public void configure(Properties p) {
			$occur = PropertiesConstants.getRaw(p,"occur",$occur);
			$enable = PropertiesConstants.getRaw(p,"enable",$enable);
		}
		
		@Override
		public Occur getOccur(Properties ctx){
			return parseOccur(PropertiesConstants.transform(ctx, $occur, "MUST").toUpperCase());
		}
		
		protected Occur parseOccur(String value){
			return Occur.valueOf(value);
		}
		
		@Override
		public boolean isEnable(Properties ctx){
			return PropertiesConstants.transform(ctx, $enable, true);
		}
	}
	
	/**
	 * 工厂类
	 * @author yyduan
	 *
	 */
	public static class TheFactory extends Factory<QueryBuilder>{
		public String getClassName(String _module) {
			if (_module.indexOf('.') < 0){
				return "com.alogic.lucene.query.By" + _module;
			}
			return _module;
		}		
	}
}
