package com.planet_ink.coffee_mud.WebMacros.grinder;

import java.util.Map;

import com.planet_ink.coffee_mud.Libraries.interfaces.AbilityMapper;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
public class GrinderAllQualifys {
	public String name() {
		return "GrinderAllQualifys";
	}

	public String editAllQualify(HTTPRequest httpReq,
			java.util.Map<String, String> parms) {
		String last = httpReq.getUrlParameter("ALLQUALID");
		if ((last == null) || (last.length() == 0))
			return " @break@";
		String which = httpReq.getUrlParameter("ALLQUALWHICH");
		if (parms.containsKey("WHICH"))
			which = parms.get("WHICH");
		if ((which == null) || (which.length() == 0))
			return " @break@";
		Map<String, Map<String, AbilityMapper.AbilityMapping>> allQualMap = CMLib
				.ableMapper().getAllQualifiesMap(httpReq.getRequestObjects());
		Map<String, AbilityMapper.AbilityMapping> map = allQualMap.get(which
				.toUpperCase().trim());
		if (map == null)
			return " @break@";

		AbilityMapper.AbilityMapping newMap = map
				.get(last.toUpperCase().trim());
		if (newMap == null) {
			newMap = new AbilityMapper.AbilityMapping(last.toUpperCase().trim());
			newMap.abilityID = last;
			newMap.allQualifyFlag = true;
		}
		String s;
		s = httpReq.getUrlParameter("LEVEL");
		if (s != null)
			newMap.qualLevel = CMath.s_int(s);
		s = httpReq.getUrlParameter("PROF");
		if (s != null)
			newMap.defaultProficiency = CMath.s_int(s);
		s = httpReq.getUrlParameter("MASK");
		if (s != null)
			newMap.extraMask = s;
		s = httpReq.getUrlParameter("AUTOGAIN");
		if (s != null)
			newMap.autoGain = s.equalsIgnoreCase("on");
		StringBuilder preReqs = new StringBuilder("");
		int curChkNum = 1;
		while (httpReq.isUrlParameter("REQABLE" + curChkNum)) {
			String curVal = httpReq.getUrlParameter("REQABLE" + curChkNum);
			if (curVal.equals("DEL") || curVal.equals("DELETE")
					|| curVal.trim().length() == 0) {
				// do nothing
			} else {
				String curLvl = httpReq.getUrlParameter("REQLEVEL" + curChkNum);
				preReqs.append(curVal);
				if ((curLvl != null) && (curLvl.trim().length() > 0)
						&& (CMath.s_int(curLvl.trim()) > 0))
					preReqs.append("(").append(curLvl).append(")");
				preReqs.append(" ");
			}
			curChkNum++;
		}
		newMap = CMLib.ableMapper().makeAbilityMapping(newMap.abilityID,
				newMap.qualLevel, newMap.abilityID, newMap.defaultProficiency,
				100, "", newMap.autoGain, false, true,
				CMParms.parseSpaces(preReqs.toString().trim(), true),
				newMap.extraMask, null);
		map.put(last.toUpperCase().trim(), newMap);
		CMLib.ableMapper().saveAllQualifysFile(allQualMap);
		return "";
	}
}
