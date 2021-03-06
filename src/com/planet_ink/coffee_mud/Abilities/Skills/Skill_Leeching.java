package com.planet_ink.coffee_mud.Abilities.Skills;

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
public class Skill_Leeching extends StdSkill {
	public String ID() {
		return "Skill_Leeching";
	}

	public String name() {
		return "Leeching";
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings = { "LEECH", "LEECHING" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_ANATOMY;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if ((!auto) && (mob != target) && (!target.willFollowOrdersOf(mob))) {
			mob.tell(target.charStats().HeShe()
					+ " must be a follower for you to leech them.");
			return false;
		}
		if (mob.isInCombat() && (mob.getVictim() == target)
				&& (mob.rangeToTarget() > 0)) {
			mob.tell("You are too far away to try that!");
			return false;
		}

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
							CMMsg.MSG_DELICATE_SMALL_HANDS_ACT
									| (auto ? CMMsg.MASK_ALWAYS : 0),
							auto ? ""
									: "^S<S-NAME> carefully applie(s) leeches to the skin of <T-NAME>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Ability A = CMClass.getAbility("Disease_Leeches");
				if (A != null)
					A.invoke(mob, target, true, adjustedLevel(mob, 0));

			}
		} else
			beneficialWordsFizzle(
					mob,
					target,
					auto ? ""
							: "<S-NAME> attempt(s) to apply leeches to <T-NAME>, but fail(s).");

		// return whether it worked
		return success;
	}
}
