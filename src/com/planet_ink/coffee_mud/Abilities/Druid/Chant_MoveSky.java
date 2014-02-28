package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.TimeClock;
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
public class Chant_MoveSky extends Chant {
	public String ID() {
		return "Chant_MoveSky";
	}

	public String name() {
		return "Move The Sky";
	}

	public String displayText() {
		return "";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_MOONSUMMONING;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	protected int overrideMana() {
		return Ability.COST_ALL - 99;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
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
							null,
							this,
							verbalCastCode(mob, null, auto),
							auto ? ""
									: "^S<S-NAME> chant(s), and the sky starts moving.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (mob.location().getArea().getTimeObj().getTODCode() == TimeClock.TIME_NIGHT) {
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,
							"The moon begin(s) to descend!");
					int x = mob.location().getArea().getTimeObj()
							.getHoursInDay()
							- mob.location().getArea().getTimeObj()
									.getTimeOfDay();
					mob.location().getArea().getTimeObj().tickTock(x);
				} else {
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,
							"The sun hurries towards the horizon!");
					int x = mob.location().getArea().getTimeObj()
							.getDawnToDusk()[TimeClock.TIME_NIGHT]
							- mob.location().getArea().getTimeObj()
									.getTimeOfDay();
					mob.location().getArea().getTimeObj().tickTock(x);
				}
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s), but the magic fades");

		// return whether it worked
		return success;
	}
}
