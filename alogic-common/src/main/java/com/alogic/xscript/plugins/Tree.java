package com.alogic.xscript.plugins;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alogic.xscript.AbstractLogiclet;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsArray;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.doc.json.JsonObject;
import com.anysoft.tree.TreeBuilder;
import com.anysoft.tree.TreeNode;
import com.anysoft.tree.TreeOutput;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * Tree相关插件
 * @author yyduan
 * @since 1.6.11.58 
 */
public class Tree extends Segment{
	protected String cid = "$tree";
	
	public Tree(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		cid = PropertiesConstants.getString(p,"cid",cid,true);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
		TreeBuilder<String> builder = new TreeBuilder.Default<String>();
		
		try{
			ctx.setObject(cid, builder);			
			super.onExecute(root, current, ctx, watcher);
		}finally{
			ctx.removeObject(cid);
		}
	}

	/**
	 * 向树中增加节点
	 * @author yyduan
	 *
	 */
	public static class Node extends AbstractLogiclet{
		protected String pid = "$tree";
		protected String $id;
		protected String $parentId = "0";
		protected String $data;
		
		public Node(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		public void configure(Properties p){
			super.configure(p);
			pid = PropertiesConstants.getString(p,"pid",pid,true);
			
			$id = PropertiesConstants.getRaw(p,"id",$id);
			$parentId = PropertiesConstants.getRaw(p,"parent",$parentId);
			$data = PropertiesConstants.getRaw(p,"data",$data);
		}
		
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			TreeBuilder<String> builder = ctx.getObject(pid);
			if (builder != null){
				String id = PropertiesConstants.transform(ctx, $id, "");
				if (StringUtils.isNotBlank(id)){
					String parent = PropertiesConstants.transform(ctx, $parentId, "0");
					String data = PropertiesConstants.transform(ctx, $data, "");
					
					builder.addTreeNode(new TreeNode.Default<String>(id,parent,data));
				}
			}
		}
	}
	
	/**
	 * 输出树
	 * @author yyduan
	 *
	 */
	public static class Output extends Segment{
		protected String pid = "$tree";
		protected String $parentId = "0";
		protected String $depth = "2";
		protected String itemTag = "items";
		
		public Output(String tag, Logiclet p) {
			super(tag, p);
		}

		@Override
		public void configure(Properties p){
			super.configure(p);
			pid = PropertiesConstants.getString(p,"pid",pid,true);
			itemTag = PropertiesConstants.getString(p,"tag",itemTag,true);
			$parentId = PropertiesConstants.getRaw(p,"parent",$parentId);
			$depth = PropertiesConstants.getRaw(p,"depth",$depth);
		}
		
		protected void onSuperExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			List<Logiclet> list = children;

			for (int i = 0 ; i < list.size(); i ++){
				Logiclet logiclet = list.get(i);
				if (logiclet != null){
					logiclet.execute(root,current,ctx,watcher);
				}
			}
		}
		
		@Override
		protected void onExecute(final XsObject root,final XsObject current, final LogicletContext ctx, final ExecuteWatcher watcher) {
			TreeBuilder<String> builder = ctx.getObject(pid);
			final Output self = this;
			
			if (builder != null){
				builder.build(PropertiesConstants.transform(ctx, $parentId, "0"), new TreeOutput<String>(){

					@Override
					public Object rootFound(String id) {
						return current;
					}

					@Override
					public Object nodeFound(Object cookies,
							TreeNode<String> node, int depth) {
						XsObject parent = (XsObject)cookies;						
						XsArray list = parent.getArrayChild(itemTag, true);						
						XsObject newChild = list.newObject();	
						
						ctx.SetValue("$tree-id", node.getId());
						ctx.SetValue("$tree-parent", node.getParentId());
						ctx.SetValue("$tree-data", node.getData());
						
						self.onSuperExecute(root, newChild, ctx, watcher);						
						list.add(newChild);
						return newChild;
					}
					
				}, PropertiesConstants.transform(ctx, $depth, 2));
			}
		}		
	}
	
	/**
	 * 树的遍历
	 * @author yyduan
	 *
	 */
	public static class Traverse extends Segment{
		protected String itemTag = "items";
		public Traverse(String tag, Logiclet p) {
			super(tag, p);
		}
		
		@Override
		public void configure(Properties p){
			super.configure(p);
			itemTag = PropertiesConstants.getString(p,"tag",itemTag,true);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onExecute(XsObject root,XsObject current, LogicletContext ctx, ExecuteWatcher watcher) {
			if (root instanceof JsonObject){
				super.onExecute(root, current, ctx, watcher);

				Map<String,Object> jsonCurrent = (Map<String,Object>)current.getContent();
				
				Object items = jsonCurrent.get(itemTag);
				if (items != null && items instanceof List){
					List<Object> list = (List<Object>)items;
					for (Object item:list){
						if (item instanceof Map){
							Map<String,Object> map = (Map<String,Object>)item;
							super.onExecute(root, new JsonObject("menu",map),ctx,watcher);
						}
					}
				}
			}else{
				throw new BaseException("core.e1000",String.format("Tag %s does not support protocol %s",this.getXmlTag(),root.getClass().getName()));	
			}
		}
	}
}