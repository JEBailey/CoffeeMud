package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.SpaceShip;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Libraries.interfaces.TrackingLibrary;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Knock extends StdCommand {
	public Knock() {
	}

	private final String[] access = { "KNOCK" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() <= 1) {
			mob.tell("Knock on what?");
			return false;
		}
		String knockWhat = CMParms.combine(commands, 1).toUpperCase();
		int dir = CMLib.tracking().findExitDir(mob, mob.location(), knockWhat);
		if (dir < 0) {
			Environmental getThis = mob.location().fetchFromMOBRoomItemExit(
					mob, null, knockWhat, Wearable.FILTER_UNWORNONLY);
			if (getThis == null) {
				mob.tell("You don't see '" + knockWhat.toLowerCase()
						+ "' here.");
				return false;
			}
			CMMsg msg = CMClass.getMsg(mob, getThis, null, CMMsg.MSG_KNOCK,
					CMMsg.MSG_KNOCK, CMMsg.MSG_KNOCK,
					"<S-NAME> knock(s) on <T-NAMESELF>."
							+ CMLib.protocol().msp("knock.wav", 50));
			if (mob.location().okMessage(mob, msg))
				mob.location().send(mob, msg);

		} else {
			Exit E = mob.location().getExitInDir(dir);
			if (E == null) {
				mob.tell("Knock on what?");
				return false;
			}
			if (!E.hasADoor()) {
				mob.tell("You can't knock on " + E.name() + "!");
				return false;
			}
			CMMsg msg = CMClass.getMsg(mob, E, null, CMMsg.MSG_KNOCK,
					CMMsg.MSG_KNOCK, CMMsg.MSG_KNOCK,
					"<S-NAME> knock(s) on <T-NAMESELF>."
							+ CMLib.protocol().msp("knock.wav", 50));
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				E = mob.location().getPairedExit(dir);
				Room R = mob.location().getRoomInDir(dir);
				if ((R != null)
						&& (E != null)
						&& (E.hasADoor())
						&& (R.showOthers(mob, E, null, CMMsg.MSG_KNOCK,
								"You hear a knock on <T-NAMESELF>."
										+ CMLib.protocol().msp("knock.wav", 50)))
						&& ((R.domainType() & Room.INDOORS) == Room.INDOORS)) {
					Vector V = new Vector();
					V.addElement(mob.location());
					TrackingLibrary.TrackingFlags flags;
					flags = new TrackingLibrary.TrackingFlags()
							.plus(TrackingLibrary.TrackingFlag.OPENONLY);
					CMLib.tracking()
							.getRadiantRooms(R, V, flags, null, 5, null);
					V.removeElement(mob.location());
					for (int v = 0; v < V.size(); v++) {
						Room R2 = (Room) V.elementAt(v);
						int dir2 = CMLib.tracking().radiatesFromDir(R2, V);
						if ((dir2 >= 0)
								&& ((R2.domainType() & Room.INDOORS) == Room.INDOORS)) {
							Room R3 = R2.getRoomInDir(dir2);
							if (((R3 != null) && (R3.domainType() & Room.INDOORS) == Room.INDOORS)) {
								final boolean useShipDirs = (R2 instanceof SpaceShip)
										|| (R2.getArea() instanceof SpaceShip);
								final String inDirName = useShipDirs ? Directions
										.getShipInDirectionName(dir2)
										: Directions.getInDirectionName(dir2);
								R2.showHappens(CMMsg.MASK_SOUND
										| CMMsg.TYP_KNOCK, "You hear a knock "
										+ inDirName + "."
										+ CMLib.protocol().msp("knock.wav", 50));
							}
						}
					}
				}
			}
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
