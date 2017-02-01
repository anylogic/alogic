package com.anysoft.util;

import java.io.PrintStream;

import org.slf4j.Logger;

/**
 * Copyright
 * 
 * @author duanyy
 * @since 1.6.1.5
 * 
 * @version 1.6.7.9 [20170201 duanyy] <br>
 * - 采用SLF4j日志框架输出日志 <br>
 */
public class Copyright {
	public static void bless(Logger logger,String tabs){
		logger.info(tabs + "                   _ooOoo_                    ");
		logger.info(tabs + "                  o8888888o                   ");
		logger.info(tabs + "                  88\" . \"88                   ");
		logger.info(tabs + "                  (| -_- |)                   ");
		logger.info(tabs + "                  O\\  =  /O                   ");
		logger.info(tabs + "               ____/`---'\\____                ");
		logger.info(tabs + "             .'  \\\\|     |//  `.              ");
		logger.info(tabs + "            /  \\\\|||  :  |||//  \\             ");
		logger.info(tabs + "           /  _||||| -:- |||||-  \\            ");
		logger.info(tabs + "           |   | \\\\\\  -  /// |   |            ");
		logger.info(tabs + "           | \\_|  ''\\---/''  |   |            ");
		logger.info(tabs + "           \\  .-\\__  `-`  ___/-. /            ");
		logger.info(tabs + "         ___`. .'  /--.--\\  `. . __           ");
		logger.info(tabs + "      .\"\" '<  `.___\\_<|>_/___.'  >'\"\".        ");
		logger.info(tabs + "     | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |      ");
		logger.info(tabs + "     \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /      ");
		logger.info(tabs + "======`-.____`-.___\\_____/___.-`____.-'====== ");
		logger.info(tabs + "                   `=---='                    ");
		logger.info(tabs + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ");
		logger.info(tabs + "         佛祖保佑       永无BUG                       ");
		logger.info(tabs + "         Thanks to God for No Bug              ");		
	}
	public static void bless(PrintStream out,String tabs){
		out.println(tabs + "                   _ooOoo_                    ");
		out.println(tabs + "                  o8888888o                   ");
		out.println(tabs + "                  88\" . \"88                   ");
		out.println(tabs + "                  (| -_- |)                   ");
		out.println(tabs + "                  O\\  =  /O                   ");
		out.println(tabs + "               ____/`---'\\____                ");
		out.println(tabs + "             .'  \\\\|     |//  `.              ");
		out.println(tabs + "            /  \\\\|||  :  |||//  \\             ");
		out.println(tabs + "           /  _||||| -:- |||||-  \\            ");
		out.println(tabs + "           |   | \\\\\\  -  /// |   |            ");
		out.println(tabs + "           | \\_|  ''\\---/''  |   |            ");
		out.println(tabs + "           \\  .-\\__  `-`  ___/-. /            ");
		out.println(tabs + "         ___`. .'  /--.--\\  `. . __           ");
		out.println(tabs + "      .\"\" '<  `.___\\_<|>_/___.'  >'\"\".        ");
		out.println(tabs + "     | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |      ");
		out.println(tabs + "     \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /      ");
		out.println(tabs + "======`-.____`-.___\\_____/___.-`____.-'====== ");
		out.println(tabs + "                   `=---='                    ");
		out.println(tabs + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ");
		out.println(tabs + "         佛祖保佑       永无BUG                       ");
		out.println(tabs + "         Thanks to God for No Bug              ");		
	}	
}
