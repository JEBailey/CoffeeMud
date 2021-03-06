package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Food;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
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
public class Chant_FortifyFood extends Chant {
	public String ID() {
		return "Chant_FortifyFood";
	}

	public String name() {
		return "Fortify Food";
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Item target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_ANY);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);

		if (!(target instanceof Food)) {
			mob.tell(target.name(mob) + " is not edible.");
			return false;
		}

		if (((Food) target).nourishment() > 1000) {
			mob.tell(target.name(mob) + " is already well fortified.");
			return false;
		}

		if (success && (((Food) target).nourishment() <= 0)) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> chant(s) to <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				mob.location().show(mob, target, CMMsg.MSG_OK_ACTION,
						"<T-NAME> look(s) much more nutritious!");
				int bites = 1;
				if (((Food) target).bite() > 0)
					bites = ((Food) target).nourishment()
							/ ((Food) target).bite();
				if (bites < 1)
					bites = 1;
				((Food) target)
						.setNourishment(((Food) target).nourishment() + 1000);
				((Food) target).setBite(((Food) target).nourishment() / bites);
			}
		} else
			beneficialWordsFizzle(mob, target,
					"<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens.");

		// return whether it worked
		return success;
	}
}
