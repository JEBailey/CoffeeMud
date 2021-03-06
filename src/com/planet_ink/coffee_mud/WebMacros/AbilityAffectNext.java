package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class AbilityAffectNext extends StdWebMacro {
	public String name() {
		return "AbilityAffectNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("ABILITY");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("ABILITY");
			return "";
		}
		String lastID = "";
		String ableType = httpReq.getUrlParameter("ABILITYTYPE");
		if ((ableType != null) && (ableType.length() > 0))
			parms.put(ableType, ableType);
		for (Enumeration<Ability> a = CMClass.abilities(); a.hasMoreElements();) {
			Ability A = a.nextElement();
			boolean okToShow = true;
			int classType = A.classificationCode() & Ability.ALL_ACODES;
			if (CMLib.ableMapper().getQualifyingLevel("Archon", true, A.ID()) >= 0)
				continue;
			boolean containsOne = false;
			for (int i = 0; i < Ability.ACODE_DESCS.length; i++)
				if (parms.containsKey(Ability.ACODE_DESCS[i])) {
					containsOne = true;
					break;
				}
			if (containsOne
					&& (!parms.containsKey(Ability.ACODE_DESCS[classType])))
				okToShow = false;
			if (parms.containsKey("NOT"))
				okToShow = !okToShow;
			if (okToShow) {
				if ((last == null)
						|| ((last.length() > 0) && (last.equals(lastID)) && (!A
								.ID().equals(lastID)))) {
					httpReq.addFakeUrlParameter("ABILITY", A.ID());
					return "";
				}
				lastID = A.ID();
			}
		}
		httpReq.addFakeUrlParameter("ABILITY", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
