package com.logicbus.models.servant.getter;

import com.anysoft.util.Properties;
import com.logicbus.backend.Context;
import com.logicbus.backend.message.Message;
import com.logicbus.models.servant.Argument;
import com.logicbus.models.servant.Getter;

/**
 * 常量的Getter
 * <br>
 * 提取所配置的常量.常量配置在argument的parameter中,变量名为value.
 * 
 * @author duanyy
 * @since 1.2.0
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 抛弃MessageDoc <br>
 * 
 * @version 1.4.0 [20141117 duanyy] <br>
 * - 抛弃MessageDoc <br>
 */
public class Constants implements Getter{
	protected String constants;
	public Constants(Properties props){
		if (props != null)
			constants = props.GetValue("value","");
	}
	
	public String getValue(Argument argu, Message msg, Context ctx){
		return constants;
	}

	public String getValue(Argument argu, Context ctx){
		return constants;
	}
}
