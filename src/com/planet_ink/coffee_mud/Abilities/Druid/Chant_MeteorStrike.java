package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Iterator;
import java.util.Set;
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
public class Chant_MeteorStrike extends Chant {
	public String ID() {
		return "Chant_MeteorStrike";
	}

	public String name() {
		return "Meteor Strike";
	}

	public int maxRange() {
		return adjustedMaxInvokerRange(5);
	}

	public int minRange() {
		return 1;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_ROCKCONTROL;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	public int castingQuality(MOB mob, Physical target) {
		if (mob != null) {
			Set<MOB> h = properTargets(mob, target, false);
			if (h == null)
				return Ability.QUALITY_INDIFFERENT;

			Room R = mob.location();
			if (R != null) {
				if ((R.domainType() & Room.INDOORS) > 0)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob, target);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Set<MOB> h = properTargets(mob, givenTarget, auto);
		if (h == null) {
			mob.tell("There doesn't appear to be anyone here worth striking at.");
			return false;
		}
		if ((mob.location().domainType() & Room.INDOORS) > 0) {
			mob.tell("You must be outdoors to strike with meteors.");
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
							(auto ? "A devastating meteor shower erupts!"
									: "^S<S-NAME> chant(s) for a devastating meteor shower!^?")
									+ CMLib.protocol().msp("meteor.wav", 40)))
				for (Iterator f = h.iterator(); f.hasNext();) {
					MOB target = (MOB) f.next();

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB. Then tell everyone else
					// what happened.
					CMMsg msg = CMClass.getMsg(mob, target, this,
							verbalCastCode(mob, target, auto), null);
					if (mob.location().okMessage(mob, msg)) {
						mob.location().send(mob, msg);
						invoker = mob;

						int damage = 0;
						int maxDie = (adjustedLevel(mob, asLevel) + (2 * super
								.getX1Level(mob))) / 2;
						damage = CMLib.dice().roll(maxDie, 6, 30);
						if (msg.value() > 0)
							damage = (int) Math.round(CMath.div(damage, 2.0));
						if (target.location() == mob.location())
							CMLib.combat().postDamage(mob, target, this,
									damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE,
									Weapon.TYPE_BASHING,
									"The meteors <DAMAGE> <T-NAME>!");
					}
				}
		} else
			return maliciousFizzle(mob, null,
					"<S-NAME> chant(s) to the sky, but nothing happens.");

		// return whether it worked
		return success;
	}
}
