package com.planet_ink.coffee_mud.Abilities.Prayers;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
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
public class Prayer_DesecrateLand extends Prayer {
	public String ID() {
		return "Prayer_DesecrateLand";
	}

	public String name() {
		return "Desecrate Land";
	}

	public String displayText() {
		return "(Desecrate Land)";
	}

	public int classificationCode() {
		return Ability.ACODE_PRAYER | Ability.DOMAIN_WARDING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_MALICIOUS;
	}

	protected int canAffectCode() {
		return CAN_ROOMS;
	}

	protected int canTargetCode() {
		return CAN_ROOMS;
	}

	public long flags() {
		return Ability.FLAG_UNHOLY;
	}

	public boolean okMessage(final Environmental myHost, final CMMsg msg) {
		if (affected == null)
			return super.okMessage(myHost, msg);

		if ((msg.sourceMinor() == CMMsg.TYP_CAST_SPELL)
				&& (msg.tool() instanceof Ability)
				&& ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_PRAYER)
				&& (!CMath.bset(((Ability) msg.tool()).flags(),
						Ability.FLAG_UNHOLY))
				&& (CMath.bset(((Ability) msg.tool()).flags(),
						Ability.FLAG_HOLY))) {
			msg.source().tell("This place is blocking holy magic!");
			return false;
		}
		return super.okMessage(myHost, msg);
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = mob.location();
		if (target == null)
			return false;
		if (target.fetchEffect(ID()) != null) {
			mob.tell("This place is already desecrated.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> " + prayForWord(mob)
									+ " to desecrate this place.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				setMiscText(mob.Name());
				if ((target instanceof Room)
						&& (CMLib.law().doesOwnThisProperty(mob,
								((Room) target)))) {
					target.addNonUninvokableEffect((Ability) this.copyOf());
					CMLib.database().DBUpdateRoom((Room) target);
				} else
					beneficialAffect(mob, target, asLevel, 0);
			}
		} else
			beneficialWordsFizzle(mob, target, "<S-NAME> " + prayForWord(mob)
					+ " to desecrate this place, but <S-IS-ARE> not answered.");

		return success;
	}
}