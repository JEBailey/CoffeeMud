package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.HashSet;
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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Prayer_ChainStrike extends Prayer {
	public String ID() {
		return "Prayer_ChainStrike";
	}

	public String name() {
		return "Chain Strike";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_CREATION;
	}

	public long flags() {
		return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY | Ability.FLAG_AIRBASED;
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(2);
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Set<MOB> h = properTargets(mob, givenTarget, auto);
		if (h == null)
			h = new HashSet();

		Vector targets = new Vector(h);

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		int damage = CMLib.dice().roll(1, adjustedLevel(mob, asLevel) / 2,
				1 + (2 * super.getX1Level(mob)));

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			if (mob.location()
					.show(mob,
							null,
							this,
							verbalCastCode(mob, null, auto),
							(auto ? "A thunderous crack of electricity erupts!"
									: "^S<S-NAME> "
											+ prayForWord(mob)
											+ " to send down a thunderous crack of electricity.^?")
									+ CMLib.protocol().msp("lightning.wav", 40))) {
				while (damage > 0) {
					int oldDamage = damage;
					for (int i = 0; i < targets.size(); i++) {
						MOB target = (MOB) targets.elementAt(i);
						if (target.amDead()
								|| (target.location() != mob.location())) {
							int count = 0;
							for (int i2 = 0; i2 < targets.size(); i2++) {
								MOB M2 = (MOB) targets.elementAt(i2);
								if ((!M2.amDead()) && (mob.location() != null)
										&& (mob.location().isInhabitant(M2))
										&& (M2.location() == mob.location()))
									count++;
							}
							if (count < 2)
								return true;
							continue;
						}

						// it worked, so build a copy of this ability,
						// and add it to the affects list of the
						// affected MOB. Then tell everyone else
						// what happened.
						boolean oldAuto = auto;
						CMMsg msg = CMClass.getMsg(mob, target, this,
								verbalCastCode(mob, target, auto), null);
						CMMsg msg2 = CMClass.getMsg(mob, target, this,
								CMMsg.MSK_CAST_MALICIOUS_VERBAL
										| CMMsg.TYP_ELECTRIC
										| (auto ? CMMsg.MASK_ALWAYS : 0), null);
						auto = oldAuto;
						if ((mob.location().okMessage(mob, msg))
								&& ((mob.location().okMessage(mob, msg2)))) {
							mob.location().send(mob, msg);
							mob.location().send(mob, msg2);
							invoker = mob;

							int dmg = damage;
							if ((msg.value() > 0) || (msg2.value() > 0))
								dmg = (int) Math.round(CMath.div(dmg, 2.0));
							if (target.location() == mob.location()) {
								CMLib.combat().postDamage(mob, target, this,
										dmg,
										CMMsg.MASK_ALWAYS | CMMsg.TYP_ELECTRIC,
										Weapon.TYPE_STRIKING,
										"The strike <DAMAGE> <T-NAME>!");
								damage = (int) Math.round(CMath
										.div(damage, 2.0));
								if (damage < 2) {
									damage = 0;
									break;
								}
							}
						}
					}
					if (oldDamage == damage)
						break;
				}
			}
		} else
			return maliciousFizzle(mob, null, "<S-NAME> " + prayWord(mob)
					+ " for a ferocious spell, but nothing happens.");

		// return whether it worked
		return success;
	}
}
