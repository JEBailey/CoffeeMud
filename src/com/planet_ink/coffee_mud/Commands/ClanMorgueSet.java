package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.Clan.Authority;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.collections.Pair;

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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ClanMorgueSet extends StdCommand {
	public ClanMorgueSet() {
	}

	private final String[] access = { "CLANMORGUESET" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		String clanName = (commands.size() > 1) ? CMParms.combine(commands, 1,
				commands.size()) : "";

		Clan C = null;
		boolean skipChecks = mob.getClanRole(mob.Name()) != null;
		if (skipChecks)
			C = mob.getClanRole(mob.Name()).first;

		if (C == null)
			for (Pair<Clan, Integer> c : mob.clans())
				if ((clanName.length() == 0)
						|| (CMLib.english().containsString(c.first.getName(),
								clanName))
						&& (c.first.getAuthority(c.second.intValue(),
								Clan.Function.MORGUE) != Authority.CAN_NOT_DO)) {
					C = c.first;
					break;
				}

		Room R = mob.location();
		if (skipChecks) {
			commands.setElementAt(getAccessWords()[0], 0);
			R = CMLib.map().getRoom(CMParms.combine(commands, 1));
		} else {
			commands.clear();
			commands.addElement(getAccessWords()[0]);
			commands.addElement(CMLib.map().getExtendedRoomID(R));
		}

		if ((C == null) || (R == null)) {
			mob.tell("You aren't allowed to set a morgue room for "
					+ ((clanName.length() == 0) ? "anything" : clanName) + ".");
			return false;
		}

		if (C.getStatus() > Clan.CLANSTATUS_ACTIVE) {
			mob.tell("You cannot set a morgue.  Your " + C.getGovernmentName()
					+ " does not have enough members to be considered active.");
			return false;
		}
		if (skipChecks
				|| CMLib.clans().goForward(mob, C, commands,
						Clan.Function.SET_HOME, false)) {
			if (!CMLib.law().doesOwnThisProperty(C.clanID(), R)) {
				mob.tell("Your " + C.getGovernmentName()
						+ " does not own this room.");
				return false;
			}
			if (skipChecks
					|| CMLib.clans().goForward(mob, C, commands,
							Clan.Function.SET_HOME, true)) {
				C.setMorgue(CMLib.map().getExtendedRoomID(R));
				C.update();
				mob.tell("Your " + C.getGovernmentName()
						+ " morgue is now set to " + R.displayText(mob) + ".");
				CMLib.clans().clanAnnounce(
						mob,
						"The morgue of " + C.getGovernmentName() + " "
								+ C.clanID() + " is now set to "
								+ R.displayText(mob) + ".");
				return true;
			}
		} else {
			mob.tell("You aren't in the right position to set your "
					+ C.getGovernmentName() + "'s morgue.");
			return false;
		}
		return false;
	}

	public boolean canBeOrdered() {
		return false;
	}

}
