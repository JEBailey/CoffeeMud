package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;

import com.planet_ink.coffee_mud.core.CMLib;
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
@SuppressWarnings("rawtypes")
public class AutoTitleNext extends StdWebMacro {
	public String name() {
		return "AutoTitleNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("AUTOTITLE");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("AUTOTITLE");
			return "";
		}
		String lastID = "";
		for (Enumeration r = CMLib.titles().autoTitles(); r.hasMoreElements();) {
			String title = (String) r.nextElement();
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!title
							.equals(lastID)))) {
				httpReq.addFakeUrlParameter("AUTOTITLE", title);
				return "";
			}
			lastID = title;
		}
		httpReq.addFakeUrlParameter("AUTOTITLE", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
