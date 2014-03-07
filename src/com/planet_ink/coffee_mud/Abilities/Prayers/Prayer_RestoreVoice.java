package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
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
public class Prayer_RestoreVoice extends Prayer implements MendingSkill {
	public String ID() {
		return "Prayer_RestoreVoice";
	}

	public String name() {
		return "Restore Voice";
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

	public boolean supportsMending(Physical item) {
		if (!(item instanceof MOB))
			return false;
		MOB caster = CMClass.getFactoryMOB();
		caster.basePhyStats().setLevel(
				CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL));
		caster.phyStats().setLevel(
				CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL));
		boolean canMend = returnOffensiveAffects(caster, item).size() > 0;
		caster.destroy();
		return canMend;
	}

	public List<Ability> returnOffensiveAffects(MOB caster, Physical fromMe) {
		MOB newMOB = CMClass.getFactoryMOB();
		Vector offenders = new Vector(1);

		for (int a = 0; a < fromMe.numEffects(); a++) // personal
		{
			Ability A = fromMe.fetchEffect(a);
			if (A != null) {
				newMOB.recoverPhyStats();
				A.affectPhyStats(newMOB, newMOB.phyStats());
				if ((!CMLib.flags().canSpeak(newMOB))
						&& ((A.invoker() == null) || ((A.invoker() != null) && (A
								.invoker().phyStats().level() <= (caster
								.phyStats().level() + 10 + (2 * super
								.getXLEVELLevel(caster)))))))
					offenders.addElement(A);
			}
		}
		newMOB.destroy();
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
		List<Ability> offensiveAffects = returnOffensiveAffects(mob, target);

		if ((success) && (offensiveAffects.size() > 0)) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto),
					auto ? "A visible glow surrounds <T-NAME>." : "^S<S-NAME> "
							+ prayWord(mob) + " for <T-NAMESELF> to speak.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				for (int a = offensiveAffects.size() - 1; a >= 0; a--)
					offensiveAffects.get(a).unInvoke();
			}
		} else
			beneficialWordsFizzle(mob, target, auto ? "" : "<S-NAME> "
					+ prayWord(mob) + " for <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}