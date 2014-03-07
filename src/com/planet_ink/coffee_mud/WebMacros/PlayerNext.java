package com.planet_ink.coffee_mud.WebMacros;

import java.util.Enumeration;

import com.planet_ink.coffee_mud.Libraries.interfaces.PlayerLibrary;
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
@SuppressWarnings("rawtypes")
public class PlayerNext extends StdWebMacro {
	public String name() {
		return "PlayerNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return CMProps.getVar(CMProps.Str.MUDSTATUS);

		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("PLAYER");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("PLAYER");
			return "";
		}
		String lastID = "";
		String sort = httpReq.getUrlParameter("SORTBY");
		if (sort == null)
			sort = "";
		Enumeration pe = CMLib.players().thinPlayers(sort,
				httpReq.getRequestObjects());
		for (; pe.hasMoreElements();) {
			PlayerLibrary.ThinPlayer user = (PlayerLibrary.ThinPlayer) pe
					.nextElement();
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!user.name
							.equals(lastID)))) {
				httpReq.addFakeUrlParameter("PLAYER", user.name);
				return "";
			}
			lastID = user.name;
		}
		httpReq.addFakeUrlParameter("PLAYER", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}