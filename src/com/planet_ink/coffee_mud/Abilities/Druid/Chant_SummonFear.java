package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Iterator;
import java.util.Set;
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
public class Chant_SummonFear extends Chant {
	public String ID() {
		return "Chant_SummonFear";
	}

	public String name() {
		return "Summon Fear";
	}

	public String displayText() {
		return "(Afraid)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(1);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			Set<MOB> h = properTargets(mob, target, false);
			if (h == null)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Set<MOB> h = properTargets(mob, givenTarget, auto);
		if (h == null) {
			mob.tell("There doesn't appear to be anyone here worth scaring.");
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
			for (Iterator f = h.iterator(); f.hasNext();) {
				MOB target = (MOB) f.next();

				// it worked, so build a copy of this ability,
				// and add it to the affects list of the
				// affected MOB. Then tell everyone else
				// what happened.
				CMMsg msg = CMClass
						.getMsg(mob, target, this,
								verbalCastCode(mob, target, auto),
								"^S<S-NAME> frighten(s) <T-NAMESELF> with <S-HIS-HER> chant.^?");
				CMMsg msg2 = CMClass.getMsg(mob, target, this,
						verbalCastMask(mob, target, auto) | CMMsg.TYP_MIND,
						null);
				if ((mob.location().okMessage(mob, msg))
						&& ((mob.location().okMessage(mob, msg2)))) {
					mob.location().send(mob, msg);
					if (msg.value() <= 0) {
						mob.location().send(mob, msg2);
						if (msg2.value() <= 0) {
							invoker = mob;
							CMLib.commands().postFlee(target, "");
						}
					}
				}
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s) in a frightening way, but the magic fades.");

		// return whether it worked
		return success;
	}
}
