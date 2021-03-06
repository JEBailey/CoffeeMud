package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.SpaceObject;
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
public class AreaNext extends StdWebMacro {
	public String name() {
		return "AreaNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("AREA");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("AREA");
			return "";
		}
		boolean all = parms.containsKey("SPACE") || parms.containsKey("ALL");
		String lastID = "";
		for (Enumeration a = CMLib.map().areas(); a.hasMoreElements();) {
			Area A = (Area) a.nextElement();
			if ((!(A instanceof SpaceObject)) || all) {
				if ((last == null)
						|| ((last.length() > 0) && (last.equals(lastID)) && (!A
								.Name().equals(lastID)))) {
					httpReq.addFakeUrlParameter("AREA", A.Name());
					if ((!CMLib.flags().isHidden(A))
							&& (!CMath
									.bset(A.flags(), Area.FLAG_INSTANCE_CHILD)))
						return "";
					last = A.Name();
				}
				lastID = A.Name();
			}
		}
		httpReq.addFakeUrlParameter("AREA", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
