package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Boot extends StdCommand {
	public Boot() {
	}

	private final String[] access = { "BOOT" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		commands.removeElementAt(0);
		if (mob.session() == null)
			return false;
		if (commands.size() == 0) {
			mob.tell("Boot out who?");
			return false;
		}
		String whom = CMParms.combine(commands, 0);
		boolean boot = false;
		for (Session S : CMLib.sessions().allIterable()) {
			if (((S.mob() != null) && (CMLib.english().containsString(S.mob()
					.name(), whom)))
					|| (S.getAddress().equalsIgnoreCase(whom))) {
				if (S == mob.session()) {
					mob.tell("Try QUIT.");
					return false;
				}
				if (S.mob() != null) {
					mob.tell("You boot " + S.mob().name());
					if (S.mob().location() != null)
						S.mob()
								.location()
								.show(S.mob(), null, CMMsg.MSG_OK_VISUAL,
										"Something is happening to <S-NAME>.");
				} else
					mob.tell("You boot " + S.getAddress());
				S.stopSession(false, false, false);
				if (((S.getPreviousCMD() == null) || (S.getPreviousCMD().size() == 0))
						&& (!CMLib.flags().isInTheGame(S.mob(), true)))
					CMLib.sessions().stopSessionAtAllCosts(S);
				boot = true;
				break;
			}
		}
		if (!boot)
			mob.tell("You can't find anyone by that name or ip address.");
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

	public boolean securityCheck(MOB mob) {
		return CMSecurity.isAllowed(mob, mob.location(),
				CMSecurity.SecFlag.BOOT);
	}

}
