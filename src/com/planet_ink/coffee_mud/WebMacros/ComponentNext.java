package com.planet_ink.coffee_mud.WebMacros;

import java.util.Iterator;

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
public class ComponentNext extends StdWebMacro {
	public String name() {
		return "ComponentNext";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("COMPONENT");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("COMPONENT");
			return "";
		}
		String lastID = "";
		String componentID;
		for (Iterator<String> i = CMLib.ableMapper().getAbilityComponentMap()
				.keySet().iterator(); i.hasNext();) {
			componentID = i.next();
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!componentID
							.equalsIgnoreCase(lastID)))) {
				httpReq.addFakeUrlParameter("COMPONENT", componentID);
				return "";
			}
			lastID = componentID;
		}
		httpReq.addFakeUrlParameter("COMPONENT", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
