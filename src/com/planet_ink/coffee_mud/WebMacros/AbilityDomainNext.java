package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
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
public class AbilityDomainNext extends StdWebMacro {
	public String name() {
		return "AbilityDomainNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("DOMAIN");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("DOMAIN");
			return "";
		}
		String lastID = "";
		for (int i = 0; i < Ability.DOMAIN_DESCS.length; i++) {
			String S = Ability.DOMAIN_DESCS[i];
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!S
							.equals(lastID)))) {
				httpReq.addFakeUrlParameter("DOMAIN", S);
				return "";
			}
			lastID = S;
		}
		httpReq.addFakeUrlParameter("DOMAIN", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
