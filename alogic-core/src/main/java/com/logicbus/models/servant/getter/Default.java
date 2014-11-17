package com.logicbus.models.servant.getter;

import com.anysoft.util.Properties;
import com.logicbus.backend.Context;
import com.logicbus.backend.ServantException;
import com.logicbus.backend.message.Message;
import com.logicbus.backend.message.MessageDoc;
import com.logicbus.models.servant.Argument;
import com.logicbus.models.servant.Getter;


/**
 * 缺省的参数Getter
 * 
 * @author duanyy
 *
 * @since 1.0.3
 * 
 * @version 1.0.8 [20140420 duanyy] <br>
 * - 接口{@link com.logicbus.models.servant.Getter Getter}有更新。<br>
 * @version 1.2.0 [20140609 duanyy]<br>
 * - 优化getter的初始化
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 抛弃MessageDoc <br>
 */
public class Default implements Getter {
	
	public Default(Properties props){
		
	}
	
	public String getValue(Argument argu, MessageDoc msg, Context ctx) throws ServantException {
		String id = argu.getId();
		String value;
		if (argu.isOption()){
			value = ctx.GetValue(id, argu.getDefaultValue());
		}else{
			value = ctx.GetValue(id, "");
			if (value == null || value.length() <= 0){
				throw new ServantException("client.args_not_found",
						"Can not find parameter:" + id);
			}
		}
		return value;
	}

	
	public String getValue(Argument argu, Message msg, Context ctx)
			throws ServantException {
		String id = argu.getId();
		String value;
		if (argu.isOption()){
			value = ctx.GetValue(id, argu.getDefaultValue());
		}else{
			value = ctx.GetValue(id, "");
			if (value == null || value.length() <= 0){
				throw new ServantException("client.args_not_found",
						"Can not find parameter:" + id);
			}
		}
		return value;
	}

	public String getValue(Argument argu, Context ctx) throws ServantException {
		String id = argu.getId();
		String value;
		if (argu.isOption()){
			value = ctx.GetValue(id, argu.getDefaultValue());
		}else{
			value = ctx.GetValue(id, "");
			if (value == null || value.length() <= 0){
				throw new ServantException("client.args_not_found",
						"Can not find parameter:" + id);
			}
		}
		return value;
	}
}
