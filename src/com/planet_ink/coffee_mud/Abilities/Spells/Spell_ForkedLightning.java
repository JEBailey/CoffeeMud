package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
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
public class Spell_ForkedLightning extends Spell {
	public String ID() {
		return "Spell_ForkedLightning";
	}

	public String name() {
		return "Forked Lightning";
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(2);
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
	}

	public long flags() {
		return Ability.FLAG_AIRBASED;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Set<MOB> h = properTargets(mob, givenTarget, auto);
		if (h == null) {
			mob.tell("There doesn't appear to be anyone here worth electrocuting.");
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

			if (mob.location()
					.show(mob,
							null,
							this,
							verbalCastCode(mob, null, auto),
							(auto ? "A thunderous crack of lightning erupts!"
									: "^S<S-NAME> invoke(s) a thunderous crack of forked lightning.^?")
									+ CMLib.protocol().msp("lightning.wav", 40)))
				for (Iterator f = h.iterator(); f.hasNext();) {
					MOB target = (MOB) f.next();

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB. Then tell everyone else
					// what happened.
					CMMsg msg = CMClass.getMsg(mob, target, this,
							verbalCastCode(mob, target, auto), null);
					CMMsg msg2 = CMClass.getMsg(mob, target, this,
							CMMsg.MSK_CAST_MALICIOUS_VERBAL
									| CMMsg.TYP_ELECTRIC
									| (auto ? CMMsg.MASK_ALWAYS : 0), null);
					if ((mob.location().okMessage(mob, msg))
							&& ((mob.location().okMessage(mob, msg2)))) {
						mob.location().send(mob, msg);
						mob.location().send(mob, msg2);
						invoker = mob;

						int maxDie = (int) Math.round(CMath.div(
								adjustedLevel(mob, asLevel)
										+ (2 * super.getX1Level(mob)), 2.0));
						int damage = CMLib.dice().roll(maxDie, 7, 1);
						if ((msg.value() > 0) || (msg2.value() > 0))
							damage = (int) Math.round(CMath.div(damage, 2.0));
						if (target.location() == mob.location())
							CMLib.combat().postDamage(mob, target, this,
									damage,
									CMMsg.MASK_ALWAYS | CMMsg.TYP_ELECTRIC,
									Weapon.TYPE_STRIKING,
									"A bolt <DAMAGE> <T-NAME>!");
					}
				}
		} else
			return maliciousFizzle(mob, null,
					"<S-NAME> attempt(s) to invoke a ferocious spell, but the spell fizzles.");

		// return whether it worked
		return success;
	}
}
