package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Coins;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.PackagedItems;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Package extends StdCommand {
	public Package() {
	}

	private final String[] access = { "PACKAGE" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		if (commands.size() < 2) {
			mob.tell("Package what?");
			return false;
		}
		commands.removeElementAt(0);
		String whatName = "";
		if (commands.size() > 0)
			whatName = (String) commands.lastElement();
		int maxToGet = CMLib.english().calculateMaxToGive(mob, commands, true,
				mob, false);
		if (maxToGet < 0)
			return false;

		String whatToGet = CMParms.combine(commands, 0);
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
		Vector<Item> V = new Vector<Item>();
		int addendum = 1;
		String addendumStr = "";
		do {
			Environmental getThis = null;
			getThis = mob.location().fetchFromRoomFavorItems(null,
					whatToGet + addendumStr);
			if (getThis == null)
				break;
			if ((getThis instanceof Item)
					&& (CMLib.flags().canBeSeenBy(getThis, mob))
					&& ((!allFlag)
							|| CMLib.flags().isGettable(((Item) getThis)) || (getThis
							.displayText().length() > 0))
					&& (!V.contains(getThis)))
				V.addElement((Item) getThis);
			addendumStr = "." + (++addendum);
		} while ((allFlag) && (addendum <= maxToGet));

		if (V.size() == 0) {
			mob.tell("You don't see '" + whatName + "' here.");
			return false;
		}

		for (int i = 0; i < V.size(); i++) {
			Item I = V.get(i);
			if ((I instanceof Coins) || (CMLib.flags().isEnspelled(I))
					|| (CMLib.flags().isOnFire(I))) {
				mob.tell("Items such as " + I.name(mob)
						+ " may not be packaged.");
				return false;
			}
		}
		PackagedItems thePackage = (PackagedItems) CMClass
				.getItem("GenPackagedItems");
		if (thePackage == null)
			return false;
		if (!thePackage.isPackagable(V)) {
			mob.tell("All items in a package must be absolutely identical.  Some here are not.");
			return false;
		}
		Item getThis = null;
		for (int i = 0; i < V.size(); i++) {
			getThis = V.elementAt(i);
			if ((!mob.isMine(getThis))
					&& (!Get.get(mob, null, getThis, true, "get", true)))
				return false;
		}
		if (getThis == null)
			return false;
		String name = CMLib.english().cleanArticles(getThis.name());
		CMMsg msg = CMClass.getMsg(mob, getThis, null, CMMsg.MSG_NOISYMOVEMENT,
				"<S-NAME> package(s) up " + V.size() + " <T-NAMENOART>(s).");
		if (mob.location().okMessage(mob, msg)) {
			mob.location().send(mob, msg);
			thePackage.setName(name);
			if (thePackage.packageMe(getThis, V.size())) {
				for (int i = 0; i < V.size(); i++)
					V.elementAt(i).destroy();
				mob.location().addItem(thePackage,
						ItemPossessor.Expire.Player_Drop);
				mob.location().recoverRoomStats();
				mob.location().recoverRoomStats();
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
