package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
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
public class Fill extends StdCommand {
	public Fill() {
	}

	private final String[] access = { "FILL" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() < 2) {
			mob.tell("Fill what, from what?");
			return false;
		}
		commands.removeElementAt(0);
		final String testFill = CMParms.combine(commands, 0);
		Environmental fillThisItem = mob.location().fetchFromRoomFavorItems(
				null, testFill);
		if ((fillThisItem instanceof Container)
				&& (!CMLib.flags().isGettable((Container) fillThisItem))
				&& (((Container) fillThisItem).material() == RawMaterial.RESOURCE_DUST)) {
			final String fillMsg = "<S-NAME> fill(s) in <T-NAMESELF>.";
			CMMsg msg = CMClass.getMsg(mob, fillThisItem, null,
					CMMsg.MSG_CLOSE, fillMsg, testFill, fillMsg);
			if (mob.location().okMessage(msg.source(), msg))
				mob.location().send(msg.source(), msg);
			return false;
		}

		if ((commands.size() < 2) && (!(mob.location() instanceof Drink))) {
			mob.tell("From what should I fill the "
					+ (String) commands.elementAt(0) + "?");
			return false;
		}
		Environmental fillFromThis = null;
		if ((commands.size() == 1) && (mob.location() instanceof Drink))
			fillFromThis = mob.location();
		else {
			int fromDex = commands.size() - 1;
			for (int i = commands.size() - 2; i >= 1; i--)
				if (((String) commands.elementAt(i)).equalsIgnoreCase("from")) {
					fromDex = i;
					commands.removeElementAt(i);
				}
			String thingToFillFrom = CMParms.combine(commands, fromDex);
			fillFromThis = mob.location().fetchFromMOBRoomFavorsItems(mob,
					null, thingToFillFrom, Wearable.FILTER_ANY);
			if ((fillFromThis == null)
					|| (!CMLib.flags().canBeSeenBy(fillFromThis, mob))) {
				mob.tell("I don't see " + thingToFillFrom + " here.");
				return false;
			}
			while (commands.size() >= (fromDex + 1))
				commands.removeElementAt(commands.size() - 1);
		}

		int maxToFill = CMLib.english().calculateMaxToGive(mob, commands, true,
				mob, false);
		if (maxToFill < 0)
			return false;

		String thingToFill = CMParms.combine(commands, 0);
		int addendum = 1;
		String addendumStr = "";
		Vector V = new Vector();
		boolean allFlag = (commands.size() > 0) ? ((String) commands
				.elementAt(0)).equalsIgnoreCase("all") : false;
		if (thingToFill.toUpperCase().startsWith("ALL.")) {
			allFlag = true;
			thingToFill = "ALL " + thingToFill.substring(4);
		}
		if (thingToFill.toUpperCase().endsWith(".ALL")) {
			allFlag = true;
			thingToFill = "ALL "
					+ thingToFill.substring(0, thingToFill.length() - 4);
		}
		boolean doBugFix = true;
		while (doBugFix || ((allFlag) && (maxToFill < addendum))) {
			doBugFix = false;
			Item fillThis = mob.findItem(null, thingToFill + addendumStr);
			if (fillThis == null)
				break;
			if ((CMLib.flags().canBeSeenBy(fillThis, mob))
					&& (!V.contains(fillThis)))
				V.addElement(fillThis);
			addendumStr = "." + (++addendum);
		}

		if (V.size() == 0)
			mob.tell("You don't seem to have '" + thingToFill + "'.");
		else
			for (int i = 0; i < V.size(); i++) {
				Environmental fillThis = (Environmental) V.elementAt(i);
				CMMsg fillMsg = CMClass.getMsg(mob, fillThis, fillFromThis,
						CMMsg.MSG_FILL,
						"<S-NAME> fill(s) <T-NAME> from <O-NAME>.");
				if ((!mob.isMine(fillThis)) && (fillThis instanceof Item)) {
					if (CMLib.commands().postGet(mob, null, (Item) fillThis,
							false))
						if (mob.location().okMessage(mob, fillMsg))
							mob.location().send(mob, fillMsg);
				} else if (mob.location().okMessage(mob, fillMsg))
					mob.location().send(mob, fillMsg);
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
