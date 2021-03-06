package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.PlayerAccount;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
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
public class NoANSI extends StdCommand {
	public NoANSI() {
	}

	private final String[] access = { "NOANSI", "NOCOLOR", "NOCOLOUR" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (!mob.isMonster()) {
			PlayerAccount acct = null;
			if (mob.playerStats() != null)
				acct = mob.playerStats().getAccount();
			if (acct != null)
				acct.setFlag(PlayerAccount.FLAG_ANSI, false);
			if (CMath.bset(mob.getBitmap(), MOB.ATT_ANSI)) {
				mob.setBitmap(CMath.unsetb(mob.getBitmap(), MOB.ATT_ANSI));
				mob.tell("ANSI colour disabled.\n\r");
			} else {
				mob.tell("ANSI is already disabled.\n\r");
			}
			mob.session().setClientTelnetMode(Session.TELNET_ANSI, false);
			mob.session().setServerTelnetMode(Session.TELNET_ANSI, false);
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

}
