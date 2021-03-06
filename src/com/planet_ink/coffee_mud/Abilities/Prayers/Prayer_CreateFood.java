package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

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
public class Prayer_CreateFood extends Prayer {
	public String ID() {
		return "Prayer_CreateFood";
	}

	public String name() {
		return "Create Food";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CREATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		// now see if it worked
		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto), auto ? "" : "^S<S-NAME> "
							+ prayWord(mob) + " for food.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Item newItem = CMClass.getBasicItem("StdFood");
				newItem.setBaseValue(1);
				mob.location().addItem(newItem, ItemPossessor.Expire.Resource);
				mob.location().showHappens(CMMsg.MSG_OK_ACTION,
						"Suddenly, " + newItem.name() + " drops from the sky.");
				mob.location().recoverPhyStats();
			}
		} else
			return beneficialWordsFizzle(mob, null, "<S-NAME> " + prayWord(mob)
					+ " for food, but there is no answer.");

		// return whether it worked
		return success;
	}
}
