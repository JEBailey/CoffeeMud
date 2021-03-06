package com.planet_ink.coffee_mud.Commands;

import java.util.Vector;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class PlayerKill extends StdCommand {
	public PlayerKill() {
	}

	private final String[] access = { "PLAYERKILL", "PKILL" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (CMProps.getVar(CMProps.Str.PKILL).startsWith("ALWAYS")
				|| CMProps.getVar(CMProps.Str.PKILL).startsWith("NEVER")) {
			mob.tell("This option has been disabled.");
			return false;
		}

		if (mob.isInCombat()) {
			mob.tell("YOU CANNOT TOGGLE THIS FLAG WHILE IN COMBAT!");
			return false;
		}
		if (CMath.bset(mob.getBitmap(), MOB.ATT_PLAYERKILL)) {
			if (CMProps.getVar(CMProps.Str.PKILL).startsWith("ONEWAY")) {
				mob.tell("Once turned on, this flag may not be turned off again.");
				return false;
			}

			if ((mob.session() != null)
					&& (mob.session().getLastPKFight() > 0)
					&& ((System.currentTimeMillis() - mob.session()
							.getLastPKFight()) < (5 * 60 * 1000))) {
				mob.tell("You'll need to wait a few minutes before you can turn off your PK flag.");
				return false;
			}

			mob.setBitmap(CMath.unsetb(mob.getBitmap(), MOB.ATT_PLAYERKILL));
			mob.tell("Your playerkill flag has been turned off.");
		} else if (!mob.isMonster()) {
			mob.tell("Turning on this flag will allow you to kill and be killed by other players.");
			if (CMProps.getVar(CMProps.Str.PKILL).startsWith("ONEWAY"))
				mob.tell("Once turned on, this flag may not be turned off again.");
			if (mob.session().confirm("Are you absolutely sure (y/N)?", "N")) {
				mob.setBitmap(CMath.setb(mob.getBitmap(), MOB.ATT_PLAYERKILL));
				mob.tell("Your playerkill flag has been turned on.");
			} else
				mob.tell("Your playerkill flag remains OFF.");
			if (!CMProps.getVar(CMProps.Str.PKILL).startsWith("ONEWAY"))
				mob.tell("Both players must have their playerkill flag turned on for sparring.");
		}
		return false;
	}

	public boolean canBeOrdered() {
		return false;
	}

}
