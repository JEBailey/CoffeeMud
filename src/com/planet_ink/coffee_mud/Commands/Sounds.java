package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMath;

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
public class Sounds extends StdCommand {
	public Sounds() {
	}

	private final String[] access = { "SOUNDS", "MSP" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (!mob.isMonster()) {
			boolean force = false;
			if (commands != null)
				for (Object o : commands)
					if (o.toString().equalsIgnoreCase("force"))
						force = true;
			Session session = mob.session();
			if ((!CMath.bset(mob.getBitmap(), MOB.ATT_SOUND))
					|| (!session.getClientTelnetMode(Session.TELNET_MSP))) {
				session.changeTelnetMode(Session.TELNET_MSP, true);
				for (int i = 0; ((i < 5) && (!session
						.getClientTelnetMode(Session.TELNET_MSP))); i++) {
					try {
						mob.session().prompt("", 500);
					} catch (Exception e) {
					}
				}
				if (session.getClientTelnetMode(Session.TELNET_MSP)) {
					mob.setBitmap(CMath.setb(mob.getBitmap(), MOB.ATT_SOUND));
					mob.tell("MSP Sound/Music enabled.\n\r");
				} else if (force) {
					session.setClientTelnetMode(Session.TELNET_MSP, true);
					session.setServerTelnetMode(Session.TELNET_MSP, true);
					mob.setBitmap(CMath.setb(mob.getBitmap(), MOB.ATT_SOUND));
					mob.tell("MSP Sound/Music has been forceably enabled.\n\r");
				} else
					mob.tell("Your client does not appear to support MSP.");
			} else {
				mob.tell("MSP Sound/Music is already enabled.\n\r");
			}
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

	public boolean securityCheck(MOB mob) {
		return super.securityCheck(mob)
				&& (!CMSecurity.isDisabled(CMSecurity.DisFlag.MSP));
	}
}
