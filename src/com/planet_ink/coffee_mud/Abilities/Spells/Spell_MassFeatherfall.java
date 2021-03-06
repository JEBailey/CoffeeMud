package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Iterator;
import java.util.Set;
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
public class Spell_MassFeatherfall extends Spell {
	public String ID() {
		return "Spell_MassFeatherfall";
	}

	public String name() {
		return "Mass FeatherFall";
	}

	public String displayText() {
		return "";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	protected int canAffectCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Set<MOB> h = properTargets(mob, givenTarget, false);
		if (h == null) {
			mob.tell("There doesn't appear to be anyone here worth floating.");
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
			if (mob.location()
					.show(mob,
							null,
							this,
							verbalCastCode(mob, null, auto),
							auto ? ""
									: "^S<S-NAME> wave(s) <S-HIS-HER> arms and speak(s) lightly.^?"))
				for (Iterator f = h.iterator(); f.hasNext();) {
					MOB target = (MOB) f.next();

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB. Then tell everyone else
					// what happened.
					CMMsg msg = CMClass.getMsg(mob, target, this,
							verbalCastCode(mob, target, auto), null);
					if (mob.location().okMessage(mob, msg)) {
						mob.location().send(mob, msg);
						Spell_FeatherFall fall = new Spell_FeatherFall();
						fall.setProficiency(proficiency());
						fall.beneficialAffect(mob, target, asLevel, 0);
					}
				}
		} else
			return beneficialWordsFizzle(
					mob,
					null,
					"<S-NAME> wave(s) <S-HIS-HER> arms and speak(s) lightly, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
