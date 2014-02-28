package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
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
public class Prayer_GreatCurse extends Prayer {
	public String ID() {
		return "Prayer_GreatCurse";
	}

	public String name() {
		return "Great Curse";
	}

	public String displayText() {
		return "(Cursed)";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected == null)
			return;
		if (!(affected instanceof MOB))
			return;

		int xlvl = super.getXLEVELLevel(invoker());
		affectableStats.setArmor(affectableStats.armor() + 10 + (4 * xlvl));
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
				- 10 - (2 * xlvl));
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("The great curse is lifted.");
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
					verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS,
					auto ? "<T-NAME> <T-IS-ARE> horribly cursed!"
							: "^S<S-NAME> curse(s) <T-NAMESELF> horribly.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					Item I = Prayer_Curse.getSomething(mob, true);
					if (I != null) {
						Prayer_Curse.endLowerBlessings(I, CMLib.ableMapper()
								.lowestQualifyingLevel(ID()));
						I.recoverPhyStats();
					}
					Prayer_Curse.endLowerBlessings(target, CMLib.ableMapper()
							.lowestQualifyingLevel(ID()));
					success = maliciousAffect(mob, target, asLevel, 0, -1);
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> attempt(s) to horribly curse <T-NAMESELF> , but nothing happens.");

		// return whether it worked
		return success;
	}
}
