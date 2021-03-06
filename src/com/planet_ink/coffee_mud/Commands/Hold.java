package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Hold extends StdCommand {
	public Hold() {
	}

	private final String[] access = { "HOLD", "HOL", "HO", "H" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() < 2) {
			mob.tell("Hold what?");
			return false;
		}
		commands.removeElementAt(0);
		List<Item> items = CMLib.english().fetchItemList(mob, mob, null,
				commands, Wearable.FILTER_UNWORNONLY, false);
		if (items.size() == 0)
			mob.tell("You don't seem to be carrying that.");
		else
			for (int i = 0; i < items.size(); i++)
				if ((items.size() == 1)
						|| (items.get(i).canWear(mob, Wearable.WORN_HELD))) {
					Item item = items.get(i);
					int msgType = CMMsg.MSG_HOLD;
					String str = "<S-NAME> hold(s) <T-NAME>.";
					if ((mob.freeWearPositions(Wearable.WORN_WIELD, (short) 0,
							(short) 0) > 0)
							&& ((item.rawProperLocationBitmap() == Wearable.WORN_WIELD) || (item
									.rawProperLocationBitmap() == (Wearable.WORN_HELD | Wearable.WORN_WIELD)))) {
						str = "<S-NAME> wield(s) <T-NAME>.";
						msgType = CMMsg.MSG_WIELD;
					}
					CMMsg newMsg = CMClass
							.getMsg(mob, item, null, msgType, str);
					if (mob.location().okMessage(mob, newMsg))
						mob.location().send(mob, newMsg);
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
