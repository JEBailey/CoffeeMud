package com.planet_ink.coffee_mud.Abilities.Prayers;

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
public class Prayer_Cleanliness extends Prayer {
	public String ID() {
		return "Prayer_Cleanliness";
	}

	public String name() {
		return "Cleanliness";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
	}

	public int castingQuality(MOB mob, Physical target) {
		if ((mob != null) && (target instanceof MOB))
			return Ability.QUALITY_INDIFFERENT;
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
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
							auto ? "A bright white glow surrounds <T-NAME>."
									: "^S<S-NAME> "
											+ prayWord(mob)
											+ ", delivering a strong touch of divine cleanliness to <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if ((target.playerStats() != null)
						&& (target.playerStats().getHygiene() > 0))
					target.playerStats().setHygiene(0);
				target.tell("You feel clean!");
			}
		} else
			beneficialWordsFizzle(mob, target, auto ? "" : "<S-NAME> "
					+ prayWord(mob) + " for <T-NAMESELF>, but nothing happens.");
		// return whether it worked
		return success;
	}
}
