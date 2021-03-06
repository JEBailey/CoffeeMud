package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
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
@SuppressWarnings("rawtypes")
public class Spell_MassHold extends Spell {
	public String ID() {
		return "Spell_MassHold";
	}

	public String name() {
		return "Mass Hold";
	}

	public String displayText() {
		return "";
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Set<MOB> h = properTargets(mob, givenTarget, auto);
		if (h == null) {
			mob.tell("There doesn't appear to be anyone here worth putting to sleep.");
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, -20, auto);

		if (success) {
			if (mob.location()
					.show(mob,
							null,
							this,
							verbalCastCode(mob, null, auto),
							auto ? ""
									: "^S<S-NAME> incant(s) and wave(s) <S-HIS-HER> arms.^?"))
				for (Iterator f = h.iterator(); f.hasNext();) {
					MOB target = (MOB) f.next();

					// if they can't hear the sleep spell, it
					// won't happen
					if (CMLib.flags().canBeHeardSpeakingBy(mob, target)) {
						// it worked, so build a copy of this ability,
						// and add it to the affects list of the
						// affected MOB. Then tell everyone else
						// what happened.
						MOB oldVictim = mob.getVictim();
						CMMsg msg = CMClass.getMsg(mob, target, this,
								verbalCastCode(mob, target, auto), null);
						if ((mob.location().okMessage(mob, msg))
								&& (target.fetchEffect(this.ID()) == null)) {
							mob.location().send(mob, msg);
							if (msg.value() <= 0) {
								int levelDiff = target.phyStats().level()
										- (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
								if (levelDiff < 0)
									levelDiff = 0;
								if (levelDiff > 6)
									levelDiff = 6;

								Spell_Hold spell = new Spell_Hold();
								spell.setProficiency(proficiency());
								success = spell.maliciousAffect(mob, target,
										asLevel, 7 - levelDiff, -1);
								if (success)
									if (target.location() == mob.location())
										target.location()
												.show(target, null,
														CMMsg.MSG_OK_ACTION,
														"<S-NAME> become(s) perfectly still!!");
							}
						}
						if (oldVictim == null)
							mob.setVictim(null);
					} else
						maliciousFizzle(mob, target,
								"<T-NAME> seem(s) unaffected by the spell from <S-NAME>.");
				}
		} else
			return maliciousFizzle(mob, null,
					"<S-NAME> incant(s) a spell, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
