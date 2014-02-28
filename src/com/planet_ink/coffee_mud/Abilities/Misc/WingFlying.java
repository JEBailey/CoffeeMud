package com.planet_ink.coffee_mud.Abilities.Misc;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.HealthCondition;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

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
public class WingFlying extends StdAbility implements HealthCondition {
	public String ID() {
		return "WingFlying";
	}

	public String name() {
		return "Winged Flight";
	}

	public String displayText() {
		return "";
	}

	protected int canAffectCode() {
		return CAN_MOBS;
	}

	protected int canTargetCode() {
		return CAN_MOBS;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean putInCommandlist() {
		return false;
	}

	private static final String[] triggerStrings = { "FLAP" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_RACIALABILITY;
	}

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	@Override
	public String getHealthConditionDesc() {
		return "Weak Paralysis";
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (affected == null)
			return;
		if (!(affected instanceof MOB))
			return;

		if (!CMLib.flags().isSleeping(affected))
			affectableStats.setDisposition(affectableStats.disposition()
					| PhyStats.IS_FLYING);
		else
			affectableStats.setDisposition(CMath.unsetb(
					affectableStats.disposition(), PhyStats.IS_FLYING));
	}

	public boolean tick(Tickable ticking, int tickID) {
		if ((tickID == Tickable.TICKID_MOB)
				&& (ticking instanceof MOB)
				&& (((MOB) ticking).charStats().getBodyPart(Race.BODY_WING) <= 0))
			unInvoke();
		return super.tick(ticking, tickID);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if (target == null)
			return false;
		if (target.charStats().getBodyPart(Race.BODY_WING) <= 0) {
			mob.tell("You can't flap without wings.");
			return false;
		}

		boolean wasFlying = CMLib.flags().isFlying(target);
		Ability A = target.fetchEffect(ID());
		if (A != null)
			A.unInvoke();
		target.recoverPhyStats();
		String str = "";
		if (wasFlying)
			str = "<S-NAME> stop(s) flapping <S-HIS-HER> wings.";
		else
			str = "<S-NAME> start(s) flapping <S-HIS-HER> wings.";

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					CMMsg.MSG_NOISYMOVEMENT, str);
			if (target.location().okMessage(target, msg)) {
				target.location().send(target, msg);
				beneficialAffect(mob, target, asLevel, 9999);
				A = target.fetchEffect(ID());
				if (A != null)
					A.makeLongLasting();
			}
		} else
			return beneficialVisualFizzle(mob, target,
					"<T-NAME> fumble(s) trying to use <T-HIS-HER> wings.");

		// return whether it worked
		return success;
	}
}
