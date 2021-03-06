package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
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
public class Chant_Sunray extends Chant {
	public String ID() {
		return "Chant_Sunray";
	}

	public String name() {
		return "Sunray";
	}

	public String displayText() {
		return "(Sunray)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected == null)
			return;
		if (!(affected instanceof MOB))
			return;

		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_NOT_SEE);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("Your vision returns.");
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (!mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
			if (target instanceof MOB) {
				if (((MOB) target).charStats().getBodyPart(Race.BODY_EYE) == 0)
					return Ability.QUALITY_INDIFFERENT;
				if (!CMLib.flags().canSee((MOB) target))
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

		if ((!auto) && (target.charStats().getBodyPart(Race.BODY_EYE) == 0)) {
			mob.tell(target.name(mob)
					+ " has no eyes, and would not be affected.");
			return false;
		}

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
									: "^S<S-NAME> chant(s) to <T-NAMESELF>. A beam of bright sunlight flashes into <T-HIS-HER> eyes!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
							"<S-NAME> go(es) blind!");
					maliciousAffect(mob, target, asLevel, 0, -1);
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> chant(s) to <T-NAMESELF>, but the magic fades.");

		// return whether it worked
		return success;
	}
}
