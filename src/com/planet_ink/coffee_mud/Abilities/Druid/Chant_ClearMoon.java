package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
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
public class Chant_ClearMoon extends Chant {
	public String ID() {
		return "Chant_ClearMoon";
	}

	public String name() {
		return "Clear Moon";
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return 0;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_MOONALTERING;
	}

	public void clearMoons(Physical P) {
		if (P != null)
			for (int a = P.numEffects() - 1; a >= 0; a--) // personal and
															// reverse
															// enumeration
			{
				Ability A = P.fetchEffect(a);
				if ((A != null)
						&& (((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_MOONALTERING) || ((A
								.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_MOONSUMMONING)))
					A.unInvoke();
			}
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (!success)
			this.beneficialVisualFizzle(mob, null,
					"<S-NAME> chant(s) for a clear moon, but the magic fades.");
		else {
			CMMsg msg = CMClass.getMsg(mob, null, this,
					verbalCastCode(mob, null, auto),
					"^S<S-NAME> chant(s) for a clear moon.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				Room thatRoom = mob.location();
				clearMoons(thatRoom);
				for (int i = 0; i < thatRoom.numInhabitants(); i++) {
					MOB M = thatRoom.fetchInhabitant(i);
					clearMoons(M);
				}
				for (int i = 0; i < thatRoom.numItems(); i++) {
					Item I = thatRoom.getItem(i);
					clearMoons(I);
				}
			}
		}

		return success;
	}
}
