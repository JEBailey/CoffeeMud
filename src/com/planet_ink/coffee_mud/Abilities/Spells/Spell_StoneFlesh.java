package com.planet_ink.coffee_mud.Abilities.Spells;

import java.util.Vector;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
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
public class Spell_StoneFlesh extends Spell {
	public String ID() {
		return "Spell_StoneFlesh";
	}

	public String name() {
		return "Stone Flesh";
	}

	protected int canTargetCode() {
		return CAN_ITEMS;
	}

	public int classificationCode() {
		return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
	}

	public int abstractQuality() {
		return Ability.QUALITY_INDIFFERENT;
	}

	public boolean invoke(MOB mob, Vector commands, Physical givenTarget,
			boolean auto, int asLevel) {

		Physical target = getTarget(mob, mob.location(), givenTarget, commands,
				Wearable.FILTER_UNWORNONLY);
		if (target == null)
			return false;

		Ability revokeThis = null;
		for (int a = 0; a < target.numEffects(); a++) // personal effects
		{
			Ability A = target.fetchEffect(a);
			if ((A != null)
					&& (A.canBeUninvoked())
					&& ((A.ID().equalsIgnoreCase("Spell_FleshStone")) || (A
							.ID().equalsIgnoreCase("Prayer_FleshRock")))) {
				revokeThis = A;
				break;
			}
		}

		if (revokeThis == null) {
			if (auto)
				mob.tell("Nothing happens.");
			else
				mob.tell(mob, target, null,
						"<T-NAME> can not be affected by this spell.");
			return false;
		}

		if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
			return false;

		boolean success = proficiencyCheck(mob, 0, auto);
		if (success) {
			CMMsg msg = CMClass.getMsg(mob, target, this,
					verbalCastCode(mob, target, auto), auto ? ""
							: "^S<S-NAME> dispel(s) " + revokeThis.name()
									+ " from <T-NAMESELF>.^?");
			if (mob.location().okMessage(mob, msg)) {
				mob.location().send(mob, msg);
				revokeThis.unInvoke();
			}
		} else
			beneficialWordsFizzle(mob, target, "<S-NAME> attempt(s) to dispel "
					+ revokeThis.name() + " from <T-NAMESELF>, but flub(s) it.");

		// return whether it worked
		return success;
	}
}
