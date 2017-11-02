package com.logicbus.kvalue.xscript.zset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.logicbus.kvalue.xscript.KVRowOperation;
import com.anysoft.util.Pair;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.kvalue.core.KeyValueRow;
import com.logicbus.kvalue.core.SortedSetRow;

/**
 * 
 * zrangebyscore指令
 * 
 * @author zhongyi
 * 
 * @version 1.6.7-20170329 [20170329 duanyy] <br>
 * - 增加kv方式的返回方法 <br>
 */
public class KVZRangeByScore extends KVRowOperation {

	protected String min = "0";
	protected String max = "150";
	protected String withscores = "false";
	protected String reverse = "false";
	protected String tag = "data";

	/**
	 * withscores=true,本参数才生效,用来指定结果集中每个元素的返回格式。withscoreItemType=string,返回的类型为字符串，格式如下“e:element;s:score”;
	 * withscoreItemType=map,item的类型为map,格式如下{“element”:"score"}
	 */
	protected String withscoreItemType = "string";
	
	/**
	 * 预留2个参数用来支持分页
	 */
	protected String offset = "0";
	protected String count = "100";

	public KVZRangeByScore(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override  
	public void configure(Properties p) {
		super.configure(p);
		min = PropertiesConstants.getRaw(p, "min", min);
		max = PropertiesConstants.getRaw(p, "max", max);
		withscores = PropertiesConstants.getRaw(p, "withscores", withscores);
		reverse = PropertiesConstants.getRaw(p, "reverse", reverse);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
		offset = PropertiesConstants.getRaw(p, "offset", offset);
		count = PropertiesConstants.getRaw(p,"count",count);
		withscoreItemType=PropertiesConstants.getRaw(p,"withscore-item-type",withscoreItemType);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {
		double _min = getDouble(ctx.transform(min), 0d);
		double _max = getDouble(ctx.transform(max), 150d);
		long _offset = getLong(ctx.transform(offset), 0l);
		long _count = getLong(ctx.transform(count), 100l);
		
		
		if (row instanceof SortedSetRow) {
			SortedSetRow r = (SortedSetRow) row;
			boolean _reverse=getBoolean(ctx.transform(reverse), false);
			
			if(getBoolean(ctx.transform(withscores), false)){
				List<Pair<String,Double>> l=null;
				l=r.rangeByScoreWithScores(_min, _max, _reverse, _offset, _count);
			    
				List<Object> result=new ArrayList<Object>();
				if(null!=l&&l.size()>0){
					Iterator<Pair<String,Double>> ite=l.iterator();
					while(ite.hasNext()){
						Pair<String,Double> p=ite.next();
						if("map".equals(withscoreItemType)){
							HashMap<String,Double> item = new HashMap<>();
							item.put(p.key(), p.value());
							result.add(item);
						}else{
							if ("kv".equals(withscoreItemType)){
								HashMap<String,Object> item = new HashMap<>();
								item.put("e", p.key());
								item.put("s", p.value());
								result.add(item);
							}else{
								StringBuffer eleWithScore=new StringBuffer("e:");
								eleWithScore.append(p.key()+";s:").append(p.value());
								result.add(eleWithScore.toString());
							}
						}
					}
				}
								
				current.put(ctx.transform(tag), result);	
			}else{
					current.put(ctx.transform(tag), r.rangeByScore(_min, _max,
							_reverse, _offset, _count));
			}			
		}
	}
}
