package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
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
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;

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
public class Push extends Go {
	public Push() {
	}

	private final String[] access = { "PUSH" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		Environmental pushThis = null;
		String dir = "";
		int dirCode = -1;
		Environmental E = null;
		if (commands.size() > 1) {
			dirCode = Directions.getGoodDirectionCode((String) commands
					.lastElement());
			if (dirCode >= 0) {
				if ((mob.location().getRoomInDir(dirCode) == null)
						|| (mob.location().getExitInDir(dirCode) == null)
						|| (!mob.location().getExitInDir(dirCode).isOpen())) {
					mob.tell("You can't push anything that way.");
					return false;
				}
				E = mob.location().getRoomInDir(dirCode);
				dir = " "
						+ (((mob.location() instanceof SpaceShip) || (mob
								.location().getArea() instanceof SpaceShip)) ? Directions
								.getShipDirectionName(dirCode) : Directions
								.getDirectionName(dirCode));
				commands.removeElementAt(commands.size() - 1);
			}
		}
		if (dir.length() == 0) {
			dirCode = Directions.getGoodDirectionCode((String) commands
					.lastElement());
			if (dirCode >= 0)
				pushThis = mob.location().getExitInDir(dirCode);
		}
		String itemName = CMParms.combine(commands, 1);
		if (pushThis == null)
			pushThis = mob.location().fetchFromRoomFavorItems(null, itemName);
		if (pushThis == null)
			pushThis = mob.location().fetchFromMOBRoomFavorsItems(mob, null,
					itemName, Wearable.FILTER_ANY);

		if ((pushThis == null) || (!CMLib.flags().canBeSeenBy(pushThis, mob))) {
			mob.tell("You don't see '" + itemName + "' here.");
			return false;
		}
		int malmask = (pushThis instanceof MOB) ? CMMsg.MASK_MALICIOUS : 0;
		String msgStr = "<S-NAME> push(es) <T-NAME>" + dir + ".";
		CMMsg msg = CMClass.getMsg(mob, pushThis, E, CMMsg.MSG_PUSH | malmask,
				msgStr);
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			if ((dir.length() > 0) && (msg.tool() instanceof Room)) {
				Room R = (Room) msg.tool();
				if (R.okMessage(mob, msg)) {
					dirCode = CMLib.tracking().findRoomDir(mob, R);
					if (dirCode >= 0) {
						if (msg.othersMessage().equals(msgStr))
							msg.setOthersMessage("<S-NAME> push(es) <T-NAME> into here.");
						R.sendOthers(mob, msg);
						if (pushThis instanceof Item)
							R.moveItemTo((Item) pushThis,
									ItemPossessor.Expire.Player_Drop,
									ItemPossessor.Move.Followers);
						else if (pushThis instanceof MOB)
							CMLib.tracking().walk((MOB) pushThis, dirCode,
									((MOB) pushThis).isInCombat(), false, true,
									true);
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