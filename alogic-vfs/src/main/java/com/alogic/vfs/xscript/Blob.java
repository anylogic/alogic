package com.alogic.vfs.xscript;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.blob.BlobManager;
import com.alogic.blob.aws.S3BlobManager;
import com.alogic.blob.naming.BlobManagerFactory;
import com.alogic.blob.vfs.VFSBlobManager;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.anysoft.util.DefaultProperties;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 定位一个blob
 * @author yyduan
 *
 * @since 1.6.11.53
 */
public class Blob extends VFS{
	
	/**
	 * 属性列表
	 */
	protected DefaultProperties props = new DefaultProperties();
	
	protected String cid = "$blob";
	
	public Blob(String tag, Logiclet p) {
		super(tag, p);
	}
	
	@Override
	public void configure(Element element, Properties p) {
		super.configure(element, p);
		
		//将element的配置保存下来
		props.Clear();
		props.loadFromElementAttrs(element);
	}		
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		cid = getCurrentId(p);
	}
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		props.PutParent(ctx);
		try{
			BlobManager bm = null;
			
			String globalId = PropertiesConstants.getString(props,"globalId","",true);		
			if (StringUtils.isEmpty(globalId)){
				//如果没有定义全局的VFS,取本地配置
				TheFactory f = new TheFactory();
				String module = PropertiesConstants.getString(props,"module","vfs",true);
				try{
					bm = f.newInstance(module, props);
				}catch (Exception ex){
					log(String.format("Can not create file system with %s",module));
				}
			}else{
				bm = BlobManagerFactory.get(globalId);
			}	
			
			if (StringUtils.isNotEmpty(cid) && bm != null){
				try{
					ctx.setObject(cid, bm);
					super.onExecute(root, current, ctx, watcher);
				}finally{
					ctx.removeObject(cid);
				}
			}
		}finally{
			props.PutParent(null);
		}
	}
	
	/**
	 * 获取当前对象id
	 * @param p 
	 * @return 当前对象id
	 */
	protected String getCurrentId(Properties p) {
		return PropertiesConstants.getString(p,"cid",cid,true);
	}

	/**
	 * 工厂类
	 * @author yyduan
	 *
	 */
	public static class TheFactory extends Factory<BlobManager>{
		protected static Map<String,String> mapping = new HashMap<String,String>();
		public String getClassName(String module){
			String found = mapping.get(module);
			return StringUtils.isEmpty(found)?module:found;
		}
		
		static{
			mapping.put("s3", S3BlobManager.class.getName());
			mapping.put("vfs", VFSBlobManager.class.getName());
		}
	}
}