package com.planet_ink.coffee_mud.Abilities.Druid;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
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
public class Chant_SensePoison extends Chant {
	public String ID() {
		return "Chant_SensePoison";
	}

	public String name() {
		return "Sense Poison";
	}

	public int abstractQuality() {
		return Ability.QUALITY_OK_OTHERS;
	}

	protected int canAffectCode() {
		return 0;
	}

	protected int canTargetCode() {
		return CAN_MOBS | CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_CHANT | Ability.DOMAIN_PRESERVING;
	}

	public List<Ability> returnOffensiveAffects(Physical fromMe) {
		Vector offenders = new Vector();

		for (final Enumeration<Ability> a = fromMe.effects(); a
				.hasMoreElements();) {
			final Ability A = a.nextElement();
			if ((A != null)
					&& ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_POISON))
				offenders.addElement(A);
		}
		if (fromMe instanceof MOB) {
			MOB mob = (MOB) fromMe;
			for (Enumeration<Ability> a = mob.allAbilities(); a
					.hasMoreElements();) {
				Ability A = a.nextElement();
				if ((A != null)
						&& ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_POISON))
					offenders.addElement(A);
			}
		}
		return offenders;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {
		Physical target = getAnyTarget(mob, commands, givenTarget,
				Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		List<Ability> offensiveAffects = returnOffensiveAffects(target);

		if ((success)
				&& ((offensiveAffects.size() > 0) || ((target instanceof Drink) && (((Drink) target)
						.liquidType() == RawMaterial.RESOURCE_POISON)))) {
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB. Then tell everyone else
			// what happened.
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> chant(s) over <T-NAME>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				StringBuffer buf = new StringBuffer(target.name()
						+ " contains: ");
				if (offensiveAffects.size() == 0)
					buf.append("weak impurities, ");
				else
					for (int i = 0; i < offensiveAffects.size(); i++)
						buf.append(offensiveAffects.get(i).name() + ", ");
				mob.tell(buf.toString().substring(0, buf.length() - 2));
			}
		} else
			beneficialWordsFizzle(
					mob,
					target,
					auto ? ""
							: "<S-NAME> chant(s) over <T-NAME>, but receives no insight.");

		// return whether it worked
		return success;
	}
}
