package com.alogic.blob.resource;

import org.w3c.dom.Element;

import com.anysoft.util.Properties;

/**
 * 画像图标集
 * 
 * @author duanyy
 *
 */
public class AvatarIconSet extends ResourceBlobManager {
	
	@Override
	public void configure(Element _e, Properties _properties){
		_e.setAttribute("home", "/com/alogic/blob/icon/avatar");
		_e.setAttribute("bootstrap", getClass().getName());
		
		super.configure(_e, _properties);	
		
		resourceFound(getHome(),"1442218377655wryvgK");
		resourceFound(getHome(),"1442218377657BPsE7V");
		resourceFound(getHome(),"1442218377658cDdcFM");
		resourceFound(getHome(),"1442218377658ghi1J7");
		resourceFound(getHome(),"1442218377659Xapj6L");
		resourceFound(getHome(),"1442218377660EUHcV4");
		resourceFound(getHome(),"1442218377660rRKrU4");
		resourceFound(getHome(),"1442218377661nV1B9S");
		resourceFound(getHome(),"144221837766274KBOH");
		resourceFound(getHome(),"1442218377662lnbI4M");
		resourceFound(getHome(),"1442218377663fhVL3u");
		resourceFound(getHome(),"1442218377664ckCKOk");
		resourceFound(getHome(),"1442218377664hkf4bs");
		resourceFound(getHome(),"14422183776656hPbbv");
		resourceFound(getHome(),"1442218377666MHEcmt");
		resourceFound(getHome(),"1442218377666tM0CkU");
		resourceFound(getHome(),"1442218377667moGfCd");
		resourceFound(getHome(),"1442218377668ISYWZ1");
		resourceFound(getHome(),"1442218377668R0rf79");
		resourceFound(getHome(),"14422183776696CdqbH");
		resourceFound(getHome(),"14422183776700GO7d5");
		resourceFound(getHome(),"1442218377670LWYHO2");
		resourceFound(getHome(),"1442218377671eEYs1E");
		resourceFound(getHome(),"1442218377671jGfZAy");
	}
}
