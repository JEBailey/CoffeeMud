package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

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
public class Read extends StdCommand {
	public Read() {
	}

	private final String[] access = { "READ" };

	public String[] getAccessWords() {
		return access;
	}

	private final static Class[][] internalParameters = new Class[][] { {
			Environmental.class, String.class, Boolean.class } };

	public boolean read(MOB mob, Environmental thisThang, String theRest,
			boolean quiet) {
		if ((thisThang == null)
				|| ((!(thisThang instanceof Item) && (!(thisThang instanceof Exit))))
				|| (!CMLib.flags().canBeSeenBy(thisThang, mob))) {
			mob.tell("You don't seem to have that.");
			return false;
		}
		if (thisThang instanceof Item) {
			Item thisItem = (Item) thisThang;
			if ((CMLib.flags().isGettable(thisItem)) && (!mob.isMine(thisItem))) {
				mob.tell("You don't seem to be carrying that.");
				return false;
			}
		}
		String srcMsg = "<S-NAME> read(s) <T-NAMESELF>.";
		String soMsg = (mob.isMine(thisThang) ? srcMsg : null);
		String tMsg = theRest;
		if ((tMsg == null) || (tMsg.trim().length() == 0)
				|| (thisThang instanceof MOB))
			tMsg = soMsg;
		CMMsg newMsg = CMClass.getMsg(mob, thisThang, null, CMMsg.MSG_READ,
				quiet ? srcMsg : null, CMMsg.MSG_READ, tMsg, CMMsg.MSG_READ,
				quiet ? null : soMsg);
		if (mob.location().okMessage(mob, newMsg)) {
			mob.location().send(mob, newMsg);
			return true;
		}
		return false;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() < 2) {
			mob.tell("Read what?");
			return false;
		}
		commands.removeElementAt(0);
		if (commands.firstElement() instanceof Environmental) {
			read(mob, (Environmental) commands.firstElement(),
					CMParms.combine(commands, 1), false);
			return false;
		}

		int dir = Directions.getGoodDirectionCode(CMParms.combine(commands, 0));
		Environmental thisThang = null;
		if (dir >= 0)
			thisThang = mob.location().getExitInDir(dir);
		thisThang = mob.location().fetchFromMOBRoomFavorsItems(mob, null,
				(String) commands.lastElement(), Look.noCoinFilter);
		if (thisThang == null)
			thisThang = mob.location().fetchFromMOBRoomFavorsItems(mob, null,
					(String) commands.lastElement(), Wearable.FILTER_ANY);
		String theRest = null;
		if (thisThang == null)
			thisThang = mob.location().fetchFromMOBRoomFavorsItems(mob, null,
					CMParms.combine(commands, 0), Wearable.FILTER_ANY);
		else {
			commands.removeElementAt(commands.size() - 1);
			theRest = CMParms.combine(commands, 0);
		}
		read(mob, thisThang, theRest, false);
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

	public Object executeInternal(MOB mob, int metaFlags, Object... args)
			throws java.io.IOException {
		if (!super.checkArguments(internalParameters, args))
			return Boolean.FALSE;
		return Boolean.valueOf(read(mob, (Environmental) args[0],
				(String) args[1], ((Boolean) args[2]).booleanValue()));
	}
}
