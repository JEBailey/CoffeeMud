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
public class Unlock extends StdCommand {
	public Unlock() {
	}

	private final String[] access = { "UNLOCK", "UNL", "UN" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		String whatTounlock = CMParms.combine(commands, 1);
		if (whatTounlock.length() == 0) {
			mob.tell("Unlock what?");
			return false;
		}
		Environmental unlockThis = null;
		int dirCode = Directions.getGoodDirectionCode(whatTounlock);
		if (dirCode >= 0)
			unlockThis = mob.location().getExitInDir(dirCode);
		if (unlockThis == null)
			unlockThis = mob.location().fetchFromMOBRoomItemExit(mob, null,
					whatTounlock, Wearable.FILTER_ANY);

		if ((unlockThis == null)
				|| (!CMLib.flags().canBeSeenBy(unlockThis, mob))) {
			mob.tell("You don't see '" + whatTounlock + "' here.");
			return false;
		}
		final String unlockMsg = "<S-NAME> unlock(s) <T-NAMESELF>."
				+ CMLib.protocol().msp("doorunlock.wav", 10);
		CMMsg msg = CMClass.getMsg(mob, unlockThis, null, CMMsg.MSG_UNLOCK,
				unlockMsg, whatTounlock, unlockMsg);
		if (unlockThis instanceof Exit) {
			boolean locked = ((Exit) unlockThis).isLocked();
			if ((mob.location().okMessage(msg.source(), msg)) && (locked)) {
				mob.location().send(msg.source(), msg);
				if (dirCode < 0)
					for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--)
						if (mob.location().getExitInDir(d) == unlockThis) {
							dirCode = d;
							break;
						}

				if ((dirCode >= 0)
						&& (mob.location().getRoomInDir(dirCode) != null)) {
					Room opR = mob.location().getRoomInDir(dirCode);
					Exit opE = mob.location().getPairedExit(dirCode);
					if (opE != null) {
						CMMsg altMsg = CMClass.getMsg(msg.source(), opE,
								msg.tool(), msg.sourceCode(), null,
								msg.targetCode(), null, msg.othersCode(), null);
						opE.executeMsg(msg.source(), altMsg);
					}
					int opCode = Directions.getOpDirectionCode(dirCode);
					if ((opE != null) && (!opE.isLocked())
							&& (!((Exit) unlockThis).isLocked())) {
						final boolean useShipDirs = (opR instanceof SpaceShip)
								|| (opR.getArea() instanceof SpaceShip);
						final String inDirName = useShipDirs ? Directions
								.getShipInDirectionName(opCode) : Directions
								.getInDirectionName(opCode);
						opR.showHappens(CMMsg.MSG_OK_ACTION, opE.name() + " "
								+ inDirName
								+ " is unlocked from the other side.");
					}
				}
			}
		} else if (mob.location().okMessage(mob, msg))
			mob.location().send(mob, msg);
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
