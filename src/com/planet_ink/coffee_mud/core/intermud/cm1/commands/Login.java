package com.planet_ink.coffee_mud.core.intermud.cm1.commands;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Login extends CM1Command {
	public String getCommandWord() {
		return "LOGIN";
	}

	public Login(RequestHandler req, String parameters) {
		super(req, parameters);
	}

	public void run() {
		try {
			int x = parameters.indexOf(' ');
			if (x < 0)
				req.sendMsg("[FAIL " + getHelp(req.getUser(), null, null) + "]");
			else {
				String user = parameters.substring(0, x);
				String pass = parameters.substring(x + 1);
				MOB M = CMLib.players().getLoadPlayer(user);
				if ((M == null) || (M.playerStats() == null)
						|| (!M.playerStats().matchesPassword(pass))) {
					Thread.sleep(5000);
					req.sendMsg("[FAIL]");
				} else {
					req.login(M);
					req.sendMsg("[OK]");
				}
			}
		} catch (Exception ioe) {
			Log.errOut(className, ioe);
			req.close();
		}
	}

	public boolean passesSecurityCheck(MOB user, PhysicalAgent target) {
		return true;
	}

	public String getHelp(MOB user, PhysicalAgent target, String rest) {
		return "USAGE: LOGIN <CHARACTER NAME> <PASSWORD>: Logs in a new character to act as the authorizing user.";
	}
}