package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
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
public class Prayer_Hellfire extends Prayer {
	public String ID() {
		return "Prayer_Hellfire";
	}

	public String name() {
		return "Hellfire";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_VEXING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			if (target instanceof MOB) {
				if (!CMLib.flags().isGood((target)))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;
		boolean undead = target.charStats().getMyRace().racialCategory()
				.equals("Undead");

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		final Room R = target.location();
		if ((success) && (CMLib.flags().isGood(target))) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			Prayer_Hellfire newOne = (Prayer_Hellfire) this.copyOf();
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							newOne,
							verbalCastCode(mob, target, auto)
									| CMMsg.MASK_MALICIOUS,
							(auto ? ""
									: "^S<S-NAME> "
											+ prayForWord(mob)
											+ " to rage against the good inside <T-NAMESELF>!^?")
									+ CMLib.protocol().msp("spelldam1.wav", 40));
			CMMsg msg2 = CMClass.getMsg(mob, target, this,
					CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_UNDEAD
							| (auto ? CMMsg.MASK_ALWAYS : 0), null);
			if ((R.okMessage(mob, msg)) && ((R.okMessage(mob, msg2)))) {
				R.send(mob, msg);
				R.send(mob, msg2);
				if ((msg.value() <= 0) && (msg2.value() <= 0)) {
					int harming = CMLib.dice().roll(2,
							adjustedLevel(mob, asLevel),
							adjustedLevel(mob, asLevel) / 2);
					if (undead)
						harming = harming / 2;
					if (CMLib.flags().isGood(target))
						CMLib.combat().postDamage(mob, target, this, harming,
								CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE,
								Weapon.TYPE_BURNING,
								"The unholy HELLFIRE <DAMAGE> <T-NAME>!");
				}
			}
		} else
			return maliciousFizzle(mob, target, "<S-NAME> " + prayWord(mob)
					+ ", but nothing emerges.");

		// return whether it worked
		return success;
	}
}
