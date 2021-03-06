package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
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
public class AbilityRaceNext extends StdWebMacro {
	public String name() {
		return "AbilityRaceNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return CMProps.getVar(CMProps.Str.MUDSTATUS);

		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("ABILITY");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("ABILITY");
			return "";
		}
		String ableType = httpReq.getUrlParameter("ABILITYTYPE");
		if ((ableType != null) && (ableType.length() > 0))
			parms.put(ableType, ableType);
		String domainType = httpReq.getUrlParameter("DOMAIN");
		if ((domainType != null) && (domainType.length() > 0))
			parms.put("DOMAIN", domainType);

		String lastID = "";
		String raceID = httpReq.getUrlParameter("RACE");
		Race R = null;
		if ((raceID != null) && (raceID.length() > 0))
			R = CMClass.getRace(raceID);
		if (R == null) {
			if (parms.containsKey("EMPTYOK"))
				return "<!--EMPTY-->";
			return " @break@";
		}

		for (Ability A : R.racialAbilities(null)) {
			boolean okToShow = true;
			int level = CMLib.ableMapper().getQualifyingLevel(R.ID(), false,
					A.ID());
			if (level < 0)
				okToShow = false;
			else {
				String levelName = httpReq.getUrlParameter("LEVEL");
				if ((levelName != null) && (levelName.length() > 0)
						&& (CMath.s_int(levelName) != level))
					okToShow = false;
			}
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
