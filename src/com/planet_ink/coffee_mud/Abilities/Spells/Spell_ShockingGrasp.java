package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Spell_ShockingGrasp extends Spell {
	public String ID() {
		return "Spell_ShockingGrasp";
	}

	public String name() {
		return "Shocking Grasp";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
	}

	public long flags() {
		return Ability.FLAG_AIRBASED;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

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
			CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_HANDS
					| verbalCastCode(mob, target, auto), (auto ? ""
					: "^S<S-NAME> grab(s) at <T-NAMESELF>.^?")
					+ CMLib.protocol().msp("shock.wav", 40));
			CMMsg msg2 = CMClass.getMsg(mob, target, this,
					CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_ELECTRIC
							| (auto ? CMMsg.MASK_ALWAYS : 0), null);
			if ((target.location().okMessage(mob, msg))
					&& ((target.location().okMessage(mob, msg2)))) {
				target.location().send(mob, msg);
				if (msg.value() <= 0) {
					target.location().send(mob, msg2);
					if (msg2.value() <= 0) {
						invoker = mob;
						int damage = CMLib.dice().roll(
								1,
								8,
								(adjustedLevel(mob, asLevel) + (2 * super
										.getX1Level(mob))) / 2);
						CMLib.combat()
								.postDamage(
										mob,
										target,
										this,
										damage,
										CMMsg.MASK_ALWAYS | CMMsg.TYP_ELECTRIC,
										Weapon.TYPE_STRIKING,
										auto ? "<T-NAME> gasp(s) in shock and pain!"
												: "The shocking grasp <DAMAGES> <T-NAME>!");
					}
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> grab(s) at <T-NAMESELF>, but fizzle(s) the spell.");

		// return whether it worked
		return success;
	}
}
