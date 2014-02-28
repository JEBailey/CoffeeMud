package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;
import java.util.TreeSet;
import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RaceCatNext extends StdWebMacro {
	public String name() {
		return "RaceCatNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("RACECAT");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("RACECAT");
			return "";
		}
		Vector raceCats = new Vector();
		for (Enumeration r = CMClass.races(); r.hasMoreElements();) {
			Race R = (Race) r.nextElement();
			if ((!raceCats.contains(R.racialCategory()))
					&& ((CMProps.isTheme(R.availabilityCode()) && (!CMath.bset(
							R.availabilityCode(), Area.THEME_SKILLONLYMASK))) || (parms
							.containsKey("ALL"))))
				raceCats.addElement(R.racialCategory());
		}
		raceCats = new Vector(new TreeSet(raceCats));
		String lastID = "";
		for (Enumeration r = raceCats.elements(); r.hasMoreElements();) {
			String RC = (String) r.nextElement();
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!RC
							.equals(lastID)))) {
				httpReq.addFakeUrlParameter("RACECAT", RC);
				return "";
			}
			lastID = RC;
		}
		httpReq.addFakeUrlParameter("RACECAT", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
