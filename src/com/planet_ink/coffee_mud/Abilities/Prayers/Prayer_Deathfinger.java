package com.planet_ink.coffee_mud.Abilities.Prayers;

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
public class Prayer_Deathfinger extends Prayer {
	public String ID() {
		return "Prayer_Deathfinger";
	}

	public String name() {
		return "Deathfinger";
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

	protected int overrideMana() {
		return 150;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		MOB target = this.getTarget(mob, commands, givenTarget);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (success) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass
					.getMsg(mob,
							target,
							this,
							verbalCastCode(mob, target, auto)
									| CMMsg.MASK_MALICIOUS,
							auto ? "A finger of death rages at <T-NAME>."
									: "^S<S-NAME> point(s) in rage at <T-NAMESELF> and "
											+ prayWord(mob) + "!^?");
			CMMsg msg2 = CMClass.getMsg(mob, target, this,
					CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_UNDEAD
							| (auto ? CMMsg.MASK_ALWAYS : 0), null);
			final Room R = target.location();
			if ((R.okMessage(mob, msg)) && ((R.okMessage(mob, msg2)))) {
				R.send(mob, msg);
				R.send(mob, msg2);
				if ((msg.value() <= 0) && (msg2.value() <= 0)) {
					int harming = CMLib.dice().roll(1,
							adjustedLevel(mob, asLevel), 48 + 16);
					int levelDiff = (mob.phyStats().level() - target.phyStats()
							.level());
					if (levelDiff > 0)
						harming += (int) Math.round(CMath.mul(CMath.div(target
								.baseState().getHitPoints(), 10), levelDiff));
					CMLib.combat().postDamage(mob, target, this, harming,
							CMMsg.MASK_ALWAYS | CMMsg.TYP_UNDEAD,
							Weapon.TYPE_BURSTING,
							"The finger of DEATH <DAMAGE> <T-NAME>!");
				}
			}
		} else
			return maliciousFizzle(mob, target,
					"<S-NAME> point(s) in rage at <T-NAMESELF> and "
							+ prayWord(mob) + ", but " + hisHerDiety(mob)
							+ " does nothing.");

		// return whether it worked
		return success;
	}
}
