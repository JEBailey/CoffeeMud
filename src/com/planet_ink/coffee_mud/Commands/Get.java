package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Coins;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Light;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.collections.XVector;
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
public class Get extends StdCommand {
	public Get() {
	}

	private final String[] access = { "GET", "G" };

	public String[] getAccessWords() {
		return access;
	}

	public static boolean get(MOB mob, Item container, Item getThis,
			boolean quiet) {
		return get(mob, container, getThis, quiet, "get", false);
	}

	public static boolean get(MOB mob, Item container, Item getThis,
			boolean quiet, String getWord, boolean optimize) {
		Room R = mob.location();
		String theWhat = "<T-NAME>";
		Item target = getThis;
		Item tool = null;
		if (container != null) {
			tool = getThis;
			target = container;
			theWhat = "<O-NAME> from <T-NAME>";
		}
		if (!getThis.amWearingAt(Wearable.IN_INVENTORY)) {
			CMMsg msg = CMClass.getMsg(mob, getThis, null,
					(optimize ? CMMsg.MASK_OPTIMIZE : 0) | CMMsg.MSG_REMOVE,
					null);
			if (!R.okMessage(mob, msg))
				return false;
			R.send(mob, msg);
		}
		CMMsg msg = CMClass.getMsg(mob, target, tool,
				(optimize ? CMMsg.MASK_OPTIMIZE : 0) | CMMsg.MSG_GET,
				quiet ? null : "<S-NAME> " + getWord + "(s) " + theWhat + ".");
		if (!R.okMessage(mob, msg))
			return false;
		// we do this next step because, when a container is involved,
		// the item deserves to be the target of the GET.
		if ((!mob.isMine(target)) && (target != getThis)) {
			CMMsg msg2 = CMClass.getMsg(mob, getThis, null,
					(optimize ? CMMsg.MASK_OPTIMIZE : 0) | CMMsg.MSG_GET, null);
			if (!R.okMessage(mob, msg2))
				return false;
			R.send(mob, msg);
			R.send(mob, msg2);
		} else {
			R.send(mob, msg);
		}
		return true;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		Room R = mob.location();
		if ((commands.size() > 1) && (commands.firstElement() instanceof Item)) {
			Item item = (Item) commands.firstElement();
			Item container = null;
			boolean quiet = false;
			if (commands.elementAt(1) instanceof Item) {
				container = (Item) commands.elementAt(1);
				if ((commands.size() > 2)
						&& (commands.elementAt(2) instanceof Boolean))
					quiet = ((Boolean) commands.elementAt(2)).booleanValue();
			} else if (commands.elementAt(1) instanceof Boolean)
				quiet = ((Boolean) commands.elementAt(1)).booleanValue();
			boolean success = get(mob, container, item, quiet);
			if (item instanceof Coins)
				((Coins) item).putCoinsBack();
			if (item instanceof RawMaterial)
				((RawMaterial) item).rebundle();
			return success;
		}

		if (commands.size() < 2) {
			mob.tell("Get what?");
			return false;
		}
		commands.removeElementAt(0);
		boolean quiet = false;
		if ((commands.size() > 0)
				&& (((String) commands.lastElement())
						.equalsIgnoreCase("UNOBTRUSIVELY"))) {
			quiet = true;
			commands.removeElementAt(commands.size() - 1);
		}

		String containerName = "";
		if (commands.size() > 0)
			containerName = (String) commands.lastElement();
		Vector containerCommands = new XVector(commands);
		java.util.List<Container> containers = CMLib.english()
				.possibleContainers(mob, commands, Wearable.FILTER_ANY, true);
		int c = 0;

		int maxToGet = CMLib.english().calculateMaxToGive(mob, commands,
				containers.size() == 0, R, true);
		if (maxToGet < 0)
			return false;

		String whatToGet = CMParms.combine(commands, 0);
		String unmodifiedWhatToGet = whatToGet;
		boolean allFlag = (commands.size() > 0) ? ((String) commands
				.elementAt(0)).equalsIgnoreCase("all") : false;
		if (whatToGet.toUpperCase().startsWith("ALL.")) {
			allFlag = true;
			whatToGet = "ALL " + whatToGet.substring(4);
		}
		if (whatToGet.toUpperCase().endsWith(".ALL")) {
			allFlag = true;
			whatToGet = "ALL " + whatToGet.substring(0, whatToGet.length() - 4);
		}
		boolean doneSomething = false;
		while ((c < containers.size()) || (containers.size() == 0)) {
			Vector V = new Vector();
			Container container = null;
			if (containers.size() > 0)
				container = containers.get(c++);
			int addendum = 1;
			String addendumStr = "";
			boolean doBugFix = true;
			while (doBugFix || ((allFlag) && (addendum <= maxToGet))) {
				doBugFix = false;
				Environmental getThis = null;
				if ((container != null) && (mob.isMine(container)))
					getThis = R
							.fetchFromMOBRoomFavorsItems(mob, container,
									whatToGet + addendumStr,
									Wearable.FILTER_UNWORNONLY);
				else {
					if (!allFlag)
						getThis = CMLib.english().possibleRoomGold(mob, R,
								container, whatToGet);
					if (getThis == null)
						getThis = R.fetchFromRoomFavorItems(container,
								whatToGet + addendumStr);
				}
				if (getThis == null)
					break;

				if ((maxToGet > 1) && (getThis instanceof RawMaterial)
						&& (container != null)
						&& (((RawMaterial) getThis).container() == container)) {
					int weight = ((RawMaterial) getThis).phyStats().weight();
					if ((weight > 1)
							&& (weight >= maxToGet)
							&& (CMStrings.containsWordIgnoreCase(
									((RawMaterial) getThis).name(), "bundle"))) {
						if (weight > maxToGet)
							getThis = CMLib.materials().splitBundle(
									(RawMaterial) getThis, maxToGet, container);
						maxToGet = 1;
					}
				}

				if ((getThis instanceof Item)
						&& ((CMLib.flags().canBeSeenBy(getThis, mob) || (getThis instanceof Light)))
						&& ((!allFlag)
								|| CMLib.flags().isGettable(((Item) getThis)) || (getThis
								.displayText().length() > 0))
						&& (!V.contains(getThis)))
					V.addElement(getThis);

				addendumStr = "." + (++addendum);
			}

			for (int i = 0; i < V.size(); i++) {
				Item getThis = (Item) V.elementAt(i);
				get(mob, container, getThis, quiet, "get", true);
				if (getThis instanceof Coins)
					((Coins) getThis).putCoinsBack();
				if (getThis instanceof RawMaterial)
					((RawMaterial) getThis).rebundle();
				doneSomething = true;
			}
			R.recoverRoomStats();
			R.recoverRoomStats();

			if (containers.size() == 0)
				break;
		}
		if (!doneSomething) {
			if (containers.size() > 0) {
				Container container = containers.get(0);
				if (container.isOpen())
					mob.tell(mob, container, null, "You don't see '"
							+ unmodifiedWhatToGet + "' in <T-NAME>.");
				else
					mob.tell(container.name() + " is closed.");
			} else if (containerName.equalsIgnoreCase("all"))
				mob.tell("You don't see anything here.");
			else {
				java.util.List<Container> V = CMLib.english()
						.possibleContainers(mob, containerCommands,
								Wearable.FILTER_ANY, false);
				if (V.size() == 0)
					mob.tell("You don't see '" + containerName + "' here.");
				else if (V.size() == 1)
					mob.tell(mob, V.get(0), null, "You don't see '"
							+ unmodifiedWhatToGet + "' in <T-NAME> here.");
				else
					mob.tell("You don't see '" + unmodifiedWhatToGet
							+ "' in any '" + containerName + "'.");
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
