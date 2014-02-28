package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.miniweb.interfaces.HTTPRequest;
import com.planet_ink.miniweb.util.MWThread;

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
public class ChannelNext extends StdWebMacro {
	public String name() {
		return "ChannelNext";
	}

	public String runMacro(HTTPRequest httpReq, String parm) {
		java.util.Map<String, String> parms = parseParms(parm);
		String last = httpReq.getUrlParameter("CHANNEL");
		if (parms.containsKey("RESET")) {
			if (last != null)
				httpReq.removeUrlParameter("CHANNEL");
			return "";
		}
		MOB mob = Authenticate.getAuthenticatedMob(httpReq);
		boolean allChannels = false;
		if ((Thread.currentThread() instanceof MWThread)
				&& CMath.s_bool(((MWThread) Thread.currentThread()).getConfig()
						.getMiscProp("ADMIN"))
				&& parms.containsKey("ALLCHANNELS"))
			allChannels = true;
		String lastID = "";
		for (int i = 0; i < CMLib.channels().getNumChannels(); i++) {
			String name = CMLib.channels().getChannel(i).name;
			if ((last == null)
					|| ((last.length() > 0) && (last.equals(lastID)) && (!name
							.equals(lastID)))) {
				if (allChannels
						|| ((mob != null) && (CMLib.channels()
								.mayReadThisChannel(mob, i, true)))) {
					httpReq.addFakeUrlParameter("CHANNEL", name);
					return "";
				}
				last = name;
			}
			lastID = name;
		}
		httpReq.addFakeUrlParameter("CHANNEL", "");
		if (parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}

}
