package com.planet_ink.coffee_mud.WebMacros;

import java.net.URLEncoder;

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
public class PollID extends StdWebMacro {
	public String name() {
		return "PollID";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		String last = httpReq.getUrlParameter("POLL");
		if (last == null)
			return " @break@";
		java.util.Map<String, String> parms = parseParms(parm);
		try {
			if (parms.containsKey("ENCODED"))
				return URLEncoder.encode(last, "UTF-8");
		} catch (Exception e) {
		}
		return clearWebMacros(last);
	}
}
