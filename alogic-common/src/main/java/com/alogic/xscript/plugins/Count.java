package com.alogic.xscript.plugins;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 计数器
 * @author yyduan
 * @version 1.6.11.33 [20180601 duanyy] <br>
 * - 修正counter处理大的整数时的bug; <br>
 */
public class Count extends Segment {
	/**
	 * 计数器
	 */
	protected static Map<String,Counter> counters = 
			new ConcurrentHashMap<String,Counter>();
	
	protected String cid = "$counter";
	
	protected String id = "default";
	
	protected boolean list = false;
	
	protected boolean create = true;
	
	public Count(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("counter-get",CounterGet.class);
		registerModule("counter-set",CounterSet.class);
		registerModule("counter-add",CounterAdd.class);
		registerModule("counter-list",CounterList.class);
	}

	public void configure(Properties p){
		super.configure(p);
		
		cid = PropertiesConstants.getString(p,"cid",cid,true);
		id = PropertiesConstants.getString(p,"id",id,true);
		list = PropertiesConstants.getBoolean(p,"list",list,true);
		create = PropertiesConstants.getBoolean(p,"create",create,true);
	}

	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		if (list){
			Counter[] list = getCounterList();
			for (Counter counter:list){
				try {			
					ctx.setObject(cid, counter);
					super.onExecute(root, current, ctx, watcher);
				}finally{
					ctx.removeObject(cid);
				}				
			}
		}else{
			Counter counter = getCounter(id,create);
			if (counter != null){
				try {			
					ctx.setObject(cid, counter);
					super.onExecute(root, current, ctx, watcher);
				}finally{
					ctx.removeObject(cid);
				}				
			}
		}
	}
	
	protected static Counter[] getCounterList(){
		return counters.values().toArray(new Counter[0]);
	}
	
	protected static Counter getCounter(String id,boolean create){
		Counter found = counters.get(id);
		if (found == null && create){
			synchronized(Count.class){
				found = counters.get(id);
				if (found == null){
					found = new Counter(id);
					counters.put(id, found);
				}
			}
		}
		
		return found;
	}
	
	public static class CounterSet extends AbstractLogiclet{
		protected String pid = "$counter";
		
		protected String $id = "";
		protected String $field = "";
		protected String $value = "0";
		
		public CounterSet(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			pid = PropertiesConstants.getString(p, "pid", pid);
			$id = PropertiesConstants.getRaw(p,"id",$id);
			$field = PropertiesConstants.getRaw(p,"field",$id);
			$value = PropertiesConstants.getRaw(p,"value",$value);
		}
		
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			Counter counter = ctx.getObject(pid);		
			if (counter == null){
				throw new BaseException("core.e1001","It must be in a counter context,check your script.");
			}
			
			String id = PropertiesConstants.transform(ctx,$id, "");
			if (StringUtils.isNotEmpty(id)){
				CounterField gf = counter.getField(PropertiesConstants.transform(ctx,$field,id), true);
				ctx.SetValue(id, String.valueOf(gf.getAndSet(PropertiesConstants.transform(ctx, $value, 0L))));
			}
		}
	}
	
	public static class CounterAdd extends AbstractLogiclet{
		protected String pid = "$counter";
		
		protected String $id = "";
		protected String $field = "";
		protected String $value = "0";
		
		public CounterAdd(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			
			pid = PropertiesConstants.getString(p, "pid", pid);
			$id = PropertiesConstants.getRaw(p,"id",$id);
			$field = PropertiesConstants.getRaw(p,"field",$id);
			$value = PropertiesConstants.getRaw(p,"value",$value);
		}
		
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			Counter counter = ctx.getObject(pid);		
			if (counter == null){
				throw new BaseException("core.e1001","It must be in a counter context,check your script.");
			}
			
			String id = PropertiesConstants.transform(ctx,$id, "");
			if (StringUtils.isNotEmpty(id)){
				CounterField gf = counter.getField(PropertiesConstants.transform(ctx,$field,id), true);
				ctx.SetValue(id, String.valueOf(gf.addAndGet(PropertiesConstants.transform(ctx, $value, 0))));
			}
		}
	}	
	
	public static class CounterGet extends AbstractLogiclet{
		protected String pid = "$counter";
		
		protected String $id = "";
		protected String $field = "";
		protected String $dft = "0";
		
		public CounterGet(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		public void configure(Properties p){
			super.configure(p);
			
			pid = PropertiesConstants.getString(p, "pid", pid);
			$id = PropertiesConstants.getRaw(p,"id",$id);
			$field = PropertiesConstants.getRaw(p,"field",$id);
			$dft = PropertiesConstants.getRaw(p,"dft",$dft);
		}
		
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			Counter counter = ctx.getObject(pid);		
			if (counter == null){
				throw new BaseException("core.e1001","It must be in a counter context,check your script.");
			}
			
			String id = PropertiesConstants.transform(ctx,$id, "");
			if (StringUtils.isNotEmpty(id)){
				CounterField gf = counter.getField(PropertiesConstants.transform(ctx,$field,id), false);
				if (gf != null){
					ctx.SetValue(id, String.valueOf(gf.get()));
				}else{
					ctx.SetValue(id, PropertiesConstants.transform(ctx, $dft, "0"));
				}
			}
		}		
	}	
	
	public static class CounterList extends Segment{
		protected String pid = "$counter";
		
		public CounterList(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		public void configure(Properties p){
			super.configure(p);
			
			pid = PropertiesConstants.getString(p, "pid", pid);
		}
		
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			Counter counter = ctx.getObject(pid);		
			if (counter == null){
				throw new BaseException("core.e1001","It must be in a counter context,check your script.");
			}
			
			CounterField[] fields = counter.getFieldList();
			
			ctx.SetValue("$id", counter.getId());
			for (CounterField f:fields){
				ctx.SetValue("$field", f.getId());
				ctx.SetValue("$value", String.valueOf(f.get()));
				super.onExecute(root, current, ctx, watcher);
			}
		}		
	}	
	
	/**
	 * 计数器
	 * @author yyduan
	 *
	 */
	public static class Counter {
		protected String id;
		protected Map<String,CounterField> fields = new ConcurrentHashMap<String,CounterField>();
		
		public Counter(String id){
			this.id = id;
		}
		
		public String getId(){
			return this.id;
		}
		
		public CounterField getField(String field,boolean create){
			CounterField found = fields.get(field);
			if (found == null && create){
				synchronized(this){
					found = fields.get(field);
					if (found == null && create){
						found = new CounterField(field);
						fields.put(field, found);
					}
				}
			}			
			return found;
		}
		
		public CounterField[] getFieldList(){
			return fields.values().toArray(new CounterField[0]);
		}
	}
	
	/**
	 * 计数器Field
	 * @author yyduan
	 *
	 */
	public static class CounterField {
		protected String id;
		protected AtomicLong value = new AtomicLong(0);
		
		public CounterField(String id){
			this.id = id;
		}
		
		public CounterField(String id,long value){
			this.id = id;
			this.value.set(value);
		}
		
		public String getId(){
			return this.id;
		}
		
		public long get(){
			return this.value.get();
		}
		
		public long getAndAdd(long value){
			return this.value.getAndAdd(value);
		}
		
		public long getAndSet(long value){
			return this.value.getAndSet(value);
		}
		
		public long addAndGet(long value){
			return this.value.addAndGet(value);
		}	
		
	}
}