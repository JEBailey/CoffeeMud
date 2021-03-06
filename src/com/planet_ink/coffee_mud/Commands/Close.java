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
public class Close extends StdCommand {
	public Close() {
	}

	private final String[] access = { "CLOSE", "CLOS", "CLO", "CL" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		String whatToClose = CMParms.combine(commands, 1);
		if (whatToClose.length() == 0) {
			mob.tell("Close what?");
			return false;
		}
		Environmental closeThis = null;
		int dirCode = Directions.getGoodDirectionCode(whatToClose);
		if (dirCode >= 0)
			closeThis = mob.location().getExitInDir(dirCode);
		if (closeThis == null)
			closeThis = mob.location().fetchFromMOBRoomItemExit(mob, null,
					whatToClose, Wearable.FILTER_ANY);

		if ((closeThis == null) || (!CMLib.flags().canBeSeenBy(closeThis, mob))) {
			mob.tell("You don't see '" + whatToClose + "' here.");
			return false;
		}
		final boolean useShipDirs = (mob.location() instanceof SpaceShip)
				|| (mob.location().getArea() instanceof SpaceShip);
		final String closeWord = (!(closeThis instanceof Exit)) ? "close"
				: ((Exit) closeThis).closeWord();
		final String closeMsg = "<S-NAME> " + closeWord + "(s) <T-NAMESELF>."
				+ CMLib.protocol().msp("dooropen.wav", 10);
		CMMsg msg = CMClass.getMsg(mob, closeThis, null, CMMsg.MSG_CLOSE,
				closeMsg, whatToClose, closeMsg);
		if (closeThis instanceof Exit) {
			boolean open = ((Exit) closeThis).isOpen();
			if ((mob.location().okMessage(msg.source(), msg)) && (open)) {
				mob.location().send(msg.source(), msg);
				if (dirCode < 0)
					for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--)
						if (mob.location().getExitInDir(d) == closeThis) {
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
					if ((opE != null) && (!opE.isOpen())
							&& (!((Exit) closeThis).isOpen()))
						opR.showHappens(
								CMMsg.MSG_OK_ACTION,
								opE.name()
										+ " "
										+ (useShipDirs ? Directions
												.getShipInDirectionName(opCode)
												: Directions
														.getInDirectionName(opCode))
										+ " closes.");
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
