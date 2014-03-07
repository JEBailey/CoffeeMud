package com.planet_ink.coffee_mud.WebMacros;

import java.util.Iterator;

import com.planet_ink.coffee_mud.core.Resources;
import com.planet_ink.miniweb.interfaces.HTTPRequest;

/* 
 Copyright 2000-2014 Bo Zimmerman

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
public class ResourceMgr extends StdWebMacro {
	public String name() {
		return "ResourceMgr";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("RESOURCE");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("RESOURCE");
			return "";
		} else if (parms.containsKey("NEXT")) {
			String lastID = "";
			for (Iterator<String> k = Resources.findResourceKeys(""); k
					.hasNext();) {
				String key = k.next();
				if ((last == null)
						|| ((last.length() > 0) && (last.equals(lastID)) && (!key
								.equals(lastID)))) {
					httpReq.addFakeUrlParameter("RESOURCE", key);
					return "";
				}
				lastID = key;
			}
			httpReq.addFakeUrlParameter("RESOURCE", "");
			if (parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		} else if (parms.containsKey("DELETE")) {
			String key = httpReq.getUrlParameter("RESOURCE");
			if ((key != null) && (Resources.getResource(key) != null)) {
				Resources.removeResource(key);
				return "Resource '" + key + "' deleted.";
			}
			return "<!--EMPTY-->";
		} else if (last != null)
			return last;
		return "<!--EMPTY-->";
	}

}