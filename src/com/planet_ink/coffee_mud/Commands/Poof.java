package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class Poof extends StdCommand {
	public Poof() {
	}

	private final String[] access = { "POOF" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean errorOut(MOB mob) {
		mob.tell("You are not allowed to do that here.");
		return false;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		int showFlag = -1;
		if (CMProps.getIntVar(CMProps.Int.EDITORTYPE) > 0)
			showFlag = -999;
		boolean ok = false;
		while ((!ok) && (mob.playerStats() != null)) {
			int showNumber = 0;
			String poofIn = CMLib.genEd().prompt(mob,
					mob.playerStats().poofIn(), ++showNumber, showFlag,
					"Poof-in", true, true);
			String poofOut = CMLib.genEd().prompt(mob,
					mob.playerStats().poofOut(), ++showNumber, showFlag,
					"Poof-out", true, true);
			String tranPoofIn = CMLib.genEd().prompt(mob,
					mob.playerStats().tranPoofIn(), ++showNumber, showFlag,
					"Transfer-in", true, true);
			String tranPoofOut = CMLib.genEd().prompt(mob,
					mob.playerStats().tranPoofOut(), ++showNumber, showFlag,
					"Transfer-out", true, true);
			mob.playerStats()
					.setPoofs(poofIn, poofOut, tranPoofIn, tranPoofOut);
			if (showFlag < -900) {
				ok = true;
				break;
			}
			if (showFlag > 0) {
				showFlag = -1;
				continue;
			}
			showFlag = CMath.s_int(mob.session().prompt("Edit which? ", ""));
			if (showFlag <= 0) {
				showFlag = -1;
				ok = true;
			}
		}
		return false;
	}

	public boolean canBeOrdered() {
		return true;
	}

	public boolean securityCheck(MOB mob) {
		return CMSecurity.isAllowedContainsAny(mob, mob.location(),
				CMSecurity.SECURITY_GOTO_GROUP);
	}

}
