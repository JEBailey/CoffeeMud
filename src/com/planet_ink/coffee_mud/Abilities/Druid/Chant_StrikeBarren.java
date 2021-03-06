package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Chant_StrikeBarren extends Chant {
	public String ID() {
		return "Chant_StrikeBarren";
	}

	public String name() {
		return "Strike Barren";
	}

	public String displayText() {
		return "(Striken Barren)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		if (canBeUninvoked())
			mob.tell("Your mystical barrenness fades.");

		super.unInvoke();

	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return super.okMessage(myHost, msg);
		MOB mob = (MOB) affected;

		if ((msg.amITarget(mob)) && (msg.tool() instanceof Ability)
				&& (((Ability) msg.tool()).ID().equalsIgnoreCase("Pregnancy"))
				&& (!mob.amDead()))
			return false;
		return super.okMessage(myHost, msg);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = super.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if ((success)
				&& (target.charStats().getStat(CharStats.STAT_GENDER) == 'F')) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> become(s) barren!"
									: "^S<S-NAME> chant(s) at <T-NAMESELF>, striking <T-HIM-HER> barren!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel,
						Ability.TICKS_ALMOST_FOREVER);
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) at <T-NAMESELF>, but nothing happens.");

		return success;
	}
}
