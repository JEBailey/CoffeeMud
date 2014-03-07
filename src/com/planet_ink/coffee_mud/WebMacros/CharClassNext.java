package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;

import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class CharClassNext extends StdWebMacro {
	public String name() {
		return "CharClassNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("CLASS");
		String base = httpReq.getUrlParameter("BASECLASS");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("CLASS");
			return "";
		}
		String lastID = "";
		for (Enumeration c = CMClass.charClasses(); c.hasMoreElements();) {
			CharClass C = (CharClass) c.nextElement();
			if (((CMProps.isTheme(C.availabilityCode())) || (parms
					.containsKey("ALL")))
					&& ((base == null) || (base.length() == 0) || (C
							.baseClass().equalsIgnoreCase(base)))) {
				if ((last == null)
						|| ((last.length() > 0) && (last.equals(lastID)) && (!C
								.ID().equals(lastID)))) {
					httpReq.addFakeUrlParameter("CLASS", C.ID());
					return "";
				}
				lastID = C.ID();
			}
		}
		httpReq.addFakeUrlParameter("CLASS", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}