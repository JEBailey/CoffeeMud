package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
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
public class Prayer_DivineGuidance extends Prayer {
	public String ID() {
		return "Prayer_DivineGuidance";
	}

	public String name() {
		return "Divine Guidance";
	}

	public String displayText() {
		return "(Awaiting Divine Guidance)";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_OTHERS;
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_COMMUNING;
	}

	public long flags() {
		return Ability.FLAG_HOLY;
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (!(affected instanceof MOB))
			return;
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_GOOD);
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
				+ 10 + (2 * getXLEVELLevel(invoker())));
		affectableStats.setDamage(affectableStats.damage() + 5
				+ getXLEVELLevel(invoker()));
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (affected instanceof MOB) {
			MOB mob = (MOB) affected;
			if (canBeUninvoked())
				mob.tell("You have received your divine guidance.");
		}
		super.unInvoke();
	}

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		super.executeMsg(myHost, msg);
		if ((invoker == null) || (!(affected instanceof MOB)))
			return;
		if (msg.amISource((MOB) affected)
				&& (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (!msg.amITarget(affected)) && (msg.tool() instanceof Weapon)
				&& (msg.value() > 0)) {
			unInvoke();
		}
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
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					somanticCastCode(mob, target, auto),
					(auto ? "<T-NAME> await(s) divine guidance!"
							: "^S<S-NAME> " + prayForWord(mob)
									+ " to give <T-NAME> divine guidance.^?")
							+ CMLib.protocol().msp("bless.wav", 10));
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
				target.recoverPhyStats();
				target.location().recoverRoomStats();
			}
		} else
			return beneficialWordsFizzle(mob, target, "<S-NAME> "
					+ prayWord(mob)
					+ " for divine guidance, but <S-IS-ARE> not heard.");
		// return whether it worked
		return success;
	}
}
