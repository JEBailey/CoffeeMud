package com.planet_ink.coffee_mud.Commands;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.Banker;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class Borrow extends StdCommand {
	public Borrow() {
	}

	private final String[] access = { "BORROW" };

	public String[] getAccessWords() {
		return access;
	}

	public boolean execute(MOB mob, Vector commands, int metaFlags)
			throws java.io.IOException {
		Environmental shopkeeper = CMLib.english().parseShopkeeper(mob,
				commands, "Borrow how much from whom?");
		if (shopkeeper == null)
			return false;
		ShopKeeper SHOP = CMLib.coffeeShops().getShopKeeper(shopkeeper);
		if (!(SHOP instanceof Banker)) {
			mob.tell("You can not borrow from " + shopkeeper.name() + ".");
			return false;
		}
		if (commands.size() == 0) {
			mob.tell("Borrow how much?");
			return false;
		}
		String str = CMParms.combine(commands, 0);
		if (str.equalsIgnoreCase("all"))
			str = "" + Integer.MAX_VALUE;
		long numCoins = CMLib.english().numPossibleGold(null, str);
		String currency = CMLib.english().numPossibleGoldCurrency(shopkeeper,
				str);
		double denomination = CMLib.english().numPossibleGoldDenomination(
				shopkeeper, currency, str);
		Item thisThang = null;
		if ((numCoins == 0) || (denomination == 0.0)) {
			mob.tell("Borrow how much?");
			return false;
		}
		thisThang = CMLib.beanCounter().makeCurrency(currency, denomination,
				numCoins);

		if ((thisThang == null) || (!CMLib.flags().canBeSeenBy(thisThang, mob))) {
			mob.tell("That doesn't appear to be available.  Try LIST.");
			return false;
		}
		String str2 = "<S-NAME> borrow(s) <O-NAME> from " + shopkeeper.name()
				+ ".";
		CMMsg newMsg = CMClass.getMsg(mob, shopkeeper, thisThang,
				CMMsg.MSG_BORROW, str2);
		if (!mob.location().okMessage(mob, newMsg))
			return false;
		mob.location().send(mob, newMsg);
		return false;
	}

	public double combatActionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getCombatActionCost(ID());
	}

	public double actionsCost(final MOB mob, final List<String> cmds) {
		return CMProps.getActionCost(ID());
	}

	public boolean canBeOrdered() {
		return false;
	}
}
