package com.planet_ink.coffee_mud.Abilities.Fighter;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Fighter_Shrug extends FighterSkill {
	public String ID() {
		return "Fighter_Shrug";
	}

	public String name() {
		return "Shrug Off";
	}

	public String displayText() {
		return "(Braced for a hit)";
	}

	private static final String[] triggerStrings = { "BRACE" };

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected int canAffectCode() {
		return Ability.CAN_MOBS;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_FITNESS;
	}

	public int usageType() {
		return USAGE_MOVEMENT;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((affected != null) && (affected instanceof MOB)
				&& (msg.amITarget(affected))
				&& (msg.targetMinor() == CMMsg.TYP_DAMAGE)
				&& (!msg.amISource((MOB) affected))
				&& (CMLib.flags().aliveAwakeMobile((MOB) affected, true))
				&& (msg.tool() != null) && (msg.tool() instanceof Weapon)) {
			MOB mob = (MOB) affected;
			if (mob.location().show(mob, msg.source(), this,
					CMMsg.MSG_OK_ACTION,
					"<S-NAME> shrug(s) off the attack from <T-NAME>.")) {
				unInvoke();
				return false;
			}
		}
		return super.okMessage(myHost, msg);
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (!mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if ((!auto) && (!mob.isInCombat())) {
			mob.tell("You must be in combat first!");
			return false;
		}

		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;

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
			CMMsg msg = CMClass.getMsg(mob, target, this,
					CMMsg.MSG_QUIETMOVEMENT,
					auto ? "<T-NAME> is braced for an attack!"
							: "<S-NAME> brace(s) for an attack!");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			return beneficialVisualFizzle(mob, null,
					"<S-NAME> attempt(s) to brace <S-HIM-HERSELF>, but get(s) distracted.");

		// return whether it worked
		return success;
	}
}
