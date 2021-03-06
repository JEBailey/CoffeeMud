package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Spell_WeaknessGas extends Spell {
	public String ID() {
		return "Spell_WeaknessGas";
	}

	public String name() {
		return "Weakness to Gas";
	}

	public String displayText() {
		return "(Weakness to Gas)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_TRANSMUTATION;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!super.okMessage(myHost, msg))
			return false;

		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;
		if ((msg.amITarget(mob)) && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (msg.sourceMinor() == CMMsg.TYP_GAS)) {
			int recovery = (int) Math.round(CMath.mul((msg.value()), 1.5));
			msg.setValue(msg.value() + recovery);
		}
		return true;
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		if (canBeUninvoked())
			mob.tell("Your weakness to gasses is now gone.");

		super.unInvoke();

	}

	public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
		super.affectCharStats(affectedMOB, affectedStats);
		affectedStats.setStat(CharStats.STAT_SAVE_GAS,
				affectedStats.getStat(CharStats.STAT_SAVE_GAS) - 100);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "A shimmering porous field appears around <T-NAMESELF>."
									: "^S<S-NAME> invoke(s) a porous field around <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0)
					success = maliciousAffect(mob, target, asLevel, 0, -1);
			}
		} else
			maliciousFizzle(mob, target,
					"<S-NAME> attempt(s) to invoke weakness to gas, but fail(s).");

		return success;
	}
}
