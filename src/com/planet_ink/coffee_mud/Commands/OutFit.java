package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class OutFit extends StdCommand {
	public OutFit() {
	}

	private final String[] access = { "OUTFIT" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean preExecute(MOB mob, Vector commands, int metaFlags,
			int secondsElapsed, double actionsRemaining)
			throws java.io.IOException {
		if (secondsElapsed > 8.0)
			mob.tell("You feel your outfit plea is almost answered.");
		else if (secondsElapsed > 4.0)
			mob.tell("Your plea swirls around you.");
		else if (actionsRemaining > 0.0)
			mob.tell("You invoke a plea for mystical outfitting and await the answer.");
		return true;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (mob == null)
			return false;
		if (mob.charStats() == null)
			return false;
		CharClass C = mob.charStats().getCurrentClass();
		Race R = mob.charStats().getMyRace();
		if (C != null)
			CMLib.utensils().outfit(mob, C.outfit(mob));
		if (R != null)
			CMLib.utensils().outfit(mob, R.outfit(mob));
		mob.tell("\n\r");
		Command C2 = CMClass.getCommand("Equipment");
		if (C2 != null)
			C2.executeInternal(mob, metaFlags);
		mob.tell("\n\rUseful equipment appears mysteriously out of the Java Plane.");
		mob.recoverCharStats();
		mob.recoverMaxState();
		mob.recoverPhyStats();
		return false;
	}

	public double combatActionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getCombatActionCost(ID(),
				CMath.div(CMProps.getIntVar(CMProps.Int.DEFCOMCMDTIME), 25.0));
	}

	public double actionsCost(MOB mob, List<String> cmds) {
		return CMProps.getActionCost(ID(),
				CMath.div(CMProps.getIntVar(CMProps.Int.DEFCMDTIME), 25.0));
	}

	public boolean canBeOrdered() {
		return false;
	}

}
