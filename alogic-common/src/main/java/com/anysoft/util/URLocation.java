package com.anysoft.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * URLocation
 * 
 * @author duanyy
 * @version 1.0.5 [20140326 duanyy]
 * - 路径中增加对windows路径的支持(支持\)
 * 
 * @version 1.0.15 [20140617 duanyy]
 * - Path,Query,Fragment的Get函数改为unescape输出
 */
public final class URLocation {
	public final int URL_SCHEME = 1;
	public final int URL_USERINFO = 2;
	public final int URL_SERVER = 4;
	public final int URL_PORT = 8;
	public final int URL_PATH = 16;
	public final int URL_QUERY = 32;
	public final int URL_FRAGMENT = 64;

	protected int fields = 0;
	protected String scheme; 
	protected String path;
	protected String query;
	protected String fragment;
	protected String userInfo;
	protected String server;
	protected String port;
	protected String encoding = "gb2312";
	
	public String getEncoding(){return encoding;}
	public void setEncoding(String _enc){encoding = _enc;}
	public boolean hasScheme(){return (fields & URL_SCHEME) == URL_SCHEME;}
	public boolean hasServer(){return (fields & URL_SERVER) == URL_SERVER;}
	public boolean hasUserInfo(){return (fields & URL_USERINFO) == URL_USERINFO;}
	public boolean hasPort(){return (fields & URL_PORT) == URL_PORT;}
	public boolean hasPath(){return (fields & URL_PATH) == URL_PATH;}
	public boolean hasQuery(){return (fields & URL_QUERY) == URL_QUERY;}
	public boolean hasFragment(){return (fields & URL_FRAGMENT) == URL_FRAGMENT;}

	public String getScheme(){return scheme;}
	public String getServer(){return server;}
	public String getPath(){return unescape(path,encoding);}
	public String getQuery(){return unescape(query,encoding);}
	public String getFragment(){return unescape(fragment,encoding);}
	public String getUser(){
		int pwdPos = userInfo.indexOf(':');
		if (pwdPos < 0){
			return userInfo;
		}
		return userInfo.substring(0, pwdPos);
	}
	public String getPassword(){
		int pwdPos = userInfo.indexOf(':');
		if (pwdPos < 0){
			return "";
		}
		return userInfo.substring(pwdPos + 1);
	}
	
	public String getPort(){
		String _port = port;
		if (_port.length() < 0){
			if (scheme.equals("http")){
				_port = "80";
			}else{
				if (scheme.equals("ftp")){
					_port = "21";
				}
			}
		}
		return _port;
	}
	public String buildPath() 
	{
		StringBuffer _path = new StringBuffer(path);

		if (hasQuery())
			_path.append('?').append(query);

		if (hasFragment())
			_path.append('#').append(fragment);

		return _path.toString();
	}
	public String buildURL(){
		StringBuffer _url = new StringBuffer();
		if (hasScheme()){
			_url.append(scheme).append(":");
		}
		if (hasServer()){
			_url.append("//");
			if (hasUserInfo()){
				_url.append(userInfo).append("@");
			}
			_url.append(server);
			if (hasPort()){
				_url.append(":").append(port);
			}
		}
		
		_url.append(path);
		
		if (hasQuery()){
			_url.append("?").append(query);
		}
		
		if (hasFragment()){
			_url.append("#").append(fragment);
		}
		
		return _url.toString();
	}

	public String buildUnescapedURL(){
		StringBuffer _url = new StringBuffer();
		if (hasScheme()){
			_url.append(scheme).append(":");
		}
		if (hasServer()){
			_url.append("//");
			if (hasUserInfo()){
				_url.append(unescape(userInfo,encoding)).append("@");
			}
			_url.append(unescape(server,encoding));
			if (hasPort()){
				_url.append(":").append(port);
			}
		}
		
		_url.append(unescape(path,encoding));
		
		if (hasQuery()){
			_url.append("?").append(unescape(query,encoding));
		}
		
		if (hasFragment()){
			_url.append("#").append(unescape(fragment,encoding));
		}
		
		return _url.toString();
	}
	
