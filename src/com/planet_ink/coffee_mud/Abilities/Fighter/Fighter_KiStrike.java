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
public class Fighter_KiStrike extends FighterSkill {
	public String ID() {
		return "Fighter_KiStrike";
	}

	public String name() {
		return "Ki Strike";
	}

	public String displayText() {
		return "(Ki Strike)";
	}

	private static final String[] triggerStrings = { "KISTRIKE", "KI" };

	public int abstractQuality() {
		return Ability.QUALITY_BENEFICIAL_SELF;
	}

	public String[] triggerStrings() {
		return triggerStrings;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_MOBS;
	}

	public int classificationCode() {
		return Ability.ACODE_SKILL | Ability.DOMAIN_PUNCHING;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if ((affected != null) && (affected instanceof MOB)
				&& (msg.amISource((MOB) affected))
				&& ((msg.tool() instanceof Weapon) || (msg.tool() == null))
				&& (msg.targetMinor() == CMMsg.TYP_DAMAGE) && (msg.value() > 0)) {
			MOB mob = (MOB) affected;
			if ((CMLib.flags().aliveAwakeMobile(mob, true))
					&& (mob.location() != null)) {
				mob.location().show(mob, null, CMMsg.MSG_SPEAK,
						"<S-NAME> yell(s) 'KIA'!");
				msg.setValue(msg.value() + adjustedLevel(invoker(), 0));
				unInvoke();
			}

		}
		return super.okMessage(myHost, msg);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = mob;
		if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
			target = (MOB) givenTarget;

		if (!CMLib.flags().canSpeak(mob)) {
			mob.tell("You can't speak!");
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
			CMMsg msg = CMClass.getMsg(mob, target, this,
					CMMsg.MSG_QUIETMOVEMENT,
					"<S-NAME> concentrate(s) <S-HIS-HER> strength.");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				beneficialAffect(mob, target, asLevel, mob.isInCombat() ? 2 : 4);
			}
		} else
			return beneficialVisualFizzle(mob, null,
					"<S-NAME> lose(s) concentration.");

		// return whether it worked
		return success;
	}
}
