package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
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
public class Prayer_ProtCold extends Prayer {
	public String ID() {
		return "Prayer_ProtCold";
	}

	public String name() {
		return "Protection Cold";
	}

	public String displayText() {
		return "(Protection from Cold)";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_HOLYPROTECTION;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY | Ability.FLAG_HEATING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
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
			mob.tell("Your warm protection warms up.");

		super.unInvoke();

	}

	public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
		super.affectCharStats(affectedMOB, affectedStats);
		affectedStats.setStat(CharStats.STAT_SAVE_COLD,
				affectedStats.getStat(CharStats.STAT_SAVE_COLD) + 100);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = getTarget(mob, commands, givenTarget);
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;
		if (target == null)
			return false;

		if (target.fetchEffect(this.ID()) != null) {
			mob.tell(target, null, null,
					"<S-NAME> <S-IS-ARE> already protected from cold.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "A warm field of protection appears around <T-NAME>."
									: "^S<S-NAME> "
											+ prayWord(mob)
											+ " for a warm field of protection around <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialWordsFizzle(mob, target, "<S-NAME> " + prayWord(mob)
					+ " for cold protection, but fail(s).");

		return success;
	}
}
