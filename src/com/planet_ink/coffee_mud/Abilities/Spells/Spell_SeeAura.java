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
public class Spell_SeeAura extends Spell {
	public String ID() {
		return "Spell_SeeAura";
	}

	public String name() {
		return "See Aura";
	}

	public int enchantQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;
		if (target == mob)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		// it worked, so build a copy of this ability,
		// and add it to the affects list of the
		// affected MOB. Then tell everyone else
		// what happened.
		CMMsg msg = CMClass
				.getMsg(mob,
						target,
						this,
						verbalCastCode(mob, target, auto),
						auto ? ""
								: "^SYou draw out <T-NAME>s aura, seeing <T-HIM-HER> from the inside out...^?",
						verbalCastCode(mob, target, auto), auto ? ""
								: "^S<S-NAME> draw(s) out your aura.^?",
						verbalCastCode(mob, target, auto), auto ? ""
								: "^S<S-NAME> draws out <T-NAME>s aura.^?");
		if (success) {
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				StringBuilder str = CMLib.commands().getScore(target);
				if (!mob.isMonster())
					mob.session().wraplessPrintln(str.toString());
			}
		} else
			beneficialVisualFizzle(mob, target,
					"<S-NAME> examine(s) <T-NAME>, incanting, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}