package com.alogic.blob;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;

/**
 * 混合部署的BlobManager
 * 
 * @author yyduan
 *
 */
public class HybirdBlobManager extends BlobManager.Abstract{
	/**
	 * 从属的BlobManager
	 */
	protected List<BlobManager> secondary= new ArrayList<BlobManager>();
	
	/**
	 * 主要的BlobManager
	 */
	protected BlobManager master = null;
	
	/**
	 * 是否从master优先读取
	 */
	protected boolean masterFirst = false;
	
	@Override
	public void configure(Element e, Properties p) {
		Properties props = new XmlElementProperties(e,p);
		
		NodeList nodeList = XmlTools.getNodeListByPath(e, "sink");
		
		Factory<BlobManager> f = new Factory<BlobManager>();
		
		for (int i = 0 ;i < nodeList.getLength() ; i ++){
			Node n = nodeList.item(i);
			
			if (n.getNodeType() != Node.ELEMENT_NODE){
				continue;
			}
			
			Element elem = (Element)n;
			
			try {
				BlobManager bm = f.newInstance(elem, props, "module");	
				
				if (XmlTools.getBoolean(elem, "default", true)){
					//标记为default的bm专门用来做写操作
					master = bm;
				}else{
					secondary.add(bm);
				}
			}catch (Exception ex){
				LOG.error("Can not create blob manager with " + XmlTools.node2String(elem));
				LOG.error(ExceptionUtils.getStackTrace(ex));
			}
		}
		
		configure(props);
	}
	
	@Override
	public void configure(Properties p){
		super.configure(p);
		
		masterFirst = PropertiesConstants.getBoolean(p,"masterFirst", masterFirst);
	}
	
	@Override
	public BlobWriter newFile(String id) {
		return master != null ? master.newFile(id) : null;
	}

	@Override
	public BlobReader getFile(String id) {
		if (masterFirst){
			BlobReader reader = getFileFromSecondary(id);
			return reader != null ? reader : master.getFile(id);
		}else{
			BlobReader reader = master == null ? null : master.getFile(id);
			return reader != null ? reader : getFileFromSecondary(id);
		}
	}

	/**
	 * 从备用管理器中获取Reader
	 * @param id 文件id
	 * @return BlobReader
	 */
	protected BlobReader getFileFromSecondary(String id){
		BlobReader reader = null;
		for (BlobManager bm:secondary){
			reader = bm.getFile(id);
			if (reader != null){
				return reader;
			}
		}
		
		return null;
	}
	
	protected boolean existFileInSecondary(String id){
		boolean exist = false;
		for (BlobManager bm:secondary){
			exist = bm.existFile(id);
			if (exist){
				break;
			}
		}		
		return exist;		
	}
	
	@Override
	public boolean existFile(String id) {
		if (masterFirst){
			boolean exist = existFileInSecondary(id);
			return exist ? exist : master.existFile(id);
		}else{
			boolean exist  = master == null ? false : master.existFile(id);
			return exist ? exist : existFileInSecondary(id);
		}
	}

	@Override
	public boolean deleteFile(String id) {
		return master != null ? master.deleteFile(id):false;
	}

}
