package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Chant_AlterTime extends Chant {
	public String ID() {
		return "Chant_AlterTime";
	}

	public String name() {
		return "Alter Time";
	}

	public String displayText() {
		return "";
	}

	public int overrideMana() {
		return 100;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_MOONSUMMONING;
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
									: "^S<S-NAME> chant(s), and reality seems to start blurring.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				int x = CMath.s_int(text());
				while (x == 0)
					x = CMLib.dice().roll(1, 3, -2);
				if (x > 0)
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,
							"Time moves forwards!");
				else
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,
							"Time moves backwards!");
				mob.location().getArea().getTimeObj().tickTock(x);
			}
		} else
			return beneficialWordsFizzle(mob, null,
					"<S-NAME> chant(s), but the magic fades");

		// return whether it worked
		return success;
	}
}