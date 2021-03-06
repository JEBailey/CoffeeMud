package com.planet_ink.coffee_mud.Commands;

import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.Clan;
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
@SuppressWarnings("rawtypes")
public class ClanDetails extends StdCommand {
	public ClanDetails() {
	}

	private final String[] access = { "CLANDETAILS", "CLAN" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		String clanName = (commands.size() > 1) ? CMParms.combine(commands, 1,
				commands.size()) : "";
		if ((clanName.length() == 0) && (mob.clans().iterator().hasNext()))
			clanName = mob.clans().iterator().next().first.clanID();
		StringBuffer msg = new StringBuffer("");
		if (clanName.length() > 0) {
			Clan foundClan = null;
			for (Enumeration e = CMLib.clans().clans(); e.hasMoreElements();) {
				Clan C = (Clan) e.nextElement();
				if (CMLib.english().containsString(C.getName(), clanName)) {
					msg.append(C.getDetail(mob));
					foundClan = C;
					break;
				}
			}
			if (foundClan == null)
				msg.append("No clan was found by the name of '" + clanName
						+ "'.\n\r");
			else {
				Pair<Clan, Integer> p = mob.getClanRole(foundClan.clanID());
				if ((p != null)
						&& (mob.clans().iterator().next().first != p.first)) {
					mob.setClan(foundClan.clanID(), mob.getClanRole(foundClan
							.clanID()).second.intValue());
					msg.append("\n\rYour default clan is now "
							+ p.first.getName() + ".");
				}
			}
		} else {
			msg.append("You need to specify which clan you would like details on. Try 'CLANLIST'.\n\r");
		}
		mob.tell(msg.toString());
		return false;
	}

	public boolean canBeOrdered() {
		return false;
	}

}
