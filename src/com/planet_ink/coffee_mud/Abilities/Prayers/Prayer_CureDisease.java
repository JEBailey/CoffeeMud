package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Prayer_CureDisease extends Prayer implements MendingSkill {
	public String ID() {
		return "Prayer_CureDisease";
	}

	public String name() {
		return "Cure Disease";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_RESTORATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	public long flags() {
		return Ability.FLAG_HOLY;
	}

	protected int abilityCode = 0;

	public void setAbilityCode(int newCode) {
		super.setAbilityCode(newCode);
		this.abilityCode = newCode;
	}

	public boolean supportsMending(Physical item) {
		if (!(item instanceof MOB))
			return false;
		boolean canMend = returnOffensiveAffects(item).size() > 0;
		return canMend;
	}

	public List<Ability> returnOffensiveAffects(Physical fromMe) {
		Vector offenders = new Vector();

		for (int a = 0; a < fromMe.numEffects(); a++) // personal
		{
			Ability A = fromMe.fetchEffect(a);
			if ((A != null) && (A instanceof DiseaseAffect))
				offenders.addElement(A);
		}
		return offenders;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (supportsMending(target))
					return super.castingQuality(mob, target,
							Ability.QUALITY_BENEFICIAL_OTHERS);
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		List<Ability> offensiveAffects = returnOffensiveAffects(target);

		if ((success) && (offensiveAffects.size() > 0)) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto),
					auto ? "A healing glow surrounds <T-NAME>." : "^S<S-NAME> "
							+ prayWord(mob) + " for <T-YOUPOSS> health.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				boolean badOnes = false;
				for (int a = offensiveAffects.size() - 1; a >= 0; a--) {
					Ability A = (offensiveAffects.get(a));
					if (A instanceof DiseaseAffect) {
						if ((A.invoker() != mob)
								&& ((((DiseaseAffect) A).difficultyLevel() * 10) > adjustedLevel(
										mob, asLevel) + abilityCode))
							badOnes = true;
						else
							A.unInvoke();
					} else
						A.unInvoke();

				}
				if (badOnes)
					mob.location()
							.show(mob, target, null, CMMsg.MSG_OK_VISUAL,
									"<T-NAME> had diseases too powerful for <S-YOUPOSS> magic.");
				else
					mob.location().show(mob, target, null, CMMsg.MSG_OK_VISUAL,
							"<S-NAME> cure(s) the diseases in <T-NAMESELF>.");
				if (!CMLib.flags().stillAffectedBy(target, offensiveAffects,
						false))
					target.tell("You feel much better!");
			}
		} else if (!auto)
			beneficialWordsFizzle(mob, target, auto ? "" : "<S-NAME> "
					+ prayWord(mob) + " for <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}
