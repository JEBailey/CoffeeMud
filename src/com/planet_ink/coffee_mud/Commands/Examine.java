package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.SpaceShip;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Examine extends StdCommand {
	public Examine() {
	}

	private final String[] access = { "EXAMINE", "EXAM", "EXA", "LONGLOOK",
			"LLOOK", "LL" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		boolean quiet = false;
		if ((commands != null)
				&& (commands.size() > 1)
				&& (((String) commands.lastElement())
						.equalsIgnoreCase("UNOBTRUSIVELY"))) {
			commands.removeElementAt(commands.size() - 1);
			quiet = true;
		}
		String textMsg = "<S-NAME> examine(s) ";
		if (mob.location() == null)
			return false;
		if ((commands != null) && (commands.size() > 1)) {
			Environmental thisThang = null;

			String ID = CMParms.combine(commands, 1);
			if (ID.length() == 0)
				thisThang = mob.location();
			else if ((ID.toUpperCase().startsWith("EXIT") && (commands.size() == 2))) {
				CMMsg exitMsg = CMClass.getMsg(mob, thisThang, null,
						CMMsg.MSG_LOOK_EXITS, null);
				if ((CMProps.getIntVar(CMProps.Int.EXVIEW) >= 2) != CMath.bset(
						mob.getBitmap(), MOB.ATT_BRIEF))
					exitMsg.setValue(CMMsg.MASK_OPTIMIZE);
				if (mob.location().okMessage(mob, exitMsg))
					mob.location().send(mob, exitMsg);
				return false;
			}
			if (ID.equalsIgnoreCase("SELF") || ID.equalsIgnoreCase("ME"))
				thisThang = mob;

			if (thisThang == null)
				thisThang = mob.location().fetchFromMOBRoomFavorsItems(mob,
						null, ID, Wearable.FILTER_ANY);
			int dirCode = -1;
			if (thisThang == null) {
				dirCode = Directions.getGoodDirectionCode(ID);
				if (dirCode >= 0) {
					Room room = mob.location().getRoomInDir(dirCode);
					Exit exit = mob.location().getExitInDir(dirCode);
					if ((room != null) && (exit != null))
						thisThang = exit;
					else {
						mob.tell("You don't see anything that way.");
						return false;
					}
				}
			}
			if (thisThang != null) {
				String name = "<T-NAMESELF>";
				if ((thisThang instanceof Room) || (thisThang instanceof Exit)) {
					if (thisThang == mob.location())
						name = "around";
					else if (dirCode >= 0)
						name = ((mob.location() instanceof SpaceShip) || (mob
								.location().getArea() instanceof SpaceShip)) ? Directions
								.getShipDirectionName(dirCode) : Directions
								.getDirectionName(dirCode);
				}
				CMMsg msg = CMClass.getMsg(mob, thisThang, null,
						CMMsg.MSG_EXAMINE, textMsg + name + " closely.");
				if (mob.location().okMessage(mob, msg))
					mob.location().send(mob, msg);
				if ((CMath.bset(mob.getBitmap(), MOB.ATT_AUTOEXITS))
						&& (thisThang instanceof Room))
					msg.addTrailerMsg(CMClass.getMsg(mob, thisThang, null,
							CMMsg.MSG_LOOK_EXITS, null));
			} else
				mob.tell("You don't see that here!");
		} else {
			CMMsg msg = CMClass.getMsg(mob, mob.location(), null,
					CMMsg.MSG_EXAMINE, (quiet ? null : textMsg
							+ "around carefully."), CMMsg.MSG_EXAMINE,
					(quiet ? null : textMsg + "at you."), CMMsg.MSG_EXAMINE,
					(quiet ? null : textMsg + "around carefully."));
			if ((CMath.bset(mob.getBitmap(), MOB.ATT_AUTOEXITS))
					&& (CMLib.flags().canBeSeenBy(mob.location(), mob)))
				msg.addTrailerMsg(CMClass.getMsg(mob, mob.location(), null,
						CMMsg.MSG_LOOK_EXITS, null));
			if (mob.location().okMessage(mob, msg))
				mob.location().send(mob, msg);
		}
		return false;
	}

	public double combatActionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getCombatActionCost(ID());
	}

	public double actionsCost(MOB mob, List<String> cmds) {
		return 1.0;
	}

	public boolean canBeOrdered() {
		return true;
	}
}
