package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class Prayer_Invigorate extends Prayer implements MendingSkill {
	public String ID() {
		return "Prayer_Invigorate";
	}

	public String name() {
		return "Invigorate";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_RESTORATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	public long flags() {
		return Ability.FLAG_HOLY;
	}

	protected long minCastWaitTime() {
		return CMProps.getTickMillis() / 2;
	}

	public boolean supportsMending(Physical item) {
		return (item instanceof MOB)
				&& (((((MOB) item).curState()).getFatigue() > 0) || ((((MOB) item)
						.curState()).getMovement() < (((MOB) item).maxState())
						.getMovement()));
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (!supportsMending(target))
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
							verbalCastCode(mob, target, auto),
							auto ? "A soft white glow surrounds <T-NAME>."
									: "^S<S-NAME> "
											+ prayWord(mob)
											+ ", delivering a strong invigorating touch to <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				int healing = CMLib.dice().roll(10,
						adjustedLevel(mob, asLevel), 50);
				target.curState().setFatigue(0);
				target.curState().adjMovement(healing, target.maxState());
				target.tell("You feel really invigorated!");
				lastCastHelp = System.currentTimeMillis();
			}
		} else
			beneficialWordsFizzle(mob, target, auto ? "" : "<S-NAME> "
					+ prayWord(mob) + " for <T-NAMESELF>, but nothing happens.");
		// return whether it worked
		return success;
	}
}