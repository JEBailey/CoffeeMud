package com.planet_ink.coffee_mud.Abilities.Misc;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.HealthCondition;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
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
public class CombatSleep extends StdAbility implements HealthCondition {
	public String ID() {
		return "CombatSleep";
	}

	public String name() {
		return "Combat Sleep";
	}

	public String displayText() {
		return "(Asleep)";
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

	private static final String[] triggerStrings = { "COMBATSLEEP" };

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY | Ability.FLAG_PARALYZING;
	}

	@Override
	public String getHealthConditionDesc() {
		return "Unconsciousness.";
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (!(affected instanceof MOB))
			return true;

		MOB mob = (MOB) affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if ((msg.amISource(mob)) && (!msg.sourceMajor(CMMsg.MASK_ALWAYS))
				&& (msg.sourceMajor() > 0)) {
			mob.tell("You are way too unconscious.");
			return false;
		}
		return super.okMessage(myHost, msg);
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		// when this spell is on a MOBs Affected list,
		// it should consistantly put the mob into
		// a sleeping state, so that nothing they do
		// can get them out of it.
		affectableStats.setDisposition(affectableStats.disposition()
				| PhyStats.IS_SLEEPING);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;

		super.unInvoke();
		if (canBeUninvoked()) {
			if ((!mob.amDead()) && (mob.location() != null))
				mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL,
						"<S-NAME> do(es)n't seem so drowsy any more.");
			CMLib.commands().postStand(mob, true);
		}
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (((MOB) target).isInCombat())
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		// sleep has a 3 level difference for PCs, so check for this.
		int levelDiff = target.phyStats().level()
				- (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
		if (levelDiff < 0)
			levelDiff = 0;
		if (levelDiff > 2)
			levelDiff = 2;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			invoker = mob;
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							CMMsg.MASK_MALICIOUS | CMMsg.TYP_MIND
									| (auto ? CMMsg.MASK_ALWAYS : 0),
							auto ? ""
									: "^S<S-NAME> make(s) <T-NAMESELF> go unconscious!^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					success = maliciousAffect(mob, target, asLevel,
							3 - levelDiff, CMMsg.MASK_MALICIOUS
									| CMMsg.TYP_MIND
									| (auto ? CMMsg.MASK_ALWAYS : 0));
					if (success)
						if (target.location() == mob.location())
							target.location().show(target, null,
									CMMsg.MSG_OK_ACTION,
									"<S-NAME> fall(s) unconscious!!");
				}
				target.makePeace();
				if (mob.getVictim() == target)
					mob.makePeace();
			}
		} else
			return maliciousFizzle(
					mob,
					target,
					auto ? ""
							: "^S<S-NAME> tr(ys) to make <T-NAMESELF> go unconscious, but fails.^?");

		// return whether it worked
		return success;
	}
}
