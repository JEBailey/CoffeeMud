package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
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
public class Pour extends StdCommand {
	public Pour() {
	}

	private final String[] access = { "POUR" };

	public String[] getAccessWords() {
		return access;
	}

	enum PourVerb {
		DEFAULT, INTO, ONTO, OUT
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() < 2) {
			mob.tell("Pour what, into/onto what?");
			return false;
		}
		commands.removeElementAt(0);
		Environmental fillFromThis = null;
		String thingToFillFrom = (String) commands.elementAt(0);
		fillFromThis = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY,
				thingToFillFrom);
		if ((fillFromThis == null)
				|| (!CMLib.flags().canBeSeenBy(fillFromThis, mob))) {
			mob.tell("You don't seem to have '" + thingToFillFrom + "'.");
			return false;
		}
		commands.removeElementAt(0);

		PourVerb verb = PourVerb.DEFAULT;
		if (commands.size() > 1) {
			if ((((String) commands.firstElement())).equalsIgnoreCase("into"))
				commands.removeElementAt(0);
			else if ((((String) commands.firstElement()))
					.equalsIgnoreCase("onto")) {
				commands.removeElementAt(0);
				verb = PourVerb.ONTO;
			} else if ((((String) commands.firstElement()))
					.equalsIgnoreCase("out")) {
				commands.removeElementAt(0);
				verb = PourVerb.OUT;
			}
		}

		Environmental fillThis;
		String msgStr;
		if (verb == PourVerb.OUT) {
			Item out = CMClass.getItem("StdDrink");
			((Drink) out).setLiquidHeld(999999);
			((Drink) out).setLiquidRemaining(0);
			out.setDisplayText("");
			out.setName("out");
			msgStr = "<S-NAME> pour(s) <O-NAME> <T-NAME>.";
			mob.location().addItem(out, ItemPossessor.Expire.Resource);
			fillThis = out;
		} else {
			if (commands.size() < 1) {
				mob.tell(CMStrings.capitalizeAndLower(verb.name())
						+ " what should I pour the " + thingToFillFrom + "?");
				return false;
			}
			String thingToFill = CMParms.combine(commands, 0);
			fillThis = mob.location().fetchFromMOBRoomFavorsItems(mob, null,
					thingToFill, Wearable.FILTER_ANY);
			if ((fillThis == null)
					|| (!CMLib.flags().canBeSeenBy(fillThis, mob))) {
				mob.tell("I don't see '" + thingToFill + "' here.");
				return false;
			}
			if ((verb == PourVerb.DEFAULT) && (!(fillThis instanceof Drink)))
				verb = PourVerb.ONTO;
			else if ((verb == PourVerb.ONTO) && (fillThis instanceof Drink))
				verb = PourVerb.INTO;
			if (verb == PourVerb.ONTO)
				msgStr = "<S-NAME> pour(s) <O-NAME> onto <T-NAME>.";
			else
				msgStr = "<S-NAME> pour(s) <O-NAME> into <T-NAME>.";
		}

		CMMsg fillMsg = CMClass.getMsg(mob, fillThis, fillFromThis,
				(verb == PourVerb.ONTO) ? CMMsg.MSG_POUR : CMMsg.MSG_FILL,
				msgStr);
		if (mob.location().okMessage(mob, fillMsg))
			mob.location().send(mob, fillMsg);

		if (verb == PourVerb.OUT)
			fillThis.destroy();
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