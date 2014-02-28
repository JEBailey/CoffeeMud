package com.planet_ink.coffee_mud.WebMacros;

import java.util.Iterator;

import com.planet_ink.coffee_mud.Common.interfaces.Poll;
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
public class PollNext extends StdWebMacro {
	public String name() {
		return "PollNext";
	}

	public boolean isAdminMacro() {
		return true;
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("POLL");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("POLL");
			return "";
		}
		String lastID = "";
		for (Iterator<Poll> q = CMLib.polls().getPollList(); q.hasNext();) {
			Poll poll = q.next();
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!poll
							.getName().equalsIgnoreCase(lastID)))) {
				httpReq.addFakeUrlParameter("POLL", poll.getName());
				return "";
			}
			lastID = poll.getName();
		}
		httpReq.addFakeUrlParameter("POLL", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
