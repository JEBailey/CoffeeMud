package com.planet_ink.coffee_mud.Abilities.Druid;

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
public class Chant_SpeedTime extends Chant {
	public String ID() {
		return "Chant_SpeedTime";
	}

	public String name() {
		return "Speed Time";
	}

	public String displayText() {
		return "(Speed Time)";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_MOONSUMMONING;
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

	protected int overrideMana() {
		return 100;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto),
					auto ? "Something is happening!"
							: "^S<S-NAME> begin(s) to chant...^?");
			if (mob.location().okMessage(mob, msg)) {
				int mana = mob.curState().getMana();
				mob.location().send(mob, msg);
				for (int i = 0; i < (adjustedLevel(mob, asLevel) / 2); i++)
					CMLib.threads().tickAllTickers(mob.location());
				if (mob.curState().getMana() > mana)
					mob.curState().setMana(mana);
				mob.location().show(mob, null, this,
						verbalCastCode(mob, null, auto),
						auto ? "It stops." : "^S<S-NAME> stop(s) chanting.^?");
			}
		} else
			beneficialVisualFizzle(mob, null,
					"<S-NAME> chant(s), but nothing happens.");

		return success;
	}
}
