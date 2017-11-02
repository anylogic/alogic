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

public class KVZRange extends KVRowOperation {

	protected String start = "0";
	protected String end = "150";
	protected String withscores = "false";
	protected String reverse = "false";
	protected String tag = "data";

	public KVZRange(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p) {
		super.configure(p);
		start = PropertiesConstants.getRaw(p, "start", start);
		end = PropertiesConstants.getRaw(p, "end", end);
		withscores = PropertiesConstants.getRaw(p, "withscores", withscores);
		reverse = PropertiesConstants.getRaw(p, "reverse", reverse);
		tag = PropertiesConstants.getRaw(p, "tag", tag);
	}

	@Override
	protected void onExecute(KeyValueRow row, Map<String, Object> root, Map<String, Object> current,
			LogicletContext ctx, ExecuteWatcher watcher) {

		if (row instanceof SortedSetRow) {
			SortedSetRow r = (SortedSetRow) row;
			if(getBoolean(ctx.transform(withscores), false)){
				List<Pair<String,Double>> l=r.rangeWithScores(getLong(ctx.transform(start), 0), getLong(ctx.transform(end), 150l),
						getBoolean(ctx.transform(reverse), false));
				
				List<Map<String,Double>> result=new ArrayList<Map<String,Double>>();
				if(null!=l&&l.size()>0){
					Iterator<Pair<String,Double>> ite=l.iterator();
					while(ite.hasNext()){
						Pair<String,Double> p=ite.next();
						Map<String,Double> map=new HashMap<String,Double>();
						map.put(p.key(), p.value());
						result.add(map);
					}
				}
				
/*				JsonProvider provider = JsonProviderFactory.createProvider();
				System.out.println("111111222------>"+provider.toJson(result));*/
				current.put(ctx.transform(tag), result);
				
				
/*				ctx.SetValue(id,
						String.valueOf( r.rangeWithScores(getLong(ctx.transform(start), 0), getLong(ctx.transform(end), 150l),
								getBoolean(ctx.transform(reverse), false))));*/
			}else{
				/*List<String> l=r.range(getLong(ctx.transform(start), 0), getLong(ctx.transform(end), 150l),
						getBoolean(ctx.transform(reverse), false));*/
		
				
				
				current.put(ctx.transform(tag), r.range(getLong(ctx.transform(start), 0), getLong(ctx.transform(end), 150l),
						getBoolean(ctx.transform(reverse), false)));
				
				
			/*	ctx.SetValue(id,
						String.valueOf(r.range(getLong(ctx.transform(start), 0), getLong(ctx.transform(end), 150l),
								getBoolean(ctx.transform(reverse), false))));*/
			}
				
		}

	}
}
