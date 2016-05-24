package com.logicbus.backend.message;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.anysoft.util.IOTools;
import com.logicbus.backend.Context;

/**
 * 验证码图片
 * <p>
 * 用于验证码图片服务.
 * @author duanyy
 * @since 1.2.6.5
 * 
 * @version 1.6.5.6 [20160523 duanyy] <br>
 * - 淘汰MessageDoc，采用Context替代 <br>
 * - 增加getContentType和getContentLength <br>
 */
public class CodeImage implements Message {
	protected static final Logger logger = LogManager.getLogger(Message.class);
	/**
	 * 输出图片的宽度
	 */
	private int width = 80;// 图片宽
	
	/**
	 * 随机数发生器
	 */
    private Random random = new Random();	

	/**
	 * 输出图片的高度
	 */
	private int height = 26;// 图片高    
    
	/**
	 * 字体
	 */
	private Font font = null;	
	
	/**
	 * 干扰线的数量
	 */
	private int disturbanceLines = 40;	
    
	/**
	 * 待输出的验证码
	 */
	private String code = "1234";	
	
	/**
	 * content-type
	 */
	private static String contentType = "image/jpeg";

	public int width(){
		return width;
	}
	
	public void width(int _width){
		width = _width;
	}

	public int height(){
		return height;
	}
	
	public void height(int _height){
		height = _height;
	}

	public Font font(){
		return font;
	}
	
	public void font(Font _font){
		font = _font;
	}
	
	public int disturbanceLines(){
		return disturbanceLines;
	}
	
	public void disturbanceLines(int _lines){
		disturbanceLines = _lines;
	}

	public String code(){
		return code;
	}
	public void code(String _code){
		code = _code;
	}
	
	public void init(Context ctx) {
		// nothing to do
	}

	public void finish(Context ctx, boolean closeStream) {
		OutputStream out = null;
		try {
			out = ctx.getOutputStream();
			ctx.setResponseContentType("image/jpeg");
			
			BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
			Graphics g = image.getGraphics();
			g.fillRect(0, 0, width, height);
			g.setFont(font == null ? new Font("Fixedsys",Font.CENTER_BASELINE,18) : font);
			g.setColor(getRandColor(110, 133));
			
			//绘制干扰线
	        for(int i=0;i<=disturbanceLines;i++){
	            drowLine(g);
	        }
	        
	        //绘制code
	        for (int i = 0 ;i < code.length() ; i ++){
	        	drowString(g,String.valueOf(code.charAt(i)),i);
	        }
	        //完成
	        g.dispose();
	        
	        ImageIO.write(image, "JPEG", out);
	        
			out.flush();
		}catch (Exception ex){
			logger.error("Error when writing data to outputstream",ex);
		}finally{
			if (closeStream)
				IOTools.close(out);
		}
	}
	
    private void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x+xl, y+yl);
	}

    private void drowString(Graphics g,String randomString,int i){
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(randomString, 13*i, 16);
    }
    
	private Color getRandColor(int fc,int bc){
        if(fc > 255)
            fc = 255;
        if(bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc-fc-16);
        int g = fc + random.nextInt(bc-fc-14);
        int b = fc + random.nextInt(bc-fc-18);
        return new Color(r,g,b);
    }

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public long getContentLength() {
		return 0;
	}

}
