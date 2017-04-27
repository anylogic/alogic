package com.alogic.tlog.handler;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.alogic.tlog.TLog;
import com.anysoft.stream.Handler;
import com.anysoft.stream.SlideHandler;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 设置主机，应用等维度
 * 
 * @author yyduan
 * 
 * @since 1.6.8.13
 *
 */
public class SetDims extends SlideHandler<TLog>{

	/**
     * 应用名 
     */
    protected String app = "${server.app}";
    
    /**
     * 主机名。格式为：ip:port.
     */
    protected String host = "${server.ip}:${server.port}";
	
	@Override
	protected void onHandle(TLog t, long timestamp) {
		if (StringUtils.isEmpty(t.app())){
        	t.app(app);
        }
        
        if (StringUtils.isEmpty(t.host())){
        	t.host(host);
        }		
        
		Handler<TLog> handler = getSlidingHandler();
		if (handler != null){
			handler.handle(t, timestamp);
		}
	}

	@Override
	protected void onConfigure(Element e, Properties p) {
		super.onConfigure(e, p);
		app = PropertiesConstants.getString(p,"app",app);
        host = PropertiesConstants.getString(p,"host",host);		
	}
	
	@Override
	public String getHandlerType() {
		return "logger";
	}
}