package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Spell_Spook extends Spell {
	public String ID() {
		return "Spell_Spook";
	}

	public String name() {
		return "Spook";
	}

	public String displayText() {
		return "(Spooked)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
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
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> scare(s) <T-NAMESELF>.^?");
			CMMsg msg2 = CMClass.getMsg(mob, target, this,
					CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND
							| (auto ? CMMsg.MASK_ALWAYS : 0), null);
			if ((mob.location().okMessage(mob, msg))
					&& ((mob.location().okMessage(mob, msg2)))) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					mob.location().send(mob, msg2);
					if (msg2.value() <= 0) {
						if (target.location() == mob.location()) {
							target.location().show(target, null,
									CMMsg.MSG_OK_ACTION,
									"<S-NAME> shake(s) in fear!");
							invoker = mob;
							CMLib.commands().postFlee(target, "");
						}
					}
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> attempt(s) to scare <T-NAMESELF>, but fizzle(s) the spell.");

		// return whether it worked
		return success;
	}
}