	public URLocation(URLocation other){
		clean();
		clone(other);
	}
	
	public void clone(URLocation other){
		fields = other.fields;
		scheme = other.scheme;
		fragment = other.fragment;
		path = other.path;
		port = other.port;
		query = other.query;
		server = other.server;
		userInfo = other.userInfo;
	}
	
	public URLocation(){
		encoding = "gb2312";
		clean();
	}
	
	public URLocation(String url,String enc){
		encoding = enc;
		clean();
		parse(url);
	}
	public URLocation(String url){
		this(url,"gb2312");
	}	
	public String toString(){
		return buildURL();
	}
	
	public static boolean isUnreserved(char c){
		return isAlpha(c) || isDigit(c) || 	c == '-' || c == '.' || c == '_' || c == '~';		
	}

	public static boolean isReserved (char c)
	{
		return isGenDelim(c) || isSubDelim(c);
	}

	public static boolean isHex(char c)
	{
		return isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}
	
	public static boolean isAlpha(char ch){
		return ch >= 'a' && ch <='z' || ch >= 'A' && ch <= 'Z';
	}
	public static boolean isDigit(char ch){
		return ch >= '0' && ch <= '9';
	}
	public static boolean isGenDelim (char c)
	{
		return c == ':' || c == '/' || 	c == '?' || c == '#' || c == '[' || c == ']' ||	c == '@';
	}
	public static boolean isSubDelim (char c)
	{
		return c == '!' || 	c == '$' || c == '&' || c == '\'' || c == '(' || c == ')' || c == '*' || c == '+' || c == ',' || c == ';' || c == '=';
	}

	public static String escape (String str,String enc)
	{
		try {
			return URLEncoder.encode(str, enc);
		} catch (UnsupportedEncodingException e) {
			return str;
		}	
	}

	public static String unescape (String uri,String enc)
	{
		try {
			return URLDecoder.decode(uri, enc);
		} catch (UnsupportedEncodingException e) {
			return uri;
		}
	}	
	
	public static boolean isEscape(String uri,int offset)
	{
		if(get(uri,offset) == '%' && isHex(get(uri,offset+1)) && isHex(get(uri,offset + 2)))
		{
			return true;
		}
		else
			return false;
	}

	private static char get(String str,int offset){
		if (offset < 0 || offset > str.length() - 1){
			return 0;
		}
		return (char)(str.charAt(offset));
	}
	public void parse(String url){
		if (fields > 0){
			clean();
		}
		parse(url,0);
	}
	
	private int parse(String url,int offset){
		int next =  offset;
		next = parseScheme(url,next);
		next = parseAuthority(url,next);
		next = parsePath(url,next);
		next = parseQuery(url,next);
		return parseFragment(url,next);
	}
	
