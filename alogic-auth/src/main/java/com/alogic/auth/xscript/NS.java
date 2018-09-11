package com.alogic.auth.xscript;

import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * Namespace
 * @author yyduan
 * @since 1.6.11.59 [20180911 duanyy]
 */
public class NS extends Segment{

	public NS(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("prcpl-info",PrincipalOperation.Info.class);
		registerModule("prcpl-clear",PrincipalOperation.Clear.class);
		registerModule("prcpl-expire",PrincipalOperation.Expire.class);
		registerModule("prcpl-add-privileges",PrincipalOperation.AddPrivileges.class);
		registerModule("prcpl-list-privileges",PrincipalOperation.ListPrivileges.class);
		registerModule("prcpl-has-privileges",PrincipalOperation.HasPrivileges.class);
		registerModule("prcpl-get",PrincipalOperation.GetProperty.class);
		registerModule("prcpl-set",PrincipalOperation.SetProperty.class);
		
		registerModule("sess-info",SessionOperation.Info.class);
		registerModule("sess-locate",SessionOperation.Locate.class);		
		registerModule("sess-expire",SessionOperation.Expire.class);
		registerModule("sess-login",SessionOperation.SetLoggedIn.class);
		registerModule("sess-logout",SessionOperation.SetLoggedOut.class);

		registerModule("sess-hdel",SessionOperation.HashDel.class);
		registerModule("sess-hexist",SessionOperation.HashExist.class);
		registerModule("sess-hget",SessionOperation.HashGet.class);
		registerModule("sess-hgetall",SessionOperation.HashGetAll.class);
		registerModule("sess-hset",SessionOperation.HashSet.class);
		registerModule("sess-hsize",SessionOperation.HashSize.class);

		registerModule("sess-sadd",SessionOperation.SetAdd.class);
		registerModule("sess-sexist",SessionOperation.SetExist.class);
		registerModule("sess-smembers",SessionOperation.SetMembers.class);
		registerModule("sess-ssize",SessionOperation.SetSize.class);		
		registerModule("sess-sdel",SessionOperation.SetDel.class);
		
		registerModule("cookie-set",CookieOperation.SetCookie.class);
		registerModule("cookie-get",CookieOperation.GetCookie.class);
		
	}

}
