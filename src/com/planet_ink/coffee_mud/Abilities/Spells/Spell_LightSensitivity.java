package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
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
public class Spell_LightSensitivity extends Spell {
	public String ID() {
		return "Spell_LightSensitivity";
	}

	public String name() {
		return "Light Sensitivity";
	}

	public String displayText() {
		return "(Light Sensitivity)";
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

	public void affectPhyStats(Physical affected, PhyStats affectableStats) {
		super.affectPhyStats(affected, affectableStats);
		if (!(affected instanceof MOB))
			return;
		if (((MOB) affected).location() == null)
			return;
		if (CMLib.flags().isInDark(((MOB) affected).location()))
			affectableStats.setSensesMask(affectableStats.sensesMask()
					| PhyStats.CAN_SEE_DARK);
		else
			affectableStats.setSensesMask(affectableStats.sensesMask()
					| PhyStats.CAN_NOT_SEE);
	}

	public void unInvoke() {
		// undo the affects of this spell
		if (!(affected instanceof MOB))
			return;
		MOB mob = (MOB) affected;
		super.unInvoke();

		if (canBeUninvoked())
			mob.tell("Your light sensitivity returns to normal.");
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (mob.isInCombat()) {
				if (CMLib.flags().isInDark(mob.location()))
					return Ability.QUALITY_INDIFFERENT;
				if (target instanceof MOB) {
					if (((MOB) target).charStats().getBodyPart(Race.BODY_EYE) == 0)
						return Ability.QUALITY_INDIFFERENT;
					if (!CMLib.flags().canSee((MOB) target))
						return Ability.QUALITY_INDIFFERENT;
				}
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if ((!auto) && (target.charStats().getBodyPart(Race.BODY_EYE) == 0)) {
			mob.tell(target.name(mob)
					+ " has no eyes, and would not be affected.");
			return false;
		}

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
			String autoStr = "A flashing light blazes in the eyes of <T-NAME>!";
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? autoStr
									: "^SYou invoke a sensitive light into <T-NAME>s eyes.^?",
							verbalCastCode(mob, target, auto),
							auto ? autoStr
									: "^S<S-NAME> invoke(s) a sensitive light into your eyes.^?",
							CMMsg.MSG_CAST_ATTACK_VERBAL_SPELL,
							auto ? autoStr
									: "^S<S-NAME> invokes a sensitive light into <T-NAME>s eyes.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				if (msg.value() <= 0) {
					if (CMLib.flags().isInDark(mob.location()))
						mob.location()
								.show(target, null, CMMsg.MSG_OK_VISUAL,
										"<S-NAME> become(s) extremely sensitive to light.");
					else
						mob.location().show(target, null, CMMsg.MSG_OK_VISUAL,
								"<S-NAME> become(s) blinded by the light.");
					if (castingQuality(mob, target) == Ability.QUALITY_MALICIOUS)
						success = maliciousAffect(mob, target, asLevel, 0, -1);
					else
						success = beneficialAffect(mob, target, asLevel, 0);
				}
			}
		} else if (castingQuality(mob, target) == Ability.QUALITY_MALICIOUS)
			return maliciousFizzle(mob, target,
					"<S-NAME> invoke(s) at <T-NAMESELF>, but the spell fizzles.");
		else
			return beneficialVisualFizzle(mob, target,
					"<S-NAME> invoke(s) at <T-NAMESELF>, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
