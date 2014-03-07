package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Enumeration;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
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
public class Prayer_NeutralizeLand extends Prayer {
	public String ID() {
		return "Prayer_NeutralizeLand";
	}

	public String name() {
		return "Neutralize Land";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return Ability.CAN_ROOMS;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			Room R = mob.location();
			if (R != null) {
				boolean foundAny = false;
				for (final Enumeration<Ability> a = R.effects(); a
						.hasMoreElements();) {
					final Ability A = a.nextElement();
					if ((A != null) && (A.invoker() != mob))
						foundAny = true;
				}
				if (!foundAny)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		Room target = mob.location();
		if ((target != null) && (success)) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto),
							auto ? "<T-NAME> become(s) neutralized."
									: "^S<S-NAME> "
											+ prayWord(mob)
											+ ", sweeping <S-HIS-HER> hands over <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Ability revokeThis = null;
				boolean foundSomethingAtLeast = false;
				for (int a = 0; a < target.numEffects(); a++) // personal
																// affects
				{
					Ability A = target.fetchEffect(a);
					if ((A != null)
							&& ((A.canBeUninvoked()) || (A.invoker() == mob)
									|| A.text().equals(mob.Name()) || (mob
									.getClanRole(A.text()) != null))
							&& (!A.isAutoInvoked())
							&& (((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SPELL)
									|| ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_PRAYER)
									|| ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_CHANT) || ((A
									.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SONG))) {
						foundSomethingAtLeast = true;
						if ((A.invoker() != null)
								&& ((A.invoker().phyStats().level() <= (mob
										.phyStats().level() + (2 * super
										.getXLEVELLevel(mob))))))
							revokeThis = A;
					}
				}

				if (revokeThis == null) {
					if (foundSomethingAtLeast)
						mob.location()
								.show(mob, target, CMMsg.MSG_OK_VISUAL,
										"The magic on <T-NAME> appears too powerful to be nullified.");
					else if (auto)
						mob.tell(mob, target, null,
								"Nothing seems to be happening to <T-NAME>.");
				} else {
					revokeThis.unInvoke();
					target.delEffect(revokeThis);
					if (!revokeThis.canBeUninvoked())
						CMLib.database().DBUpdateRoom(mob.location());
				}
			} else
				beneficialWordsFizzle(
						mob,
						target,
						auto ? ""
								: "<S-NAME> sweep(s) <S-HIS-HER> hands over <T-NAMESELF>, but "
										+ hisHerDiety(mob) + " does not heed.");
		}

		// return whether it worked
		return success;
	}
}