package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.MOBS.interfaces.Deity;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class AbilityCursesNext extends StdWebMacro {
	public String name() {
		return "AbilityCursesNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return " @break@";

		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("ABILITY");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("ABILITY");
			return "";
		}

		String lastID = "";
		String deityName = httpReq.getUrlParameter("DEITY");
		Deity D = null;
		if ((deityName != null) && (deityName.length() > 0))
			D = CMLib.map().getDeity(deityName);
		if (D == null) {
			if (parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		}
		for (int a = 0; a < D.numCurses(); a++) {
			Ability A = D.fetchCurse(a);
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!A
							.ID().equals(lastID)))) {
				httpReq.addFakeUrlParameter("ABILITY", A.ID());
				return "";
			}
			lastID = A.ID();
		}
		httpReq.addFakeUrlParameter("ABILITY", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
