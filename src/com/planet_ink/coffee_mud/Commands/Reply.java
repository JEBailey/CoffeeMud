package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.PlayerStats;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Reply extends StdCommand {
	public Reply() {
	}

	private final String[] access = { "REPLY", "REP", "RE" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (mob == null)
			return false;
		PlayerStats pstats = mob.playerStats();
		if (pstats == null)
			return false;
		if (pstats.replyTo() == null) {
			mob.tell("No one has told you anything yet!");
			return false;
		}
		if ((pstats.replyTo().Name().indexOf('@') < 0)
				&& ((CMLib.players().getPlayer(pstats.replyTo().Name()) == null)
						|| (pstats.replyTo().isMonster()) || (!CMLib.flags()
						.isInTheGame(pstats.replyTo(), true)))) {
			mob.tell(pstats.replyTo().Name() + " is no longer logged in.");
			return false;
		}
		if (CMParms.combine(commands, 1).length() == 0) {
			mob.tell("Tell '" + pstats.replyTo().Name() + "' what?");
			return false;
		}
		int replyType = pstats.replyType();

		switch (replyType) {
		case PlayerStats.REPLY_SAY:
			if ((pstats.replyTo().Name().indexOf('@') < 0)
					&& ((mob.location() == null) || (!mob.location()
							.isInhabitant(pstats.replyTo())))) {
				mob.tell(pstats.replyTo().Name() + " is no longer in the room.");
				return false;
			}
			CMLib.commands().postSay(mob, pstats.replyTo(),
					CMParms.combine(commands, 1), false, false);
			break;
		case PlayerStats.REPLY_TELL: {
			Session S = pstats.replyTo().session();
			if (S != null)
				S.snoopSuspension(1);
			CMLib.commands().postSay(mob, pstats.replyTo(),
					CMParms.combine(commands, 1), true, true);
			if (S != null)
				S.snoopSuspension(-11);
			break;
		}
		case PlayerStats.REPLY_YELL: {
			Command C = CMClass.getCommand("Say");
			if ((C != null) && (C.securityCheck(mob))) {
				commands.setElementAt("Yell", 0);
				C.execute(mob, commands, metaFlags);
			}
			break;
		}
		}
		if ((pstats.replyTo().session() != null)
				&& (pstats.replyTo().session().isAfk()))
			mob.tell(pstats.replyTo().session().getAfkMessage());
		return false;
	}

	public double combatActionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getCombatActionCost(ID());
	}

	public double actionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getActionCost(ID());
	}

	public boolean canBeOrdered() {
		return false;
	}

}
