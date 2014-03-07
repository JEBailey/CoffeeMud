package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Food;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
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
public class Chant_SummonFood extends Chant {
	public String ID() {
		return "Chant_SummonFood";
	}

	public String name() {
		return "Summon Food";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {

		if (((mob.location().domainType() & Room.INDOORS) > 0) && (!auto)) {
			mob.tell("You must be outdoors to try this.");
			return false;
		}
		if ((mob.location().domainType() == Room.DOMAIN_OUTDOORS_CITY)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_SPACEPORT)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_UNDERWATER)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_AIR)
				|| (mob.location().domainType() == Room.DOMAIN_OUTDOORS_WATERSURFACE)) {
			mob.tell("This magic will not work here.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		// now see if it worked
		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto), auto ? ""
							: "^S<S-NAME> chant(s) to the ground.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Food newItem = null;
				int berryType = RawMaterial.CODES.BERRIES()[CMLib.dice().roll(
						1, RawMaterial.CODES.BERRIES().length, -1)];
				for (int i = 0; i < ((adjustedLevel(mob, asLevel) / 4) + 1); i++) {
					newItem = (Food) CMClass.getBasicItem("GenFoodResource");
					newItem.setName("some "
							+ RawMaterial.CODES.NAME(berryType).toLowerCase());
					newItem.setDisplayText(CMStrings.capitalizeAndLower(newItem
							.name()) + " are growing here.");
					newItem.setDescription("These little berries look juicy and good.");
					newItem.setMaterial(berryType);
					newItem.setNourishment(150 + (10 * super.getX1Level(mob)));
					newItem.setBaseValue(1);
					CMLib.materials().addEffectsToResource(newItem);
					newItem.setMiscText(newItem.text());
					mob.location().addItem(newItem,
							ItemPossessor.Expire.Resource);
				}
				if (newItem != null)
					mob.location().showHappens(
							CMMsg.MSG_OK_ACTION,
							CMStrings.capitalizeAndLower(newItem.name())
									+ " quickly begin to grow here.");
				mob.location().recoverPhyStats();
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s) to the ground, but nothing happens.");

		// return whether it worked
		return success;
	}
}