package com.planet_ink.coffee_mud.core.intermud.cm1.commands;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMSecurity;
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
public class Shutdown extends CM1Command {
	public String getCommandWord() {
		return "SHUTDOWN";
	}

	public Shutdown(RequestHandler req, String parameters) {
		super(req, parameters);
	}

	public void run() {
		try {
			req.sendMsg("[OK]");
			com.planet_ink.coffee_mud.application.MUD.globalShutdown(null,
					true, null);
			req.close();
		} catch (java.io.IOException ioe) {
			Log.errOut(className, ioe);
			req.close();
		}
	}

	public boolean passesSecurityCheck(MOB user, PhysicalAgent target) {
		return (user != null)
				&& CMSecurity.isAllowed(user, user.location(),
						CMSecurity.SecFlag.SHUTDOWN);
	}

	public String getHelp(MOB user, PhysicalAgent target, String rest) {
		return "USAGE: SHUTDOWN: Shuts down the mud.";
	}
}
