package com.planet_ink.coffee_mud.WebMacros;

import java.util.List;

import com.planet_ink.coffee_mud.core.CMSecurity;
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
public class BanListMgr extends StdWebMacro {
	public String name() {
		return "BanListMgr";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("BANNEDONE");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("BANNEDONE");
			return "";
		} else if (parms.containsKey("NEXT")) {
			String lastID = "";
			List<String> banned = Resources.getFileLineVector(Resources
					.getFileResource("banned.ini", false));
			for (int i = 0; i < banned.size(); i++) {
				String key = banned.get(i);
				if ((last == null)
						|| ((last.length() > 0) && (last.equals(lastID)) && (!key
								.equals(lastID)))) {
					httpReq.addFakeUrlParameter("BANNEDONE", key);
					return "";
				}
				lastID = key;
			}
			httpReq.addFakeUrlParameter("BANNEDONE", "");
			if (parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		} else if (parms.containsKey("DELETE")) {
			String key = httpReq.getUrlParameter("BANNEDONE");
			if (key == null)
				return "";
			CMSecurity.unban(key);
			return "'" + key + "' no longer banned.";
		} else if (parms.containsKey("ADD")) {
			String key = httpReq.getUrlParameter("NEWBANNEDONE");
			if (key == null)
				return "";
			CMSecurity.ban(key);
			return "'" + key + "' is now banned.";
		} else if (last != null)
			return last;
		return "<!--EMPTY-->";
	}

}
