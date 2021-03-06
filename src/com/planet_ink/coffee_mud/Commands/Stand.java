package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;

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
public class Stand extends StdCommand {
	public Stand() {
	}

	private final String[] access = { "STAND", "ST", "STA", "STAN" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		boolean ifnecessary = ((commands.size() > 1) && (((String) commands
				.lastElement()).equalsIgnoreCase("IFNECESSARY")));
		Room room = CMLib.map().roomLocation(mob);
		if (CMLib.flags().isStanding(mob)) {
			if (!ifnecessary)
				mob.tell("You are already standing!");
		} else if ((mob.session() != null) && (mob.session().isStopped()))
			mob.tell("You may not stand up.");
		else if (room != null) {
			CMMsg msg = CMClass.getMsg(mob, null, null, CMMsg.MSG_STAND,
					mob.amDead() ? null : "<S-NAME> stand(s) up.");
			if (room.okMessage(mob, msg))
				room.send(mob, msg);
		}
		return false;
	}

	public double combatActionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getCombatActionCost(ID());
	}

	public double actionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getActionCost(ID());
	}

	public boolean canBeOrdered() {
		return true;
	}

}
