package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.ShopKeeper;

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
public class View extends StdCommand {
	public View() {
	}

	private final String[] access = { "VIEW" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		Environmental shopkeeper = CMLib.english().parseShopkeeper(mob,
				commands, "View what merchandise from whom?");
		if (shopkeeper == null)
			return false;
		if (commands.size() == 0) {
			mob.tell("View what merchandise?");
			return false;
		}

		if (CMLib.coffeeShops().getShopKeeper(shopkeeper) == null) {
			mob.tell(shopkeeper.name() + " is not a shopkeeper!");
			return false;
		}

		int maxToDo = Integer.MAX_VALUE;
		if ((commands.size() > 1)
				&& (CMath.s_int((String) commands.firstElement()) > 0)) {
			maxToDo = CMath.s_int((String) commands.firstElement());
			commands.setElementAt("all", 0);
		}

		String whatName = CMParms.combine(commands, 0);
		Vector V = new Vector();
		boolean allFlag = ((String) commands.elementAt(0))
				.equalsIgnoreCase("all");
		if (whatName.toUpperCase().startsWith("ALL.")) {
			allFlag = true;
			whatName = "ALL " + whatName.substring(4);
		}
		if (whatName.toUpperCase().endsWith(".ALL")) {
			allFlag = true;
			whatName = "ALL " + whatName.substring(0, whatName.length() - 4);
		}
		int addendum = 1;
		boolean doBugFix = true;
		while (doBugFix || ((allFlag) && (addendum <= maxToDo))) {
			doBugFix = false;
			ShopKeeper SK = CMLib.coffeeShops().getShopKeeper(shopkeeper);
			Environmental itemToDo = SK.getShop().getStock(whatName, mob);
			if (itemToDo == null)
				break;
			if (CMLib.flags().canBeSeenBy(itemToDo, mob))
				V.addElement(itemToDo);
			if (addendum >= CMLib.coffeeShops().getShopKeeper(shopkeeper)
					.getShop().numberInStock(itemToDo))
				break;
			addendum++;
		}

		if (V.size() == 0)
			mob.tell(mob, shopkeeper, null,
					"<T-NAME> do(es)n't appear to have any '" + whatName
							+ "' for sale.  Try LIST.");
		else
			for (int v = 0; v < V.size(); v++) {
				Environmental thisThang = (Environmental) V.elementAt(v);
				CMMsg newMsg = CMClass.getMsg(mob, shopkeeper, thisThang,
						CMMsg.MSG_VIEW, null);
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
