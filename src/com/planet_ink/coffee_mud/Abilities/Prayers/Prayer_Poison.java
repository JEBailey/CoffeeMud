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
public class Prayer_Poison extends Prayer {
	public String ID() {
		return "Prayer_Poison";
	}

	public String name() {
		return "Unholy Poison";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CORRUPTION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	public String displayText() {
		return "";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (target.fetchEffect("Poison") != null)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
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
							verbalCastCode(mob, target, auto)
									| CMMsg.MASK_MALICIOUS,
							auto ? ""
									: "^S<S-NAME> inflict(s) an unholy poison upon <T-NAMESELF>.^?");
			CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS
					| CMMsg.TYP_POISON, null);
			if (mob.location().okMessage(mob, msg)
					&& mob.location().okMessage(mob, msg2)) {
				mob.location().send(mob, msg);
				mob.location().send(mob, msg2);
				if ((msg.value() <= 0) && (msg2.value() <= 0)) {
					Ability A = CMClass.getAbility("Poison");
					A.invoke(mob, target, true, asLevel);
				}
			}
		} else
			return maliciousFizzle(
					mob,
					target,
					"<S-NAME> attempt(s) to inflict an unholy poison upon <T-NAMESELF>, but flub(s) it.");

		// return whether it worked
		return success;
	}
}
