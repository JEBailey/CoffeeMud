package com.planet_ink.coffee_mud.core.intermud.cm1.commands;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.interfaces.PhysicalAgent;
import com.planet_ink.coffee_mud.core.intermud.cm1.RequestHandler;

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
public class MUDInfo extends CM1Command {
	public String getCommandWord() {
		return "MUDINFO";
	}

	public MUDInfo(RequestHandler req, String parameters) {
		super(req, parameters);
	}

	public void run() {
		try {
			if ((parameters.length() == 0)
					|| (parameters.equalsIgnoreCase("STATUS")))
				req.sendMsg("[OK " + CMProps.getVar(CMProps.Str.MUDSTATUS)
						+ "]");
			else if (parameters.equalsIgnoreCase("PORTS"))
				req.sendMsg("[OK " + CMProps.getVar(CMProps.Str.MUDPORTS) + "]");
			else if (parameters.equalsIgnoreCase("VERSION"))
				req.sendMsg("[OK " + CMProps.getVar(CMProps.Str.MUDVER) + "]");
			else if (parameters.equalsIgnoreCase("DOMAIN"))
				req.sendMsg("[OK " + CMProps.getVar(CMProps.Str.MUDDOMAIN)
						+ "]");
			else if (parameters.equalsIgnoreCase("NAME"))
				req.sendMsg("[OK " + CMProps.getVar(CMProps.Str.MUDNAME) + "]");
			else
				req.sendMsg("[FAIL " + getHelp(req.getUser(), null, null) + "]");
		} catch (java.io.IOException ioe) {
			Log.errOut(className, ioe);
			req.close();
		}
	}

	public boolean passesSecurityCheck(MOB user, PhysicalAgent target) {
		return true;
	}

	public String getHelp(MOB user, PhysicalAgent target, String rest) {
		return "USAGE: MUDINFO STATUS, PORTS, VERSION, DOMAIN, NAME";
	}
}