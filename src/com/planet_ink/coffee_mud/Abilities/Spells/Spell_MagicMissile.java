package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
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
public class Spell_MagicMissile extends Spell {
	public String ID() {
		return "Spell_MagicMissile";
	}

	public String name() {
		return "Magic Missile";
	}

	public String displayText() {
		return "(Magic Missile spell)";
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(1);
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_CONJURATION;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			int numMissiles = ((int) Math.round(Math.floor(CMath.div(
					adjustedLevel(mob, asLevel), 5))) + 1);
			final Room R = target.location();
			for (int i = 0; (i < numMissiles) && (target.location() == R); i++) {
				CMMsg msg = CMClass
						.getMsg(mob,
								target,
								this,
								somanticCastCode(mob, target, auto),
								(i == 0) ? ((auto ? "A magic missile appears hurling full speed at <T-NAME>!"
										: "^S<S-NAME> point(s) at <T-NAMESELF>, shooting forth a magic missile!^?") + CMLib
										.protocol().msp("spelldam2.wav", 40))
										: null);
				if (mob.location().okMessage(mob, msg)) {
					mob.location().send(mob, msg);
					if (msg.value() <= 0) {
						int damage = CMLib.dice().roll(1, 11, 11 / numMissiles);
						if (target.location() == mob.location())
							CMLib.combat().postDamage(
									mob,
									target,
									this,
									damage,
									CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL,
									Weapon.TYPE_BURSTING,
									((i == 0) ? "^SThe missile "
											: "^SAnother missile ")
											+ "<DAMAGE> <T-NAME>!^?");
					}
				}
				if (target.amDead()) {
					target = this.getTarget(mob, commands, givenTarget, true,
							false);
					if (target == null)
						break;
					if (target.amDead())
						break;
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> point(s) at <T-NAMESELF>, but fizzle(s) the spell.");

		// return whether it worked
		return success;
	}
}
