package com.alogic.auth.service;

import java.awt.Font;

import com.alogic.auth.Session;
import com.alogic.auth.SessionManagerFactory;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.logicbus.backend.Context;
import com.logicbus.backend.Servant;
import com.logicbus.backend.message.CodeImage;
import com.logicbus.models.servant.ServiceDescription;
import com.anysoft.util.KeyGen;

/**
 * 获取验证码
 * 
 * @author yyduan
 * 
 * @since 1.6.10.10
 */
public class AuthCode extends Servant{

	/**
	 * 验证码字符个数,可通过参数text.chars配置,缺省4个字符
	 */
	protected int charNumbers = 4;
	
	/**
	 * 验证码图片宽度(像素点),可通过参数image.width配置,缺省80个像素
	 */
	protected int imageWidth = 80;
	
	/**
	 * 验证码图片高度(像素点)，可通过参数image.height配置，缺省26个像素
	 */
	protected int imageHeight = 26;
	
	/**
	 * 干扰线条数,可通过参数line.disturbance配置,缺省40条
	 */
	protected int disturbanceLines = 15;
	
	/**
	 * 字体，可通过参数text.font配置，缺省为Fixedsys
	 */
	protected Font font = null;
	
	/**
	 * 是否忽略大小写,可通过text.ignoreCase配置，缺省为true
	 */
	protected boolean ignoreCase = true;
	
	public void create(ServiceDescription sd){
		super.create(sd);
		
		Properties p = sd.getProperties();
		charNumbers = PropertiesConstants.getInt(p, "text.chars", charNumbers,true);
		imageWidth = PropertiesConstants.getInt(p, "image.width", imageWidth,true);
		imageHeight = PropertiesConstants.getInt(p, "image.height", imageHeight,true);
		disturbanceLines = PropertiesConstants.getInt(p, "line.disturbance", disturbanceLines,true);		
		font = new Font(PropertiesConstants.getString(p, "text.font", "Liberation Serif",true),Font.CENTER_BASELINE,18);
		ignoreCase = PropertiesConstants.getBoolean(p, "text.ignoreCase", ignoreCase,true);
	}
	
	@Override
	public int actionProcess(Context ctx)  {
		//采用CodeImage消息
		CodeImage msg = (CodeImage) ctx.asMessage(CodeImage.class);

		String codes = KeyGen.getKey(charNumbers);
		msg.code(codes);
		msg.width(imageWidth);
		msg.height(imageHeight);
		msg.disturbanceLines(disturbanceLines);
		msg.font(font);
			
		//记录到Session
		Session session = SessionManagerFactory.getDefault().getSession(ctx, true);
		session.hSet(Session.DEFAULT_GROUP,"$login.code", 
				getArgument("ignoreCase", Boolean.toString(ignoreCase), ctx).equals("true") ? codes.toLowerCase():codes, 
				true);
		
		return 0;
	}

}
