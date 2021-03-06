package com.planet_ink.coffee_mud.WebMacros;

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
public class LevelNext extends StdWebMacro {
	public String name() {
		return "LevelNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("LEVEL");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("LEVEL");
			return "";
		}
		int lastLevel = CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL);
		for (String key : parms.keySet()) {
			if (CMath.isInteger(key))
				lastLevel = CMath.s_int(key);
		}
		if ((last == null) || (last.length() > 0)) {
			int level = 0;
			if (last != null)
				level = CMath.s_int(last);
			level++;
			if (level <= lastLevel) {
				httpReq.addFakeUrlParameter("LEVEL", "" + level);
				return "";
			}
		}
		httpReq.addFakeUrlParameter("LEVEL", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
