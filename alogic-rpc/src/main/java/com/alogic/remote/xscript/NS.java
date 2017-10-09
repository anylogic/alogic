package com.alogic.remote.xscript;

import com.alogic.remote.xscript.request.ByForm;
import com.alogic.remote.xscript.request.ByJson;
import com.alogic.remote.xscript.request.ByText;
import com.alogic.remote.xscript.request.SetContentType;
import com.alogic.remote.xscript.request.SetHeader;
import com.alogic.remote.xscript.response.AsJson;
import com.alogic.remote.xscript.response.AsText;
import com.alogic.remote.xscript.response.Discard;
import com.alogic.remote.xscript.response.GetContentType;
import com.alogic.remote.xscript.response.GetHeader;
import com.alogic.remote.xscript.response.GetReason;
import com.alogic.remote.xscript.response.GetStatusCode;
import com.alogic.remote.xscript.util.Copy;
import com.alogic.remote.xscript.util.Decoder;
import com.alogic.remote.xscript.util.Encoder;
import com.alogic.remote.xscript.util.MD5;
import com.alogic.remote.xscript.util.UrlBuilder;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.plugins.Segment;

/**
 * Namespace
 * @author yyduan
 * @since 1.6.10.3
 */
public class NS extends Segment{

	public NS(String tag, Logiclet p) {
		super(tag, p);
		
		registerModule("remote-request",CreateRequest.class);
		registerModule("remote-call",ExecuteRequest.class);
		registerModule("remote-call-direct",ExecuteRequestDirectly.class);
		
		registerModule("remote-by-form",ByForm.class);
		registerModule("remote-by-text",ByText.class);
		registerModule("remote-by-json",ByJson.class);
		registerModule("remote-set-content-type",SetContentType.class);
		registerModule("remote-set-header",SetHeader.class);
		
		registerModule("remote-discard",Discard.class);
		registerModule("remote-as-json",AsJson.class);
		registerModule("remote-as-text",AsText.class);
		registerModule("remote-get-content-type",GetContentType.class);
		registerModule("remote-get-header",GetHeader.class);
		registerModule("remote-get-code",GetStatusCode.class);
		registerModule("remote-get-reason",GetReason.class);
		
		registerModule("remote-encode",Encoder.class);
		registerModule("remote-decode",Decoder.class);
		registerModule("remote-copy",Copy.class);
		registerModule("remote-url-builder",UrlBuilder.class);
		registerModule("remote-md5",MD5.class);
	}

}
