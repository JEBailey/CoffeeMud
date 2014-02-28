package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.Libraries.interfaces.ExpertiseLibrary;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.miniweb.interfaces.HTTPRequest;

/* 
 Copyright 2006-2014 Bo Zimmerman

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
public class ExpertiseID extends StdWebMacro {
	public String name() {
		return "ExpertiseID";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		String last = httpReq.getUrlParameter("EXPERTISE");
		if (last == null)
			return " @break@";
		if (last.length() > 0) {
			ExpertiseLibrary.ExpertiseDefinition E = CMLib.expertises()
					.getDefinition(last);
			if (E != null)
				return E.ID;
		}
		return "";
	}
}
