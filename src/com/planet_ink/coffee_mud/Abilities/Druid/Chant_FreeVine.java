package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Chant_FreeVine extends Chant {
	public String ID() {
		return "Chant_FreeVine";
	}

	public String name() {
		return "Free Vine";
	}

	public String displayText() {
		return "";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTCONTROL;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if ((affected != null) && (affected instanceof MOB)
				&& (msg.amISource((MOB) affected))) {
			if (((msg.targetMinor() == CMMsg.TYP_LEAVE)
					|| (msg.sourceMinor() == CMMsg.TYP_ADVANCE) || (msg
						.sourceMinor() == CMMsg.TYP_RETREAT)))
				unInvoke();
		}
		super.executeMsg(myHost, msg);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!target.charStats().getMyRace().ID().equals("Vine")) {
			mob.tell(target.name(mob) + " can not be uprooted.");
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
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> chant(s)freely to <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
							"<S-NAME> pull(s) <S-HIS-HER> roots up!");
					beneficialAffect(mob, target, asLevel, 0);
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> chant(s) freely to <T-NAMESELF>, but the magic fades");

		// return whether it worked
		return success;
	}
}
