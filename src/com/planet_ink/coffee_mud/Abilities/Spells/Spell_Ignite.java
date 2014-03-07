package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Spell_Ignite extends Spell {
	public String ID() {
		return "Spell_Ignite";
	}

	public String name() {
		return "Ignite";
	}

	public String displayText() {
		return "Ignite";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canTargetCode() {
		return CAN_ITEMS | CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
	}

	public void ignite(MOB mob, Item I) {
		int durationOfBurn = 5;
		switch (I.material() & RawMaterial.MATERIAL_MASK) {
		case RawMaterial.MATERIAL_LEATHER:
			durationOfBurn = 20 + I.phyStats().weight();
			break;
		case RawMaterial.MATERIAL_CLOTH:
		case RawMaterial.MATERIAL_SYNTHETIC:
		case RawMaterial.MATERIAL_PAPER:
			durationOfBurn = 5 + I.phyStats().weight();
			break;
		case RawMaterial.MATERIAL_WOODEN:
			durationOfBurn = 40 + (I.phyStats().weight() * 2);
			break;
		default:
			switch (I.material()) {
			case RawMaterial.RESOURCE_COAL:
				durationOfBurn = 20 * (1 + I.phyStats().weight() * 3);
				break;
			case RawMaterial.RESOURCE_LAMPOIL:
				durationOfBurn = 5 + I.phyStats().weight();
				break;
			default:
				return;
			}
			break;
		}
		mob.location().showHappens(CMMsg.MSG_OK_VISUAL, I.name() + " ignites!");
		Ability B = CMClass.getAbility("Burning");
		if (B != null)
			B.invoke(mob, I, true, durationOfBurn);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = getAnyTarget(mob, commands, givenTarget,
				Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;
		if ((!(target instanceof MOB)) && (!(target instanceof Item))) {
			mob.tell("You can't ignite '" + target.name(mob) + "'!");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			invoker = mob;
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> flares up!"
									: "^S<S-NAME> evoke(s) a spell upon <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					if (target instanceof Item)
						ignite(mob, (Item) target);
					else if (target instanceof MOB) {
						MOB mob2 = (MOB) target;
						for (int i = 0; i < mob2.numItems(); i++) {
							Item I = mob2.getItem(i);
							if ((I != null) && (I.container() == null))
								ignite(mob2, I);
						}
					}
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> evoke(s) at <T-NAMESELF>, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}