package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMFile;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMStrings;
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
public class AutoTitleData extends StdWebMacro {
	public String name() {
		return "AutoTitleData";
	}

	public String deleteTitle(String title) {
		CMLib.titles().dispossesTitle(title);
		CMFile F = new CMFile(Resources.makeFileResourceName("titles.txt"),
				null, CMFile.FLAG_LOGERRORS);
		if (F.exists()) {
			boolean removed = Resources.findRemoveProperty(F, title);
			if (removed) {
				Resources.removeResource("titles.txt");
				CMLib.titles().reloadAutoTitles();
				return null;
			}
			return "Unable to delete title!";
		}
		return "Unable to open titles.txt!";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("AUTOTITLE");
		if ((last == null) && (!parms.containsKey("EDIT")))
			return " @break@";

		if (parms.containsKey("EDIT")) {
			MOB M = Authenticate.getAuthenticatedMob(httpReq);
			if (M == null)
				return "[authentication error]";
			if (!CMSecurity.isAllowed(M, M.location(),
					CMSecurity.SecFlag.TITLES))
				return "[authentication error]";
			String req = httpReq.getUrlParameter("ISREQUIRED");
			String newTitle = httpReq.getUrlParameter("TITLE");
			if ((req != null) && (req.equalsIgnoreCase("on")))
				newTitle = "{" + newTitle + "}";
			String newMask = httpReq.getUrlParameter("MASK");
			if ((newTitle == null) || (newMask == null)
					|| (newTitle.length() == 0))
				return "[missing data error]";

			if ((last != null)
					&& ((last.length() == 0) && (CMLib.titles()
							.isExistingAutoTitle(newTitle)))) {
				CMLib.titles().reloadAutoTitles();
				return "[new title already exists!]";
			}

			String error = CMLib.titles().evaluateAutoTitle(
					newTitle + "=" + newMask, false);
			if (error != null)
				return "[error: " + error + "]";

			if ((last != null) && (CMLib.titles().isExistingAutoTitle(last))) {
				String err = deleteTitle(last);
				if (err != null) {
					CMLib.titles().reloadAutoTitles();
					return err;
				}
			}
			CMFile F = new CMFile(Resources.makeFileResourceName("titles.txt"),
					null, CMFile.FLAG_LOGERRORS);
			F.saveText("\n" + newTitle + "=" + newMask, true);
			Resources.removeResource("titles.txt");
			CMLib.titles().reloadAutoTitles();
		} else if (parms.containsKey("DELETE")) {
			MOB M = Authenticate.getAuthenticatedMob(httpReq);
			if (M == null)
				return "[authentication error]";
			if (!CMSecurity.isAllowed(M, M.location(),
					CMSecurity.SecFlag.TITLES))
				return "[authentication error]";
			if (last == null)
				return " @break@";
			if (!CMLib.titles().isExistingAutoTitle(last))
				return "Unknown title!";
			String err = deleteTitle(last);
			if (err == null)
				return "Auto-Title deleted.";
			return err;
		} else if (last == null)
			return " @break@";
		StringBuffer str = new StringBuffer("");

		if (parms.containsKey("MASK")) {
			String mask = httpReq.getUrlParameter("MASK");
			if ((mask == null) && (last != null) && (last.length() > 0))
				mask = CMLib.titles().getAutoTitleMask(last);
			if (mask != null)
				str.append(CMStrings.replaceAll(mask, "\"", "&quot;") + ", ");
		}
		if (parms.containsKey("TITLE")) {
			String title = httpReq.getUrlParameter("TITLE");
			if (title == null)
				title = last;
			if (title != null) {
				if (title.startsWith("{") && title.endsWith("}"))
					title = title.substring(1, title.length() - 1);
				str.append(title + ", ");
			}
		}
		if (parms.containsKey("ISREQUIRED")) {
			String req = httpReq.getUrlParameter("ISREQUIRED");
			if ((req == null) && (last != null))
				req = (last.startsWith("{") && last.endsWith("}")) ? "on" : "";
			if (req != null)
				str.append((req.equalsIgnoreCase("on") ? "CHECKED" : "") + ", ");
		}
		if (parms.containsKey("MASKDESC")) {
			String mask = httpReq.getUrlParameter("MASK");
			if ((mask == null) && (last != null) && (last.length() > 0))
				mask = CMLib.titles().getAutoTitleMask(last);
			if (mask != null)
				str.append(CMLib.masking().maskDesc(mask) + ", ");
		}
		String strstr = str.toString();
		if (strstr.endsWith(", "))
			strstr = strstr.substring(0, strstr.length() - 2);
		return clearWebMacros(strstr);
	}
}
