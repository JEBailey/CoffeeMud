package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Chant_SensePregnancy extends Chant {
	public String ID() {
		return "Chant_SensePregnancy";
	}

	public String name() {
		return "Sense Pregnancy";
	}

	protected int canAffectCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	protected int overrideMana() {
		return 5;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
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
									: "^S<S-NAME> chant(s) over <T-YOUPOSS> stomach.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Ability A = target.fetchEffect("Pregnancy");
				if ((A == null) || (A.displayText().length() == 0))
					mob.tell(target.name(mob) + " is not pregnant.");
				else {
					String s = A.displayText();
					if (s.startsWith("("))
						s = s.substring(1);
					if (s.endsWith(")"))
						s = s.substring(0, s.length() - 1);
					mob.tell(target.name(mob) + " is " + s + ".");
				}
			}
		} else
			return beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) over <T-YOUPOSS> stomach, but the magic fades.");

		// return whether it worked
		return success;
	}
}
