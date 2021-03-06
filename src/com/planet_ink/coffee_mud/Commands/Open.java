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
public class Open extends StdCommand {
	public Open() {
	}

	private final String[] access = { "OPEN", "OP", "O" };

	public String[] getAccessWords() {
		return access;
	}

	private final static Class[][] internalParameters = new Class[][] { {
			Environmental.class, Boolean.class } };

	public boolean open(MOB mob, Environmental openThis, String openableWord,
			int dirCode, boolean quietly) {
		final String openWord = (!(openThis instanceof Exit)) ? "open"
				: ((Exit) openThis).openWord();
		final String openMsg = quietly ? null
				: ("<S-NAME> " + openWord + "(s) <T-NAMESELF>.")
						+ CMLib.protocol().msp("dooropen.wav", 10);
		CMMsg msg = CMClass.getMsg(mob, openThis, null, CMMsg.MSG_OPEN,
				openMsg, openableWord, openMsg);
		if (openThis instanceof Exit) {
			boolean open = ((Exit) openThis).isOpen();
			if ((mob.location().okMessage(msg.source(), msg)) && (!open)) {
				mob.location().send(msg.source(), msg);

				if (dirCode < 0)
					for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--)
						if (mob.location().getExitInDir(d) == openThis) {
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
					if ((opE != null) && (opE.isOpen())
							&& (((Exit) openThis).isOpen())) {
						final boolean useShipDirs = (opR instanceof SpaceShip)
								|| (opR.getArea() instanceof SpaceShip);
						final String inDirName = useShipDirs ? Directions
								.getShipInDirectionName(opCode) : Directions
								.getInDirectionName(opCode);
						opR.showHappens(CMMsg.MSG_OK_ACTION, opE.name() + " "
								+ inDirName + " opens.");
					}
					return true;
				}
			}
		} else if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			return true;
		}
		return false;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		String whatToOpen = CMParms.combine(commands, 1);
		if (whatToOpen.length() == 0) {
			mob.tell("Open what?");
			return false;
		}
		Environmental openThis = null;
		int dirCode = Directions.getGoodDirectionCode(whatToOpen);
		if (dirCode >= 0)
			openThis = mob.location().getExitInDir(dirCode);
		if (openThis == null)
			openThis = mob.location().fetchFromMOBRoomItemExit(mob, null,
					whatToOpen, Wearable.FILTER_ANY);

		if ((openThis == null) || (!CMLib.flags().canBeSeenBy(openThis, mob))) {
			mob.tell("You don't see '" + whatToOpen + "' here.");
			return false;
		}
		open(mob, openThis, whatToOpen, dirCode, false);
		return false;
	}

	public Object executeInternal(MOB mob, int metaFlags, Object... args)
			throws java.io.IOException {
		if (!super.checkArguments(internalParameters, args))
			return Boolean.FALSE;
		return Boolean.valueOf(open(mob, (Environmental) args[0],
				((Environmental) args[0]).name(), -1,
				((Boolean) args[1]).booleanValue()));
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