	private int parseFragment(String url, int _next) {
		int next = _next;
		char current = get(url,next);
		if (current == ('#'))
		{
			current = get(url,++next);
			while(current != 0)
			{
				if (isUnreserved(current) || isSubDelim(current) || isEscape(url,next) ||
						current == (':') || current == ('@') || current == ('/') || current == ('?'))
					fragment += current;
				else
					fragment += escape(String.valueOf(current),encoding);
				
				current = get(url,++next);
			}

			fields |= URL_FRAGMENT;
		}

		return next;
	}
	private int parseQuery(String url, int _next) {
		int next = _next;
		char current = get(url,next);
		if (current == ('?'))
		{
			current = get(url,++next);
			while(current != 0 && current != ('#'))
			{
				if (isUnreserved(current) || isSubDelim(current) || isEscape(url,next) ||
						current == (':') || current == ('@') || current == ('/') || current == ('?'))
					query += current;
				else
					query += escape(String.valueOf(current),encoding);
				
				current = get(url,++next);
			}

			fields |= URL_QUERY;
		}

		return next;
	}
	private int parsePath(String url, int _next) {
		int next = _next;
		char current = get(url,next);
		if (current == '/'){
			path += current;
			current = get(url,++next);
			
			while (current != 0 && current != '#' && current != '?'){
				if (isUnreserved(current) || isSubDelim(current) || isEscape(url,next) || current == ':' || current == '@' || current == '/' || current == '\\'){
					path += current;
				}else{
					path += escape(String.valueOf(current),encoding);
				}
				current = get(url,++next);
			}
			fields |= URL_PATH;			
		}else{
			if (current != 0){
				current = get(url,++next);
				
				while (current != 0 && current != '#' && current != '?'){
					if (isUnreserved(current) || isSubDelim(current) || isEscape(url,next) || current == ':' || current == '@' || current == '/' || current == '\\'){
						path += current;
					}else{
						path += escape(String.valueOf(current),encoding);
					}
					current = get(url,++next);
				}
				fields |= URL_PATH;				
			}
		}
		if (path.length() <= 0){
			path = "/";
		}
		return next;
	}
	private int parseAuthority(String url, int _next) {
		int next = _next;
		if (get(url,next) == '/' && get(url,next+1) == '/')
		{
			next += 2;
			next = parseUserInfo(url,next);
			next = parseServer(url,next);
			next = parsePort(url,next);
		}
		return next;
	}
	private int parsePort(String url,int _next) {
		int next = _next;
		char current = get(url,next);
		if(current == ':')
		{
			next ++;
			current = get(url,next);
			while(isDigit(current))
			{
				port += current;
				next ++;
				current = get(url,next);				
			}

			fields |= URL_PORT;
		}
		return next;
	}
	private int parseServer(String url,int _next) {
		int next = _next;
		char current = get(url, next);
		while (current != 0 && current != '/' && current != ':'
				&& current != '#' && current != '?') {
			if (isUnreserved(current) || isEscape(url, current)
					|| isSubDelim(current)) {
				server += current;
			} else {
				server += escape(String.valueOf(current),encoding);
			}
			current = get(url, ++next);
		}
		fields |= URL_SERVER;
		return next;
	}
	private int parseUserInfo(String url,int _next) {
		int next = _next;
		char current = get(url,next);
		
		while (current != 0 && current != '@' && current != '/' && current != '#' && current != '?'){
			if (isUnreserved(current) || isEscape(url,current) || isSubDelim(current) || current == ':'){
				userInfo += current;
			}else{
				userInfo += escape(String.valueOf(current),encoding);
			}
			current = get(url,++next);
		}
		current = get(url,next);
		if (current == '@'){
			fields |= URL_USERINFO;
			next ++;
		}else{
			userInfo = "";
			next = _next;
		}
		
		return next;
	}
	private int parseScheme(String url, int _next) {
		int next = _next;
		char current = get(url,next);
		if (isAlpha(current)){
			//绗竴涓槸瀛楁瘝
			scheme += current;
			current = get(url,++next);
			while (current != 0 && (isAlpha(current) || isDigit(current) || current == '+' || current == '-' || current == '.')){
				scheme += current;
				current = get(url,++next);
			}
			current = get(url,next);
			if (current == ':'){
				fields = URL_SCHEME;
				next ++;
			}else{
				scheme = "";
				next = _next;
			}
		}
		return next;

	}
	public void clean(){
		fields = 0;
		fragment = "";
		path = "";
		port = "";
		query = "";
		scheme = "";
		server = "";
		userInfo = "";
	}
	
	public URL makeURL() throws MalformedURLException{
		return new URL(buildURL());
	}
	
	public  static void main(String [] args){
		URLocation parser = new URLocation("http://ucs91.stct.gtm.ucweb.com:9080/*");
		System.out.println(parser.getScheme());
		System.out.println(parser.getServer());
		System.out.println(parser.getPath());

	}
}
