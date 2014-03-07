package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;

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
public class Ban extends StdCommand {
	public Ban() {
	}

	private final String[] access = { "BAN" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		commands.removeElementAt(0);
		String banMe = CMParms.combine(commands, 0);
		if (banMe.length() == 0) {
			mob.tell("Ban what?  Enter an IP address or name mask.");
			return false;
		}
		banMe = banMe.toUpperCase().trim();
		int b = CMSecurity.ban(banMe);
		if (b < 0)
			mob.tell("Logins and IPs matching " + banMe + " are now banned.");
		else {
			mob.tell("That is already banned.  Do LIST BANNED and check out #"
					+ (b + 1) + ".");
			return false;
		}
		return true;
	}

	public boolean canBeOrdered() {
		return true;
	}

	public boolean securityCheck(MOB mob) {
		return CMSecurity
				.isAllowed(mob, mob.location(), CMSecurity.SecFlag.BAN);
	}

}