package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
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
public class WizEmote extends StdCommand {
	public WizEmote() {
	}

	private final String[] access = { "WIZEMOTE" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() > 2) {
			String who = (String) commands.elementAt(1);
			String msg = CMParms.combineWithQuotes(commands, 2);
			Room R = CMLib.map().getRoom(who);
			if (who.toUpperCase().equals("HERE"))
				R = mob.location();
			Area A = CMLib.map().findAreaStartsWith(who);
			Clan C = CMLib.clans().findClan(who);
			if (who.toUpperCase().equals("ALL")) {
				for (Session S : CMLib.sessions().localOnlineIterable()) {
					if ((S.mob() != null)
							&& (S.mob().location() != null)
							&& (CMSecurity.isAllowed(mob, S.mob().location(),
									CMSecurity.SecFlag.WIZEMOTE)))
						S.stdPrintln("^w" + msg + "^?");
				}
			} else if (R != null) {
				for (Session S : CMLib.sessions().localOnlineIterable()) {
					if ((S.mob() != null)
							&& (S.mob().location() == R)
							&& (CMSecurity.isAllowed(mob, R,
									CMSecurity.SecFlag.WIZEMOTE)))
						S.stdPrintln("^w" + msg + "^?");
				}
			} else if (A != null) {
				for (Session S : CMLib.sessions().localOnlineIterable()) {
					if ((S.mob() != null)
							&& (S.mob().location() != null)
							&& (A.inMyMetroArea(S.mob().location().getArea()))
							&& (CMSecurity.isAllowed(mob, S.mob().location(),
									CMSecurity.SecFlag.WIZEMOTE)))
						S.stdPrintln("^w" + msg + "^?");
				}
			} else if (C != null) {
				for (Session S : CMLib.sessions().localOnlineIterable()) {
					if ((S.mob() != null)
							&& (S.mob().getClanRole(C.clanID()) != null)
							&& (CMSecurity.isAllowed(mob, S.mob().location(),
									CMSecurity.SecFlag.WIZEMOTE)))
						S.stdPrintln("^w" + msg + "^?");
				}
			} else {
				boolean found = false;
				for (Session S : CMLib.sessions().localOnlineIterable()) {
					if ((S.mob() != null)
							&& (S.mob().location() != null)
							&& (CMSecurity.isAllowed(mob, S.mob().location(),
									CMSecurity.SecFlag.WIZEMOTE))
							&& (CMLib.english().containsString(S.mob().name(),
									who) || CMLib.english().containsString(
									S.mob().location().getArea().name(), who))) {
						S.stdPrintln("^w" + msg + "^?");
						found = true;
						break;
					}
				}
				if (!found)
					mob.tell("You can't find anyone or anywhere by that name.");
			}
		} else
			mob.tell("You must specify either all, or an area/mob name, and an message.");
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

	public boolean securityCheck(MOB mob) {
		return CMSecurity.isAllowed(mob, mob.location(),
				CMSecurity.SecFlag.WIZEMOTE);
	}

}