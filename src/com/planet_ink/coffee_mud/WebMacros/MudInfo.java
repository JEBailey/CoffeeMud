package com.planet_ink.coffee_mud.WebMacros;

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
public class MudInfo extends StdWebMacro {
	public String name() {
		return "MudInfo";
	}

	public boolean isAdminMacro() {
		return false;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		if (parms.containsKey("DOMAIN"))
			return CMProps.getVar(CMProps.Str.MUDDOMAIN);
		if (parms.containsKey("EMAILOK"))
			return "" + (CMProps.getVar(CMProps.Str.MAILBOX).length() > 0);
		if (parms.containsKey("MAILBOX"))
			return CMProps.getVar(CMProps.Str.MAILBOX);
		if (parms.containsKey("NAME"))
			return CMProps.getVar(CMProps.Str.MUDNAME);
		if (parms.containsKey("CHARSET"))
			return CMProps.getVar(CMProps.Str.CHARSETOUTPUT);
		if (parms.containsKey("PORT")) {
			String ports = CMProps.getVar(CMProps.Str.MUDPORTS);
			if (ports == null)
				return "Booting";
			ports = ports.trim();
			int x = ports.indexOf(' ');
			if (x < 0)
				return clearWebMacros(ports);
			return clearWebMacros(ports.substring(0, x));
		}
		return "";
	}
}
