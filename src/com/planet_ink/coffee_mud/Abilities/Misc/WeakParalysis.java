package com.planet_ink.coffee_mud.Abilities.Misc;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
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
public class WeakParalysis extends StdAbility {
	public String ID() {
		return "WeakParalysis";
	}

	public String name() {
		return "Weak Paralysis";
	}

	public String displayText() {
		return "(Paralyzed)";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public boolean putInCommandlist() {
		return false;
	}

	private static final String[] triggerStrings = { "WPARALYZE" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL;
	}

	public long flags() {
		return Ability.FLAG_PARALYZING | Ability.FLAG_UNHOLY;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected == null)
			return;
		if (!(affected instanceof MOB))
			return;

		affectableStats.setSensesMask(affectableStats.sensesMask()
				| PhyStats.CAN_NOT_MOVE);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("The paralysis eases out of your muscles.");
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
			CMMsg msg = CMClass.getMsg(mob, target, this,
					CMMsg.MSK_MALICIOUS_MOVE | CMMsg.TYP_PARALYZE
							| (auto ? CMMsg.MASK_ALWAYS : 0), auto ? ""
							: "^S<S-NAME> paralyze(s) <T-NAMESELF>.^?");
			if (target.location().okMessage(target, msg)) {
				target.location().send(target, msg);
				if (msg.value() <= 0) {
					success = maliciousAffect(mob, target, asLevel, 5, -1);
					mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
							"<S-NAME> can't move!");
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> attempt(s) to paralyze <T-NAMESELF>, but fail(s)!");

		// return whether it worked
		return success;
	}
}