package com.planet_ink.coffee_mud.Abilities.Fighter;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Fighter_Charge extends FighterSkill {
	public String ID() {
		return "Fighter_Charge";
	}

	public String name() {
		return "Charge";
	}

	private static final String[] triggerStrings = { "CHARGE" };

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public String[] triggerStrings() {
		return triggerStrings;
	}

	public String displayText() {
		return "(Charging!!)";
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_ACROBATIC;
	}

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	public int minRange() {
		return 1;
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(2);
	}

	protected int code = 0;

	public int abilityCode() {
		return code;
	}

	public void setAbilityCode(int c) {
		code = c;
	}

	public boolean done = false;

	public void executeMsg(final Environmental myHost, final CMMsg msg) {
		if ((affected != null) && (affected instanceof MOB)
				&& (msg.amISource((MOB) affected))
				&& (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK))
			done = true;
		super.executeMsg(myHost, msg);
	}

	public boolean tick(Tickable ticking, int tickID) {
		if (tickID == Tickable.TICKID_MOB)
			if (done)
				unInvoke();
		return super.tick(ticking, tickID);
	}

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		int xlvl = getXLEVELLevel(invoker());
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
				+ (2 * (affected.phyStats().level() + xlvl)));
		affectableStats.setDamage(affectableStats.damage()
				+ (affected.phyStats().level()) + abilityCode() + xlvl);
		affectableStats.setArmor(affectableStats.armor()
				+ (2 * (xlvl + affected.phyStats().level())));
	}

	public int castingQuality(MOB mob, Physical target) {
		if ((mob != null) && (target != null)) {
			if ((mob.isInCombat()) && (mob.rangeToTarget() <= 0))
				return Ability.QUALITY_INDIFFERENT;
			if ((CMLib.flags().isSitting(mob)) || (mob.riding() != null))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		boolean notInCombat = !mob.isInCombat();
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if ((mob.isInCombat()) && (mob.rangeToTarget() <= 0)) {
			mob.tell("You can not charge while in melee!");
			return false;
		}
		if ((CMLib.flags().isSitting(mob)) || (mob.riding() != null)) {
			mob.tell("You must be on your feet to charge!");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		// now see if it worked
		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass
					.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS
							| CMMsg.MSG_ADVANCE,
							"^F^<FIGHT^><S-NAME> charge(s) at <T-NAMESELF>!^</FIGHT^>^?");
			CMLib.color().fixSourceFightColor(msg);
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (mob.getVictim() == target) {
					mob.setAtRange(0);
					target.setAtRange(0);
					beneficialAffect(mob, mob, asLevel, 2);
					mob.recoverPhyStats();
					if (notInCombat) {
						done = true;
						CMLib.combat().postAttack(mob, target,
								mob.fetchWieldedItem());
					} else
						done = false;
					if (mob.getVictim() == null)
						mob.setVictim(null); // correct range
					if (target.getVictim() == null)
						target.setVictim(null); // correct range
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> attempt(s) to charge <T-NAME>, but then give(s) up.");

		// return whether it worked
		return success;
	}
}
