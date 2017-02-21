package com.alogic.rpc;

/**
 * <p>
 * rpc invoke filter，用于rpc调用中客户端向服务端传递额外参数
 * </p>
 * 
 * Useage:<br>
 * 客户端:<br>
 * 
 * <pre>
 * public void doFilter(InvokeContext ctx) {
 * 	ctx.setAttribute("username", "zhangsan");
 * }
 * </pre>
 * 
 * 服务端:<br>
 * 
 * <pre>
 * public void doFilter(InvokeContext ctx) {
 * 	String username = (String) ctx.getAttribute("username");
 * }
 * 
 * </pre>
 * 
 * 说明：可以配置多个filter
 * 
 * @author xkw
 * @since 1.6.7.15
 */
public interface InvokeFilter {

	void doFilter(InvokeContext invokeContext);

}
