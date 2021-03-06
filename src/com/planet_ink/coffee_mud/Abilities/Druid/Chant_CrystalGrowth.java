package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.ItemCraftor;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Chant_CrystalGrowth extends Chant {
	public String ID() {
		return "Chant_CrystalGrowth";
	}

	public String name() {
		return "Crystal Growth";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_ROCKCONTROL;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int overrideMana() {
		return 50;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (mob.location().domainType() != Room.DOMAIN_INDOORS_CAVE) {
			mob.tell("This magic will not work here.");
			return false;
		}
		int material = RawMaterial.RESOURCE_CRYSTAL;
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		// now see if it worked
		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto), auto ? ""
							: "^S<S-NAME> chant(s) to the cave walls.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);

				ItemCraftor A = null;
				switch (CMLib.dice().roll(1, 10, 0)) {
				case 1:
				case 2:
				case 3:
				case 4:
					A = (ItemCraftor) CMClass.getAbility("Blacksmithing");
					break;
				case 5:
				case 6:
				case 7:
					A = (ItemCraftor) CMClass.getAbility("Armorsmithing");
					break;
				case 8:
				case 9:
				case 10:
					A = (ItemCraftor) CMClass.getAbility("Weaponsmithing");
					break;
				}
				ItemCraftor.ItemKeyPair pair = null;
				if (A != null)
					pair = A.craftAnyItem(material);
				if (pair == null) {
					mob.tell("The chant failed for some reason...");
					return false;
				}
				Item building = pair.item;
				Item key = pair.key;
				mob.location().addItem(building, ItemPossessor.Expire.Resource);
				if (key != null)
					mob.location().addItem(key, ItemPossessor.Expire.Resource);
				Ability A2 = CMClass.getAbility("Chant_Brittle");
				if (A2 != null)
					building.addNonUninvokableEffect(A2);

				mob.location()
						.showHappens(
								CMMsg.MSG_OK_ACTION,
								"a tiny crystal fragment drops out of the stone, swells and grows, forming into "
										+ building.name() + ".");
				mob.location().recoverPhyStats();
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s) to the walls, but nothing happens.");

		// return whether it worked
		return success;
	}
}
